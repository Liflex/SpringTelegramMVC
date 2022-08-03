package org.arthur.telegram.bfpp;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserScopeConfig {

    @Bean
    public static BeanFactoryPostProcessor getBeanFactoryPostProcessor() {
        return new UserScopeBeanFactoryPostProcessor();
    }

}
