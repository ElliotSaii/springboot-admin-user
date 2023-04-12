package com.techguy.manager;

import com.techguy.constant.CacheKeys;
import com.techguy.vo.AdminTokenVo;
import com.techguy.vo.AppTokenVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CacheManager {

    private static final long ADMIN_TOKEN_EXPIRE = 60L;
    private static final long APP_TOKEN_EXPIRE = 7 * 24 * 60L;

    private static final long PWD_FAIL_PER_MINUTES = 30;
    private static final long PWD_FAIL_LIMIT = 5;
    private static final long ADMIN_TOKEN_EXPIRE_MINUTES = 60L;


    @Resource(name = "masterRedis")
    private RedissonClient masterRedissonClient;

    @Resource(name = "slaveRedis")
    private RedissonClient slaveRedissonClient;

   public boolean hasKey(String key){
    return slaveRedissonClient.getBucket(key).isExists();
   }

   public  RRateLimiter getRateLimiter(String limitKey){
       return masterRedissonClient.getRateLimiter(limitKey);
   }

   public void cacheBlackIp(String ip, long blockTime, TimeUnit timeUnit){
     String key =  CacheKeys.blackIpKey(ip);
     masterRedissonClient.getBucket(key).set(1,blockTime,timeUnit);
   }


    public boolean isBlackIp(String clientIp) {
      return hasKey(CacheKeys.blackIpKey(clientIp));
    }

    public AppTokenVo getAppToken(String token) {
        String key = CacheKeys.appTokenKey(token);
        Object object = slaveRedissonClient.getBucket(key).get();
        return object == null ? null : (AppTokenVo) object;
    }

    public String getLiveAppToken(Integer memberId) {
        String key = CacheKeys.liveAppTokenKey(memberId);
        Object object = slaveRedissonClient.getBucket(key).get();
        return object == null ? null : (String) object;
    }

    public void expireAppToken(String token) {
        String key = CacheKeys.appTokenKey(token);
        // 延迟10秒失效
        masterRedissonClient.getBucket(key).expire(30L, TimeUnit.SECONDS);
    }

    public void removeAppToken(String token) {
        String key = CacheKeys.appTokenKey(token);
        masterRedissonClient.getBucket(key).delete();
    }

    public void removeLiveAppToken(Integer memberId) {
        String key = CacheKeys.liveAppTokenKey(memberId);
        masterRedissonClient.getBucket(key).delete();
    }

    public AdminTokenVo getAdminToken(String token) {
        String key = CacheKeys.adminTokenKey(token);
        Object object = slaveRedissonClient.getBucket(key).get();
        return object == null ? null : (AdminTokenVo) object;
    }

    public String getLiveAdminToken(Integer adminId) {
        String key = CacheKeys.liveAdminTokenKey(adminId);
        Object object = slaveRedissonClient.getBucket(key).get();
        return object == null ? null : (String) object;
    }

    public void expireAdminToken(String token) {
        String key = CacheKeys.adminTokenKey(token);
        // 延迟10秒失效
        masterRedissonClient.getBucket(key).expire(30L, TimeUnit.SECONDS);
    }

    public void expireLiveAdminToken(Integer adminId, String token) {
        String tokenKey = CacheKeys.adminTokenKey(token);
        String adminIdKey = CacheKeys.liveAdminTokenKey(adminId);
        masterRedissonClient.getBucket(tokenKey).expire(ADMIN_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
        masterRedissonClient.getBucket(adminIdKey).expire(ADMIN_TOKEN_EXPIRE_MINUTES + 1, TimeUnit.MINUTES);
    }
}
