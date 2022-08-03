package org.arthur.telegram.bpp.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.arthur.telegram.bfpp.UserScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
@Scope(UserScope.SCOPE)
public @interface BotService {
}
