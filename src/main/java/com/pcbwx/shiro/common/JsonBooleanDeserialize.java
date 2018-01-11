package com.pcbwx.shiro.common;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

/**
 * 用于Boolean字符串反序列化到Byte对象
 * 
 * @author 王海龙
 *
 */
public class JsonBooleanDeserialize extends JsonDeserializer<Byte> {

	@Override
	public Byte deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		int value = jp.getBooleanValue() == true ? 1 : 0;
		return (byte) value;
	}
}
