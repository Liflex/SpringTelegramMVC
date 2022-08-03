package org.arthur.telegram.bpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.arthur.telegram.TelegramPostProcessorConfiguration;
import org.arthur.telegram.bpp.annotation.BotController;
import org.arthur.telegram.bpp.annotation.BotRequestMapping;
import org.arthur.telegram.bpp.annotation.KeyBoard;
import org.arthur.telegram.bpp.annotation.RequestOrder;
import org.arthur.telegram.bpp.container.BotApiMethodContainer;
import org.arthur.telegram.bpp.wrapper.BotApiMethodController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class TelegramUpdateHandlerBeanPostProcessor implements BeanPostProcessor, Ordered {

    @Autowired
    private BotApiMethodContainer container;
    private Map<String, Class> botControllerMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(BotController.class))
            botControllerMap.put(beanName, beanClass);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(!botControllerMap.containsKey(beanName)) return bean;
        Object original = botControllerMap.get(beanName);
        Method[] methods = ((Class) original).getMethods();
        TreeMap<Integer, Method> sortedMethods = new TreeMap<>();
        for (Method method : methods) {
            if(method.isAnnotationPresent(BotRequestMapping.class)) {
                if(method.isAnnotationPresent(RequestOrder.class)) {
                    sortedMethods.put(method.getAnnotation(RequestOrder.class).value(), method);
                } else {
                    generateController(bean, method);
                }
            }
        }
        initializationWithRequestOrder(bean, sortedMethods);
        return bean;
    }

    private void initializationWithRequestOrder(Object bean, TreeMap<Integer, Method> sortedMethods) {
        BotApiMethodController current;
        BotRequestMapping botRequestMapping;
        BotApiMethodController prev = null;
        BotApiMethodController first = null;
        for (Method method: sortedMethods.values()) {
            botRequestMapping = method.getAnnotation(BotRequestMapping.class);
            current = generateController(bean, method, first, botRequestMapping.value(), true);
            if(first == null) {
                first = current;
            }
            if (prev != null) {
                current.setPrev(prev);
                prev.setNext(current);
            }
            prev = current;
        }
    }

    private void generateController(Object bean, Method method) {
        BotController botController = bean.getClass().getAnnotation(BotController.class);
        BotRequestMapping botRequestMapping = method.getAnnotation(BotRequestMapping.class);
        KeyBoard keyBoard = method.getAnnotation(KeyBoard.class);

        botController.value();
        String path = botController.value() + botRequestMapping.value();

        BotApiMethodController controller = null;

        switch (botRequestMapping.method()[0]){
            case MSG:
                controller = createControllerUpdate2ApiMethod(bean, method, botRequestMapping.value());
                break;
            case CALLBACK:
                controller = createProcessListForController(bean, method, botRequestMapping.value());
                break;
            default:
                break;
        }

        if(keyBoard != null) {
            controller.setKeyboardBean(TelegramPostProcessorConfiguration.keyboardsBean.get(keyBoard.value()));
            controller.setKeyboard(TelegramPostProcessorConfiguration.keyboardsMethod.get(keyBoard.value()));
        }

        container.addBotController(path, controller);
    }

    private BotApiMethodController generateController(Object bean, Method method, BotApiMethodController firstMethod, String currentState, boolean isOrdered) {
        BotController botController = bean.getClass().getAnnotation(BotController.class);
        BotRequestMapping botRequestMapping = method.getAnnotation(BotRequestMapping.class);
        KeyBoard keyBoard = method.getAnnotation(KeyBoard.class);

        String path = botController.value() + botRequestMapping.value();

        BotApiMethodController controller = null;

        switch (botRequestMapping.method()[0]){
            case MSG:
                controller = createControllerUpdate2ApiMethod(bean, method, currentState, isOrdered);
                break;
            case CALLBACK:
                controller = createProcessListForController(bean, method, currentState, isOrdered);
                break;
            default:
                break;
        }
        if(firstMethod == null) {
            container.addBotController(path, controller);
        }
        if(keyBoard != null) {
            controller.setKeyboardBean(TelegramPostProcessorConfiguration.keyboardsBean.get(keyBoard.value()));
            controller.setKeyboard(TelegramPostProcessorConfiguration.keyboardsMethod.get(keyBoard.value()));
        }
        return controller;
    }

    private BotApiMethodController createControllerUpdate2ApiMethod(Object bean, Method method, String currentState){
        return new BotApiMethodController(bean, method, currentState) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update != null && update.hasMessage() && update.getMessage().hasText();
            }
        };
    }

    private BotApiMethodController createControllerUpdate2ApiMethod(Object bean,
                                                                    Method method,
                                                                    String currentState,
                                                                    boolean isOrdered){
        return new BotApiMethodController(bean, method, currentState, isOrdered) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update != null && update.hasMessage() && update.getMessage().hasText();
            }
        };
    }

    private BotApiMethodController createProcessListForController(Object bean, Method method, String currentState){
        return new BotApiMethodController(bean, method, currentState) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update !=null && update.hasCallbackQuery() && update.getCallbackQuery().getData() != null
                        || update !=null && update.getMessage() != null && update.getMessage().getContact() != null
                        || update !=null && update.getMessage() != null && update.getMessage().getLocation() != null
                        || update != null && update.getMessage() != null && update.getMessage().getText() != null;
            }
        };
    }

    private BotApiMethodController createProcessListForController(Object bean,
                                                                  Method method,
                                                                  String currentState,
                                                                  boolean isOrdered){
        return new BotApiMethodController(bean, method, currentState, true) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update!=null && update.hasCallbackQuery() && update.getCallbackQuery().getData() != null
                        || update !=null && update.getMessage() != null && update.getMessage().getContact() != null
                        || update !=null && update.getMessage() != null && update.getMessage().getLocation() != null;
            }
        };
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
