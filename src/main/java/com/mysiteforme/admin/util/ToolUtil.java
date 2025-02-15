/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 22:38:24
 * @ Description: 基础工具类
 */

package com.mysiteforme.admin.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.exception.MyException;
import com.xiaoleilu.hutool.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class ToolUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(ToolUtil.class);

	/**
	 * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
	 */
	public static void entryptPassword(User user) {
		byte[] salt = Digests.generateSalt(Constants.SALT_SIZE);
		user.setSalt(Encodes.encodeHex(salt));
		byte[] hashPassword = Digests.sha1(user.getPassword().getBytes(), salt, Constants.HASH_INTERATIONS);
		user.setPassword(Encodes.encodeHex(hashPassword));
	}

	/**
	 *
	 * @param paramStr 输入需要加密的字符串
     */
	public static String entryptPassword(String paramStr,String salt) {
		if(StringUtils.isNotEmpty(paramStr)){
			byte[] saltStr = Encodes.decodeHex(salt);
			byte[] hashPassword = Digests.sha1(paramStr.getBytes(), saltStr, Constants.HASH_INTERATIONS);
            return Encodes.encodeHex(hashPassword);
		}else{
			return null;
		}

	}

	/**
	 * 将BufferedImage转换为Base64字符串
	 * @param image BufferedImage对象
	 * @return Base64编码的图片字符串
	 * @throws IOException 如果转换失败
	 */
	public static String getBase64FromImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
		byte[] imageBytes = baos.toByteArray();
		return Base64.getEncoder().encodeToString(imageBytes);
    }


	/**
	 * 验证上传图片类型
	 * @param imgSrc 图片路径
	 * @return 图片来源类型，local：本地图片，web：网络图片，unknown：未知图片
	 */
	@NotNull
	public static String parseImageUrl(@NotNull String imgSrc) {
		if (imgSrc.contains("file:")) {
			return "local";
		} else if (imgSrc.startsWith("http://") || imgSrc.startsWith("https://")) {
			return "web";
		} else if (imgSrc.startsWith("data:image")) {
			return "base64";
		} else {
			return "unknown";
		}
	}

	/**
	 * 判断是否为图片
	 * @param url 图片地址
	 * @return 是否为图片
	 */
	public static boolean isImage(@NotNull String url) {
		try {
			if(url.startsWith("data:image")){
				return false;
			}
			URL u = new URL(url);
			String file = u.getFile();
			return file == null || !file.toLowerCase().matches(".*\\.(jpg|jpeg|gif|png|bmp|svg|ico)$");
		} catch (MalformedURLException e) {
			LOGGER.error("URL格式错误", e);
			return true;
		}
	}

	/**
	 * 判断是否为图片
	 * @param url 图片地址
	 * @return 是否为图片
	 */
	public static boolean imageEasyCheck(@NotNull String url) {
		if(StringUtils.isEmpty(url)){
			return true;
		}
		return !url.toLowerCase().matches(".*\\.(jpg|jpeg|gif|png|bmp|svg|ico)$");
	}

	/**
	 * 从base64字符串中提取文件格式
	 * @param base64String base64字符串
	 * @return 文件格式（如 "jpg", "png" 等）
	 */
	public static String getFileFormat(String base64String) {
		// 匹配data:image/xxx;base64,格式的正则表达式
		Pattern pattern = Pattern.compile("data:image/(.*?);base64,");
		Matcher matcher = pattern.matcher(base64String);

		if (matcher.find()) {
			String format = matcher.group(1);
			// 处理特殊情况
			if (format.equals("jpeg")) {
				return "jpg";
			}
			return format.toLowerCase();
		}

		// 如果没有找到格式，默认返回jpg
		return "jpg";
	}


	/**
	 * 通过泛型检查工具方法封装
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> getSessionMapAttribute(HttpSession session, String attributeName, Class<K> keyType, Class<V> valueType) {
		Object attr = session.getAttribute(attributeName);
		if (attr instanceof Map) {
			Map<?, ?> rawMap = (Map<?, ?>) attr;
			for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
				if (!keyType.isInstance(entry.getKey()) || !valueType.isInstance(entry.getValue())) {
					return null;
				}
			}
			return Collections.checkedMap((Map<K, V>) rawMap, keyType, valueType);
		}
		return null;
	}

	/**
	 * 获取客户端的ip信息
	 *
     */
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		LOGGER.info("ipadd : {}" , ip);
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknow".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		LOGGER.info(" ip --> {}" , ip);
		return ip;
	}
	
	/**
     * 将bean转换成map
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> convertBeanToMap(Object condition) {
		if (condition == null) {
			return null;
		}
		if (condition instanceof Map) {
			return (Map<String, Object>) condition;
		}
		Map<String, Object> objectAsMap = new HashMap<>();
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(condition.getClass());
		} catch (IntrospectionException e) {
			LOGGER.error("转换bean为map失败", e);
			throw MyException.builder().code(MyException.SERVER_ERROR).msg("转换bean为map失败").build();
		}

		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			Method reader = pd.getReadMethod();
			if (reader != null&&!"class".equals(pd.getName()))
				try {
					objectAsMap.put(pd.getName(), reader.invoke(condition));
				} catch (IllegalArgumentException e) {
					LOGGER.error("出现IllegalArgumentException异常", e);
					throw MyException.builder().code(MyException.SERVER_ERROR).msg("出现IllegalArgumentException异常").throwable(e).build();
				} catch (IllegalAccessException e) {
					LOGGER.error("出现IllegalAccessException异常", e);
					throw MyException.builder().code(MyException.SERVER_ERROR).msg("出现IllegalAccessException异常").throwable(e).build();
				} catch (InvocationTargetException e) {
					LOGGER.error("出现InvocationTargetException异常", e);
					throw MyException.builder().code(MyException.SERVER_ERROR).msg("出现InvocationTargetException异常").throwable(e).build();
				}
		}
		return objectAsMap;
	}

	/**

	 * 通过文件名判断并获取OSS服务文件上传时文件的contentType

	 * @param fileName 文件名

	 * @return 文件的contentType

	 */
	public static  String getContentType(String fileName){
		int d = fileName.lastIndexOf(".");
		if( d== -1){
			return "text/html";
		}
		String fileExtension = fileName.substring(fileName.lastIndexOf("."));
		if(".bmp".equalsIgnoreCase(fileExtension)) return "image/bmp";
		if(".gif".equalsIgnoreCase(fileExtension)) return "image/gif";
		if(".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)) return "image/jpeg";
		if(".png".equalsIgnoreCase(fileExtension)) return "image/png";
		if(".html".equalsIgnoreCase(fileExtension)) return "text/html";
		if(".txt".equalsIgnoreCase(fileExtension)) return "text/plain";
		if(".vsd".equalsIgnoreCase(fileExtension)) return "application/vnd.visio";
		if(".ppt".equalsIgnoreCase(fileExtension) || ".pptx".equalsIgnoreCase(fileExtension)) return "application/vnd.ms-powerpoint";
		if(".doc".equalsIgnoreCase(fileExtension) || ".docx".equalsIgnoreCase(fileExtension)) return "application/msword";
		if(".xml".equalsIgnoreCase(fileExtension)) return "text/xml";
		return "text/html";
	}

	/**
	 * 判断请求是否是ajax请求
     */
	public static boolean isAjax(HttpServletRequest request){
		String accept = request.getHeader("accept");
        return accept != null && accept.contains("application/json") || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").contains("XMLHttpRequest"));
    }

	/**
	 * 获取操作系统,浏览器及浏览器版本信息
     */
	public static Map<String,String> getOsAndBrowserInfo(HttpServletRequest request){
		Map<String,String> map = Maps.newHashMap();
        String  userAgent       = request.getHeader("User-Agent");
		String  user            =   userAgent.toLowerCase();

		String os;
		String browser = "";

		//=================OS Info=======================
		if (userAgent.toLowerCase().contains("windows"))
		{
			os = "Windows";
		} else if(userAgent.toLowerCase().contains("mac"))
		{
			os = "Mac";
		} else if(userAgent.toLowerCase().contains("x11"))
		{
			os = "Unix";
		} else if(userAgent.toLowerCase().contains("android"))
		{
			os = "Android";
		} else if(userAgent.toLowerCase().contains("iphone"))
		{
			os = "IPhone";
		}else{
			os = "UnKnown, More-Info: "+userAgent;
		}
		//===============Browser===========================
		if (user.contains("edge"))
		{
			browser=(userAgent.substring(userAgent.indexOf("Edge")).split(" ")[0]).replace("/", "-");
		} else if (user.contains("msie"))
		{
			String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
			browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
		} else if (user.contains("safari") && user.contains("version"))
		{
			browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]
					+ "-" +(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
		} else if ( user.contains("opr") || user.contains("opera"))
		{
			if(user.contains("opera")){
				browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]
						+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
			}else if(user.contains("opr")){
				browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-"))
						.replace("OPR", "Opera");
			}

		} else if (user.contains("chrome"))
		{
			browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
		} else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6"))  ||
				(user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) ||
				(user.contains("mozilla/4.08")) || (user.contains("mozilla/3")) )
		{
			browser = "Netscape-?";

		} else if (user.contains("firefox"))
		{
			browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
		} else if(user.contains("rv"))
		{
			String IEVersion = (userAgent.substring(userAgent.indexOf("rv")).split(" ")[0]).replace("rv:", "-");
			browser="IE" + IEVersion.substring(0,IEVersion.length() - 1);
		} else
		{
			browser = "UnKnown, More-Info: "+userAgent;
		}
		map.put("os", HtmlUtils.htmlEscape(os));
		map.put("browser",HtmlUtils.htmlEscape(browser));
		return map;
	}

	/**
     */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getAddressByIP(String ip) {
		String area;
		String province;
		String city;
		String isp = "";
		Map<String,String> finalMap = Maps.newHashMap();
		try{
			if("0:0:0:0:0:0:0:1".equals(ip)){
				ip = "0.0.0.0";
			}
            String result= HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=" + ip, "GBK");
			Map<String,String> resultMap = JSON.parseObject(result,Map.class);
			String status = resultMap.get("err");

			province = resultMap.get("pro");
			city = resultMap.get("city");
			if("noprovince".equalsIgnoreCase(status)){
				area = resultMap.get("addr");
			}else{
				area = "中国";
				String addr = resultMap.get("addr");
				if(StringUtils.isNotBlank(addr)){
					isp = addr.split(" ")[1];
				}
			}
		}catch (Exception e){
			LOGGER.error("获取地理位置异常",e);
			throw MyException.builder().code(MyException.SERVER_ERROR).msg("获取地理位置异常").throwable(e).build();
		}


		finalMap.put("area",area);
		finalMap.put("province",province);
		finalMap.put("city",city);
		finalMap.put("isp",isp);
		return finalMap;
	}

	public static void main(String[] args) throws Exception {
		Map<String,String> maps = getAddressByIP("117.82.187.111");
		System.out.println(JSONObject.toJSONString(maps));
	}
}
