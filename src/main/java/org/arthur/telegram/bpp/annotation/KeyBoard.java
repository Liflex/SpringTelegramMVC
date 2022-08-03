package org.arthur.telegram.bpp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Связывает метод {@link BotRequestMapping} и метод помеченый этой аннотацией
 * Указывая какую клавиатуру необходимо выказать, не является stateful для пользователя.
 * Впрочем не мешает и иное действие производить после выполнения контроллера.
 * Отсылается пользователю сразу же после отсылки ответа по основному контроллеру.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyBoard {
    /**
     * Ключ по которому идет связка
     */
    String value();
}
