1. Java版本升级到17
2. 升级SpringBoot版本到3.2.2
3. 核心依赖升级：
    commons-lang3.version 升级到3.13.0
    commons-codec.version 升级到1.16.0
    fastjson.version 升级到2.0.45
    mysql-connector-j.version 升级到8.3.0
    okhttp.version 升级到4.12.0
    lombok.version 升级到1.18.36
    添加依赖：Jakarta EE 9+ 的 Servlet API jakarta.servlet-api.version 6.0.0
    添加依赖： jjwt-api.version 0.12.3
    添加依赖： 安全框架 security.version 6.2.0


    移除依赖：shiro-web 
    移除依赖：shiro-cas
    移除依赖：shiro-core
    移除依赖：shiro-spring
    移除依赖：shiro-redis

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
        <configuration>
            <source>17</source>
            <target>17</target>
            <encoding>UTF-8</encoding>
        </configuration>
    </plugin>
    这里java版本要升级到17

4. 代码更新：
    javax.servlet 替换为 jakarta.servlet
    javax.servlet.http 替换为 jakarta.servlet.http
    javax.servlet.annotation 替换为 jakarta.servlet.annotation
    javax.servlet.filter 替换为 jakarta.servlet.filter
    javax.servlet.FilterChain 替换为 jakarta.servlet.FilterChain
    javax.servlet.FilterConfig 替换为 jakarta.servlet.FilterConfig
    

5. 添加SecurityConfig 配置类

/**
 * 处理链路：
 *      login : CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthenticationManager（优先级高于  SecurityUserDetailsService）  -> AuthenticationSuccessHandler/AuthenticationFailHandler
 *      logout: CookieToHeadersFilter -> ScSecurityContextRepository -> LogoutHandler -> LogoutSuccessHandler
 *      未登录进行 url request: CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthorizationManager -> ScAuthenticationEntryPoint
 *      登录后进行url request: CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthorizationManager -> CookieToHeadersFilter（子线程, 可以在前面ScSecurityContextRepository更新token并重新设置请求头）-> 服务接口
 *      鉴权失败: CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthorizationManager -> ScAuthenticationEntryPoint -> ScAccessDeniedHandler
 */



6. 配置文件更新
    application.yml 更新
    添加配置：
    mvc:
      pathmatch:
      matching-strategy: ant_path_matcher  
    更新配置：
    datasource:
        druid:
            url: jdbc:mysql://localhost:3306/mysiteforme?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false


    spring:
        profiles: dev 
    改为：
    spring:
        config:
            activate:
            on-profile: dev

    移除 spring.http.encoding 配置，因为在 Spring Boot 3.x 中已经默认使用 UTF-8 编码，不需要显式配置。


升级spring-boot-starter-quartz 2.5.7之后解决SchedulerConfigException(“DataSource name not set.“)错误记录。
升级spring boot > 2.5.6的版本后将不再支持此方式进行配置默认数据源，需改为如下配置：

org.quartz.jobStore.class=org.springframework.scheduling.quartz.LocalDataSourceJobStore
这边改了之后定时任务还是报错，找到原因：
项目初始化的时候会执行一个定时任务：
```java
    @PostConstruct
    public void init(){
        QueryWrapper<QuartzTask> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        List<QuartzTask> scheduleJobList = list(wrapper);
        for(QuartzTask scheduleJob : scheduleJobList){
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob);
            //如果不存在，则创建
            if(cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }
```
这个任务在数据库中获取了定时任务列表后，就会要么更新要么新增。
```java
CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob);
```
这段代码是获取定时任务的CronTrigger,也就是表达式触发器
```java
    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw MyException.builder().code(MyException.SERVER_ERROR).msg("获取定时任务CronTrigger出现异常").build();
        }
    }
```
其中：
```java
    (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
```
这段代码获取的值一直是null，导致一直不能创建定时器，但下面的方法却是能创建的，所以创建的时候会报这个错误：表达式触发器已经创建过了，不能再次创建了。
这个段创建表达式触发器的代码如下：
```java
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
            		.withMisfireHandlingInstructionDoNothing();
            //按新的cronExpression表达式构建一个新的trigger
    CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getId())).withSchedule(scheduleBuilder).build();
```
springboot由3.1.5升级到3.2.0 报Invalid value type for attribute ‘factoryBeanObjectType‘: java.lang.String

1、由于使用了mybatils，所以首先想到的应该是

mybatis-spring 这个包版本应该比较低，造成不支持springboot3.2.0，

2、然后先把pom文件中mybatisplus依赖中的mybatis-spring移除：
```xml
        <dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>mybatis-plus-boot-starter</artifactId>
			<exclusions>
				<exclusion>
					<groupId>org.mybatis</groupId>
					<artifactId>mybatis-spring</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
```

然后再添加mybatis-spring-boot-starter依赖：

```xml
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>3.0.3</version>
        </dependency>
```



    
