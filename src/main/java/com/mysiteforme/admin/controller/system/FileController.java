package com.mysiteforme.admin.controller.system;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Created by tnt on 2017/12/7.
 * updated by wangl on 2018/1/10
 * 文件上传下载
 */
@Controller
@RequestMapping("file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);


    private UploadService qiniuService;

    private UploadService ossService;

    private UploadService localService;

    private SiteService siteService;

    public FileController() {}

    @Autowired
    public FileController(UploadService qiniuService, UploadService ossService, UploadService localService, SiteService siteService) {
        this.qiniuService = qiniuService;
        this.ossService = ossService;
        this.localService = localService;
        this.siteService= siteService;
    }

    @PostMapping("upload")
    @ResponseBody
    @SysLog("文件上传")
    public RestResponse uploadFile(@RequestParam("test") MultipartFile file,HttpServletRequest httpServletRequest) {
        Site site = (Site)httpServletRequest.getAttribute("site");
        if(site == null){
            return RestResponse.failure("加载信息错误");
        }

        if(file == null){
            return RestResponse.failure("上传文件为空 ");
        }

        String fullName = file.getOriginalFilename();
        if (fullName != null && ToolUtil.imageEasyCheck(fullName)) {
            return RestResponse.failure("上传文件格式不正确 ");
        }
        String url = null;
        Map<String,String> m = Maps.newHashMap();
        try {
            if("local".equals(site.getFileUploadType())){
                url = localService.upload(file);
            }
            if("qiniu".equals(site.getFileUploadType())){
                url = qiniuService.upload(file);
            }
            if("oss".equals(site.getFileUploadType())){
                url = ossService.upload(file);
            }
            m.put("url", url);
            m.put("name", file.getOriginalFilename());
        } catch (Exception e) {
            logger.error("文件上传失败",e);
            return RestResponse.failure(e.getMessage());
        }
        return RestResponse.success().setData(m);
    }

    @PostMapping("uploadBase64")
    @ResponseBody
    @SysLog("base64格式文件上传")
    public RestResponse uploadBase64(@RequestParam(value = "file",required = false) String file,
                                     @RequestParam(value = "name",required= false)String name){
        Site site = siteService.getCurrentSite();
        if(site == null){
            return RestResponse.failure("加载信息错误");
        }
        if(StringUtils.isBlank(file)){
            return RestResponse.failure("图片不能为空");
        }
        if(StringUtils.isBlank(name)){
            return RestResponse.failure("图片名称不能为空");
        }
        String url = null;
        String fileUploadType = site.getFileUploadType();
        if ("local".equals(fileUploadType)) {
            url = localService.uploadBase64(file);
        }
        if ("qiniu".equals(fileUploadType)) {
            url = qiniuService.uploadBase64(file);
        }
        if ("oss".equals(fileUploadType)) {
            url = ossService.uploadBase64(file);
        }
        return RestResponse.success().setData(url);
    }

    /**
     * wangEditor批量上传图片
     */
    @PostMapping("uploadWang")
    @ResponseBody
    @SysLog("富文本编辑器文件上传")
    public Map<String,Object> uploadWang(@RequestParam("test")MultipartFile[] test) {
        Site site = siteService.getCurrentSite();
        if(site == null){
            return RestResponse.failure("加载信息错误");
        }
        if(test == null || test.length == 0){
            return RestResponse.failure("上传文件为空 ");
        }
        List<String> data = Lists.newArrayList();
        Map<String,Object> m = Maps.newHashMap();
        String fileUploadType = site.getFileUploadType();
        try {
            for (MultipartFile multipartFile : test) {
                String fullName = multipartFile.getOriginalFilename();
                if (fullName != null && ToolUtil.imageEasyCheck(fullName)) {
                    return RestResponse.failure("上传文件格式不正确 ");
                }
                String url = null;
                if ("local".equals(fileUploadType)) {
                    url = localService.upload(multipartFile);
                }
                if ("qiniu".equals(fileUploadType)) {
                    url = qiniuService.upload(multipartFile);
                }
                if ("oss".equals(fileUploadType)) {
                    url = ossService.upload(multipartFile);
                }
                data.add(url);
            }
            m.put("errno",0);
        } catch (Exception e) {
            logger.error("文件上传失败",e);
            m.put("errno",1);
            return RestResponse.failure(e.getMessage());
        }
        m.put("data",data);
        return m;
    }

    /**
     * wangEditor复制新闻中包含图片的话吧图片上传到七牛上并更换图片地址
     */
    @PostMapping("doContent")
    @ResponseBody
    @SysLog("富文本编辑器内容图片上传")
    public RestResponse doContent(@RequestParam(value="content",required = false) String content,
                                  HttpServletRequest httpServletRequest) throws IOException, NoSuchAlgorithmException {
        Site site = (Site)httpServletRequest.getAttribute("site");
        if(site == null){
            return RestResponse.failure("加载信息错误");
        }
        if (StringUtils.isBlank(content)){
            return RestResponse.failure("复制内容为空");
        }
        Document doc = Jsoup.parseBodyFragment(content);
        Elements links = doc.select("img[src]");
        for(Element e : links){
            String imgSrc = e.attr("src");
            // 过滤掉非图片文件
            if(ToolUtil.isImage(imgSrc)){
                continue;
            }
            String urlType = ToolUtil.parseImageUrl(imgSrc);
            // 如果当前网站设置的是本地上传模式
            if("local".equals(site.getFileUploadType())){
                if(!uploadResult(localService,urlType,e,imgSrc)){
                    continue;
                }
            }
            // 如果当前系统设置的是七牛上传模式
            if("qiniu".equals(site.getFileUploadType())){
                if(!uploadResult(qiniuService,urlType,e,imgSrc)){
                    continue;
                }
            }
            // 如果当前系统设置的是阿里云上传模式
            if("oss".equals(site.getFileUploadType())){
                uploadResult(ossService,urlType,e,imgSrc);
            }
        }
        return RestResponse.success().setData(doc.body().html());
    }

    private boolean uploadResult(UploadService service,String urlType ,Element e, String imgSrc) throws IOException, NoSuchAlgorithmException {
        //区分文件的来源类型
        if("local".equals(urlType)){
            imgSrc = imgSrc.substring(6);
            File file = new File(imgSrc);
            if(!file.exists()){
                return false;
            }
            e.attr("src",service.uploadLocalImg(imgSrc));
        }else if("web".equals(urlType)){
            e.attr("src",service.uploadNetFile(imgSrc));
        }else if("base64".equals(urlType)){
            e.attr("src",service.uploadBase64(imgSrc));
        }else {
            // 未知的文件来源类型过滤掉
            return false;
        }
        return true;
    }



    @PostMapping("downCheck")
    @ResponseBody
    public RestResponse downCheck(@RequestParam(value="url",required = false) String url,
                                  @RequestParam(value="name",required = false) String name){
        if(ToolUtil.isImage(url)){
            return RestResponse.failure("图片格式不正确");
        }
        if(StringUtils.isBlank(url)){
            return RestResponse.failure("图片地址不能为空");
        }
        if(StringUtils.isBlank(name)){
            return RestResponse.failure("图片名称不能为空");
        }
        // 此功能未来版本会实现
        return RestResponse.success();
    }

    @GetMapping("download")
    @ResponseBody
    @SysLog("文件下载")
    public RestResponse downFile(@RequestParam(value="url",required = false) String realurl,
                                 @RequestParam(value="name",required = false) String name,
                                 HttpServletResponse response) throws IOException {
        if(StringUtils.isBlank(realurl)){
            return RestResponse.failure("下载地址不能为空");
        }
        if(StringUtils.isBlank(name)){
            return RestResponse.failure("文件名称不能为空");
        }
        if("text/html".equals(ToolUtil.getContentType(name))){
            return RestResponse.failure("文件格式不正确");
        }
        name = new String(name.getBytes("GB2312"),"ISO8859-1");
        URL url=new URL(realurl);
        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
        conn.connect();
        BufferedInputStream br = new BufferedInputStream(conn.getInputStream());
        byte[] buf = new byte[1024];
        int len;
        response.reset();
        response.setHeader("Content-type","application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename="+name);
        ServletOutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0) out.write(buf, 0, len);
        br.close();
        out.flush();
        out.close();
        conn.disconnect();
        return RestResponse.success();
    }

}
