package com.techguy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;

@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.master.myredisson}")
    private String myredisson;
    @ConfigurationProperties(prefix = "spring.redis.master")
    @Bean(name = "masterRedisConfig")
    @Primary
    public RedisProperties master(){
        return new RedisProperties();
    }
    @ConfigurationProperties(prefix = "spring.redis.slave")
    @Bean(name ="slaveRedisConfig" )
    public RedisProperties slave(){
        return new RedisProperties();
    }

    @Primary
    @Bean(name = "masterRedis")
    public RedissonClient redissonClient(@Qualifier("masterRedisConfig") RedisProperties redisProperties) throws IOException {
        final String host = redisProperties.getHost();
        final int port = redisProperties.getPort();
        final String password = redisProperties.getPassword();
        final int database = redisProperties.getDatabase();

        Config config = Config.fromYAML(myredisson);

        SingleServerConfig singleServerConfig = config.useSingleServer();
        String url = "redis://" + host + ":" + port;
        log.info("MASTER RedissonClient:{}",url);
        singleServerConfig.setAddress(url);
        if (StringUtils.isNotBlank(password)){
            singleServerConfig.setPassword(password);
        }
        singleServerConfig.setDatabase(database);
//        singleServerConfig.setConnectionPoolSize(1000);// 无用配置
//        singleServerConfig.setSubscriptionConnectionPoolSize(1000);// 无用配置

        singleServerConfig.setConnectionMinimumIdleSize(500);
        singleServerConfig.setSubscriptionConnectionMinimumIdleSize(500);
        singleServerConfig.setPingConnectionInterval(1000);
        return Redisson.create(config);
    }
    @Bean(name = "slaveRedis")
    public RedissonClient redissonClient1(@Qualifier("slaveRedisConfig") RedisProperties slaveRedisProperties) throws IOException {
        final String host = slaveRedisProperties.getHost();
        final int port = slaveRedisProperties.getPort();
        final String password = slaveRedisProperties.getPassword();
        final int database = slaveRedisProperties.getDatabase();

        Config config = Config.fromYAML(myredisson);

        SingleServerConfig singleServerConfig = config.useSingleServer();

        String url = "redis://" + host + ":" + port;
        log.info("SLAVE RedissonClient:{}",url);
        singleServerConfig.setAddress(url);
        if (StringUtils.isNotBlank(password)){
            singleServerConfig.setPassword(password);
        }
        singleServerConfig.setDatabase(database);

        singleServerConfig.setConnectionMinimumIdleSize(500);
        singleServerConfig.setSubscriptionConnectionMinimumIdleSize(500);

        singleServerConfig.setPingConnectionInterval(1000);
        return Redisson.create(config);
    }
}
