package com.rpa.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**

 /**
 * <p>
 *  spring上下文
 * </p>
 *
 * @author LMX
 * @date 2022/12/02/14:59
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
