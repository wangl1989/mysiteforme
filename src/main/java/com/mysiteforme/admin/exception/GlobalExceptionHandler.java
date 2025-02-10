package com.mysiteforme.admin.exception;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

/**
 * 处理全局异常
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 自定义一个异常处理接口
    @FunctionalInterface
    private interface ExceptionStrategy{
        MyException handle(Exception e, HttpServletRequest request);
    }

    // 异常处理策略映射
    private final Map<Class<? extends Exception>, ExceptionStrategy> ExceptionStrategyMap;

    // 在构造器里实例化异常测率，添加具体处理方法
    public GlobalExceptionHandler() {
        ExceptionStrategyMap = Maps.newHashMap();
        // 初始化异常处理策略
        initExceptionHandlers();
    }

    private void initExceptionHandlers() {

        // 自定义错误
        ExceptionStrategyMap.put(MyException.class, (e, req) -> {
            MyException ex = (MyException) e;
            // 自定义错误也跳转到500页面
            ex.setViewName(MyException.ERROR_PAGE);
            log.error("自定义错误: {}, URL: {}", ex.getMsg(), req.getRequestURL(),ex);
            return ex;
        });

        // HTTP请求相关异常
        ExceptionStrategyMap.put(HttpMessageNotReadableException.class, (e, req) -> {
            HttpMessageNotReadableException ex = (HttpMessageNotReadableException) e;
            log.error("请求体解析失败: {}, URL: {}", ex.getMessage(), req.getRequestURL(),ex);
            return MyException.builder()
                    .msg("请求体格式错误")
                    .errorType(e.getClass().getSimpleName())
                    .viewName(MyException.ERROR_PAGE)
                    .build();
        });

        // 不支持的请求方法异常
        ExceptionStrategyMap.put(HttpRequestMethodNotSupportedException.class, (e, req) -> {
            HttpRequestMethodNotSupportedException ex = (HttpRequestMethodNotSupportedException)e;
            log.error("请求方法不支持: {}, URL: {}", e.getMessage(), req.getRequestURL(), ex);
            return MyException.builder()
                    .msg("不支持的请求方法: " + ex.getMethod())
                    .errorType(e.getClass().getSimpleName())
                    .viewName(MyException.ERROR_PAGE)
                    .build();
        });

        // 数据库异常
        ExceptionStrategyMap.put(SQLException.class, (e, req) -> {
            SQLException ex = (SQLException) e;
            log.error("数据库操作异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(),ex);
            return MyException.builder()
                    .msg("数据库操作失败")
                    .errorType("DatabaseError")
                    .viewName(MyException.ERROR_PAGE)
                    .build();
        });

        // 认证异常
        ExceptionStrategyMap.put(AuthenticationException.class, (e, req) -> {
            AuthenticationException ex = (AuthenticationException) e;
            log.error("认证异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex);
            return MyException.builder()
                    .msg("认证异常")
                    .viewName("login")
                    .build();
        });

        // 权限异常
        ExceptionStrategyMap.put(AuthorizationException.class, (e,req) -> {
            AuthorizationException ex = (AuthorizationException) e;
            log.error("权限异常: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex);
            return MyException.builder()
                    .msg("权限异常")
                    .viewName("login")
                    .build();
        });

        // 404异常
        ExceptionStrategyMap.put(NoHandlerFoundException.class, (e,req) -> {
            NoHandlerFoundException ex = (NoHandlerFoundException) e;
            log.error("路径错误: {}, URL: {}", ex.getMessage(), req.getRequestURL(), ex);
            return MyException.builder()
                    .viewName(MyException.NOT_FOUND_PAGE)
                    .build();
        });
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Exception e) throws IOException {

        // 1. 获取对应的异常处理器
        ExceptionStrategy handler = ExceptionStrategyMap.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(e))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse((ex, req) -> {
                    // 默认的异常处理器
                    log.error("未知异常: {}, URL: {}", ex.getMessage(), req.getRequestURL());
                    return MyException.builder()
                            .msg("系统发生未知错误")
                            .errorType(ex.getClass().getSimpleName())
                            .viewName(MyException.ERROR_PAGE)
                            .build();
                });

        // 2. 处理异常
        MyException errorInfo = handler.handle(e, request);

        // 3. 处理Ajax请求
        if (ToolUtil.isAjax(request)) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            RestResponse failResponse = RestResponse.failure(errorInfo.getMsg());
            writer.write(JSONObject.toJSONString(failResponse));
            writer.flush();
            writer.close();
            return null;
        }

        // 4. 返回错误视图
        ModelAndView mv = new ModelAndView(errorInfo.getViewName());
        mv.addObject("errorType", errorInfo.getErrorType());
        mv.addObject("errorMessage", errorInfo.getMsg());
        mv.addObject("requestUrl", request.getRequestURL());
        return mv;
    }

}
