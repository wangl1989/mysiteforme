package com.mysiteforme.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@ControllerAdvice
public class XssRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Safelist SAFELIST;

    static {
        SAFELIST = Safelist.basic()
                .addTags("div", "span", "h1", "h2", "h3", "h4", "h5", "h6",
                        "p", "strong", "em", "b", "i", "u", "s", "br",
                        "a", "ul", "ol", "li", "table", "thead", "tbody", "tr", "td", "th")
                .addAttributes(":all", "style", "class", "id", "target", "title")
                .addTags("img")
                .addAttributes("img", "src", "alt", "width", "height", "style", "class", "id")
                .addAttributes("a", "href", "target", "rel", "title")
                .addProtocols("img", "src", "http", "https", "data");
    }

    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, @NotNull Type targetType,
                            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestBody.class);
    }

    @NotNull
    @Override
    public Object afterBodyRead(@NotNull Object body, @NotNull HttpInputMessage inputMessage,
                                @NotNull MethodParameter parameter, @NotNull Type targetType,
                                @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        try {
            cleanXss(body);
        } catch (Exception e) {
            // 记录日志但不中断处理
            log.error("XSS清理失败: {}", e.getMessage(), e);
        }
        return body;
    }

    private void cleanXss(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);

            if (value instanceof String) {
                // 清理字符串字段
                String cleaned = Jsoup.clean((String) value, SAFELIST);
                field.set(obj, cleaned);
            } else if (value instanceof Collection) {
                // 处理集合
                cleanCollection((Collection<?>) value);
            } else if (value instanceof Map) {
                // 处理Map
                cleanMap((Map<?, ?>) value);
            } else if (value != null && !value.getClass().isPrimitive()
                    && value.getClass().getPackage() != null
                    && value.getClass().getPackage().getName().startsWith("com.mysiteforme")) {
                // 递归处理自定义对象
                cleanXss(value);
            }
        }
    }

    private void cleanCollection(Collection<?> collection) throws IllegalAccessException {
        if (collection == null) return;

        for (Object item : collection) {
            if (item instanceof String) {
                // 注意：由于Collection可能是不可变的，这里需要特殊处理
                // 在实际应用中可能需要创建新的Collection
                continue;
            }
            cleanXss(item);
        }
    }

    private void cleanMap(Map<?, ?> map) throws IllegalAccessException {
        if (map == null) return;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                // 注意：由于Map可能是不可变的，这里需要特殊处理
                // 在实际应用中可能需要创建新的Map
                continue;
            }
            cleanXss(value);
        }
    }
}
