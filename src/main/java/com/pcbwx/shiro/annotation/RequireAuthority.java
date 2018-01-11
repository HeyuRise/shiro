/**
 * 
 */
package com.pcbwx.shiro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pcbwx.shiro.enums.AuthorityEnum;


/**
 * 用于在Controller的方法上添加Annotation,指定Controller方法的需要权限
 * 
 * @author 王海龙
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuthority {

	/**
	 * @return 权限代码的数组
	 */
	AuthorityEnum[] value() default {};
	
}
