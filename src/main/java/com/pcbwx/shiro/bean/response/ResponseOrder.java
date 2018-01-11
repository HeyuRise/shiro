package com.pcbwx.shiro.bean.response;

public class ResponseOrder {
	private Integer isOk;			//请求是否成功
	private String service;			//请求类型
	private String orderId;			//客户订单号
	private String mailno;			//顺丰运单号，一个订单只能有一个母单号，如果是子母单的情况，以半角逗号分隔，主单号在第一个位置
	private String origincode;		//原寄地区域代码
	private String destcode;		//目的地区域代码
	private Integer filterResult;	//筛单结果： 1：人工确认,2：可收派,3：不可以收派
	private String remark;			//如果 filter_result=3 时为必填，不可以收派的原因代码：1：收方超范围,2：派方超范围,3-：其它原因
	private String errCode;			//错误代码
	private String errMsg;			//错误消息
	public Integer getIsOk() {
		return isOk;
	}
	public void setIsOk(Integer isOk) {
		this.isOk = isOk;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
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
	public String getOrigincode() {
		return origincode;
	}
	public void setOrigincode(String origincode) {
		this.origincode = origincode;
	}
	public String getDestcode() {
		return destcode;
	}
	public void setDestcode(String destcode) {
		this.destcode = destcode;
	}
	public Integer getFilterResult() {
		return filterResult;
	}
	public void setFilterResult(Integer filterResult) {
		this.filterResult = filterResult;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
}
