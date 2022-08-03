package org.arthur.telegram.bfpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;

public class UserScopeBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Bean
    public UserScope userScope(ConfigurableListableBeanFactory beanFactory) {
        return new UserScope(beanFactory);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope(UserScope.SCOPE, new UserScope(beanFactory));
    }
}