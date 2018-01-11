package com.pcbwx.shiro.service;

import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pcbwx.shiro.bean.contact.Recipient;
import com.pcbwx.shiro.bean.contact.SenderAddressBean;
import com.pcbwx.shiro.bean.contact.SenderBean;

/**
 * 联系人模块接口
 * 
 * @author 孙贺宇
 *
 */
public interface ContactService {
	
	/**
	 * 获取收件人信息列表
	 * @param company
	 * @param contact
	 * @param address
	 * @return
	 */
	List<Recipient> getRecipients(String company, String contact, String province, String city, String county, String address);
	
	SXSSFWorkbook getRecipientsExport(String company, String contact, String province, String city, String county, String address);
	
	/**
	 * 获取寄件人信息列表
	 * @param contact
	 * @return
	 */
	List<SenderBean> getSenderBeans(String contact);
	
	XSSFWorkbook getSenderBeansExport(String contact);
	
	/**
	 * 获取寄件人地址列表
	 * @param address
	 * @return
	 */
	List<SenderAddressBean> getSenderAddresses(String address, String province, String city, String county);
	
	XSSFWorkbook getSenderAddressExport(String address, String province, String city, String county);
}
