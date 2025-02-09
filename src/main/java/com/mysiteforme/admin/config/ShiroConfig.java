package com.mysiteforme.admin.config;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.base.CaptchaFormAuthenticationFilter;
import com.mysiteforme.admin.realm.AuthRealm;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;
import redis.clients.jedis.JedisPool;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro安全框架配置类
 * 配置认证、授权、会话管理等安全特性
 * @author wangl
 * @since 2017/11/22
 */
@Configuration
public class ShiroConfig {
    private final Logger  logger = LoggerFactory.getLogger(ShiroConfig.class);

    @Value("${spring.redis.host}")
    private String jedisHost;

    @Value("${spring.redis.port}")
    private Integer jedisPort;

    @Value("${spring.redis.password}")
    private String jedisPassword;

    @Value("${spring.redis.database}")
    private String dataBase;

    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy(){
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.ERROR,DispatcherType.REQUEST,DispatcherType.FORWARD,DispatcherType.INCLUDE);
        return filterRegistrationBean;
    }

    /**
     * 配置安全管理器
     * @param authRealm 自定义Realm
     * @return SecurityManager实例
     */
    @Bean
    public DefaultWebSecurityManager securityManager(@Qualifier("authRealm")AuthRealm authRealm){
        logger.info("- - - - - - -shiro开始加载- - - - - - ");
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(authRealm);
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager());
        defaultWebSecurityManager.setSessionManager(webSessionManager());
        defaultWebSecurityManager.setCacheManager(cacheManager());
        return defaultWebSecurityManager;
    }

    /**
     * 配置Shiro过滤器
     * @param securityManager securityManager
     * @return ShiroFilterFactoryBean实例
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        bean.setSuccessUrl("/index");
        bean.setLoginUrl("/login");
        bean.setUnauthorizedUrl("/login");
        Map<String,Filter> map = Maps.newHashMap();
        map.put("authc",new CaptchaFormAuthenticationFilter());
        bean.setFilters(map);
        //配置访问权限
        LinkedHashMap<String, String> filterChainDefinitionMap = Maps.newLinkedHashMap();
        //配置不会被拦截的链接 顺序判断
        //静态资源
        filterChainDefinitionMap.put("/static/**","anon");
        //博客相关
        filterChainDefinitionMap.put("/showBlog/**","anon");
        filterChainDefinitionMap.put("/blog/**","anon");
        //登录相关
        filterChainDefinitionMap.put("/login/main","anon");
        filterChainDefinitionMap.put("/login","anon");
        filterChainDefinitionMap.put("/genCaptcha","anon");

        //配置会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/systemLogout","authc");
        filterChainDefinitionMap.put("/**","authc");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    @Bean
    public SimpleCookie rememberMeCookie(){
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setHttpOnly(true);
        //记住我有效期长达30天
        cookie.setMaxAge(2592000);
        return cookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCookie(rememberMeCookie());
        // 1.13.0 版本中不再使用 Base64.decode
        // 建议使用更安全的密钥生成方式
        rememberMeManager.setCipherKey("2AvVhdsgUs0FSA3SDFAdag==".getBytes());
        return rememberMeManager;
    }

    /**
     * 配置生命周期处理器
     * @return LifecycleBeanPostProcessor实例
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("authRealm")AuthRealm authRealm) {
        DefaultWebSecurityManager manager= securityManager(authRealm);
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    @Bean
    public SessionManager webSessionManager(){
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        //设置session过期时间为1小时(单位：毫秒)，默认为30分钟
        manager.setGlobalSessionTimeout(60 * 60 * 1000);
        manager.setSessionValidationSchedulerEnabled(true);
        manager.setSessionDAO(redisSessionDAO());
        return manager;
    }

    @Bean
    public RedisManager redisManager(){
        RedisManager manager = new RedisManager();
        manager.setHost(jedisHost);
        manager.setDatabase(Integer.parseInt(dataBase));
        JedisPool pool = new JedisPool(jedisHost,jedisPort);
        manager.setJedisPool(pool);
        //这里是用户session的时长 跟上面的setGlobalSessionTimeout 应该保持一直（上面是1个小时 下面是秒做单位的 我们设置成3600）
        manager.setTimeout(60 * 60);
        manager.setPassword(jedisPassword);
        return manager;
    }

    @Bean(name = "customRedisSessionDAO")
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setKeyPrefix("wl_");
        sessionDAO.setRedisManager(redisManager());
        return sessionDAO;
    }

    @Bean("myCacheManager")
    public RedisCacheManager cacheManager(){
        RedisCacheManager manager = new RedisCacheManager();
        manager.setRedisManager(redisManager());
        manager.setExpire(60 * 60);
        return manager;
    }

    /**
     * 开启Shiro注解支持
     * @return DefaultAdvisorAutoProxyCreator实例
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
}
