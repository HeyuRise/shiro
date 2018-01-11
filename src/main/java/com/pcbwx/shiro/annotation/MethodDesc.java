/**
 * 
 */
package com.pcbwx.shiro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法功能描述Annotation，标注于Controller方法上
 * 
 * @author 王海龙
 *
 */
@Deprecated
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodDesc {

	/** 
	 * @return 中文描述
	 */
	String cn() default "";

	/**
	 * 
	 * @return 英文描述
	 */
	String en() default "";
}
