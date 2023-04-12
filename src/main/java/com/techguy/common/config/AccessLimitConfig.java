package com.techguy.common.config;
import com.techguy.common.aspect.AccesLimitAspect;
import com.techguy.manager.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AccessLimitConfig {

    private static final Logger log = LoggerFactory.getLogger(AccessLimitConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public AccesLimitAspect rateLimitAspect(@Autowired CacheManager cacheManager) {
        log.info(">> Config redis to handle @AccessLimit");
        return new AccesLimitAspect(cacheManager);
    }


}