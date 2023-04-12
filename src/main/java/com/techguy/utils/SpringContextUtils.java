package com.techguy.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring加载的时候加载的ApplicationContextAware，方便在pojo类里面获得context或者bean
 *
 * @since 2016-04-12
 */
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext appContext;

    /**
     * @return 当前运行的sping应用上下文
     */
    public static ApplicationContext getAppContext() {
        return appContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) appContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return appContext.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return appContext.getBean(name, requiredType);
    }

    public static boolean containsBean(String name) {
        return appContext.containsBean(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return appContext.isSingleton(name);
    }

    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return appContext.getType(name);
    }

    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return appContext.getAliases(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.appContext = applicationContext;
    }
}
