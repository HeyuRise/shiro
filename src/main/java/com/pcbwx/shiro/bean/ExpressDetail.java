package com.pcbwx.shiro.bean;

import java.util.List;

public class ExpressDetail {
	private String destCode;
	private String expressType;
	private String custId;
	private List<String> mailnos;
	private ExpressItem express;
	private SenderItem sender;
	private ReceiveItem receiver;
	private PayInfo payInfo;
	private PackageInfo packageInfo;
	private List<ServiceItem> serviceInfos;
	public String getDestCode() {
		return destCode;
	}
	public void setDestCode(String destCode) {
		this.destCode = destCode;
	}
	public String getExpressType() {
		return expressType;
	}
	public void setExpressType(String expressType) {
		this.expressType = expressType;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public List<String> getMailnos() {
		return mailnos;
	}
	public void setMailnos(List<String> mailnos) {
		this.mailnos = mailnos;
	}
	public ExpressItem getExpress() {
		return express;
	}
	public void setExpress(ExpressItem express) {
		this.express = express;
	}
	public SenderItem getSender() {
		return sender;
	}
	public void setSender(SenderItem sender) {
		this.sender = sender;
	}
	public ReceiveItem getReceiver() {
		return receiver;
	}
	public void setReceiver(ReceiveItem receiver) {
		this.receiver = receiver;
	}
	public PayInfo getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(PayInfo payInfo) {
		this.payInfo = payInfo;
	}
	public PackageInfo getPackageInfo() {
		return packageInfo;
	}
	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}
	public List<ServiceItem> getServiceInfos() {
		return serviceInfos;
	}
	public void setServiceInfos(List<ServiceItem> serviceInfos) {
		this.serviceInfos = serviceInfos;
	}
}
