package com.mysiteforme.admin.service;


import java.io.IOException;

import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.security.MyUserDetails;
import com.mysiteforme.admin.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SecurityService {

    Result loginFailData(String username) throws MyException;

    void loginSuccess(MyUserDetails user, HttpServletResponse response) throws ServletException, IOException;

    Boolean checkToken(String authHeader, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    void validateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    boolean checkPermission(HttpServletRequest request);
}
