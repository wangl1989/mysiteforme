package com.mysiteforme.admin.controller.system;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.util.QiniuFileUtil;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
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
 * ${TODO}
 */
@Controller
@RequestMapping("file")
public class FileController {

    //推荐创建不可变静态类成员变量
    private static final Log log = LogFactory.get();

    @Value("uploadType")
    private String uploadType;

    @Value("localUploadPath")
    private String localUploadPath;

    @PostMapping("upload")
    @ResponseBody
    @SysLog("文件上传")
    public RestResponse uploadFile(@RequestParam("test") MultipartFile file) {

        if(file == null){
            return RestResponse.failure("上传文件为空 ");
        }
        Map m = Maps.newHashMap();
        try {
            String url = QiniuFileUtil.upload(file);
            m.put("url", url);
            m.put("name", file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.failure(e.getMessage());
        }
        return RestResponse.success().setData(m);
    }

    @PostMapping("uploadBase64")
    @ResponseBody
    @SysLog("base64格式文件上传")
    public RestResponse uploadBase64(@RequestParam(value = "file",required = false) String file,
                                     @RequestParam(value = "name",required= false)String name){
        if(StringUtils.isBlank(file)){
            return RestResponse.failure("图片不能为空");
        }
        if(StringUtils.isBlank(name)){
            return RestResponse.failure("图片名称不能为空");
        }
        String url = QiniuFileUtil.uploadBase64(file,name);
        return RestResponse.success().setData(url);
    }

    /**
     * wangEditor批量上传图片
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("uploadWang")
    @ResponseBody
    @SysLog("富文本编辑器文件上传")
    public Map<String,Object> uploadWang(@RequestParam("test") MultipartFile[] file) {

        if(file == null || file.length == 0){
            return RestResponse.failure("上传文件为空 ");
        }
        List<String> data = Lists.newArrayList();
        Map<String,Object> m = Maps.newHashMap();
        try {
            for (int i=0;i<file.length;i++) {
                String url = QiniuFileUtil.upload(file[i]);
                data.add(url);
            }
            m.put("errno",0);
        } catch (Exception e) {
            e.printStackTrace();
            m.put("errno",1);
        }
        m.put("data",data);
        return m;
    }

    /**
     * wangEditor复制新闻中包含图片的话吧图片上传到七牛上并更换图片地址
     * @return
     */
    @PostMapping("doContent")
    @ResponseBody
    @SysLog("富文本编辑器内容图片上传")
    public RestResponse doContent(@RequestParam(value="content",required = false) String content) throws IOException, NoSuchAlgorithmException {
        if (StringUtils.isBlank(content)){
            return RestResponse.failure("复制内容为空");
        }
        Document doc = Jsoup.parseBodyFragment(content);
        Elements links = doc.select("img[src]");
        for(Element e : links){
            String imgSrc = e.attr("abs:src");
            if(imgSrc.contains("file:")){
                imgSrc.replace("\\","/");
                e.attr("src",QiniuFileUtil.uploadLocalImg(imgSrc.substring(6)));
            }else{
                e.attr("src",QiniuFileUtil.uploadImageSrc(imgSrc));
            }
        }
        String outHtml = doc.body().toString();
        return RestResponse.success().setData(outHtml.replace("<body>","").replace("</body>",""));
    }

    @PostMapping("downCheck")
    @ResponseBody
    public RestResponse downCheck(@RequestParam(value="url",required = false) String url,
                                  @RequestParam(value="name",required = false) String name){
        if(StringUtils.isBlank(url)){
            return RestResponse.failure("图片地址不能为空");
        }
        if(StringUtils.isBlank(name)){
            return RestResponse.failure("图片名称不能为空");
        }
        return RestResponse.success();
    }

    @GetMapping("download")
    @ResponseBody
    @SysLog("文件下载")
    public RestResponse downFile(@RequestParam(value="url",required = false) String realurl,
                                 @RequestParam(value="name",required = false) String name,
                                 HttpServletResponse response) throws IOException {
        if(StringUtils.isBlank(realurl)){
            return RestResponse.failure("图片地址不能为空");
        }
        if(StringUtils.isBlank(name)){
            return RestResponse.failure("图片名称不能为空");
        }
        if("text/html".equals(ToolUtil.getContentType(name))){
            return RestResponse.failure("图片格式不正确");
        }
        name = new String(name.getBytes("GB2312"),"ISO8859-1");
        URL url=new URL(realurl);
        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
        conn.connect();
        BufferedInputStream br = new BufferedInputStream(conn.getInputStream());
        byte[] buf = new byte[1024];
        int len = 0;
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




    public String localUpload(HttpServletRequest request, MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // 文件保存路径
                if (StringUtils.isBlank(localUploadPath)) {
                    localUploadPath = request.getSession().getServletContext().getRealPath("/") + "/static/upload/" + System.currentTimeMillis() + "/";
                    String filePath = localUploadPath + file.getOriginalFilename();
                    file.transferTo(new File(filePath));
                    return filePath;
                } else {
                    Long t = System.currentTimeMillis();
                    String filePath = localUploadPath + t + "/" + file.getOriginalFilename();
                    file.transferTo(new File(filePath));
                    return "/upload/" + t + filePath;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return  null;
            }
        }else{
            return null;
        }
    }
}
