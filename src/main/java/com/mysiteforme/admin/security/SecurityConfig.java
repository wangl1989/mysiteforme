/**
 * @ Author: wangl
 * @ Create Time: 2025-02-12 19:20:36
 * @ Modified by: wangl
 * @ Modified time: 2025-02-14 15:52:58
 * @ Description: Securit配置信息
 */

package com.mysiteforme.admin.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mysiteforme.admin.redis.LoginCache;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DataSource dataSource;

    private final MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
 
    private final MyAuthenticationFailureHandler myAuthenticationFailureHandler;
 
    private final MyLogoutSuccessHandler myLogoutSuccessHandler;
 
    private final UserDetailsService userDetailsService;

    private final LoginCache loginCache;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final MyPasswordEncoder myPasswordEncoder;

    @Autowired
    public SecurityConfig(DataSource dataSource, 
            UserDetailsService userDetailsService,
            MyAuthenticationSuccessHandler myAuthenticationSuccessHandler,
            MyAuthenticationFailureHandler myAuthenticationFailureHandler,
            MyLogoutSuccessHandler myLogoutSuccessHandler,
            LoginCache loginCache,
            AuthenticationConfiguration authenticationConfiguration,
            MyPasswordEncoder myPasswordEncoder) {
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
        this.myAuthenticationSuccessHandler = myAuthenticationSuccessHandler;
        this.myAuthenticationFailureHandler = myAuthenticationFailureHandler;
        this.myLogoutSuccessHandler = myLogoutSuccessHandler;
        this.loginCache = loginCache;
        this.authenticationConfiguration = authenticationConfiguration;
        this.myPasswordEncoder = myPasswordEncoder;
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        // 创建自定义的 JsonAuthenticationFilter
        JsonAuthenticationFilter jsonAuthenticationFilter = new JsonAuthenticationFilter();
        jsonAuthenticationFilter.setFilterProcessesUrl("/login"); // 设置登录接口地址
        // 设置登录成功和失败处理器
        jsonAuthenticationFilter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        // 设置登录失败处理器
        jsonAuthenticationFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);

        /*
         * 一、负责处理身份认证的核心接口，主要职责如下：
         * 1. 它负责验证用户的身份信息（用户名和密码）
         * 2. 它决定是否允许用户登录
         * 3. 它负责调用各种 AuthenticationProvider 来处理具体的认证逻辑
         * 二、AuthenticationManager 的默认实现是 ProviderManager
         * ProviderManager 内部维护一个 List<AuthenticationProvider>，用来存储多个 AuthenticationProvider
         *      public Authentication authenticate(Authentication authentication) {
         *        // 遍历所有的 AuthenticationProvider
         *        for (AuthenticationProvider provider : providers) {
         *            if (provider.supports(authentication.getClass())) {
         *                return provider.authenticate(authentication);
         *            }
         *        }
         *        // ...
         * 管理一系列的 AuthenticationProvider
         * 当需要认证时，遍历这些 provider 找到合适的来处理认证请求
         * 最常用的是 DaoAuthenticationProvider，它通过 UserDetailsService 加载用户信息并验证密码
         * 三、设置 AuthenticationManager的原因：
         * 1. 自定义的 Filter 需要知道如何验证用户身份
         * 2. 没有 AuthenticationManager，Filter 就无法进行认证
         * 3. AuthenticationConfiguration.getAuthenticationManager() 会返回 Spring Security 配置好的 AuthenticationManager
         * 四、实际的认证流程：
         * 1. JsonAuthenticationFilter 接收到请求
         * 2. 解析 JSON，创建 Authentication 对象
         * 3. AuthenticationManager 开始认证
         *      3.1 DaoAuthenticationProvider 处理认证
         *          3.1.1 加载用户信息
         *          3.1.2 验证密码
         *      3.2 如果认证成功，创建已认证的 Authentication
         * 4. 认证成功后，调用 successHandler
         * 5. 认证失败则调用 failureHandler
         * 整个认证流程是一个责任链模式的实现
         */
        jsonAuthenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());

        httpSecurity
            //  禁用basic明文验证
            .httpBasic(Customizer.withDefaults())
            //  基于 token ，不需要 csrf
            .csrf(AbstractHttpConfigurer::disable)
            //  允许跨域请求
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            //  基于token，不需要session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权
            .authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
                    // 验证码接口放行
                    .requestMatchers("/genCaptcha").permitAll()
                    //  允许直接访问 授权登录接口
                    .requestMatchers("/login").permitAll()
                    //  允许任意请求被已登录用户访问，不检查Authority
                    .anyRequest().authenticated()
            )
            // 配置记住我功能
            //.rememberMe(Customizer.withDefaults())
            //  禁用默认的表单登录配置：前端传递的都是json数据
            .formLogin(AbstractHttpConfigurer::disable)
            // 添加验证码拦截器
            .addFilterBefore(captchaFilter(), UsernamePasswordAuthenticationFilter.class)
            // 添加JWT过滤器
            .addFilterBefore(myJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            // 异常处理器
            .exceptionHandling(ex -> ex
                    // 未登录异常
                   .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                    // 权限认证失败异常
                   .accessDeniedHandler(new MyAccessDeniedHandler()))
            .addFilterAt(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 添加Logout filter
            .logout(logout -> logout.logoutSuccessHandler(myLogoutSuccessHandler));


        return httpSecurity.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return myPasswordEncoder;
    }

    /*
     * 记住我功能的数据库配置（在配置项中rememberMe最好配置token过期时间）
     * 当用户勾选"记住我"登录后，会生成一个持久化令牌
     * 这个令牌会存储在数据库中（需要创建特定的数据表）
     * 用户下次访问时，即使 session 过期，也可以通过这个令牌自动登录
     * 设置了tokenRepository.setCreateTableOnStartup(true);
     * 启动时，如果数据库中没有persistent_logins表，则会自动创建
     * 数据库表结构：
     *   CREATE TABLE persistent_logins (
     *       username VARCHAR(64) NOT NULL,
     *       series VARCHAR(64) PRIMARY KEY,
     *       token VARCHAR(64) NOT NULL,
     *       last_used TIMESTAMP NOT NULL
     *   );
     */
     @Bean
     public PersistentTokenRepository persistentTokenRepository(){
         JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
         tokenRepository.setDataSource(dataSource);
         return tokenRepository;
     }

     /*
      * 直接创建并配置完整的认证管理器链
      *
      */
//    @Bean
//    public AuthenticationManager authenticationManager()  {
//        DaoAuthenticationProvider provider =  new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(provider);
//    }

    /**
     * JWT token拦截器截器，验证token信息
     * @return JWT token拦截器
     */
    @Bean
    public JwtAuthenticationFilter myJwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(loginCache);
    }

    /**
     * 验证码拦截器
     * @return 验证码拦截器
     */
    @Bean
    public CaptchaFilter captchaFilter() {
        return new CaptchaFilter(loginCache);
    }
 
    /*
     * 调用loadUserByUserName获取userDetail信息，在AbstractUserDetailsAuthenticationProvider里执行用户状态检查
     * 配置认证提供者，让 Spring Security 自动装配到认证管理器中
     * 这种方式的好处：
     * 1. 符合 Spring Security 的自动配置理念
     * 2. 代码更简洁
     * 3. 更容易扩展（如果需要添加多个 AuthenticationProvider）
     * 如果需要多个认证提供者，可以配置这个
     *  @Bean
     *  public AuthenticationProvider customAuthenticationProvider() {
     *      // 自定义的认证提供者
     *      return new CustomAuthenticationProvider();
     *  }
     * Spring Security 会自动将所有类型为 AuthenticationProvider 的 Bean 注册到认证管理器中
     * 不需要同时配置这两种方式，只需要配置其中一种即可
     * 通常情况下，我们选择配置 AuthenticationProvider，让 Spring Security 自动管理认证管理器的装配
     * 如果需要在自定义 Filter 中使用 AuthenticationManager，通过 AuthenticationConfiguration 获取即可
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        // 添加日志
        authProvider.setPreAuthenticationChecks(user -> {
            System.out.println("===========正在进行预认证检查：{}========="+user.getUsername());
        });
        return authProvider;
    }

    /**
     * 配置跨源访问(CORS)
     *
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    

}