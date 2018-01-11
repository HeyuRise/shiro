package com.pcbwx.shiro.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
/**
 * 自定义返回JSON 数据格中日期格式化处理
 * @author 王海龙
 * @version 1.0
 *
 */
public class CustomDateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat(ConstantVar.DATE_TIMESTAMP);
		String formattedDate = formatter.format(value);
		jgen.writeString(formattedDate);
		
	}

}
