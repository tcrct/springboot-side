package com.springbootside.duang.common.annotation;

import java.lang.annotation.*;

/**
 * 事件注解
 *
 * @author Laotang
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Event {

	String name() default "";

}
