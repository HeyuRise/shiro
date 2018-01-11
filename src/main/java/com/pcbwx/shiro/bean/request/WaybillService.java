package com.pcbwx.shiro.bean.request;

import com.pcbwx.shiro.common.ConfigProperties;

/**
 * 查找运单运费、重量等信息
 * @author shy19940918
 *
 */
public class WaybillService {
	/*
	 * 请求类型
	 * 1、按订单号查找
	 * 2、按运单号查找
	 * 因为测试的时候按运单号查询不可用，
	 * 所以默认用订单号查询
	 * type
	 */
	private int type = 1;
	
	/*
	 * 运单号
	 *waybillNo 
	 */
	private String mailno;
	
	/*
	 * 订单号
	 * orderId
	 */
	private String orderId;
	
	public String toXml(){
        if (mailno == null) {
			mailno = "";
		}
        if (orderId == null) {
			orderId = "";
		}
		StringBuilder sb = new StringBuilder();
        sb.append("<Request service='QuerySFWaybillService' lang='zh-CN'>");
        sb.append("<Head>").append(ConfigProperties.getApiCode()).append("</Head>");
        sb.append("<Body>");
        sb.append("<Waybill ");
        sb.append(" type='").append(type).append("' ");
        sb.append(" waybillNo='").append(mailno).append("' ");
        sb.append(" orderId='").append(orderId).append("' ");
        sb.append("/>");
        sb.append("</Body>");
        sb.append("</Request>");
		return sb.toString();
	}

	public String getMailno() {
		return mailno;
	}

	public void setMailno(String mailno) {
		this.mailno = mailno;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
}
