package com.mysiteforme.admin.util;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.exception.MyException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcepttionHandlerUtils {

    public static Map<Class<? extends Exception>, ExceptionStrategy> initExceptionHandlers(){
        Map<Class<? extends Exception>, ExceptionStrategy> exceptionStrategyMap = Maps.newHashMap();
        // 自定义错误MyException的处理策略
        exceptionStrategyMap.put(MyException.class, (e, req) -> {
            MyException ex = (MyException) e;
            log.error("自定义错误: {}, URL: {}", ex.getMsg(), req.getRequestURL(), ex); // 记录错误信息
            return ex;
        });

        // 处理HTTP请求体解析失败的异常HttpMessageNotReadableException
        exceptionStrategyMap.put(HttpMessageNotReadableException.class, (e, req) -> {
            HttpMessageNotReadableException ex = (HttpMessageNotReadableException) e;
            log.error("请求体解析失败: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder().paramMsgError(MessageConstants.Exception.EXCEPTION_REQUEST_BODY_ERROR).errorType(e.getClass().getSimpleName()).build();
        });

        // 处理不支持的HTTP请求方法的异常HttpRequestMethodNotSupportedException
        exceptionStrategyMap.put(HttpRequestMethodNotSupportedException.class, (e, req) -> {
            HttpRequestMethodNotSupportedException ex = (HttpRequestMethodNotSupportedException) e;
            log.error("请求方法不支持: {}, URL: {}", e.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder().businessError(MessageConstants.Exception.EXCEPTION_REQUEST_METHOD_NOT_SUPPORTED).errorType(e.getClass().getSimpleName()).build();
        });

        // 处理数据库操作异常SQLException
        exceptionStrategyMap.put(SQLException.class, (e, req) -> {
            SQLException ex = (SQLException) e;
            log.error("数据库操作异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder().businessError(MessageConstants.Exception.EXCEPTION_DATABASE_OPERATION_FAILED).errorType(e.getClass().getSimpleName()).build();
        });

        // 处理认证异常AuthenticationException
        exceptionStrategyMap.put(AuthenticationException.class, (e, req) -> {
            AuthenticationException ex = (AuthenticationException) e;
            log.error("认证异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder().forbidden().errorType(e.getClass().getSimpleName()).build();
        });
        // 处理认证异常AccessDeniedException
        exceptionStrategyMap.put(AccessDeniedException.class, (e, req) -> {
            AccessDeniedException ex = (AccessDeniedException) e;
            log.error("权限认证异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder().unauthorized().errorType(e.getClass().getSimpleName()).build();
        });

        // 处理404异常，即请求路径未找到的异常NoHandlerFoundException
        exceptionStrategyMap.put(NoHandlerFoundException.class, (e, req) -> {
            NoHandlerFoundException ex = (NoHandlerFoundException) e;
            log.error("路径错误: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder().businessError(MessageConstants.Exception.EXCEPTION_PATH_NOT_FOUND).errorType(e.getClass().getSimpleName()).build();
        });
        return exceptionStrategyMap;
    }

    /**
     * 定义了一个函数式接口ExceptionStrategy，用于处理具体的异常。
     * 该接口只有一个方法handle，接收一个Exception对象和一个HttpServletRequest对象，并返回一个MyException对象。
     */
    @FunctionalInterface
    public interface ExceptionStrategy {
        
        MyException handle(Throwable e, HttpServletRequest request);
    }
    
}
