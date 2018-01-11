package com.pcbwx.shiro.common;

import java.util.Date;
import java.util.List;

/**
 * ldap联系人结构节点
 * 
 * @author 王海龙
 *
 */
public class MailNotify {
	private Long meetingId;
	private String uuid;
	private List<String> to; 		// 邮件接收人
	private List<String> cc; 		// 抄送人
	private List<String> bcc; 		// 秘密接收人
	private Date begin;				// 会议开始时间
	private Date end;				// 会议结束时间
	private List<String> address;	// 会议地址
	private String subject; 		// 邮件主题
	private String content;			// 邮件内容
	
	public Long getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public List<String> getCc() {
		return cc;
	}
	public void setCc(List<String> cc) {
		this.cc = cc;
	}
	public List<String> getBcc() {
		return bcc;
	}
	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}
	public Date getBegin() {
		return begin;
	}
	public void setBegin(Date begin) {
		this.begin = begin;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public List<String> getAddress() {
		return address;
	}
	public void setAddress(List<String> address) {
		this.address = address;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
