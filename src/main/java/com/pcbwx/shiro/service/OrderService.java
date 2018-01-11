package com.pcbwx.shiro.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pcbwx.shiro.bean.system.AddressInfo;

/**
 * 订单模块接口
 * 
 * @author 孙贺宇
 *
 */
public interface OrderService {

	/**
	 * 获取快递单列表
	 * @param mailno
	 * @param mailnoChild
	 * @param sendCompany
	 * @param receiveCompany
	 * @param sendContact
	 * @param receiveContact
	 * @param sendDateBegin
	 * @param sendDateEnd
	 * @param receiveDateBegin
	 * @param receiveDateEnd
	 * @param status
	 * @return
	 */
	Map<String, Object> getExpressOrders(String orderId, String mailno, String mailnoChild,
			String sendCompany, String receiveCompany, String sendContact,
			String receiveContact, String sendDateBegin, String sendDateEnd,
			String receiveDateBegin, String receiveDateEnd, String status, Integer page, Integer size);
	
	/**
	 * 导出快递单
	 * @param mailno
	 * @param mailnoChild
	 * @param sendCompany
	 * @param receiveCompany
	 * @param sendContact
	 * @param receiveContact
	 * @param sendDateBegin
	 * @param sendDateEnd
	 * @param receiveDateBegin
	 * @param receiveDateEnd
	 * @param status
	 */
	SXSSFWorkbook expressExport(String orderId, String mailno, String mailnoChild,
			String sendCompany, String receiveCompany, String sendContact,
			String receiveContact, String sendDateBegin, String sendDateEnd,
			String receiveDateBegin, String receiveDateEnd, String status);
	
	/**
	 * 获取运单详情
	 * @param mailno
	 * @return
	 */
	Map<String, Object> getExpressDetail(String mailno, String orderId);
	
	/**
	 * 拉取运单重量和运费等信息
	 */
	void addMailNoInfo();

	/**
	 * 省下拉框
	 * 
	 * @return
	 */
	Map<String, Object> getProvinces();

	/**
	 * 市下拉框
	 * 
	 * @param province
	 * @return
	 */
	Map<String, Object> getCitys(String province);

	/**
	 * 县/区下拉框
	 * 
	 * @param province
	 * @param city
	 * @return
	 */
	Map<String, Object> getCountys(String province, String city);

	/**
	 * 获取全国行政区列表
	 * 
	 * @param province
	 * @param city
	 * @param county
	 * @return
	 */
	List<AddressInfo> getAddressInfos(String province, String city,
			String county);
	
	/**
	 * 导出全国行政区
	 * @param province
	 * @param city
	 * @param county
	 * @return
	 */
	XSSFWorkbook addressExport(String province, String city,
			String county);
}
