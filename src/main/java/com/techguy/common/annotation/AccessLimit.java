package com.techguy.common.annotation;

import com.techguy.type.AccessLimitType;



import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    AccessLimitType type() default AccessLimitType.IP;

    /**
     * Current limit key
     * @return
     */
    String key();

    /**
     * access limit
     * @return
     */
    int limit() default 10;

    /**
     * limit token per second
     * @return
     */
    int perSecond() default 30;

    String errMsg() default "Frequently requests";

    /**
     * block when limitation is exceeded
     * @return
     */
    boolean blockWhenExceed() default false;

    /**
     * block 10 mins
     * @return
     */
    int blockMinutes() default 10;
}
