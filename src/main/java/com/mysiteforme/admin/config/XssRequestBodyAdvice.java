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
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理所有@RequestBody 里的数据
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:19:56
 */
@Slf4j
@ControllerAdvice
public class XssRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Safelist SAFELIST;

    // 缓存那些不需要检查内部字段的类，避免重复判断
    private static final Set<Class<?>> SKIPPED_CLASSES = ConcurrentHashMap.newKeySet();
    // 缓存字段信息，避免重复反射查找（可选的性能优化）
    // private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

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
        // 预先填充常见的、不需要检查内部字段的JDK类型
        SKIPPED_CLASSES.add(String.class);
        SKIPPED_CLASSES.add(Integer.class);
        SKIPPED_CLASSES.add(Long.class);
        SKIPPED_CLASSES.add(Double.class);
        SKIPPED_CLASSES.add(Float.class);
        SKIPPED_CLASSES.add(Boolean.class);
        SKIPPED_CLASSES.add(Short.class);
        SKIPPED_CLASSES.add(Byte.class);
        SKIPPED_CLASSES.add(Character.class);
        SKIPPED_CLASSES.add(Date.class);
        SKIPPED_CLASSES.add(java.time.temporal.Temporal.class); // 涵盖 LocalDate, LocalDateTime 等 JSR-310 时间类
        SKIPPED_CLASSES.add(java.math.BigDecimal.class);
        SKIPPED_CLASSES.add(java.math.BigInteger.class);
        SKIPPED_CLASSES.add(Enum.class); // 涵盖所有枚举类型
        SKIPPED_CLASSES.add(UUID.class);
    }

    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, @NotNull Type targetType,
                            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 只对标注了 @RequestBody 的参数生效
        return methodParameter.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestBody.class);
    }

    @NotNull
    @Override
    public Object afterBodyRead(@NotNull Object body, @NotNull HttpInputMessage inputMessage,
                                @NotNull MethodParameter parameter, @NotNull Type targetType,
                                @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 使用 IdentityHashMap 来跟踪在当前请求处理中已经处理过的对象，防止无限循环（对象图中存在循环引用时）
        Set<Object> processedObjects = Collections.newSetFromMap(new IdentityHashMap<>());
        try {
            cleanXss(body, processedObjects);
        } catch (Exception e) {
            // 记录日志但不中断处理
            log.error("XSS清理失败: for request body. Class: {}, Error: {}", body.getClass().getName(), e.getMessage(), e);
        }
        return body;
    }

    /**
     * 递归清理对象中的 XSS 风险内容。
     *
     * @param obj              要清理的对象
     * @param processedObjects 用于跟踪已处理对象的集合，防止循环引用导致的无限递归
     * @throws IllegalAccessException 如果无法访问字段
     */
    private void cleanXss(Object obj, Set<Object> processedObjects) throws IllegalAccessException {
        if (obj == null || !processedObjects.add(obj)) {
            // 对象为 null，或者在本次处理中已经被处理过（防止循环引用），则停止递归
            return;
        }

        Class<?> clazz = obj.getClass();

        // **关键检查:** 判断是否应该检查这个类的字段
        if (shouldSkipInspection(clazz)) {
            // 如果是 JDK 核心类、基本类型、枚举等，则不检查其内部字段
            return;
        }
        // 可选：使用缓存的字段提高性能
        // Field[] fields = FIELD_CACHE.computeIfAbsent(clazz, Class::getDeclaredFields);
        // 获取当前类声明的所有字段
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 可选：跳过静态或 final 字段（通常是合理的，但要注意 final 字段如果是集合/Map/对象，其内容可能仍需清理）
            int modifiers = field.getModifiers();
             if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
             }
            // **关键改动:** 使用 try-finally 块确保 setAccessible 被正确处理
            // Java 9+ 可以用这个检查，但对 private 仍需 setAccessible
            boolean originallyAccessible = field.canAccess(obj);
            if (!originallyAccessible) {
                try {
                    field.setAccessible(true);
                } catch (SecurityException e) {
                    // 如果因为 JPMS 限制无法访问（理论上 shouldSkipInspection 应该已经避免了），记录警告并跳过
                    log.warn("Could not make field accessible (likely due to JPMS restrictions): {} in class {}", field.getName(), clazz.getName());
                    continue; // 跳过这个无法访问的字段
                }
            }
            try {
                Object value = field.get(obj);
                // 值是 null，跳过
                if (value == null) {
                    continue;
                }
                if (value instanceof String original) {
                    // 清理字符串字段
                    String cleaned = Jsoup.clean(original, SAFELIST);
                    if (!Objects.equals(original, cleaned)) { // 只有在内容确实被修改后才更新
                        log.trace("Cleaning field '{}': '{}' -> '{}'", field.getName(), original, cleaned); // 可选：记录清理日志
                        field.set(obj, cleaned); // 将清理后的字符串设置回字段
                    }
                } else if (value instanceof Collection) {
                    // 处理集合
                    cleanCollection((Collection<?>) value, processedObjects);
                } else if (value instanceof Map) {
                    // 处理Map
                    cleanMap((Map<?, ?>) value, processedObjects);
                } else {
                    // 递归处理自定义对象
                    cleanXss(value, processedObjects);
                }
            } finally {
                // 如果我们之前修改了可访问性，恢复它
                if (!originallyAccessible) {
                    try {
                        field.setAccessible(false);
                    } catch (SecurityException e) {
                        // 恢复失败通常不严重，可以忽略
                    }
                }
            }
        }
    }

    /**
     * 判断是否应该跳过对一个类的内部字段进行检查。
     *
     * @param clazz 要判断的类
     * @return 如果应该跳过则返回 true，否则返回 false
     */
    private boolean shouldSkipInspection(Class<?> clazz) {
        // 跳过基本类型和数组类型
        if (clazz.isPrimitive() || clazz.isArray()) {
            return true;
        }
        // 快速检查已知的跳过列表
        if (SKIPPED_CLASSES.contains(clazz)) {
            return true;
        }
        // 检查接口/父类是否在跳过列表中 (例如 Temporal 涵盖 LocalDate 等)
        for (Class<?> skipped : SKIPPED_CLASSES) {
            if (skipped.isAssignableFrom(clazz)) {
                SKIPPED_CLASSES.add(clazz); // 缓存这个具体类的判断结果
                return true;
            }
        }

        // 检查是否是标准的 Java 库类（启发式判断） - 如果需要，调整包前缀
        String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        if (packageName.startsWith("java.") || packageName.startsWith("javax.")) {
            // 可选：如果性能关键，也可以缓存这个结果
            // SKIPPED_CLASSES.add(clazz);
            return true;
        }

        // 明确允许检查我们自己应用程序的类（调整你项目的包前缀）
        if (packageName.startsWith("com.mysiteforme")) {
            return false; // 不跳过，需要检查
        }

        // 对于未知包或非应用包，默认跳过检查（更安全，避免破坏第三方库的封装）
        // 如果你的 DTO 使用了其他需要清理的第三方库类型，你可能需要显式允许它们的包
        log.trace("Skipping inspection for class in potentially non-application package: {}", clazz.getName());
        SKIPPED_CLASSES.add(clazz); // 缓存跳过的决定
        return true;
    }

    /**
     * 清理集合中的元素。
     * 注意：直接修改集合（特别是其中的 String 元素）可能因集合不可变性而失败。
     * 此实现主要递归处理集合中的非 String 对象元素。
     *
     * @param collection       要清理的集合
     * @param processedObjects 用于跟踪已处理对象的集合
     * @throws IllegalAccessException 如果无法访问字段
     */
    private void cleanCollection(Collection<?> collection, Set<Object> processedObjects) throws IllegalAccessException {
        if (collection == null || collection.isEmpty() || !processedObjects.add(collection)) {
            // 集合为 null、为空或已处理过，则返回
            return;
        }

        // 创建一个列表来迭代，避免在原始集合上迭代时进行修改（如果需要修改的话）
        // List<Object> itemsToProcess = new ArrayList<>(collection);

        // --- 简化方法：只递归处理非 String 类型的元素 ---
        // 因为直接在集合中替换 String 元素比较复杂（集合可能不可变），
        // 我们依赖 cleanXss 在后续递归中找到并清理对象字段中的 String。
        // 复制一份用于迭代，防止 ConcurrentModificationException
        for (Object item : new ArrayList<>(collection)) {
            if (item != null && !(item instanceof String)) {
                // 如果元素不是 String（可能是 Collection, Map 或自定义对象），则递归清理
                cleanXss(item,processedObjects);
            }
            // 对于 String 类型的元素，此简化方法不直接处理。
            // 如果集合元素本身就是 String，且你需要清理它们，需要更复杂的逻辑来处理可能的不可变性。

        }
    }

    /**
     * 清理 Map 中的值。
     * 注意：对于不可变的 Map，尝试修改值会抛出 UnsupportedOperationException。
     *
     * @param map              要清理的 Map
     * @param processedObjects 用于跟踪已处理对象的集合
     * @throws IllegalAccessException 如果无法访问字段
     */
    @SuppressWarnings("unchecked")
    private void cleanMap(Map<?, ?> map, Set<Object> processedObjects) throws IllegalAccessException {
        if (map == null || map.isEmpty() || !processedObjects.add(map)) {
            // Map 为 null、为空或已处理过，则返回
            return;
        }

        // 遍历 Map 的条目并清理值
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            if (value instanceof String original) {
                // 如果值是 String
                String cleaned = Jsoup.clean(original, SAFELIST);
                if (!Objects.equals(original, cleaned)) {
                    try {
                        // 尝试更新 Map 中的值（对可变 Map 有效）
                        // 注意：对于 Map.of() 或 Guava ImmutableMap 等创建的不可变 Map，这里会抛出异常
                        ((Map.Entry<?, Object>) entry).setValue(cleaned);
                        log.trace("Cleaned value for key '{}' in a map", entry.getKey());
                    } catch (UnsupportedOperationException e) {
                        log.warn("Cannot modify map entry for key '{}' (Map type: {}). XSS cleaning might be incomplete for this entry.",
                                entry.getKey(), map.getClass().getName());
                    }
                }
            } else {
                // 如果值是其他类型（Collection, Map, 自定义对象），递归清理
                cleanXss(value, processedObjects);
            }
            // 通常不建议清理 Map 的键 (key)，因为这可能破坏业务逻辑
        }
    }
}
