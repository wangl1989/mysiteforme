/**
 * @ Author: wangl
 * @ Create Time: 2025-02-15 19:51:35
 * @ Modified by: wangl
 * @ Modified time: 2025-02-17 12:15:15
 * @ Description:
 */

package com.mysiteforme.admin.service;
import java.io.IOException;

import com.mysiteforme.admin.entity.DTO.GlobalHeadParam;
import com.mysiteforme.admin.security.MyUserDetails;
import com.mysiteforme.admin.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SecurityService {

    Result loginFailData(HttpServletRequest request, HttpServletResponse response);

    void loginSuccess(MyUserDetails user, HttpServletRequest request,HttpServletResponse response);

    Boolean checkToken(HttpServletRequest request, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);

    void validateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException;

    boolean checkPermission(HttpServletRequest request);

    GlobalHeadParam checkCommonParam(HttpServletRequest request);
}
