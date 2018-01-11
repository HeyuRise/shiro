package com.pcbwx.shiro.bean.syn;

import java.util.List;

public class ContactSynItem {
	private RecipientsSyn recipients;
	private SenderSyn sender;
	private List<SenderAddressSyn> sender_address;
	public RecipientsSyn getRecipients() {
		return recipients;
	}
	public void setRecipients(RecipientsSyn recipients) {
		this.recipients = recipients;
	}
	public SenderSyn getSender() {
		return sender;
	}
	public void setSender(SenderSyn sender) {
		this.sender = sender;
	}
	public List<SenderAddressSyn> getSender_address() {
		return sender_address;
	}
	public void setSender_address(List<SenderAddressSyn> sender_address) {
		this.sender_address = sender_address;
	}
}
