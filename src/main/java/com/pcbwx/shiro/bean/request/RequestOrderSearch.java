package com.pcbwx.shiro.bean.request;

import com.pcbwx.shiro.common.ConfigProperties;

public class RequestOrderSearch {
	/*
	 * 客户订单号
	 * orderid
	 */
	private String orderId;
	public RequestOrderSearch(String orderId){
		this.orderId = orderId;
	}
	public String toXml(){
		StringBuilder sb = new StringBuilder();
        sb.append("<Request service='OrderSearchService' lang='zh-CN'>");
        sb.append("<Head>").append(ConfigProperties.getApiCode()).append("</Head>");
        sb.append("<Body>");
        sb.append("<OrderSearch ");
        sb.append(" orderid='").append(orderId).append("' ");
        sb.append("/>");
        sb.append("</Body>");
        sb.append("</Request>");
		return sb.toString();
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	
}
