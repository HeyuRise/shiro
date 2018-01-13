package com.pcbwx.shiro.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;


/**
 * json工具类
 * 
 * @author 王海龙
 * @version 1.0
 */
public class JsonUtil {

	private static JsonUtil ju;
	private static JsonFactory jf;
	private static ObjectMapper mapper;
	private static final Logger logger = LogManager.getLogger(JsonUtil.class);

	private JsonUtil() {
	}

	public static JsonUtil getInstance() {
		if (ju == null)
			ju = new JsonUtil();
		return ju;
	}

	public static ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);  
		}
		return mapper;
	}

	public static JsonFactory getFactory() {
		if (jf == null)
			jf = new JsonFactory();
		return jf;
	}
	
	/**
	 * 对象转json
	 * @param obj
	 * @return
	 */
	public static String obj2json(Object obj) {
		JsonGenerator jg = null;
		try {
			jf = getFactory();
			mapper = getMapper();
			StringWriter out = new StringWriter();
			jg = jf.createGenerator(out);
			mapper.writeValue(jg, obj);
			return out.toString();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (jg != null){
					jg.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * json转对象
	 * @param json
	 * @param clz
	 * @return
	 */
	public static Object json2obj(String json, Class<?> clz) {
		try {
			mapper = getMapper();
			return mapper.readValue(json, clz);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} 
		return null;
	}
	
	public static <T> List<T> json2array(String json, Class<?> clz) {
		try {
			mapper = getMapper();
			ArrayType arrayType = mapper.getTypeFactory().constructArrayType(clz);
			T[] datas = mapper.readValue(json, arrayType);
			List<T> arrayList = new ArrayList<T>(Arrays.asList(datas));
			return arrayList;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} 
		return null;
	}

	public static void main(String arg[]) {
		byte[] data = FileUtil.readFile("d:/new.txt");
		String dataStr = new String(data);
		System.out.println(dataStr);

//		GetTrainTimeTableResp timeTable = (GetTrainTimeTableResp) JsonUtil.json2obj(dataStr,
//				GetTrainTimeTableResp.class);
//		if (timeTable == null) {
//			System.out.print("timeTable is null");
//			return;
//		}
//		System.out.print("code=" + timeTable.getErrorCode() + ",reason=" + timeTable.getReason());
	}
}
