package com.pcbwx.shiro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pcbwx.shiro.map.ChannelDeserialize;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldMap {

	public String value() default "";
	
    public Class<? extends ChannelDeserialize<?>> using()
        default ChannelDeserialize.None.class;
}
