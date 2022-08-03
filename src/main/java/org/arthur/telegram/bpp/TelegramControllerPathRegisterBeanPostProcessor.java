package org.arthur.telegram.bpp;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.arthur.telegram.TelegramPostProcessorConfiguration;
import org.arthur.telegram.bpp.annotation.BotRequestMapping;
import org.arthur.telegram.bpp.annotation.EnableTelegramMVC;
import org.arthur.telegram.bpp.annotation.TelegramControllerScan;

@Component
public class TelegramControllerPathRegisterBeanPostProcessor implements BeanPostProcessor, Ordered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(EnableTelegramMVC.class)){
            TelegramControllerScan annotation = beanClass.getAnnotation(TelegramControllerScan.class);
            mapAllPaths(annotation.value());
        }
        return bean;
    }

    private void mapAllPaths(String[] values) {
        for (String value : values) {
            new Reflections(value, new MethodAnnotationsScanner())
                    .getMethodsAnnotatedWith(BotRequestMapping.class)
                    .forEach(method -> TelegramPostProcessorConfiguration.paths.put(method.getAnnotation(BotRequestMapping.class).value(), method.getDeclaringClass()));
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
