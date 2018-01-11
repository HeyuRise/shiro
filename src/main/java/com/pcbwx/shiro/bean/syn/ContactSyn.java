package com.pcbwx.shiro.bean.syn;

import java.util.Date;

public class ContactSyn {
	private Date date;
	private ContactSynItem contactSynItem;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ContactSynItem getContactSynItem() {
		return contactSynItem;
	}
	public void setContactSynItem(ContactSynItem contactSynItem) {
		this.contactSynItem = contactSynItem;
	}
}
