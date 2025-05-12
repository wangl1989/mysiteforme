/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 13:53:29
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 22:09:41
 * @ Description: API工具类
 */
package com.mysiteforme.admin.util;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysiteforme.admin.exception.MyException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.DefaultCorsProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiToolUtil {

    private final CorsProcessor corsProcessor = new DefaultCorsProcessor(); // 可以直接 new，或注入 Spring 容器中的实例

    private final CorsConfigurationSource corsConfigurationSource;

    private final ObjectMapper objectMapper;
    /**
     * 返回系统错误
     */
    public void returnSystemDate(Result result, HttpServletRequest request, HttpServletResponse response) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration(request);
        if (corsConfiguration != null) {
            // 让 CorsProcessor 根据配置和请求信息，添加必要的 CORS 响应头
            // 例如：Access-Control-Allow-Origin 等
            boolean isValid;
            try {
                isValid = this.corsProcessor.processRequest(corsConfiguration, request, response);
            } catch (IOException e) {
                log.error("写入错误响应失败: {}", e.getMessage(), e);
                throw MyException.builder().systemError(MessageConstants.System.SYSTEM_ERROR).build();
            }
            if (!isValid) {
                // 如果 processRequest 返回 false，可能表示请求无效（如 Origin 不匹配）
                // 在这种情况下，标准行为可能是不添加 CORS 头并可能拒绝请求
                // 对于异常处理，我们通常还是希望返回错误信息，但 CORS 头可能不会被添加
                log.warn("CORS 处理未成功应用于错误响应，请求来源可能不允许。");
                // 注意：即使 processRequest 返回 false，它可能已经设置了 Vary: Origin 头
            }
            log.debug("已尝试为错误响应应用CORS头部。");
        } else {
            log.debug("未找到适用于该请求的CORS配置，不添加CORS头部。");
        }
        // 3. 设置响应状态码和内容类型 (确保在写入内容之前设置)
        //    注意：corsProcessor.processRequest 可能会修改响应状态（例如对于无效的预检请求）
        //    检查 response.isCommitted() 可能有帮助，但对于错误处理通常我们强制设置状态
        if (!response.isCommitted()) {
            // 或者根据异常类型设置更具体的状态码
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // 4. 写入 JSON 响应体
            try {
                response.getWriter().write(objectMapper.writeValueAsString(result));
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                log.error("写入错误响应失败: {}", e.getMessage(), e);
                // 如果连写入都失败，可能无法再做什么了
                throw MyException.builder().systemError(MessageConstants.System.SYSTEM_ERROR).build();
            }
        } else {
            log.warn("响应已被提交，无法设置错误信息和CORS头。这可能由之前的 Filter 或 CORS 预检处理导致。");
        }
    }

}
