package com.mysiteforme.admin.exception;


import com.alibaba.fastjson.JSONObject;
import com.mysiteforme.admin.util.RestResponse;
import com.mysiteforme.admin.util.ToolUtil;
import freemarker.template.TemplateModelException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 500 - Bad Request
     */
    @ExceptionHandler({HttpMessageNotReadableException.class,
                       HttpRequestMethodNotSupportedException.class,
                       HttpMediaTypeNotSupportedException.class,
                       TemplateModelException.class,
                       SQLException.class})
    public ModelAndView handleHttpMessageNotReadableException(HttpServletRequest request,
                                                              HttpServletResponse response,
                                                              Exception e){
        RestResponse restResponse = RestResponse.failure(e.getMessage());
        return new ModelAndView("admin/error/500",restResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         UnauthorizedException unauthorizedException) {
        if (ToolUtil.isAjax(request)) {
            try {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                RestResponse failResponse = RestResponse.failure("您无此权限,请联系管理员!");
                writer.write(JSONObject.toJSONString(failResponse));
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                log.error("resolveException",e1);
                throw new MyException("resolveException",e1);
            }
        }else {
            RestResponse restResponse = RestResponse.failure(unauthorizedException.getMessage());
            return new ModelAndView("admin/error/500",restResponse);
        }

        return null;
    }

    /**
     * 404的拦截.
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String notFound(HttpServletRequest request, HttpServletResponse response, Exception ex,Model model) {
        model.addAttribute("url",request.getRequestURI());
        return "admin/error/404";
    }

    @ExceptionHandler(MyException.class)
    public String myException(HttpServletRequest request, HttpServletResponse response, MyException ex,Model model) {
        log.info(ex.getMsg());
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
