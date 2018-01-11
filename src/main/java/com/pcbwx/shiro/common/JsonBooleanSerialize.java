/**
 * 
 */
package com.pcbwx.shiro.common;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * 用于Boolean字符串反序列化到Byte对象
 * 
 * @author 王海龙
 *
 */
public class JsonBooleanSerialize extends JsonSerializer<Byte> {

	@Override
	public void serialize(Byte value, JsonGenerator gen, SerializerProvider provider)
			throws JsonGenerationException, IOException {
		Boolean result = (value != null && value == 1) ? true : false;
		gen.writeBoolean(result);
	}
}
