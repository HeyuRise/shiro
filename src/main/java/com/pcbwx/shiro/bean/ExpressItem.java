package com.pcbwx.shiro.bean;

import java.util.List;

public class ExpressItem {

	private String mailno;
	private List<String> mailnoChild;
	private String receiver;
	private String receiveDate;
	private String status;
	private Integer printCount;
	public String getMailno() {
		return mailno;
	}
	public void setMailno(String mailno) {
		this.mailno = mailno;
	}
	public List<String> getMailnoChild() {
		return mailnoChild;
	}
	public void setMailnoChild(List<String> mailnoChild) {
		this.mailnoChild = mailnoChild;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
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
	public Integer getPrintCount() {
		return printCount;
	}
	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}
	
}
