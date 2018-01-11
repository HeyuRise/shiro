package com.pcbwx.shiro.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 读取配置文件工具类
 * @author 王海龙
 * @version 1.0
 *
 */
public class PropertyUtil {

	private static PropertyUtil util = null;
	private static Map<String,Properties> props = null;
	private static final Logger logger = Logger.getLogger(PropertyUtil.class);
	private PropertyUtil(){
		
	}
	
	public static PropertyUtil getInstance() {
		if(util==null) {
			props = new HashMap<String, Properties>();
			util = new PropertyUtil();
		}
		return util;
	}
	
	public Properties load(String name) {
		if(props.get(name)!=null) {
			return props.get(name);
		} else {
			Properties prop = new Properties();
			try {
				prop.load(PropertyUtil.class.getResourceAsStream("/"+name+".properties"));
				props.put(name, prop);
				return prop;
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return null;
	}
	
}


