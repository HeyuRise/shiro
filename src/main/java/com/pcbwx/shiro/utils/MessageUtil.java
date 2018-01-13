package com.pcbwx.shiro.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageUtil {

	private static final Logger logger = LogManager.getLogger(MessageUtil.class);

	/**
	 * 发送微信通知
	 * 
	 * @param auth
	 * @param flag
	 */
//	public static String sendServiceTextMessage(String openid, String content, String token) {
//		JSONObject json = new JSONObject();
//		JSONObject text = new JSONObject();
//		text.put("content", content);
//		json.put("touser", openid);
//		json.put("msgtype", "text");
//		json.put("text", text);
////		resp = json.toJSONString();
//		JSONObject resp = WechatUtil.sendMessage(token, json.toJSONString());
//		if (resp == null) {
//			return "";
//		}
//		return json.getString("errcode");
//	}
}
