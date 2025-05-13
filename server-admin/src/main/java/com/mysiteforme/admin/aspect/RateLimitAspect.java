package com.mysiteforme.admin.aspect;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.util.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    // Lua 脚本，保证原子性
    private static final String LUA_SCRIPT =
            "local key = KEYS[1] " +
                    "local limit = tonumber(ARGV[1]) " +
                    "local expire_time = tonumber(ARGV[2]) " +
                    "local current = tonumber(redis.call('get', key) or '0') " +
                    "if current + 1 > limit then " +
                    "  return 0 " + // 0 表示超出限制
                    "else " +
                    "  redis.call('incr', key) " +
                    "  if current == 0 then " + // 首次设置，需要设置过期时间
                    "    redis.call('expire', key, expire_time) " +
                    "  end " +
                    "  return 1 " + // 1 表示允许访问
                    "end";

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisScript<Long> rateLimitScript;


    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);
        this.rateLimitScript = redisScript;
    }

    // 切点表达式现在匹配任何带有 @RateLimit 注解的方法或类中的方法
    // 注意：直接使用 @annotation(RateLimit) 会匹配到方法上的注解
    // 为了处理类注解，我们需要在逻辑内部查找
    // 或者修改切点表达式，但这可能更复杂。在环绕通知内部处理更清晰。
    // 匹配类或方法上有注解的情况
    @Around("execution(* *(..)) && (@within(com.mysiteforme.admin.annotation.RateLimit) || @annotation(com.mysiteforme.admin.annotation.RateLimit))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass(); // 获取方法所在的类

        // 查找生效的 RateLimit 注解: 优先方法，其次类
        RateLimit rateLimitAnnotation = AnnotationUtils.findAnnotation(method, RateLimit.class);
        if (rateLimitAnnotation == null) {
            rateLimitAnnotation = AnnotationUtils.findAnnotation(targetClass, RateLimit.class);
        }

        // 如果方法和类上都没有 RateLimit 注解，则不进行限流
        if (rateLimitAnnotation == null) {
            return joinPoint.proceed();
        }

        // --- 后续逻辑与原方案类似，但使用找到的 rateLimitAnnotation ---

        String uniqueKeyPart = "";
        switch (rateLimitAnnotation.limitType()) {
            case IP:
                uniqueKeyPart = ToolUtil.getClientIp(ToolUtil.getCurrentRequest());
                break;
            case USER:
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                    uniqueKeyPart = authentication.getName();
                } else {
                    log.warn("限制规则为用户, 但是当前方法:【{}】获取不到用户凭证,降级使用IP限制.", method.getName());
                    // 降级为IP
                    uniqueKeyPart = ToolUtil.getClientIp(ToolUtil.getCurrentRequest());
                }
                break;
            case API:
                // API 类型，uniqueKeyPart 可以为空或固定值
                break;
        }

        // Key 可以包含类名和方法名，以区分不同接口
        String redisKey = rateLimitAnnotation.keyPrefix() + targetClass.getName() + ":" + method.getName() + ":" + rateLimitAnnotation.limitType();

        if(StringUtils.isNotBlank(uniqueKeyPart)){
            redisKey = redisKey + ":" + uniqueKeyPart;
        }

        long limit = rateLimitAnnotation.limit();
        // 执行redis脚本操作
        long period = rateLimitAnnotation.timeUnit().toSeconds(rateLimitAnnotation.period());

        List<String> keys = Collections.singletonList(redisKey);
        Long result = redisTemplate.execute(rateLimitScript, keys, limit, period);

        if (result == 1) {
            return joinPoint.proceed();
        } else {
            log.warn("触发接口限流: key={}, limit={}, period={}s", redisKey, limit, period);
            throw MyException.builder().businessError(rateLimitAnnotation.message()).build();
        }
    }
}
