package com.techguy.common.aspect;

import com.techguy.common.annotation.AccessLimit;
import com.techguy.constant.CacheKeys;
import com.techguy.constant.MyConstants;
import com.techguy.context.ThreadContext;
import com.techguy.exception.AuthorizationException;
import com.techguy.manager.CacheManager;
import com.techguy.type.AccessLimitType;
import com.techguy.utils.AppTokenUtils;
import com.techguy.vo.AppTokenVo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.time.Instant;
import java.util.concurrent.TimeUnit;


@Aspect
public class AccesLimitAspect implements Ordered {
private static final Logger accessLimitLog = LoggerFactory.getLogger("accessLimit");
private static final int AOP_ORDER = 1;

    private final CacheManager cacheManager;
    public AccesLimitAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Before("@annotation(limitAnnotation)")
    public void limitBeforeExecute(JoinPoint joinpoint, AccessLimit limitAnnotation){
        int limit = limitAnnotation.limit();
        int perSecond = limitAnnotation.perSecond();

        String limitKey = buildLimitKey(limitAnnotation,joinpoint);
        if (limitKey == null || limitKey.trim().length() == 0) {
            return;
        }
        RRateLimiter rRateLimiter = cacheManager.getRateLimiter(limitKey);
        rRateLimiter.trySetRate(RateType.OVERALL, limit, perSecond, RateIntervalUnit.SECONDS);

        // set the expiration time to 60 seconds from now
        Instant expirationTime = Instant.now().plusSeconds(perSecond * 2L);

        rRateLimiter.expire(expirationTime);
        boolean tryAcquire = rRateLimiter.tryAcquire();

        //access limit not exceeded;
        if (tryAcquire) {
            return;
        }
        if(AccessLimitType.IP == limitAnnotation.type() && limitAnnotation.blockWhenExceed()){
            String clientIp = ThreadContext.get(MyConstants.CLIENT_IP);
            cacheManager.cacheBlackIp(clientIp, limitAnnotation.blockMinutes(), TimeUnit.MINUTES);
            accessLimitLog.warn("current ip:{} key:{} set to blacklist {}:mins",clientIp, limitKey, limitAnnotation.blockMinutes());
        }
        throw new AuthorizationException(limitAnnotation.errMsg());

    }

    private String buildLimitKey(AccessLimit accessLimit, JoinPoint joinpoint) {
       String limitKey = "";
        String key = accessLimit.key();
        AccessLimitType type = accessLimit.type();
        Object[] args = joinpoint.getArgs();

        switch (type) {
            case IP:
                String clientIp = ThreadContext.get(MyConstants.CLIENT_IP);
                limitKey = CacheKeys.accessLimitKey(AccessLimitType.IP, key, clientIp);
                break;
            case UID:
                AppTokenVo appTokenVo = AppTokenUtils.getFromContext();
                if(appTokenVo!=null){
                   limitKey = CacheKeys.accessLimitKey(AccessLimitType.UID, key, String.valueOf(appTokenVo.getMemberId()));
                }else {
                    accessLimitLog.warn(">>Login user not found: {}", joinpoint);
                }
                break;
            case KEY:
                limitKey = CacheKeys.accessLimitKey(AccessLimitType.KEY, key);
                break;

            default:
                //noting to do

        }
        return limitKey;

    }

    @Override
    public int getOrder() {
        return AOP_ORDER;
    }

}
