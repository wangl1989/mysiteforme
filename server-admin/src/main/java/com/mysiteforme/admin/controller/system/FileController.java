/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 00:31:23
 * @ Description: 文件上传controller
 */

package com.mysiteforme.admin.controller.system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.util.LimitType;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.config.UploadServiceFactory;
import com.mysiteforme.admin.entity.Site;
import com.mysiteforme.admin.service.SiteService;
import com.mysiteforme.admin.service.UploadService;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import com.mysiteforme.admin.util.ToolUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
@RateLimit(limit = 20, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class FileController {

    private final UploadServiceFactory uploadServiceFactory;

    private final SiteService siteService;

    @SysLog(MessageConstants.SysLog.FILE_UPLOAD)
    @PostMapping("upload")
    public Result uploadFile(@RequestParam(value = "test", required = false) MultipartFile test,
                             @RequestParam(value = "name",required = false) String name,
                             @RequestParam(value = "uploadType",required = false) String uploadType) {
        Site site = siteService.getCurrentSite();
        if(site == null){
            return Result.paramMsgError(MessageConstants.file.SITE_INFO_ERROR);
        }
        if(test == null){
            return Result.paramMsgError(MessageConstants.file.UPLOAD_EMPTY);
        }

        String fullName = test.getOriginalFilename();
        if (fullName != null && ToolUtil.imageEasyCheck(fullName)) {
            return Result.paramMsgError(MessageConstants.file.FORMAT_ERROR);
        }
        Map<String,String> m = Maps.newHashMap();
        try {
            String url = uploadServiceFactory.getUploadService(StringUtils.isBlank(uploadType)?site.getFileUploadType():uploadType).upload(test,name);
            m.put("url", url);
            m.put("name", test.getOriginalFilename());
        } catch (IOException e) {
            log.error("文件上传失败: IO异常", e);
            return Result.businessMsgError(MessageConstants.file.IO_EXCEPTION);
        } catch (IllegalStateException e) {
            log.error("文件上传失败: 非法状态", e);
            return Result.businessMsgError(MessageConstants.file.ILLEGAL_STATE_EXCEPTION);
        } catch (NoSuchAlgorithmException e) {
            log.error("文件上传失败: SHA-1算法不可用", e);
            return Result.businessMsgError(MessageConstants.file.NO_SUCH_ALGORITHM_EXCEPTION);
        }
        return Result.success(m);
    }

    @SysLog(MessageConstants.SysLog.FILE_UPLOAD_BASE64)
    @PostMapping("uploadBase64")
    public Result uploadBase64(@RequestParam(value = "file",required = false) String file,
                                     @RequestParam(value = "name",required= false)String name){
        Site site = siteService.getCurrentSite();
        if(site == null){
            return Result.paramMsgError(MessageConstants.file.SITE_INFO_ERROR);
        }
        if(StringUtils.isBlank(file)){
            return Result.paramMsgError(MessageConstants.file.IMAGE_EMPTY);
        }
        if(StringUtils.isBlank(name)){
            return Result.paramMsgError(MessageConstants.file.IMAGE_NAME_EMPTY);
        }
        String url = uploadServiceFactory.getUploadService(site.getFileUploadType()).uploadBase64(file);
        return Result.success(url);
    }

    /**
     * wangEditor批量上传图片
     */
    @SysLog(MessageConstants.SysLog.FILE_UPLOAD_WANG)
    @PostMapping("uploadWang")
    public Map<String,Object> uploadWang(@RequestParam("file")MultipartFile[] file){
        Site site = siteService.getCurrentSite();
        if(site == null){
            return Result.businessMsgError(MessageConstants.file.SITE_INFO_ERROR);
        }
        if(file == null || file.length == 0){
            return Result.businessMsgError(MessageConstants.file.UPLOAD_EMPTY);
        }
        List<String> data = Lists.newArrayList();
        Map<String,Object> m = Maps.newHashMap();
        try {
            String fileUploadType = site.getFileUploadType();
            for (MultipartFile multipartFile : file) {
                String fullName = multipartFile.getOriginalFilename();
                if (fullName != null && ToolUtil.imageEasyCheck(fullName)) {
                    return Result.businessMsgError(MessageConstants.file.FORMAT_ERROR);
                }
                String url = uploadServiceFactory.getUploadService(fileUploadType).upload(multipartFile);
                data.add(url);
            }
            m.put("errno",0);
            m.put("data",data);
            return m;
        } catch (IOException e) {
            log.error("文件批量上传失败: IO异常", e);
            return Result.businessMsgError(MessageConstants.file.IO_EXCEPTION);
        } catch (IllegalStateException e) {
            log.error("文件批量上传失败: 非法状态", e);
            return Result.businessMsgError(MessageConstants.file.ILLEGAL_STATE_EXCEPTION);
        } catch (NoSuchAlgorithmException e) {
            log.error("文件批量上传失败: SHA-1算法不可用", e);
            return Result.businessMsgError(MessageConstants.file.NO_SUCH_ALGORITHM_EXCEPTION);
        }
    }

    /**
     * wangEditor复制新闻中包含图片的话吧图片上传到服务器并更换图片地址
     */
    @SysLog(MessageConstants.SysLog.FILE_UPLOAD_DO_CONTENT)
    @PostMapping("doContent")
    public Result doContent(@RequestParam(value="content",required = false) String content){
        Site site = siteService.getCurrentSite();
        if(site == null){
            return Result.paramMsgError(MessageConstants.file.SITE_INFO_ERROR);
        }
        if (StringUtils.isBlank(content)){
            return Result.paramMsgError(MessageConstants.file.COPY_CONTENT_EMPTY);
        }
        Document doc = Jsoup.parseBodyFragment(content);
        Elements links = doc.select("img[src]");
        try {
            for(Element e : links){
                String imgSrc = e.attr("src");
                // 过滤掉非图片文件
                if(ToolUtil.isImage(imgSrc)){
                    continue;
                }
                String urlType = ToolUtil.parseImageUrl(imgSrc);
                UploadService uploadService = uploadServiceFactory.getUploadService(site.getFileUploadType());
                if(uploadService == null){
                    continue;
                }
                uploadResult(uploadService,urlType,e,imgSrc);
            }
        } catch (IOException e) {
            log.error("编辑器中的文件上传失败: IO异常", e);
            return Result.businessMsgError(MessageConstants.file.IO_EXCEPTION);
        } catch (IllegalStateException e) {
            log.error("编辑器中的文件上传失败: 非法状态", e);
            return Result.businessMsgError(MessageConstants.file.ILLEGAL_STATE_EXCEPTION);
        } catch (NoSuchAlgorithmException e) {
            log.error("编辑器中的文件上传失败: SHA-1算法不可用", e);
            return Result.businessMsgError(MessageConstants.file.NO_SUCH_ALGORITHM_EXCEPTION);
        }
        return Result.success(doc.body().html());
    }

    private void uploadResult(UploadService service,String urlType ,Element e, String imgSrc) throws NoSuchAlgorithmException, IOException{
        //区分文件的来源类型
        switch (urlType) {
            case "local" -> {
                imgSrc = imgSrc.substring(6);
                File file = new File(imgSrc);
                if(!file.exists()){
                    return;
                }
                try {
                    e.attr("src",service.uploadLocalImg(imgSrc));
                }catch (Exception ex){
                    log.error("本地文件上传失败",ex);
                    e.attr("src",imgSrc);
                }

            }
            case "web" -> {
                try {
                    e.attr("src",service.uploadNetFile(imgSrc));
                }catch (Exception ex){
                    log.error("网络文件上传失败",ex);
                    e.attr("src",imgSrc);
                }
            }
            case "base64" -> {
                try {
                    e.attr("src",service.uploadBase64(imgSrc));
                } catch (Exception ex) {
                    log.error("上传base64文件失败",ex);
                    e.attr("src",imgSrc);
                }
            }
        }
    }

    @SysLog(MessageConstants.SysLog.FILE_DOWNLOAD)
    @GetMapping("download")
    public Result downFile(@RequestParam(value="url",required = false) String realurl,
                            @RequestParam(value="name",required = false) String name,
                            HttpServletResponse response) {
        if(StringUtils.isBlank(realurl)){
            return Result.paramMsgError(MessageConstants.file.DOWNLOAD_URL_EMPTY);
        }
        if(StringUtils.isBlank(name)){
            return Result.paramMsgError(MessageConstants.file.DOWNLOAD_NAME_EMPTY);
        }
        if("text/html".equals(ToolUtil.getContentType(name))){
            return Result.paramMsgError(MessageConstants.file.FORMAT_ERROR);
        }
        try {
            name = new String(name.getBytes("GB2312"),"ISO8859-1");
            URL url = new URL(realurl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            response.reset();
            response.setHeader("Content-type","application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename="+name);
            try (BufferedInputStream br = new BufferedInputStream(conn.getInputStream());
                ServletOutputStream out = response.getOutputStream()) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = br.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            } finally {
                conn.disconnect();
            }
        } catch (UnsupportedEncodingException e) {
            log.error("文件下载失败: 编码异常", e);
            return Result.businessMsgError(MessageConstants.file.FILE_DOWNLOAD_ENCODING_EXCEPTION);
        } catch (MalformedURLException e) {
            log.error("文件下载失败: URL异常", e);
            return Result.businessMsgError(MessageConstants.file.FILE_DOWNLOAD_URL_EXCEPTION);
        } catch (IOException e) {
            log.error("文件下载失败: IO异常", e);
            return Result.businessMsgError(MessageConstants.file.FILE_DOWNLOAD_IO_EXCEPTION);
        }
        
        return Result.success();
    }

}
