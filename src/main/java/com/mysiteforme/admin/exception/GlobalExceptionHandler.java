package com.mysiteforme.admin.exception;


import com.alibaba.fastjson.JSONObject;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import freemarker.template.TemplateModelException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get();

    /**
     * 500 - Bad Request
     */
    @ExceptionHandler({HttpMessageNotReadableException.class,
                       HttpRequestMethodNotSupportedException.class,
                       HttpMediaTypeNotSupportedException.class,
                       TemplateModelException.class,
                       Exception.class,
                       SQLException.class})
    public ModelAndView handleHttpMessageNotReadableException(HttpServletRequest request,
                                                              HttpServletResponse response,
                                                              Exception e){
        RestResponse restResponse = RestResponse.failure(e.getMessage());
        return new ModelAndView("admin/error/500",restResponse);
    }

    /**
     * 404的拦截.
     * @param request
     * @param response
     * @param ex
     * @return
     * @throws Exception
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String notFound(HttpServletRequest request, HttpServletResponse response, Exception ex,Model model) {
        model.addAttribute("url",request.getRequestURI());
        return "admin/error/404";
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(MyException.class)
    public String myException(HttpServletRequest request, HttpServletResponse response, MyException ex,Model model) {
        if(ex.getCode() == 404){
            model.addAttribute("url",request.getRequestURI());
            return "admin/error/404";
        }else{
            RestResponse restResponse = RestResponse.failure(ex.getMessage());
            model.addAttribute("url",restResponse);
            return "admin/error/500";
        }
    }
}
