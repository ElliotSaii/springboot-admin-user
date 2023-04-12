package com.techguy.interceptor;

import com.techguy.constant.MyConstants;
import com.techguy.context.ThreadContext;
import com.techguy.exception.AuthorizationException;
import com.techguy.manager.CacheManager;
import com.techguy.utils.AdminTokenUtils;
import com.techguy.vo.AdminTokenVo;
import org.springframework.web.servlet.HandlerInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AdminTokenInterceptor implements HandlerInterceptor {

    private static final Logger accessLog = LoggerFactory.getLogger("admin-access-logger");

    private static final String HEADER_ADMIN_TOKEN = "x-admin-token";

    @Autowired
    private CacheManager cacheManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod().toUpperCase();
        if ("HEAD".equals(method) || "OPTIONS".equals(method)) {
            return true;
        }
        String tokenStr = request.getHeader(HEADER_ADMIN_TOKEN);
        // token length must be 36
        if (!AdminTokenUtils.isValidToken(tokenStr)) {
            throw new AuthorizationException("token is invalid");
        }


        AdminTokenVo cachedToken = cacheManager.getAdminToken(tokenStr);
        if (cachedToken == null) {
            throw new AuthorizationException("token is expired");
        }


        String liveAppToken = cacheManager.getLiveAdminToken(cachedToken.getAdminId());
        if (liveAppToken == null || !liveAppToken.equals(tokenStr)) {
            cacheManager.expireAdminToken(tokenStr);
            throw new AuthorizationException("login expired, please login again");
        }
        AdminTokenUtils.set2context(cachedToken);


        cacheManager.expireLiveAdminToken(cachedToken.getAdminId(), tokenStr);
        String clientIp = ThreadContext.get(MyConstants.CLIENT_IP);
        accessLog.info(">> {} admin {}({}) is requesting {}", clientIp, cachedToken.getAdminId(), cachedToken.getUname(), request.getRequestURI());

        return true;
    }



}
