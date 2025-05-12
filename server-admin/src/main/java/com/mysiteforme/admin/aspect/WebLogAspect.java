/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-13 19:52:05
 * @ Description: Web层日志切面:系统操作日志切面,实现系统操作日志的记录
 */

package com.mysiteforme.admin.aspect;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mysiteforme.admin.entity.DTO.AgentDTO;
import com.mysiteforme.admin.entity.DTO.IndexLogDTO;
import com.mysiteforme.admin.entity.DTO.LocationDTO;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.UserCacheService;
import com.mysiteforme.admin.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import com.mysiteforme.admin.base.MySecurityUser;
import com.mysiteforme.admin.entity.Log;
import com.mysiteforme.admin.service.LogService;
import com.mysiteforme.admin.util.ToolUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@Order(5)
public class WebLogAspect {

    private final LogService logService;

    private final RedisUtils redisUtils;

    private final UserCacheService userCacheService;


    public static ThreadLocal<Long> startTime = new ThreadLocal<>();

    public WebLogAspect(LogService logService, RedisUtils redisUtils, UserCacheService userCacheService) {
        this.logService = logService;
        this.redisUtils = redisUtils;
        this.userCacheService = userCacheService;
    }

    private Log sysLog;

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
    public void doBefore(JoinPoint joinPoint){
        sysLog = new Log();
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpSession session = (HttpSession) attributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
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
                String str = JSONObject.toJSONString(objs.size()>1?objs:objs.get(0));
                sysLog.setParams(str.length() > 5000 ? JSONObject.toJSONString("请求参数数据过长不与显示") : str);
            }
            sysLog.setRequestUri(request.getRequestURI());
            sysLog.setSessionId(session.getId());
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            com.mysiteforme.admin.annotation.SysLog myLog = method.getAnnotation(com.mysiteforme.admin.annotation.SysLog.class);
            if (myLog != null) {
                if (StringUtils.isNotBlank(myLog.value())) {
                    //系统方法注解上的描述
                    log.debug("myLog.value():{}",myLog.value());
                    sysLog.setTitle(MessageUtil.getMessage(myLog.value()));
                }

            }
            LocationDTO locationDTO = userCacheService.getLocationByIp(ToolUtil.getClientIp(request));
            sysLog.setRemoteAddr(locationDTO.getIp());
            AgentDTO browserMap = ToolUtil.getOsAndBrowserInfo(request);

            sysLog.setBrowser(browserMap.getBrowser());
            sysLog.setArea(StringEscapeUtils.escapeHtml4(locationDTO.getRegion()));
            sysLog.setProvince(StringEscapeUtils.escapeHtml4(locationDTO.getProvince()));
            sysLog.setCity(StringEscapeUtils.escapeHtml4(locationDTO.getCity()));
            sysLog.setIsp(StringEscapeUtils.escapeHtml4(locationDTO.getIp()));
            if (MySecurityUser.securityUser() != null) {
                sysLog.setUsername(StringUtils.isNotBlank(MySecurityUser.nickName()) ? MySecurityUser.nickName() : MySecurityUser.loginName());
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
            log.error(e.getMessage(),e);
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
        if(MySecurityUser.securityUser() != null) {
            sysLog.setUsername(StringUtils.isNotBlank(MySecurityUser.nickName()) ? MySecurityUser.nickName() : MySecurityUser.loginName());
        }
        String retString = JSONObject.toJSONString(ret);
        sysLog.setResponse(retString.length()>5000?JSONObject.toJSONString("请求参数数据过长不与显示"):retString);
        sysLog.setUseTime(System.currentTimeMillis() - startTime.get());
        logService.save(sysLog);
        IndexLogDTO response = new IndexLogDTO();
        response.setId(sysLog.getId());
        response.setUserName(sysLog.getUsername());
        response.setMethod(sysLog.getHttpMethod());
        response.setTitle(sysLog.getTitle());
        response.setCreateDate(sysLog.getCreateDate());
        response.setHttpMethod(sysLog.getHttpMethod());
        response.setRequestUri(sysLog.getRequestUri());
        if(redisUtils.hasKey(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY)){
            redisUtils.leftPushList(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY,response);
            redisUtils.expire(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY,20, TimeUnit.MINUTES);
            Long size = redisUtils.getListSize(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY);
            if(size != null && size > RedisConstants.ANALYTICS_INDEX_LOG_SIZE){
                redisUtils.removeRightList(RedisConstants.ANALYTICS_USER_OPERATOR_LOG_KEY);
            }
        }else{
            logService.getIndexLogList(RedisConstants.ANALYTICS_INDEX_LOG_SIZE);
        }
    }
}
