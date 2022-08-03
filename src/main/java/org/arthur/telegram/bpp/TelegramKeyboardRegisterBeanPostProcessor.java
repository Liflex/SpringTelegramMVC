package org.arthur.telegram.bpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.arthur.telegram.TelegramPostProcessorConfiguration;
import org.arthur.telegram.bpp.annotation.KeyBoard;
import org.arthur.telegram.bpp.annotation.KeyBoardMap;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class TelegramKeyboardRegisterBeanPostProcessor implements BeanPostProcessor, Ordered {

    private final Map<String, Class<?>> botControllerMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(KeyBoardMap.class))
            botControllerMap.put(beanName, beanClass);
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(!botControllerMap.containsKey(beanName)) return bean;
        Object original = botControllerMap.get(beanName);
        Method[] methods = ((Class<?>) original).getMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(KeyBoard.class)) {
                KeyBoard keyBoard  = method.getAnnotation(KeyBoard.class);
                TelegramPostProcessorConfiguration.keyboardsMethod.put(keyBoard.value(), method);
                TelegramPostProcessorConfiguration.keyboardsBean.put(keyBoard.value(), bean);
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return -99;
    }
}
