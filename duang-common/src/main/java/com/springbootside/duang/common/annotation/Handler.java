package com.springbootside.duang.common.annotation;

import java.lang.annotation.*;

/**
 * 处理器注解
 *
 * @author Laotang
 * @since 1.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handler {

	/**排序，数据越少越靠前，作用类似List下标*/
	int sort() default 0;

}
