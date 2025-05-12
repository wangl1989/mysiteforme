package com.mysiteforme.admin.util.quartz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysiteforme.admin.entity.QuartzTask;
import com.mysiteforme.admin.entity.QuartzTaskLog;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class QuartzJobExecution extends AbstractQuartzJob {

    private ObjectMapper objectMapper;

    public QuartzJobExecution() {
        // 使用 SpringUtil 获取 ObjectMapper
    }

    @Autowired
    public QuartzJobExecution(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doExecute(JobExecutionContext context, QuartzTaskLog quartzTaskLog) throws Exception {
        if (objectMapper == null) {
            ApplicationContext applicationContext = SpringContextHolder.getApplicationContext();
            this.objectMapper = applicationContext.getBean(ObjectMapper.class);
        }
        QuartzTask sysJob = (QuartzTask) context.getMergedJobDataMap().get(Constants.JOB_PARAM_KEY);

        // 这里实现具体的业务逻辑
        // 可以根据sysJob中的invokeTarget来调用不同的方法
        // 例如: 调用Spring Bean的方法、静态方法、Groovy脚本等

        log.debug("执行任务: " + sysJob.getTargetBean() + " - " + sysJob.getGroupName());
        log.debug("调用目标: " + sysJob.getTrgetMethod());
        log.debug("Cron表达式: " + sysJob.getCron());

        // 实际业务逻辑处理
        executeBusinessLogic(sysJob,quartzTaskLog);
    }

    private void executeBusinessLogic(QuartzTask sysJob, QuartzTaskLog quartzTaskLog) {
        try {
            Object target = SpringUtil.getBean(sysJob.getTargetBean());
            Method method;
            Object[] params = null;

            if (StringUtils.isNotBlank(sysJob.getParams())) {
                // 将JSON字符串转换为Map
                Map<String, Object> paramMap = objectMapper.readValue(sysJob.getParams(),
                        new TypeReference<>() {});

                // 收集参数值和类型
                List<Object> paramValues = new ArrayList<>();
                List<Class<?>> paramTypes = new ArrayList<>();

                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    Object value = entry.getValue();
                    if (value != null) {
                        paramValues.add(value);
                        paramTypes.add(value.getClass());
                    }
                }

                // 获取方法
                method = target.getClass().getDeclaredMethod(
                        sysJob.getTrgetMethod(),
                        paramTypes.toArray(new Class<?>[0])
                );
                params = paramValues.toArray();
            } else {
                method = target.getClass().getDeclaredMethod(sysJob.getTrgetMethod());
            }

            ReflectionUtils.makeAccessible(method);
            // 执行方法
            if (params != null) {
                method.invoke(target, params);
            } else {
                method.invoke(target);
            }
            quartzTaskLog.setStatus(JobStatus.COMPLETE.getValue());
        } catch (JsonProcessingException e) {
            quartzTaskLog.setError(e.getMessage());
            quartzTaskLog.setStatus(JobStatus.ERROR.getValue());
            log.error("JSON解析失败", e);
            throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_PARAMS_PARSE_FAILED).build();
        } catch (ReflectiveOperationException e) {
            quartzTaskLog.setError(e.getMessage());
            quartzTaskLog.setStatus(JobStatus.ERROR.getValue());
            log.error("反射调用失败", e);
            throw MyException.builder().businessError(MessageConstants.QuartzTask.TASK_EXECUTE_FAILED).build();
        }
    }

}
