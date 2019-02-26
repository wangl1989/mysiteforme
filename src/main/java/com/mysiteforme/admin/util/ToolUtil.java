package com.mysiteforme.admin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.entity.User;
import com.xiaoleilu.hutool.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
	 * @return
	 */
	public static String entryptPassword(String paramStr,String salt) {
		if(StringUtils.isNotEmpty(paramStr)){
			byte[] saltStr = Encodes.decodeHex(salt);
			byte[] hashPassword = Digests.sha1(paramStr.getBytes(), saltStr, Constants.HASH_INTERATIONS);
			String password = Encodes.encodeHex(hashPassword);
			return password;
		}else{
			return null;
		}

	}

	/**
	 * 获取客户端的ip信息
	 * 
	 * @param request
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		LOGGER.info("ipadd : " + ip);
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		LOGGER.info(" ip --> " + ip);
		return ip;
	}
	
	/**
     * 将bean转换成map
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> convertBeanToMap(Object condition) {
		if (condition == null) {
			return null;
		}
		if (condition instanceof Map) {
			return (Map<String, Object>) condition;
		}
		Map<String, Object> objectAsMap = new HashMap<String, Object>();
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(condition.getClass());
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			Method reader = pd.getReadMethod();
			if (reader != null&&!"class".equals(pd.getName()))
				try {
					objectAsMap.put(pd.getName(), reader.invoke(condition));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
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
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request){
		String accept = request.getHeader("accept");
        return accept != null && accept.contains("application/json") || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").contains("XMLHttpRequest"));
    }

	/**
	 * 获取操作系统,浏览器及浏览器版本信息
	 * @param request
	 * @return
	 */
	public static Map<String,String> getOsAndBrowserInfo(HttpServletRequest request){
		Map<String,String> map = Maps.newHashMap();
		String  browserDetails  =   request.getHeader("User-Agent");
		String  userAgent       =   browserDetails;
		String  user            =   userAgent.toLowerCase();

		String os = "";
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
		map.put("os",os);
		map.put("browser",browser);
		return map;
	}

	/***
	 * @param ip
	 * @return
	 */
	public static Map<String,String> getAddressByIP(String ip) {
		String area = "";
		String province = "";
		String city = "";
		String isp = "";
		Map finalMap = Maps.newHashMap();
		try{
			if("0:0:0:0:0:0:0:1".equals(ip)){
				ip = "0.0.0.0";
			}
			StringBuilder sb = new StringBuilder("http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=");
			sb.append(ip);
			String result= HttpUtil.get(sb.toString(), "GBK");
			Map resultMap = JSON.parseObject(result,Map.class);
			String status = (String) resultMap.get("err");

			province = (String) resultMap.get("pro");
			city = (String) resultMap.get("city");
			if("noprovince".equalsIgnoreCase(status)){
				area = (String) resultMap.get("addr");
			}else{
				area = "中国";
				String addr = (String) resultMap.get("addr");
				if(StringUtils.isNotBlank(addr)){
					isp = addr.split(" ")[1];
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}


		finalMap.put("area",area);
		finalMap.put("province",province);
		finalMap.put("city",city);
		finalMap.put("isp",isp);
		return finalMap;
	}

	public static void main(String args[]) throws Exception {
		//long t1 = System.currentTimeMillis();
		//Map<String,String> map = getAddressByIP("0.0.0.0");
		//LOGGER.info("地区："+map.get("country"));
		//LOGGER.info("省："+map.get("province"));
		//LOGGER.info("市："+map.get("city"));
		//LOGGER.info("互联网服务提供商："+map.get("isp"));
		//long t2 = System.currentTimeMillis();
		//System.out.println("执行时间为"+(t2-t1));

		//StringBuilder sb = new StringBuilder("https://apis.map.qq.com/ws/location/v1/ip?ip=117.82.187.111&key=N7XBZ-NX764-OFOUH-D5LJY-KZ3QK-6WFNX");
		//String result= HttpUtil.get(sb.toString(), "UTF-8");
		//Map<String,String> map = Maps.newHashMap();
		//Map resultMap = JSON.parseObject(result,Map.class);
		//Map m = (Map) resultMap.get("result");
		//Map r = (Map) m.get("ad_info");
		//Integer code = (Integer) resultMap.get("code");
		//if(code == 0){
		//	Map<String,String> detail = (Map<String,String>)resultMap.get("data");
		//	String country = detail.get("country");
		//}
		Map maps = getAddressByIP("117.82.187.111");
		System.out.println(JSONObject.toJSONString(maps));
	}
}
