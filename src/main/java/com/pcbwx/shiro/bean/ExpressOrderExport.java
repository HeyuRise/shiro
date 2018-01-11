package com.pcbwx.shiro.bean;

import java.util.Date;

import com.pcbwx.shiro.model.Express;
import com.pcbwx.shiro.utils.DateTimeUtil;

public class ExpressOrderExport {
	private String orderId;
	private String mailno;
	private String mailnoChild;
	private String sendDate;
	private String status;
	private String receiveDate;
	private String receiveAddress;
	private String receiveTel;
	private String receiveContact;
	private String receiveCompany;
	private String receiveMobile;
	private String cargoName;
	private String cargoCount;
	private String sendCompany;
	private String sendAddress;
	private String sendTel;
	private String sendContact;
	private String sendCity;
	private String receiveCity;
	private String payWeight;
	private String realWeight;
	private String money;
	private String note;
	private String expressType;
	private String payType;
	private Integer parcelQuantity;
	private String addServices;
	public ExpressOrderExport(Express express) {
		super();
		String receiveCity = express.getRecipientsCity();
		this.orderId = express.getOrderId();
		this.mailno = express.getMailNo();
		this.mailnoChild = express.getMailNoChild();
		Date senDate = express.getCreateTime();
		this.sendDate = DateTimeUtil.date2dateStr(senDate);
		this.receiveAddress = (express.getRecipientsProvince() == null ? "" : express.getRecipientsProvince()) 
				+ (receiveCity == null ? "" : receiveCity)
				+ (express.getRecipientsCounty() == null ? "" : express.getRecipientsCounty())
				+ (express.getAddress() == null ? "" : express.getAddress());
		this.receiveTel = express.getRecipientsTel();
		this.receiveContact = express.getRecipientsName();
		this.receiveCompany = express.getRecipientsCompany();
		this.receiveMobile = express.getRecipientsMobile();
		this.sendCompany = express.getSenderCompany();
		this.sendAddress = (express.getSenderProvince() == null ? "" : express.getSenderProvince())
				+ (express.getSenderCity() == null ? "" : express.getSenderCity())
				+ (express.getSenderCounty() == null ? "" : express.getSenderCounty())
				+ (express.getSenderAddress() == null ? "" : express.getSenderAddress());
		this.sendTel = express.getSenderTel();
		this.sendContact = express.getSenderName();
		this.sendCity = express.getSenderCity();
		this.receiveCity = receiveCity;
		this.money = express.getMoney();
		this.payType = express.getPayTypeName();
		this.parcelQuantity = express.getParcelNumber();
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
	public String getMailnoChild() {
		return mailnoChild;
	}
	public void setMailnoChild(String mailnoChild) {
		this.mailnoChild = mailnoChild;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getReceiveAddress() {
		return receiveAddress;
	}
	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}
	public String getReceiveTel() {
		return receiveTel;
	}
	public void setReceiveTel(String receiveTel) {
		this.receiveTel = receiveTel;
	}
	public String getReceiveContact() {
		return receiveContact;
	}
	public void setReceiveContact(String receiveContact) {
		this.receiveContact = receiveContact;
	}
	public String getReceiveCompany() {
		return receiveCompany;
	}
	public void setReceiveCompany(String receiveCompany) {
		this.receiveCompany = receiveCompany;
	}
	public String getReceiveMobile() {
		return receiveMobile;
	}
	public void setReceiveMobile(String receiveMobile) {
		this.receiveMobile = receiveMobile;
	}
	public String getCargoName() {
		return cargoName;
	}
	public void setCargoName(String cargoName) {
		this.cargoName = cargoName;
	}
	public String getCargoCount() {
		return cargoCount;
	}
	public void setCargoCount(String cargoCount) {
		this.cargoCount = cargoCount;
	}
	public String getSendCompany() {
		return sendCompany;
	}
	public void setSendCompany(String sendCompany) {
		this.sendCompany = sendCompany;
	}
	public String getSendAddress() {
		return sendAddress;
	}
	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}
	public String getSendTel() {
		return sendTel;
	}
	public void setSendTel(String sendTel) {
		this.sendTel = sendTel;
	}
	public String getSendContact() {
		return sendContact;
	}
	public void setSendContact(String sendContact) {
		this.sendContact = sendContact;
	}
	public String getSendCity() {
		return sendCity;
	}
	public void setSendCity(String sendCity) {
		this.sendCity = sendCity;
	}
	public String getReceiveCity() {
		return receiveCity;
	}
	public void setReceiveCity(String receiveCity) {
		this.receiveCity = receiveCity;
	}
	public String getPayWeight() {
		return payWeight;
	}
	public void setPayWeight(String payWeight) {
		this.payWeight = payWeight;
	}
	public String getRealWeight() {
		return realWeight;
	}
	public void setRealWeight(String realWeight) {
		this.realWeight = realWeight;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getExpressType() {
		return expressType;
	}
	public void setExpressType(String expressType) {
		this.expressType = expressType;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public Integer getParcelQuantity() {
		return parcelQuantity;
	}
	public void setParcelQuantity(Integer parcelQuantity) {
		this.parcelQuantity = parcelQuantity;
	}
	public String getAddServices() {
		return addServices;
	}
	public void setAddServices(String addServices) {
		this.addServices = addServices;
	}

}
