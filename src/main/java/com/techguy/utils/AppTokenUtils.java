package com.techguy.utils;
import com.techguy.context.ThreadContext;
import com.techguy.manager.CacheManager;
import com.techguy.vo.AppTokenVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class AppTokenUtils {

    private static final String APP_TOKEN_CONTEXT_KEY = "app-token";

    private static final String TOKEN_REGEX = "^[0-9a-zA-Z]{32}$";

    /**
     * Don't let anyone instantiate this class
     */
    private AppTokenUtils() {
    }


    public static String newToken() {
        return RandomUtils.randomBase64UrlSafedString(24)
                .replace('-', 'x')
                .replace('_', '0');
    }



    public static boolean isValidToken(String tokenStr) {
        if (!StringUtils.hasText(tokenStr)) {
            return false;
        }
        return tokenStr.matches(TOKEN_REGEX);
    }

    /**
     * 将app用户登录信息放入线程上下文
     *
     * @param appToken
     */
    public static void set2context(AppTokenVo appToken) {
        if (appToken != null) {
            ThreadContext.set(APP_TOKEN_CONTEXT_KEY, appToken);
        }
    }

    /**
     * 从线程上下文获取app用户登录信息
     *
     * @return
     */
    public static AppTokenVo getFromContext() {
        return ThreadContext.get(APP_TOKEN_CONTEXT_KEY);
    }

    public static String getLiveAppToken() {
        AppTokenVo appTokenVo = getFromContext();
        if (appTokenVo == null) {
            return null;
        }
        CacheManager cacheManager = SpringContextUtils.getBean(CacheManager.class);
        return cacheManager.getLiveAppToken(appTokenVo.getMemberId());
    }

    public static void logoutMember() {
        AppTokenVo appTokenVo = getFromContext();
        if (appTokenVo != null) {
            CacheManager cacheManager = SpringContextUtils.getBean(CacheManager.class);
            String token = cacheManager.getLiveAppToken(appTokenVo.getMemberId());
            if (StringUtils.hasText(token)) {
                cacheManager.removeAppToken(token);
            }
            cacheManager.removeLiveAppToken(appTokenVo.getMemberId());
        }
    }

}
