package com.pcbwx.shiro.bean.request;

import com.pcbwx.shiro.common.ConfigProperties;

public class RequestOrderZD {
	/*
	 * 客户订单号
	 * orderid
	 */
	private String orderId;
	/*
	 * 新增加的包裹数，最大 20
	 * parcel_quantity
	 */
	private Integer parcelQuantity;
	public RequestOrderZD(String orderId, Integer parcelQuantity){
		this.orderId = orderId;
		this.parcelQuantity = parcelQuantity;
	}
	public String toXml(){
		StringBuilder sb = new StringBuilder();
        sb.append("<Request service='OrderZDService' lang='zh-CN'>");
        sb.append("<Head>").append(ConfigProperties.getApiCode()).append("</Head>");
        sb.append("<Body>");
        sb.append("<OrderZD ");
        sb.append(" orderid='").append(orderId).append("' ");
        sb.append(" parcel_quantity='").append(parcelQuantity).append("' ");
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
	public Integer getParcelQuantity() {
		return parcelQuantity;
	}
	public void setParcelQuantity(Integer parcelQuantity) {
		this.parcelQuantity = parcelQuantity;
	}
}
