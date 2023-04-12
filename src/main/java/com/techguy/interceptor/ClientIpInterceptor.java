package com.techguy.interceptor;
import com.techguy.constant.MyConstants;
import com.techguy.context.ThreadContext;
import com.techguy.exception.BusinessException;
import com.techguy.manager.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClientIpInterceptor implements HandlerInterceptor {
 private static final String Unknown_Ip = "unknown";
    private static final Logger log = LoggerFactory.getLogger(ClientIpInterceptor.class);
 @Autowired
  private  CacheManager cacheManager;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);

         if(cacheManager.isBlackIp(clientIp)){
             throw new BusinessException(String.format("Current IP:%s baned",clientIp));
         }
         ThreadContext.set(MyConstants.CLIENT_IP, clientIp);
         return true;
    }

    private String getClientIp(HttpServletRequest request) {
      String ip = "";
      for(String header : HEADERS_TO_TRY){
          ip = request.getHeader(header);
          if(StringUtils.isNotBlank(ip) && !ip.equalsIgnoreCase(Unknown_Ip)){
              break;
          }
      }
      //If cannot find valid IP use remoteAddress
        if(StringUtils.isBlank(ip) || ip.equalsIgnoreCase(Unknown_Ip)){
            ip = request.getRemoteAddr();
        }
       //If try many times ip can be list use the first child
        if(StringUtils.isNotBlank(ip) && ip.indexOf(",") > 0){
         String[] ipArray = ip.split(",");
         ip = ipArray[0];
        }
        return ip;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Need to clean up to avoid reuse data disorder
        ThreadContext.close();

    }



    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"};
}
