package com.pcbwx.shiro.bean.response;

import java.math.BigDecimal;

public class QueryWayBill {
	/*
	 * 订单号
	 */
	private String orderId;
	/*
	 * 运单号
	 */
	private String mailno;
	/*
	 * 月结卡号
	 */
	private String custId;
	/*
	 * 计费重量
	 */
	private BigDecimal weight;
	/*
	 * 实效类型
	 */
	private String limitType;
	/*
	 * 运费
	 */
	private BigDecimal expressCost;
	/*
	 * 保价
	 */
	private BigDecimal insure;
	/*
	 *包装费 
	 */
	private BigDecimal pkfee;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getMailno() {
		return mailno;
	}
	public void setMailno(String mailno) {
		this.mailno = mailno;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public String getLimitType() {
		return limitType;
	}
	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}
	public BigDecimal getExpressCost() {
		return expressCost;
	}
	public void setExpressCost(BigDecimal expressCost) {
		this.expressCost = expressCost;
	}
	public BigDecimal getInsure() {
		return insure;
	}
	public void setInsure(BigDecimal insure) {
		this.insure = insure;
	}
	public BigDecimal getPkfee() {
		return pkfee;
	}
	public void setPkfee(BigDecimal pkfee) {
		this.pkfee = pkfee;
	}
}
