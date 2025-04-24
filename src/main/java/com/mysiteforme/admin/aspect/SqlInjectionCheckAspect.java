package com.mysiteforme.admin.aspect;

import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.MessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

@Aspect
@Component
@Slf4j
public class SqlInjectionCheckAspect {

    // SQL注入危险字符
    private static final Set<String> SQL_INJECTION_KEYWORDS = new HashSet<>(Arrays.asList(
            "drop", "delete", "truncate", "alter", "--", "/*", "*/", "declare",
            "exec", "execute", "xp_", "sp_", "update", "insert"
    ));

    // 数据库对象名称的合法字符模式
    private static final Pattern VALID_OBJECT_NAME_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_$]+$");

    // 表名长度限制
    private static final int MAX_TABLE_NAME_LENGTH = 64;

    @Pointcut("@within(com.mysiteforme.admin.annotation.SqlInjectionCheck) || @annotation(com.mysiteforme.admin.annotation.SqlInjectionCheck)")
    public void sqlInjectionCheckPointcut() {}

    @Before("sqlInjectionCheckPointcut()")
    public void beforeMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            checkParameter(parameters[i], args[i]);
        }
    }

    private void checkParameter(Parameter parameter, Object arg) {
        log.debug("当前检查的参数名称是:{}",parameter.getName());
        if (arg == null) {
            return;
        }
        // 使用Set来跟踪已经检查过的对象，避免循环引用
        checkObject(arg, new HashSet<>());
    }

    private void checkObject(Object obj, Set<Object> checkedObjects) {
        if (obj == null || !checkedObjects.add(obj)) {
            // 如果对象为null或已经检查过，直接返回
            return;
        }

        try {
            Class<?> clazz = obj.getClass();
            
            // 跳过基本类型、包装类型、String类型和一些常见的不需要递归的类型
            if (clazz.isPrimitive() || 
                clazz.getName().startsWith("java.lang") ||
                clazz.getName().startsWith("java.util") ||
                clazz.getName().startsWith("java.time")) {
                return;
            }

            // 处理字符串类型
            if (obj instanceof String) {
                checkString((String) obj, null);
                return;
            }

            // 处理集合类型
            if (obj instanceof Collection<?>) {
                for (Object item : (Collection<?>) obj) {
                    checkObject(item, checkedObjects);
                }
                return;
            }

            // 处理Map类型
            if (obj instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    checkObject(entry.getKey(), checkedObjects);
                    checkObject(entry.getValue(), checkedObjects);
                }
                return;
            }

            // 处理数组类型
            if (obj.getClass().isArray()) {
                Object[] array = (Object[]) obj;
                for (Object item : array) {
                    checkObject(item, checkedObjects);
                }
                return;
            }

            // 获取所有字段（包括父类的字段）
            List<Field> fields = new ArrayList<>();
            while (clazz != null && !clazz.equals(Object.class)) {
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass();
            }

            // 检查每个字段
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    
                    if (value instanceof String) {
                        checkString((String) value, field);
                    } else {
                        checkObject(value, checkedObjects);
                    }
                } catch (IllegalAccessException e) {
                    log.debug("Cannot access field: " + field.getName(), e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to check object fields", e);
            throw MyException.builder().businessError(MessageConstants.Sql.SQL_PARAM_CHECK_FAILED).build();
        }
    }

    private void checkString(String value, Field field) {
        if (value == null || value.isEmpty()) {
            return;
        }

        // 检查SQL注入关键字 - 这个应该对所有字符串进行检查
        String lowerValue = value.toLowerCase();
        for (String keyword : SQL_INJECTION_KEYWORDS) {
            if (lowerValue.contains(keyword)) {
                log.warn("SQL injection attempt detected: {}", value);
                throw MyException.builder().businessError(MessageConstants.Sql.SQL_INJECTION_ACCEPT_DETECTED,value).build();
            }
        }

        // 只对特定命名字段进行对象名称格式和长度检查
        if (isDbObjectNameField(field)) {
            // 检查对象名称格式
            if (!VALID_OBJECT_NAME_PATTERN.matcher(value).matches()) {
                log.warn("Invalid object name format detected: {}", value);
                throw MyException.builder().businessError(MessageConstants.Sql.SQL_INVALID_OBJECT_NAME_FORMAT,value).build();
            }

            // 检查长度限制
            if (value.length() > MAX_TABLE_NAME_LENGTH) {
                log.warn("Object name too long: {}", value);
                throw MyException.builder().businessError(MessageConstants.Sql.SQL_OBJECT_NAME_TOO_LONG,value).build();
            }
        }
    }

    private boolean isDbObjectNameField(Field field) {
        if (field == null) {
            return false;
        }
        
        String fieldName = field.getName().toLowerCase();
        // 定义可能包含数据库对象名称的字段名模式
        return fieldName.contains("tablename") ||
               fieldName.contains("columnname") ||
               fieldName.contains("schemaname") ||
               fieldName.contains("databasename") ||
               fieldName.contains("indexname") ||
               fieldName.contains("viewname") ||
               (fieldName.endsWith("name") && 
                (fieldName.contains("table") || 
                 fieldName.contains("column") || 
                 fieldName.contains("field")));
    }
}
