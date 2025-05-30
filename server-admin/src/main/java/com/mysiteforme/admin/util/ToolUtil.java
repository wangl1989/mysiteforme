/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:06:40
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:40:39
 * @ Description: 基础工具类
 */
package com.mysiteforme.admin.util;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.mysiteforme.admin.entity.DTO.AgentDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.HtmlUtils;

import com.mysiteforme.admin.exception.MyException;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
public class ToolUtil {

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
			log.error("URL格式错误", e);
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
	 * 获取客户端的ip信息
	 *
     */
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		log.debug("ipadd : {}" , ip);
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
		log.debug(" ip --> {}" , ip);
		// 对于通过多个代理的情况，X-Forwarded-For可能包含多个IP，取第一个非unknown的
		if (ip != null && ip.contains(",")) {
			ip = ip.split(",")[0];
		}
		return ip;
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
	 * 判断请求是否是json请求
     */
	public static boolean isJson(HttpServletRequest request){
		String accept = request.getHeader("accept");
        return accept != null && accept.contains("application/json");
    }

	/**
	 * 获取当前请求的 HttpServletRequest
	 * @return HttpServletRequest对象
	 */
	public static HttpServletRequest getCurrentRequest() {
		try {
			// 从 RequestContextHolder 获取当前请求
			ServletRequestAttributes attributes = (ServletRequestAttributes)
					RequestContextHolder.currentRequestAttributes();
			return attributes.getRequest();
		} catch (IllegalStateException e) {
			// 当前线程没有绑定请求（如异步任务、定时任务等）
			log.error("获取HttpServletRequest出现异常:{}",e.getMessage());
			throw MyException.builder().businessError(MessageConstants.System.SYSTEM_GET_HTTP_SERVLET_REQUEST_ERROR).build();
		}
	}

	/**
	 * 获取操作系统,浏览器及浏览器版本信息
     */
	public static AgentDTO getOsAndBrowserInfo(HttpServletRequest request){
		return getOsAndBrowserInfo(request.getHeader("User-Agent"));
	}

	/**
	 * 获取操作系统,浏览器及浏览器版本信息
	 */
	public static AgentDTO getOsAndBrowserInfo(String  userAgent){
		AgentDTO agent = new AgentDTO();
		agent.setUserAgent(userAgent);
		String  user = userAgent.toLowerCase();
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
		agent.setOs(HtmlUtils.htmlEscape(os));
		agent.setBrowser(HtmlUtils.htmlEscape(browser));
		return agent;
	}

	/**
	 * 获取操作系统类型
	 * @return 操作系统类型
	 */
	public static String getOs(){
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return "windows";
		} else if (osName.contains("mac")) {
			return "mac";
		} else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
			return "linux";
		} else {
			return "other";
		}
	}

	/**
	 * 验证文件路径格式是否正确
	 * @param path 待验证的路径
	 * @return 路径是否合法
	 */
	public static boolean isValidPath(String path) {
		if (StringUtils.isBlank(path)) {
			return true;
		}

		// 获取操作系统类型
		boolean isWindows = getOs().equals("windows");

		try {
			// Windows 路径特殊处理
			if (isWindows) {
				// 检查基本格式（驱动器号:\ 或 UNC 路径 \\）
				if (!path.matches("^([A-Za-z]:\\\\|\\\\\\\\).*")) {
					return true;
				}

				// Windows 路径长度限制
				if (path.length() > 260) {
					return true;
				}

				// 检查每个路径段是否合法
				String[] segments = path.split("\\\\");
				for (String segment : segments) {
					// 跳过驱动器号检查
					if (segment.matches("^[A-Za-z]:$")) {
						continue;
					}

					// Windows 文件名限制
					if (segment.matches("^(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])$")) {
						return true;
					}

					// 检查段名是否包含非法字符
					if (segment.matches(".*[<>:\"/\\\\|?*\u0000-\u001F].*")) {
						return true;
					}
				}
			} else {
				// Unix/Linux 路径处理
				// 检查基本格式
				if (!path.matches("^(/[^/\u0000]+)*/?$")) {
					return true;
				}

				// Unix 路径长度限制
				if (path.length() > 4096) {
					return true;
				}

				// 检查每个路径段
				String[] segments = path.split("/");
				for (String segment : segments) {
					if (StringUtils.isBlank(segment)) {
						continue;
					}

					// 检查段名是否包含非法字符
					if (segment.contains("\u0000")) {
						return true;
					}
				}
			}

			// 使用 Java NIO 进行最终验证
			Path normalizedPath = Paths.get(path).normalize();
			// 检查路径是否被归一化（防止 .. 或 . 等相对路径成分）
            return !normalizedPath.toString().equals(path);
        } catch (SecurityException | InvalidPathException e) {
			log.error("路径验证失败: {} - {}", path, e.getMessage());
			return true;
		}
	}
}
