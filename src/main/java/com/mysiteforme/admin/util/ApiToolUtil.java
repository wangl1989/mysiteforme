/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 13:53:29
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 12:28:54
 * @ Description:
 */

package com.mysiteforme.admin.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public class ApiToolUtil {

    /**
     * 返回系统错误
     */
    public static void returnSystemDate(Result result,HttpServletResponse response) throws IOException, ServletException{
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
        response.getWriter().close();
    }

}
