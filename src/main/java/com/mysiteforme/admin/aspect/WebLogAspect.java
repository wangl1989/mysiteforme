package com.mysiteforme.admin.aspect;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.text.StringEscapeUtils;
import com.mysiteforme.admin.base.MySysUser;
import com.mysiteforme.admin.entity.Log;
import com.mysiteforme.admin.service.LogService;
import com.mysiteforme.admin.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Web层日志切面
 * 实现系统操作日志的记录
 * @author wangl
 * @since 2018/1/13
 */
@Aspect
@Component
@Order(5)
public class WebLogAspect {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WebLogAspect.class);

    private LogService logService;


    public static ThreadLocal<Long> startTime = new ThreadLocal<>();

    public WebLogAspect() {}

    @Autowired
    public WebLogAspect(LogService logService) {
        this.logService = logService;
    }



    private Log sysLog = new Log();

    /**
     * 切入点表达式,拦截使用@SysLog注解的方法
     */
    @Pointcut("@annotation(com.mysiteforme.admin.annotation.SysLog)")
    public void webLog(){}

    /**
     * 前置通知,在方法执行前记录系统日志
     * @param joinPoint 连接点,包含被拦截方法的信息
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws JsonProcessingException {
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpSession session = (HttpSession) attributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
            sysLog = new Log();
            sysLog.setClassMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            sysLog.setHttpMethod(request.getMethod());
            //获取传入目标方法的参数
            Object[] args = joinPoint.getArgs();
            List<Object> objs = Lists.newArrayList();
            for (Object o : args) {
                //排序不需要序列化的对象
                if (!(o instanceof ServletRequest) &&
                        !(o instanceof ServletResponse) &&
                        !(o instanceof MultipartFile) &&
                        !(o instanceof MultipartFile[])) {
                    objs.add(o);
                }
            }
            if(!objs.isEmpty()) {
                String str = JSONObject.toJSONString(objs);
                sysLog.setParams(str.length() > 5000 ? JSONObject.toJSONString("请求参数数据过长不与显示") : str);
            }
            String ip = ToolUtil.getClientIp(request);
            if ("0.0.0.0".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip) || "127.0.0.1".equals(ip)) {
                ip = "127.0.0.1" ;
            }
            sysLog.setRemoteAddr(ip);
            sysLog.setRequestUri(request.getRequestURL().toString());
            sysLog.setSessionId(session.getId());
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            com.mysiteforme.admin.annotation.SysLog mylog = method.getAnnotation(com.mysiteforme.admin.annotation.SysLog.class);
            if (mylog != null) {
                //注解上的描述
                sysLog.setTitle(mylog.value());
            }

            Map<String, String> browserMap = ToolUtil.getOsAndBrowserInfo(request);
            sysLog.setBrowser(browserMap.get("os") + "-" + browserMap.get("browser"));
            if (!"127.0.0.1".equals(ip)) {
                Map<String, String> map = null;
                if (session.getAttribute("addressIp") != null) {
                    map = ToolUtil.getSessionMapAttribute(session,"addressIp",String.class, String.class);
                }
                if (map == null) {
                    map = ToolUtil.getAddressByIP(ToolUtil.getClientIp(request));
                    // Sanitize the map values before setting them in the session
                    Map<String, String> cleanedMap = Maps.newHashMap();
                    map.forEach((k, v) ->
                            cleanedMap.put(k, v != null ? StringEscapeUtils.escapeHtml4(v) : "")
                    );
                    session.setAttribute("addressIp", cleanedMap);
                } else {
                    map.replaceAll((k, v) -> StringEscapeUtils.escapeHtml4(v));
                }
                sysLog.setArea(StringEscapeUtils.escapeHtml4(map.get("area")));
                sysLog.setProvince(StringEscapeUtils.escapeHtml4(map.get("province")));
                sysLog.setCity(StringEscapeUtils.escapeHtml4(map.get("city")));
                sysLog.setIsp(StringEscapeUtils.escapeHtml4(map.get("isp")));
            }
            sysLog.setType(ToolUtil.isAjax(request) ? "Ajax请求" : "普通请求");
            if (MySysUser.ShiroUser() != null) {
                sysLog.setUsername(StringUtils.isNotBlank(MySysUser.nickName()) ? MySysUser.nickName() : MySysUser.loginName());
            }
        }
    }

    /**
     * 环绕通知,统一异常处理
     * @param proceedingJoinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            sysLog.setException(e.getMessage());
            throw e;
        }
    }

    /**
     * 返回通知,记录响应结果
     * @param ret 返回结果对象
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        if(MySysUser.ShiroUser() != null) {
            sysLog.setUsername(StringUtils.isNotBlank(MySysUser.nickName()) ? MySysUser.nickName() : MySysUser.loginName());
        }
        String retString = JSONObject.toJSONString(ret);
        sysLog.setResponse(retString.length()>5000?JSONObject.toJSONString("请求参数数据过长不与显示"):retString);
        sysLog.setUseTime(System.currentTimeMillis() - startTime.get());
        logService.save(sysLog);
    }
}
