package com.pcbwx.shiro.bean.request;

import com.pcbwx.shiro.common.ConfigProperties;

public class RouteSearch {
	/*
	 * 1：根据顺丰运单号查询
	 * 2：根据客户订单号查询
	 * tracking_type
	 */
	private Integer trackingType;
	/*
	 * 查询号
	 * 如果 tracking_type=1，则此值为顺丰运单号
	 * 如果 tracking_type=2，则此值为客户订单号
	 * 如果有多个单号，以逗号分隔，如”123,124,125”。
	 * tracking_number
	 */
	private String trackingNumber;
	
	/*路由查询类别：
	 * 1：标准路由查询, 2：定制路由查询
	 * method_type
	 */
	private Integer methodType;

	public RouteSearch(Integer trackingType, String trackingNumber, Integer methodType) {
		super();
		this.trackingType = trackingType;
		this.trackingNumber = trackingNumber;
		this.methodType = methodType;
	}
	
	public String toXML(){
		StringBuilder sb = new StringBuilder();
        sb.append("<Request service='RouteService' lang='zh-CN'>");
        sb.append("<Head>").append(ConfigProperties.getApiCode()).append("</Head>");
        sb.append("<Body>");
        sb.append("<RouteRequest ");
        sb.append(" tracking_type='").append(trackingType).append("' ");
        sb.append(" tracking_number='").append(trackingNumber).append("' ");
        if (methodType != null) {
        	  sb.append(" method_type='").append(methodType).append("' ");
		}
        sb.append(">");
        sb.append("</RouteRequest>");
        sb.append("</Body>");
        sb.append("</Request>");
		return sb.toString();
	}

	public Integer getTrackingType() {
		return trackingType;
	}

	public void setTrackingType(Integer trackingType) {
		this.trackingType = trackingType;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public Integer getMethodType() {
		return methodType;
	}

	public void setMethodType(Integer methodType) {
		this.methodType = methodType;
	}
}
