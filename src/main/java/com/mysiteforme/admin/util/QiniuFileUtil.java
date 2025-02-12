package com.mysiteforme.admin.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.service.RescourceService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

/**
 * 七牛云文件操作工具类
 * 提供七牛云文件上传、删除等操作
 */
public class QiniuFileUtil {

	private static final Logger logger = LoggerFactory.getLogger(QiniuFileUtil.class);

	private static final String PATH = "你的域名";
	private static final String QINIU_ACCESS = "****************";
	private static final String QINIU_KEY = "****************";
	private static final String BUCKT_NAME = "wanggg";

	/***
	 * 普通上传图片
	 */
	public static String upload(MultipartFile file, RescourceService rescourceService) throws IOException, NoSuchAlgorithmException {
		Configuration config = new Configuration();
		String fileName, extName, filePath = "";
		if (null != file && !file.isEmpty()) {
			String originalFilename = file.getOriginalFilename();
			extName = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
			fileName = UUID.randomUUID() + extName;
			UploadManager uploadManager = new UploadManager(config);
			Auth auth = Auth.create(QINIU_ACCESS, QINIU_KEY);
			String token = auth.uploadToken(BUCKT_NAME);
			byte[] data = file.getBytes();
			QETag tag = new QETag();
			String hash = tag.calcETag(file);
			QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
			wrapper.eq("hash",hash);
			Rescource rescource = rescourceService.getOne(wrapper);
			if( rescource!= null && StringUtils.isNotBlank(rescource.getWebUrl())){
				return rescource.getWebUrl();
			}
			Response r = uploadManager.put(data, fileName, token);
			if (r.isOK()) {
				filePath = PATH + fileName;
				rescource = getRescource(file, fileName, extName, hash);
                rescource.setWebUrl(filePath);
				rescource.setSource("qiniu");
				rescourceService.save(rescource);
			}
		}
		return filePath;
	}

	@NotNull
	public static Rescource getRescource(MultipartFile file, String fileName, String extName, String hash) {
		Rescource rescource = new Rescource();
		rescource.setFileName(fileName);
		rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.getSize()/1024)+"kb");
		rescource.setHash(hash);
		rescource.setFileType(StringUtils.isBlank(extName)?"unknown":extName);
		return rescource;
	}

	/***
	 * 删除已经上传的图片
	 */
	public static void deleteQiniuP(String imgPath) {
		Configuration config = new Configuration();
		Auth auth = Auth.create(QINIU_ACCESS, QINIU_KEY);
		BucketManager bucketManager = new BucketManager(auth,config);
		imgPath = imgPath.replace(PATH, "");
		try {
			bucketManager.delete(BUCKT_NAME, imgPath);
		} catch (QiniuException e) {
			logger.error("删除七牛云图片失败:{}",e.getMessage());
			throw MyException.builder().code(MyException.SERVER_ERROR).msg("删除七牛云图片失败").build();
		}
	}

	/***
	 * 上传网络图片
	 */
	public static String uploadImageSrc(String src,RescourceService rescourceService){
		Configuration config = new Configuration();
		Auth auth = Auth.create(QINIU_ACCESS, QINIU_KEY);
		BucketManager bucketManager = new BucketManager(auth, config);
		String filePath;
		try {
			FetchRet fetchRet = bucketManager.fetch(src, BUCKT_NAME);
			filePath = PATH + fetchRet.key;
			Rescource rescource = new Rescource();
			rescource.setFileName(fetchRet.key);
			rescource.setFileSize(new java.text.DecimalFormat("#.##").format(fetchRet.fsize/1024)+"kb");
			rescource.setHash(fetchRet.hash);
			rescource.setFileType(fetchRet.mimeType);
			rescource.setWebUrl(filePath);
			rescource.setSource("qiniu");
			rescourceService.save(rescource);
		} catch (QiniuException e) {
			logger.error("七牛云上传网络图片失败:{}",e.getMessage());
			throw MyException.builder().code(MyException.SERVER_ERROR).msg("七牛云上传网络图片失败").throwable(e).build();
		}
		return filePath;
	}

	private static Rescource getFileRescource(File file, RescourceService rescourceService) throws IOException, NoSuchAlgorithmException {
		QETag tag = new QETag();
		String hash = tag.calcETag(file);
		QueryWrapper<Rescource> wrapper = new QueryWrapper<>();
		wrapper.eq("hash",hash);
		Rescource rescource = rescourceService.getOne(wrapper);
		if(rescource != null){
			return rescource;
		}else{
			rescource = new Rescource();
		}
		rescource.setHash(hash);
		return rescource;
    }

	/***
	 * 上传本地图片
	 */
	public static String uploadLocalImg(String src,RescourceService rescourceService) throws IOException, NoSuchAlgorithmException{
		Configuration config = new Configuration();
		UploadManager uploadManager = new UploadManager(config);
		Auth auth = Auth.create(QINIU_ACCESS, QINIU_KEY);
		String token = auth.uploadToken(BUCKT_NAME);
		File file = new File(src);
		if(!file.exists()){
			throw MyException.builder().code(MyException.SERVER_ERROR).msg("本地文件不存在").build();
		}
		Rescource rescource = getFileRescource(file, rescourceService);
		if(StringUtils.isNotEmpty(rescource.getWebUrl())){
			return rescource.getWebUrl();
		}
		String filePath="",
				extName,
		name = UUID.randomUUID().toString();
		extName = file.getName().substring(
				file.getName().lastIndexOf("."));
		Response response = uploadManager.put(file,name,token);
		if(response.isOK()){
			filePath = PATH + name;
			rescource = new Rescource();
			rescource.setFileName(name);
            saveSource(file, rescource.getHash(), rescource, filePath, extName);
            rescourceService.save(rescource);
		}
		return filePath;
	}

	public static void saveSource(File file, String hash, Rescource rescource, String filePath, String extName) {
		rescource.setFileSize(new java.text.DecimalFormat("#.##").format(file.length()/1024)+"kb");
		rescource.setHash(hash);
		rescource.setFileType(StringUtils.isBlank(extName)?"unknown":extName);
		rescource.setWebUrl(filePath);
		rescource.setSource("qiniu");
	}

	/**
	 * 上传base64位的图片
	 */
	public static String uploadBase64(String base64,String name) {
		Configuration config = new Configuration();
		UploadManager uploadManager = new UploadManager(config);
		Auth auth = Auth.create(QINIU_ACCESS, QINIU_KEY);
		String token = auth.uploadToken(BUCKT_NAME),filePath;

		byte[] data = Base64.decodeBase64(base64);
		try {
			uploadManager.put(data,name,token);
		} catch (IOException e) {
			logger.error("上传base64图片失败:{}",e.getMessage());
			throw MyException.builder().code(MyException.SERVER_ERROR).msg("上传base64图片失败").build();
		}
		filePath = PATH+name;
		return filePath;
	}
}
