package com.pcbwx.shiro.bean.request;

import java.util.List;

import com.pcbwx.shiro.common.ConfigProperties;

public class Order {
	 /*
     * 订单号
     */
    public String orderId;
    /*
     * 寄件方公司名称
     * j_company String(100)
     */
    public String sendCompany;
    /*
     * 寄件方联系人
     * j_contact String(100)
     */
    public String sendContact;
    /*
     * 寄件方电话
     * j_tel String(20)
     */
    public String sendTel;
    /*
     *寄件方手机
     * j_mobile
     */
    public String sendMobile;
    /*
     * 寄件方省份
     * j_province
     */
    public String sendProvince;
    /*
     * 寄件方城市
     * j_city
     */
    public String sendCity;
    /*
     * 寄件方县区
     * j_county
     */
    public String sendCounty;
    /*
     * 寄件方详细地址
     * j_address String(200)
     */
    public String sendAddress;
    /*
     * 收件方公司名称
     * d_company String(100）
     */
    public String receiverCompany;
    /*
     * 收件方联系人
     * d_contact String(100)
     */
    public String receiverContact;
    /*
     * 收件方联系电话
     * d_tel String(20)
     */
    public String receiverTel;
    /*
     *收件方手机
     *d_mobile String(20)
     */
    public String receiverMobile;
    /*
     * 到件方所在省份,必须是标准的省名称称谓如:广东省,如果是直辖市, 请直接传北京、上海等。
     * d_province String(30)
     */
    public String receiverProvince;
    /*
     * 到件方所在城市名称,必须是标准的 城市称谓如:深圳市,如果是直辖市,请直接传北京(或北京市)、上海(或上海市)等。
     * d_city String(100)
     */
    public String receiverCity;
    /*
     * 收件方县区
     * d_county
     */
    public String receiverCounty;
    /*
     * 到件方详细地址
     * d_address 
     */
    public String receiverAddress;
    /*
     * 月结卡号
     * custid
     */
    public String custId;
    /*
     * 付款方式
     * 1 - 寄件方付
     * 2 - 收件方付
     * 3 - 第三方付
     * pay_method
     */
    public Integer payMethod;
    /*
     * 快件产品类别,默认1
     * 1: 标准快递
     * 2: 顺丰特惠
     * 5: 顺丰次晨
     * 6：即日件
     */
    public Integer expressType; 
    
    /*
     * 包裹数，默认1
     * parcel_quantity
     */
    public Integer parcelQuantity;
    /*
     * 发送货物信息
     */
    public Cargo cargo;
    
    public List<AddedService> addedServices;
    
    public Order() {
	}
    public Order(String orderId, String sendCompany, String sendContact,
			String sendTel, String sendMobile, String sendProvince,
			String sendCity, String sendCounty, String sendAddress,
			String receiverCompany, String receiverContact, String receiverTel,
			String receiverMobile, String receiverProvince,
			String receiverCity, String receiverCounty, String receiverAddress,
			String custId, Integer payMethod, Integer expressType,
			Integer parcelQuantity, Cargo cargo,
			List<AddedService> addedServices) {
		super();
		this.orderId = orderId;
		this.sendCompany = sendCompany;
		this.sendContact = sendContact;
		this.sendTel = sendTel;
		this.sendMobile = sendMobile;
		this.sendProvince = sendProvince;
		this.sendCity = sendCity;
		this.sendCounty = sendCounty;
		this.sendAddress = sendAddress;
		this.receiverCompany = receiverCompany;
		this.receiverContact = receiverContact;
		this.receiverTel = receiverTel;
		this.receiverMobile = receiverMobile;
		this.receiverProvince = receiverProvince;
		this.receiverCity = receiverCity;
		this.receiverCounty = receiverCounty;
		this.receiverAddress = receiverAddress;
		this.custId = custId;
		this.payMethod = payMethod;
		this.expressType = expressType;
		this.parcelQuantity = parcelQuantity;
		this.cargo = cargo;
		this.addedServices = addedServices;
	}

	public String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Request service='OrderService' lang='zh-CN'>");
        sb.append("<Head>").append(ConfigProperties.getApiCode()).append("</Head>");
        sb.append("<Body>");
        sb.append("<Order ");
        sb.append(" orderid='").append(orderId).append("' ");
        if (sendCompany != null && !sendCompany.equals("")) {
        	 sb.append(" j_company='").append(sendCompany).append("' ");
		}
        if (sendContact != null && !sendContact.equals("")) {
        	sb.append(" j_contact='").append(sendContact).append("' ");
		}
        if (sendTel != null && !sendTel.equals("")) {
        	sb.append(" j_tel='").append(sendTel).append("' ");
		}
        if (sendMobile != null && !sendMobile.equals("")) {
        	sb.append(" j_mobile='").append(sendMobile).append("' ");
		}
        if (sendProvince != null && !sendProvince.equals("")) {
        	sb.append(" j_province='").append(sendProvince).append("' ");
		}
        if (sendCity != null && !sendCity.equals("")) {
        	sb.append(" j_city='").append(sendCity).append("' ");
		}
        if (sendCounty != null && !sendCounty.equals("")) {
        	sb.append(" j_county='").append(sendCounty).append("' ");
		}
        if (sendAddress != null && !sendAddress.equals("")) {
        	sb.append(" j_address='").append(sendAddress).append("' ");
		}
        if (receiverCompany != null && !receiverCompany.equals("")) {
        	sb.append(" d_company='").append(receiverCompany).append("' ");
		}
        if (receiverContact != null && !receiverContact.equals("")) {
        	sb.append(" d_contact='").append(receiverContact).append("' ");
		}
        if (receiverTel != null && !receiverTel.equals("")) {
        	sb.append(" d_tel='").append(receiverTel).append("' ");
		}
        if (receiverMobile != null && !receiverMobile.equals("")) {
        	sb.append(" d_mobile='").append(receiverMobile).append("' ");
		}
        if (receiverProvince != null && !receiverProvince.equals("")) {
        	sb.append(" d_province='").append(receiverProvince).append("' ");
		}
        if (receiverCity != null && !receiverCity.equals("")){
        	sb.append(" d_city='").append(receiverCity).append("' ");
        }
        if (receiverCounty != null && !receiverCounty.equals("")) {
        	sb.append(" d_county='").append(receiverCounty).append("' ");
		}
        if (receiverAddress != null && !receiverAddress.equals("")) {
        	sb.append(" d_address='").append(receiverAddress).append("' ");
		}
        if (custId != null && !custId.equals("")) {
        	sb.append(" custid='").append(custId).append("' ");
		}
        if (payMethod != null) {
        	sb.append(" pay_method='").append(payMethod).append("' ");
		}
        if (expressType != null) {
        	sb.append(" express_type='").append(expressType).append("' ");
		}
        if (parcelQuantity != null) {
        	sb.append(" parcel_quantity='").append(parcelQuantity).append("' ");
		}
        sb.append(">");
        if (cargo != null) {
        	sb.append(cargo.toXml());
		}
        if (addedServices != null && !addedServices.isEmpty()) {
        	for (AddedService addedService : addedServices) {
    			String addXml = addedService.toXml();
    			if (addXml == null) {
    				continue;
    			}
    			sb.append(addXml);
    		}
		}
        sb.append("</Order>");
        sb.append("</Body>");
        sb.append("</Request>");
        return sb.toString();
    }
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
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
	public String getSendTel() {
		return sendTel;
	}
	public void setSendTel(String sendTel) {
		this.sendTel = sendTel;
	}
	public String getSendMobile() {
		return sendMobile;
	}
	public void setSendMobile(String sendMobile) {
		this.sendMobile = sendMobile;
	}
	public String getSendProvince() {
		return sendProvince;
	}
	public void setSendProvince(String sendProvince) {
		this.sendProvince = sendProvince;
	}
	public String getSendCity() {
		return sendCity;
	}
	public void setSendCity(String sendCity) {
		this.sendCity = sendCity;
	}
	public String getSendCounty() {
		return sendCounty;
	}
	public void setSendCounty(String sendCounty) {
		this.sendCounty = sendCounty;
	}
	public String getSendAddress() {
		return sendAddress;
	}
	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
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
	public String getReceiverTel() {
		return receiverTel;
	}
	public void setReceiverTel(String receiverTel) {
		this.receiverTel = receiverTel;
	}
	public String getReceiverMobile() {
		return receiverMobile;
	}
	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}
	public String getReceiverProvince() {
		return receiverProvince;
	}
	public void setReceiverProvince(String receiverProvince) {
		this.receiverProvince = receiverProvince;
	}
	public String getReceiverCity() {
		return receiverCity;
	}
	public void setReceiverCity(String receiverCity) {
		this.receiverCity = receiverCity;
	}
	public String getReceiverCounty() {
		return receiverCounty;
	}
	public void setReceiverCounty(String receiverCounty) {
		this.receiverCounty = receiverCounty;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}
	public Integer getExpressType() {
		return expressType;
	}
	public void setExpressType(Integer expressType) {
		this.expressType = expressType;
	}
	public Integer getParcelQuantity() {
		return parcelQuantity;
	}
	public void setParcelQuantity(Integer parcelQuantity) {
		this.parcelQuantity = parcelQuantity;
	}
	public Cargo getCargo() {
		return cargo;
	}
	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}
	public List<AddedService> getAddedServices() {
		return addedServices;
	}
	public void setAddedServices(List<AddedService> addedServices) {
		this.addedServices = addedServices;
	}
}
