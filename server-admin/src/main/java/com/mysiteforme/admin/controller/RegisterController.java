package com.mysiteforme.admin.controller;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.entity.User;
import com.mysiteforme.admin.entity.VO.UserVO;
import com.mysiteforme.admin.entity.request.*;
import com.mysiteforme.admin.redis.RedisConstants;
import com.mysiteforme.admin.redis.RedisUtils;
import com.mysiteforme.admin.service.UserService;
import com.mysiteforme.admin.util.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 发送邮件controller
 * @author wangl1989
 * @since 2025-05-05
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
@RateLimit(limit = 20, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.IP)
public class RegisterController {

    private final RedisUtils redisUtils;

    private final UserService userService;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sendFrom;

    /**
     * 发送邮件
     * @param request 邮件参数对象
     * @return 返回结果
     */
    @RateLimit(limit = 1, period = 30, timeUnit = TimeUnit.SECONDS, limitType = LimitType.IP)
    @PostMapping("sendEmail")
    public Result sendEmail(@RequestBody @Valid SendEmailRequest request) {
        // 检测这个邮箱是否被注册过
        if(userService.userCounByEmail(request.getEmail(),null)>0){
            return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
        }
        if(redisUtils.hasKey(RedisConstants.EMAIL_VALIDATE_SUCCESS)){
            Set<Object> emails = redisUtils.sGet(RedisConstants.EMAIL_VALIDATE_SUCCESS);
            if(emails.contains(request.getEmail())){
                return Result.success().setCode(ResultCode.CHECK_EMAIL_SUCCESS);
            }
        }
        SimpleMailMessage message = new SimpleMailMessage();
        String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_NUM_ONLY, 6, null);
        message.setFrom(sendFrom);
        message.setTo(request.getEmail());
        message.setSubject(Constants.DEFAULT_EMAIL_CHECK_REGISTE_SUBJECT);
        message.setText(String.format(Constants.DEFAULT_EMAIL_REGIST_CONTENT,verifyCode));
        message.setSentDate(new Date());
        try {
            mailSender.send(message);
            redisUtils.set(RedisConstants.USER_EMAIL_KEY+request.getEmail(), verifyCode, Constants.USER_EMAIL_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            return Result.success();
        } catch (MailException e) {
            log.error("注册新账户发送邮件失败: {}", e.getMessage(), e);
            return Result.businessMsgError(MessageConstants.User.EMAIL_SEND_FAILED,e.getMessage());
        }
    }

    /**
     * 验证邮箱验证码
     */
    @PostMapping("checkEmail")
    public Result checkEmail(@RequestBody @Valid CheckEmailRequest request){
        if(userService.userCounByEmail(request.getEmail(),null)>0){
            return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
        }
        if(!redisUtils.hasKey(RedisConstants.USER_EMAIL_KEY+request.getEmail())){
            return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
        }
        String code = redisUtils.get(RedisConstants.USER_EMAIL_KEY+request.getEmail(),String.class);
        if(!code.equalsIgnoreCase(request.getCode())){
            return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
        }
        // 清除邮箱验证缓存
        redisUtils.del(RedisConstants.USER_EMAIL_KEY+request.getEmail());
        // 设置验证成功的邮箱
        redisUtils.sSet(RedisConstants.EMAIL_VALIDATE_SUCCESS,Constants.CHECK_EMAIL_SUCCESS_TIME,TimeUnit.HOURS,request.getEmail());
        return Result.success();
    }

    @PostMapping("user")
    public Result registerUser(@RequestBody @Valid RegisterUserRequest request){
        if(userService.userCounByEmail(request.getEmail(),null)>0){
            return Result.businessMsgError(MessageConstants.User.EMAIL_HAS_EXIST);
        }
        if(!redisUtils.hasKey(RedisConstants.EMAIL_VALIDATE_SUCCESS)){
            return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
        }
        Set<Object> emails = redisUtils.sGet(RedisConstants.EMAIL_VALIDATE_SUCCESS);
        if(!emails.contains(request.getEmail())){
            return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
        }
        if(userService.userCounByLoginName(request.getLoginName(),null)>0){
            return Result.businessMsgError(MessageConstants.User.LOGIN_NAME_HAS_EXIST);
        }
        User user = new User();
        BeanUtils.copyProperties(request,user);
        user.setNickName(String.format(Constants.DEFAULT_USER_NICK_NAME,user.getLoginName()));
        UserVO userVO = userService.saveUser(user);
        if(userVO != null && userVO.getId() != null) {
            // 清除验证key
            redisUtils.sRemove(RedisConstants.EMAIL_VALIDATE_SUCCESS, request.getEmail());
        } else {
            return Result.businessMsgError(MessageConstants.User.REGISTER_FAILED);
        }
        return Result.success();
    }

    /**
     * 发送忘记密码验证码
     * @param request 参数对象
     * @return result返回结果
     */
    @RateLimit(limit = 1, period = 30, timeUnit = TimeUnit.SECONDS, limitType = LimitType.IP)
    @PostMapping("forgetPassword")
    public Result forgetPassword(@RequestBody @Valid ForgetPasswordRequest request){
        User user = userService.getUserByEmail(request.getEmail());
        if(user == null){
            return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
        }
        if(user.getDelFlag()){
            return Result.businessMsgError(MessageConstants.User.USER_DISABLED);
        }
        SimpleMailMessage message = new SimpleMailMessage();
        String verifyCode = VerifyCodeUtil.generateTextCode(VerifyCodeUtil.TYPE_NUM_ONLY, 6, null);
        message.setFrom(sendFrom);
        message.setTo(request.getEmail());
        message.setSubject(Constants.DEFAULT_EMAIL_REST_PASSWORD_SUBJECT);
        message.setText(String.format(Constants.DEFAULT_EMAIL_REST_PASSWORD_CONTENT,verifyCode));
        message.setSentDate(new Date());
        try {
            mailSender.send(message);
            redisUtils.set(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail(), verifyCode, Constants.USER_EMAIL_CACHE_EXPIRE_TIME,TimeUnit.MINUTES);
            return Result.success();
        } catch (MailException e) {
            log.error("忘记密码发送邮件失败: {}", e.getMessage(), e);
            return Result.businessMsgError(MessageConstants.User.EMAIL_SEND_FAILED,e.getMessage());
        }
    }

    /**
     * 忘记密码验证发送的验证码
     * @param request 验证码对象
     * @return 返回结果
     */
    @PostMapping("checkRestPasswordEmail")
    public Result checkRestPassowrdEmail(@RequestBody @Valid CheckRestPasswordEmailRequest request){
        if(!redisUtils.hasKey(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail())){
            return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
        }
        String code = redisUtils.get(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail(),String.class);
        if(!code.equalsIgnoreCase(request.getCode())){
            return Result.businessMsgError(MessageConstants.User.EMAIL_OR_CODE_INVALID);
        }
        // 清除邮箱验证缓存
        redisUtils.del(RedisConstants.USER_EMAI_REST_PASSWORD_KEY+request.getEmail());
        // 设置验证成功的邮箱
        redisUtils.sSet(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS,Constants.CHECK_EMAIL_SUCCESS_TIME,TimeUnit.HOURS,request.getEmail());
        return Result.success();
    }

    /**
     * 重置密码
     * @param request 重置密码对象
     * @return Result返回对象
     */
    @PutMapping("resetPassword")
    public Result resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        // 是否有重置密码成功的邮箱key
        if(!redisUtils.hasKey(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS)){
            return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
        }
        // 重置密码成功的邮箱key对应的集合中是否有当前email
        Set<Object> emails = redisUtils.sGet(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS);
        if(!emails.contains(request.getEmail())){
            return Result.businessMsgError(MessageConstants.User.EMAIL_NOT_VALIDATE);
        }
        // 根据email获取用户对象
        User user = userService.getUserByEmail(request.getEmail());
        if(user == null){
            return Result.businessMsgError(MessageConstants.User.USER_NOT_FOUND);
        }
        if(user.getDelFlag() || user.getLocked()){
            return Result.businessMsgError(MessageConstants.User.USER_DISABLED);
        }
        // 设置用户输入的新密码
        user.setPassword(request.getPassword());
        userService.emailChangePassword(user);
        // 清除邮箱验证缓存
        redisUtils.sRemove(RedisConstants.EMAIL_REST_PASSWORD_VALIDATE_SUCCESS, request.getEmail());
        return Result.success();
    }
}
