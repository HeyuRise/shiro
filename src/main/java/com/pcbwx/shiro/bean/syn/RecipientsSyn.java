package com.pcbwx.shiro.bean.syn;

import java.util.List;

public class RecipientsSyn {
	private String code;
	private String name;
	private String mobile;
	private String company;
	private String sender_company;
	private String tel;
	private String update_time;
	private Integer enable;
	private List<RecipientsAddressSyn> address;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getSender_company() {
		return sender_company;
	}
	public void setSender_company(String sender_company) {
		this.sender_company = sender_company;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public List<RecipientsAddressSyn> getAddress() {
		return address;
	}
	public void setAddress(List<RecipientsAddressSyn> address) {
		this.address = address;
	}
}
