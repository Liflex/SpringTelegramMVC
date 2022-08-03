package org.arthur.telegram.bpp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Наличие данной аннотации сообщает что она являются частью процедуры цепочки состояний
 * Порядок исполнения которой описано в параметре {@link #value()}
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestOrder {
    /**
     * возвращает порядковый номер в очереди
     * исполнения процедуры (от меньшего к большему)
     * @return {@link Integer}
     */
    int value();
}
