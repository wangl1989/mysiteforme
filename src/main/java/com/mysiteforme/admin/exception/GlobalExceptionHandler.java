/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 04:02:46
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:54:46
 * @ Description: 全局异常处理器类，用于捕获并处理应用程序中抛出的各种异常。
*                 通过使用@ControllerAdvice注解，该类可以处理所有控制器中的异常。
 */

package com.mysiteforme.admin.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 日志记录器，用于记录异常信息。
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 定义了一个函数式接口ExceptionStrategy，用于处理具体的异常。
     * 该接口只有一个方法handle，接收一个Exception对象和一个HttpServletRequest对象，并返回一个MyException对象。
     */
    @FunctionalInterface
    private interface ExceptionStrategy {
        MyException handle(Exception e, HttpServletRequest request);
    }

    /**
     * 异常处理策略映射，使用Map存储异常类型及其对应的处理策略。
     */
    private final Map<Class<? extends Exception>, ExceptionStrategy> exceptionStrategyMap;

    /**
     * 构造器，初始化异常处理策略映射，并调用initExceptionHandlers方法来填充异常处理策略。
     */
    public GlobalExceptionHandler() {
        exceptionStrategyMap = Maps.newHashMap();
        // 初始化异常处理策略
        initExceptionHandlers();
    }

    /**
     * 初始化异常处理策略，将各种异常类型与具体的处理方式关联起来。
     */
    private void initExceptionHandlers() {

        // 自定义错误MyException的处理策略
        exceptionStrategyMap.put(MyException.class, (e, req) -> {
            MyException ex = (MyException) e;
            ex.setViewName(MyException.ERROR_PAGE); // 设置错误页面为500页面
            log.error("自定义错误: {}, URL: {}", ex.getMsg(), req.getRequestURL(), ex); // 记录错误信息
            return ex;
        });

        // 处理HTTP请求体解析失败的异常HttpMessageNotReadableException
        exceptionStrategyMap.put(HttpMessageNotReadableException.class, (e, req) -> {
            HttpMessageNotReadableException ex = (HttpMessageNotReadableException) e;
            log.error("请求体解析失败: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder()
                    .msg("请求体格式错误") // 设置错误消息
                    .errorType(e.getClass().getSimpleName()) // 设置错误类型
                    .viewName(MyException.ERROR_PAGE) // 设置错误页面为500页面
                    .build();
        });

        // 处理不支持的HTTP请求方法的异常HttpRequestMethodNotSupportedException
        exceptionStrategyMap.put(HttpRequestMethodNotSupportedException.class, (e, req) -> {
            HttpRequestMethodNotSupportedException ex = (HttpRequestMethodNotSupportedException) e;
            log.error("请求方法不支持: {}, URL: {}", e.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder()
                    .msg("不支持的请求方法: " + ex.getMethod()) // 设置错误消息，包含不支持的请求方法
                    .errorType(e.getClass().getSimpleName()) // 设置错误类型
                    .viewName(MyException.ERROR_PAGE) // 设置错误页面为500页面
                    .build();
        });

        // 处理数据库操作异常SQLException
        exceptionStrategyMap.put(SQLException.class, (e, req) -> {
            SQLException ex = (SQLException) e;
            log.error("数据库操作异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder()
                    .msg("数据库操作失败") // 设置错误消息
                    .errorType("DatabaseError") // 设置错误类型为DatabaseError
                    .viewName(MyException.ERROR_PAGE) // 设置错误页面为500页面
                    .build();
        });

        // 处理认证异常AuthenticationException
        exceptionStrategyMap.put(AuthenticationException.class, (e, req) -> {
            AuthenticationException ex = (AuthenticationException) e;
            log.error("认证异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder()
                    .msg("认证异常") // 设置错误消息
                    .viewName("login") // 设置错误页面为登录页面
                    .build();
        });

        // 处理404异常，即请求路径未找到的异常NoHandlerFoundException
        exceptionStrategyMap.put(NoHandlerFoundException.class, (e, req) -> {
            NoHandlerFoundException ex = (NoHandlerFoundException) e;
            log.error("路径错误: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex); // 记录错误信息
            return MyException.builder()
                    .viewName(MyException.NOT_FOUND_PAGE) // 设置错误页面为404页面
                    .build();
        });
    }

    /**
     * 统一处理所有的异常，根据异常类型调用相应的处理策略。
     * @param request 当前HTTP请求对象
     * @param response 当前HTTP响应对象
     * @param e 抛出的异常对象
     * @return 返回错误视图或null（对于Ajax请求）
     * @throws IOException 可能抛出的IO异常
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Exception e) throws IOException {

        // 获取对应的异常处理器
        ExceptionStrategy handler = exceptionStrategyMap.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(e)) // 过滤出匹配的异常类型
                .findFirst() // 获取第一个匹配项
                .map(Map.Entry::getValue) // 获取处理策略
                .orElse((ex, req) -> { // 如果没有匹配的处理策略，则使用默认的处理策略
                    log.error("未知异常: {}, URL: {}", ex.getMessage(), req.getRequestURL()); // 记录未知错误信息
                    return MyException.builder()
                            .msg("系统发生未知错误") // 设置错误消息为系统发生未知错误
                            .errorType(ex.getClass().getSimpleName()) // 设置错误类型
                            .viewName(MyException.ERROR_PAGE) // 设置错误页面为500页面
                            .build();
                });

        // 处理异常，得到MyException对象
        MyException errorInfo = handler.handle(e, request);

        // 处理Ajax请求
        if (ToolUtil.isAjax(request)) {
            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter writer = response.getWriter()) {
                RestResponse failResponse = RestResponse.failure(errorInfo.getMsg());
                writer.write(JSONObject.toJSONString(failResponse));
                writer.flush();
            }
            return null;
        }

        // 返回错误视图
        ModelAndView mv = new ModelAndView(errorInfo.getViewName()); // 创建ModelAndView对象，设置视图名称
        mv.addObject("errorType", errorInfo.getErrorType()); // 向视图添加错误类型
        mv.addObject("errorMessage", errorInfo.getMsg()); // 向视图添加错误消息
        mv.addObject("requestUrl", request.getRequestURL()); // 向视图添加请求URL
        return mv; // 返回视图
    }
}