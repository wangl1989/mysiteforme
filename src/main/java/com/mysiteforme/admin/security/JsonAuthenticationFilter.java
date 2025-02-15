package com.mysiteforme.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysiteforme.admin.entity.DTO.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;

public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("登录方法请求Method错误: " + request.getMethod());
        }

        try {
            // 从请求体中读取登录信息
            UserDTO userDTO = new ObjectMapper().readValue(request.getInputStream(), UserDTO.class);

            String username = userDTO.getUsername();
            String password = userDTO.getPassword();

            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }

            username = username.trim();

            // 2. 创建未认证的 Authentication
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

            setDetails(request, authRequest);
            // 3. 使用 AuthenticationManager 进行认证
            // 这里就是调用之前设置的 AuthenticationManager 进行认证
            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new AuthenticationServiceException("登录异常：认证过程出错", e);
        }
    }
}
