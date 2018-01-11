package com.pcbwx.shiro.bean;

import com.pcbwx.shiro.model.Express;

public class ExpressOrder {
	private String orderId;
	private String mailno;
	private Integer parcelQuantity;
	private String expressType;
	private String receiveDate;
	private String status;
	private String sendDate;
	private String receiverCompany;
	private String receiverContact;
	private String sendCompany;
	private String sendContact;
	private Integer printCount;

	private String mailnoChild;
	private String receiverTel;
	private String receiverAddress;
	private String sendTel;

	public ExpressOrder(Express express) {
		super();
		this.orderId = express.getOrderId();
		this.mailno = express.getMailNo();
		this.parcelQuantity = express.getParcelNumber();
		this.receiverCompany = express.getRecipientsCompany();
		this.receiverContact = express.getRecipientsName();
		this.sendCompany = express.getSenderCompany();
		this.sendContact = express.getSenderName();
		this.printCount = express.getCount();

		this.mailnoChild = express.getMailNoChild();
		this.receiverTel = express.getRecipientsTel();
		this.receiverAddress = (express.getRecipientsProvince() == null ? "" : express.getRecipientsProvince()) 
						+ (express.getRecipientsCity() == null ? "" : express.getRecipientsCity())
						+ (express.getRecipientsCounty() == null ? "" : express.getRecipientsCounty())
						+ (express.getAddress() == null ? "" : express.getAddress());
		this.sendTel = express.getSenderTel();
	}

	
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

	public Integer getParcelQuantity() {
		return parcelQuantity;
	}

	public void setParcelQuantity(Integer parcelQuantity) {
		this.parcelQuantity = parcelQuantity;
	}

	public String getExpressType() {
		return expressType;
	}

	public void setExpressType(String expressType) {
		this.expressType = expressType;
	}

	public String getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getReceiverCompany() {
		return receiverCompany;
	}

	public void setReceiverCompany(String receiverCompany) {
		this.receiverCompany = receiverCompany;
	}

	public String getReceiverContact() {
		return receiverContact;
	}

	public void setReceiverContact(String receiverContact) {
		this.receiverContact = receiverContact;
	}

	public String getSendCompany() {
		return sendCompany;
	}

	public void setSendCompany(String sendCompany) {
		this.sendCompany = sendCompany;
	}

	public String getSendContact() {
		return sendContact;
	}

	public void setSendContact(String sendContact) {
		this.sendContact = sendContact;
	}

	public Integer getPrintCount() {
		return printCount;
	}

	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}

	public String getMailnoChild() {
		return mailnoChild;
	}

	public void setMailnoChild(String mailnoChild) {
		this.mailnoChild = mailnoChild;
	}

	public String getReceiverTel() {
		return receiverTel;
	}

	public void setReceiverTel(String receiverTel) {
		this.receiverTel = receiverTel;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getSendTel() {
		return sendTel;
	}

	public void setSendTel(String sendTel) {
		this.sendTel = sendTel;
	}
}
