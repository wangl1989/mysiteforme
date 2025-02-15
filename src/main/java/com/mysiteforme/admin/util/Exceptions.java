/**
 * Copyright (c) 2005-2012 springside.org.cn
 */
package com.mysiteforme.admin.util;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常处理工具类
 * 提供异常包装和处理的常用方法
 * @author calvin
 * @version 2013-01-15
 */
public class Exceptions {

	/**
	 * 将CheckedException转换为UncheckedException
	 * @param e 检查型异常
	 * @return 运行时异常
	 */
	public static RuntimeException unchecked(Exception e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

	/**
	 * 将ErrorStack转化为String
	 * @param e 异常
	 * @return 异常堆栈信息字符串
	 */
	public static String getStackTraceAsString(Exception e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	/**
	 * 判断异常是否由某些底层的异常引起
	 * @param ex 异常
	 * @param causeExceptionClasses 可能的底层异常类
	 * @return 是否由指定异常引起
	 */
	@SuppressWarnings("unchecked")
	public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
		Throwable cause = ex.getCause();
		while (cause != null) {
			for (Class<? extends Exception> causeClass : causeExceptionClasses) {
				if (causeClass.isInstance(cause)) {
					return true;
				}
			}
			cause = cause.getCause();
		}
		return false;
	}

	/**
	 * 在request中获取异常类
	 * @param request 默认参数
	 * @return 异常
	 */
	public static Throwable getThrowable(HttpServletRequest request){
		Throwable ex = null;
		if (request.getAttribute("exception") != null) {
			ex = (Throwable) request.getAttribute("exception");
		} else if (request.getAttribute("javax.servlet.error.exception") != null) {
			ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
		}
		return ex;
	}
	
}
