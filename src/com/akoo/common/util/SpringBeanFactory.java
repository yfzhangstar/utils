package com.akoo.common.util;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;


/**
 * SpringBeanFactory
 */

public class SpringBeanFactory {
    private static final Logger log = LoggerFactory.getLogger(SpringBeanFactory.class);

    /**
     * SPRING_CONFIG_NAME
     */
    private static final String SPRING_CONFIG_PATH = "classpath*:app.xml";
    /**
     * SPRING_BEAN_Factory
     */
    private static SpringBeanFactory factory = new SpringBeanFactory();
    /**
     * ctx
     */
    private static FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(SPRING_CONFIG_PATH);

    /**
     * private Conctrutor
     */
    private SpringBeanFactory() {
    }

    public static void init() {

    }

    /**
     * getInstance
     */
    public static synchronized SpringBeanFactory getInstance() {
        return factory;
    }

    /**
     * getBean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) ctx.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    public static Resource[] getResources(String resource) throws IOException {
        return ctx.getResources(resource);
    }

    public static <T> Map<String, T> getBeanByType(Class<T> clazz) {
        return ctx.getBeansOfType(clazz);
    }


    public static Object getBean(String beanName, Object... args) {
        return ctx.getBean(beanName, args);
    }

    public static <T> T getBean(String beanName, Class<T> clz) {
        return ctx.getBean(beanName, clz);
    }

    /**
     * 自动注入bean
     */
    public static void autowireBean(Object bean) {
        ctx.getBeanFactory().autowireBean(bean);
    }

    public static void removeBean(String beanName) {
        try {
            ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
            if (beanFactory instanceof DefaultListableBeanFactory) {
                ((DefaultListableBeanFactory) beanFactory).removeBeanDefinition(beanName);
            } else {
                log.warn(" unSupport remove Bean {}" + beanName);
            }
        } catch (Throwable e) {
            log.warn("", e);
        }
    }
}
