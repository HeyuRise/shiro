/**
 * 
 */
package com.pcbwx.shiro.common;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 用于jason字符串序列化
 * 
 * @author 王海龙
 *
 */
public class JsonObjectMapper extends ObjectMapper {

	 public JsonObjectMapper(){  
	        super();  
	        //设置null转换""  
	        getSerializerProvider().setNullValueSerializer(new NullSerializer());  
//	        //设置日期转换yyyy-MM-dd HH:mm:ss  
//	        setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));  
	    }  
	      
	    //null的JSON序列  
	    private class NullSerializer extends JsonSerializer<Object> {  
	        public void serialize(Object value, JsonGenerator jgen,  
	                SerializerProvider provider) throws IOException,  
	                JsonProcessingException {  
	            jgen.writeString("");  
	        }  
	    }
}
