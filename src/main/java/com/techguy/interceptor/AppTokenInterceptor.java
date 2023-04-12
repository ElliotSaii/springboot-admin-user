package com.techguy.interceptor;
import com.techguy.constant.MyConstants;
import com.techguy.context.ThreadContext;
import com.techguy.exception.AuthorizationException;
import com.techguy.manager.CacheManager;
import com.techguy.utils.AppTokenUtils;
import com.techguy.vo.AppTokenVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class AppTokenInterceptor implements HandlerInterceptor {

    private static final Logger accessLog = LoggerFactory.getLogger("app-access-logger");

    private static final String HEADER_APP_TOKEN = "x-app-token";

    @Autowired
    private CacheManager cacheManager;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String tokenStr = request.getHeader(HEADER_APP_TOKEN);

        if (!AppTokenUtils.isValidToken(tokenStr)) {
            throw new AuthorizationException("token is invalid");
        }


        AppTokenVo cachedToken = cacheManager.getAppToken(tokenStr);
        if (cachedToken == null) {
            throw new AuthorizationException("please login again");
        }


        String liveAppToken = cacheManager.getLiveAppToken(cachedToken.getMemberId());
        if (liveAppToken == null || !liveAppToken.equals(tokenStr)) {
            cacheManager.expireAppToken(tokenStr);
            throw new AuthorizationException("login expired,please login again");
        }

        AppTokenUtils.set2context(cachedToken);

        String clientIp = ThreadContext.get(MyConstants.CLIENT_IP);
        accessLog.info(">> {} member {}({}) is requesting {} ", clientIp, cachedToken.getMemberId(), cachedToken.getAccount(), request.getRequestURI());

        return true;
    }

}
