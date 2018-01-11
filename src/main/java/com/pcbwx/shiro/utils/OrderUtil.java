package com.pcbwx.shiro.utils;

import org.apache.log4j.Logger;

/**
 * 公共通用util工具包
 * 
 * @author 王海龙
 * @version 1.0
 *
 */
public class OrderUtil {

	private static final Logger logger = Logger.getLogger(OrderUtil.class);
	
	public static boolean isFinished(String orderState) {
		if (orderState.equals("设计结算") || orderState.equals("设计投产")) {
			return true;
		}
		if (orderState.equals("PCB设计完成")) {
			return true;
		}
		return false;
	}
}
