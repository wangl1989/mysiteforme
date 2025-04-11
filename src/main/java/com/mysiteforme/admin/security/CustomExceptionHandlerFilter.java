/**
 * @ Author: wangl
 * @ Create Time: 2025-02-17 14:29:52
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 21:58:56
 * @ Description: security全局异常，在所有控制链之前
 */

package com.mysiteforme.admin.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ArrayUtil;
import com.mysiteforme.admin.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.ExcepttionHandlerUtils.ExceptionStrategy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomExceptionHandlerFilter extends OncePerRequestFilter {

    private final ApiToolUtil apiToolUtil;
    /**
     * 异常处理策略映射，使用Map存储异常类型及其对应的处理策略。
     */
    private final Map<Class<? extends Exception>, ExceptionStrategy> exceptionStrategyMap;

    public CustomExceptionHandlerFilter(ApiToolUtil apiToolUtil) {
        exceptionStrategyMap = ExcepttionHandlerUtils.initExceptionHandlers();
        this.apiToolUtil = apiToolUtil;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 如果是 error 页面的请求，直接放行
        if (request.getRequestURI().equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }
    
        String requestURI = request.getRequestURI();
        log.debug("CustomExceptionHandlerFilter开始处理请求: {}", requestURI);
        log.debug("请求方法: {}", request.getMethod());
        log.debug("Content-Type: {}", request.getContentType());
        
        try {
            log.debug("我们开始进入一个校验器==========----->,请求地址为:{}", request.getRequestURI());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("security全局异常，URI: {}, 异常类型: {}, 异常内容: {}",
                    requestURI,
                    e.getClass().getName(),
                    e.getMessage(),
                    e  // 添加完整的堆栈跟踪
            );

            // 设置响应状态为200，避免触发错误页面
            response.setStatus(HttpServletResponse.SC_OK);

            // 区分不同类型的异常
            Throwable cause = e.getCause();
            if (cause instanceof NoHandlerFoundException || e instanceof NoHandlerFoundException) {
                apiToolUtil.returnSystemDate(
                        Result.error(ResultCode.NOT_FOUND, MessageConstants.System.SYSTEM_NOT_FOUND), request,
                        response);
            } else if ((cause != null ? cause : e) instanceof AuthenticationException) {
                apiToolUtil.returnSystemDate(Result.forbidden(), request, response);
            } else if ((cause != null ? cause : e) instanceof AccessDeniedException) {
                apiToolUtil.returnSystemDate(Result.unauthorized(), request, response);
            } else if ((cause != null? cause : e) instanceof MethodArgumentNotValidException ex){
                List<String> errors = new ArrayList<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> {
                    String errorMessage = error.getDefaultMessage();
                    errors.add(MessageUtil.getMessage(errorMessage));
                });
                apiToolUtil.returnSystemDate(Result.orgenalError(ResultCode.INVALID_PARAM, String.join(System.lineSeparator(),errors)), request, response);
            } else if ((cause != null? cause : e) instanceof MyException myException) {
                apiToolUtil.returnSystemDate(Result.orgenalError(myException.getCode(), myException.getMsg()), request, response);
            } else {
                handleUnexpectedException(request, response, cause != null? cause : e);
            }
            
            // 确保响应已经提交，移到catch块内
            response.flushBuffer();
        }
    }

    /**
     * 处理异常
     * @param request 请求
     * @param response 响应
     * @param cause 异常
     */
    private void handleUnexpectedException(HttpServletRequest request, HttpServletResponse response, Throwable cause){
        // 获取对应的异常处理器
        ExceptionStrategy handler = exceptionStrategyMap.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(cause)) // 过滤出匹配的异常类型
                .findFirst() // 获取第一个匹配项
                .map(Map.Entry::getValue) // 获取处理策略
                .orElse((ex, req) -> { // 如果没有匹配的处理策略，则使用默认的处理策略
                    log.error("未知异常: {}, URL: {}", ex.getMessage(), req.getRequestURL()); // 记录未知错误信息
                    return MyException.builder().systemError(MessageConstants.System.SYSTEM_ERROR).build();
                });

        // 处理异常，得到MyException对象
        MyException errorInfo = handler.handle(cause, request);
        apiToolUtil.returnSystemDate(Result.orgenalError(errorInfo.getCode(), errorInfo.getMsg()),request,response);
    }
    
}
