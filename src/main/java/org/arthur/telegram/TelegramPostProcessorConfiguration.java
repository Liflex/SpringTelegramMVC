package org.arthur.telegram;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Log4j2
public class TelegramPostProcessorConfiguration {

    public static Map<String, Class<?>> paths = new HashMap<>();
    public static Map<String, Method> keyboardsMethod = new HashMap<>();
    public static Map<String, Object> keyboardsBean = new HashMap<>();
}
