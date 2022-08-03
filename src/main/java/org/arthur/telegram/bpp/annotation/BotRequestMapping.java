package org.arthur.telegram.bpp.annotation;

import org.arthur.telegram.bpp.BotRequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BotRequestMapping {
    String value();

    /**
     * В случае использования {@link BotRequestMethod#CALLBACK} не забывайте указывать
     * информацию в callback data через _,
     * например /store_id32_get, где /store является вашим callback контроллером.
     */
    BotRequestMethod[] method() default {BotRequestMethod.MSG};
}
