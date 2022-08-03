package org.arthur.telegram.bpp.annotation;

import org.arthur.telegram.bfpp.UserScope;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope(UserScope.SCOPE)
public @interface BotController {
    String value() default "";
}
