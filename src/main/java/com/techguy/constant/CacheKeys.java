package com.techguy.constant;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.techguy.type.AccessLimitType;
import org.redisson.api.RRateLimiter;


public abstract class CacheKeys {
 public static final String PREFIX = "loo:";
 private static final String RATE_LIMIT = PREFIX + "access-limit:";
 private static final String PWD_FAIL_COUNT = PREFIX + "pwd-fail:";
 private static final String APP_TOKEN = PREFIX + "app-token:";
 private static final String LIVE_APP_TOKEN = PREFIX + "live-app-token:";
    private static final String ADMIN_TOKEN = PREFIX + "adm-token:";
    private static final String LIVE_ADMIN_TOKEN = PREFIX + "live-admin-token:";


    public static String blackIpKey(String ip){
      return PREFIX+ip;
  }
    public static String accessLimitKey(AccessLimitType type, String key){
        return accessLimitKey(type,key, null);
    }

    public static String accessLimitKey(AccessLimitType type, String key, String param) {
        String result = RATE_LIMIT;
        switch (type) {
            case IP:
                result += "ip.";
                break;
            case UID:
                result += "u.";
                break;
            case PARAM:
                result += "p.";
                break;
            case POJO_FIELD:
                result += "f.";
                break;
            case KEY:
                result += "k.";
                break;
            default:
                // nothing to do
        }
        result += key;
        if (StringUtils.isNotBlank(param)) {
            result += "." + param;
        }
        return result;
    }
    public static String pwdFailCountKey(String account) {
        return PWD_FAIL_COUNT + account;
    }

    public static String appTokenKey(String token) {
        return APP_TOKEN + token;
    }

    public static String liveAppTokenKey(Integer memberId) {
        return LIVE_APP_TOKEN + memberId;
    }

    public static String adminTokenKey(String token) {
        return ADMIN_TOKEN + token;
    }

    public static String liveAdminTokenKey(Integer adminId) {
        return LIVE_ADMIN_TOKEN + adminId;
    }


}
