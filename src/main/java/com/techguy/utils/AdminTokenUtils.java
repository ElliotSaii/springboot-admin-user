package com.techguy.utils;
import com.techguy.context.ThreadContext;
import com.techguy.manager.CacheManager;
import com.techguy.vo.AdminTokenVo;
import org.springframework.util.StringUtils;

/**
 * @since 2019-12-31
 */
public class AdminTokenUtils {

    private static final String TOKEN_CONTEXT_KEY = "admin-token";

    private static final String TOKEN_REGEX = "^[0-9a-zA-Z]{36}$";

    /**
     * 随机生成一个长度为36个字符的token
     *
     * @return
     */
    public static String newToken() {
        return RandomUtils.randomBase64UrlSafedString(27)
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
     * 将管理员登录信息放入线程上下文
     *
     * @param adminToken
     */
    public static void set2context(AdminTokenVo adminToken) {
        if (adminToken != null) {
            ThreadContext.set(TOKEN_CONTEXT_KEY, adminToken);
        }
    }

    /**
     * 从线程上下文获取管理员登录信息
     *
     * @return
     */
    public static AdminTokenVo getFromContext() {
        return ThreadContext.get(TOKEN_CONTEXT_KEY);
    }

    /**
     * 从redis缓存中获取当前管理员的有效token(该管理员最新的登录token)
     *
     * @return
     */
    public static String getLiveAdminToken() {
        AdminTokenVo adminToken = getFromContext();
        if (adminToken == null) {
            return null;
        }
        CacheManager cacheManager = SpringContextUtils.getBean(CacheManager.class);
        return cacheManager.getLiveAdminToken(adminToken.getAdminId());
    }
}
