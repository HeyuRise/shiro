package com.pcbwx.shiro.bean;

public class QRCodeInfo {
	private String orderId;
	private String cargoName;
	private QRSenderInfo senderInfo;
	private QRReceiveInfo receiveInfo;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCargoName() {
		return cargoName;
	}
	public void setCargoName(String cargoName) {
		this.cargoName = cargoName;
	}
	public QRSenderInfo getSenderInfo() {
		return senderInfo;
	}
	public void setSenderInfo(QRSenderInfo senderInfo) {
		this.senderInfo = senderInfo;
	}
	public QRReceiveInfo getReceiveInfo() {
		return receiveInfo;
	}
	public void setReceiveInfo(QRReceiveInfo receiveInfo) {
		this.receiveInfo = receiveInfo;
	}
}
