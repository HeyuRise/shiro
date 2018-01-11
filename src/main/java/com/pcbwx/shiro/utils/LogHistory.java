package com.pcbwx.shiro.utils;


import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LogHistory {
	
	private static ObjectMapper objectMapper;
	
	static {
		objectMapper = new ObjectMapper();
	}

	public static <T> String beanToJson(T bean) {
		try {
			return objectMapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static String upload(CloseableHttpClient httpClient, Map<String, Object> map, String ip) {
//		CloseableHttpClient httpClient = HttpClients.createDefault();  
		HttpPost httpPost = new HttpPost(ip);
		String str = LogHistory.beanToJson(map);
		
		StringEntity entity = new StringEntity(str.toString(),"utf-8");//解决中文乱码问题    
		entity.setContentEncoding("UTF-8");    
		entity.setContentType("application/json");    
		httpPost.setEntity(entity);    
		
		String result = "";
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if(httpResponse.getStatusLine().getStatusCode() == 200)
			{
				HttpEntity httpEntity = httpResponse.getEntity();  
				result = EntityUtils.toString(httpEntity);//取出应答字符串  
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 0:代表接口正常 1:接口参数不存在，需要添加 2:接口参数传输类型不对 3:接口内部异常 陈浩2394(陈浩2394)
		return result;
	}

}
