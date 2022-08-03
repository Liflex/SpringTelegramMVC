package org.arthur.telegram;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.arthur.telegram")
@EnableConfigurationProperties(TelegramConfigurationProperties.class)
public class BotConfiguration {

}
