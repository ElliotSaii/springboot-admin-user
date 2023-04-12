package com.techguy.config;

import com.techguy.endpoint.WhiteList;
import com.techguy.interceptor.AdminTokenInterceptor;
import com.techguy.interceptor.AppTokenInterceptor;
import com.techguy.interceptor.ClientIpInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public HandlerInterceptor clientIpInterceptor(){return new ClientIpInterceptor();}

    @Bean
    public HandlerInterceptor appTokenInterceptor(){return new AppTokenInterceptor();}

    @Bean HandlerInterceptor  adminTokenInterceptor(){return new AdminTokenInterceptor();}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //Intercept all request
        registry.addInterceptor(clientIpInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui/**");

        //Intercept app  request
        registry.addInterceptor(appTokenInterceptor())
                .addPathPatterns("/app/**")
                .excludePathPatterns(WhiteList.APP_END_POINT);

        //Intercept admin  request
        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns(WhiteList.ADMIN_END_POINT);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/admin/**")
                .allowedHeaders("x-admin-token")
                .allowCredentials(false)
                .allowedMethods("POST", "GET", "HEAD", "OPTIONS")
                .allowedOrigins("*");
    }
}
