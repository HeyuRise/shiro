package com.pcbwx.shiro.service;

import java.util.List;
import java.util.Map;

import com.pcbwx.shiro.bean.system.AddServiceBean;
import com.pcbwx.shiro.bean.system.ExpressCompanyInfo;
import com.pcbwx.shiro.bean.system.ProductInfo;
import com.pcbwx.shiro.model.WxtbUser;
/**
 * 系统设置模块接口
 * 
 * @author 孙贺宇
 *
 */
public interface SystemService {
	
	/**
	 * 获取业务列表
	 * @param expressType
	 * @return
	 */
	List<ProductInfo> getProductInfos(WxtbUser wxtbUser, String productName);
	
	/**
	 * 获取快递公司下拉框
	 * @return
	 */
	Map<String, Object> getEnableExpressCompany();
	
	/**
	 * 编辑业务
	 * @param companyId
	 * @param productCode
	 * @param productName
	 * @param enable
	 * @return
	 */
	Integer enableProduct(Integer productId);
	
	/**
	 * 新增业务
	 * @param companyId
	 * @param productCode
	 * @param productName
	 * @param enable
	 * @return
	 */
	boolean addProduct(Integer companyId, String productCode, String productName, String enable);
	
	/**
	 * 删除业务
	 * @param companyId
	 * @param productCode
	 * @return
	 */
	boolean deleteProduct(Integer companyId, String productCode);
	
	/**
	 * 获取可用业务
	 * @return
	 */
	Map<String, Object> getEnableProduct();
	
	/**
	 * 把快递公司转为快递Id
	 * @param companyName
	 * @return
	 */
	Integer companyName2Id(String companyName);
	
	/**
	 * 获取快递公司列表
	 * @param companyName
	 * @return
	 */
	List<ExpressCompanyInfo> getExpressCompanyInfos(String companyName);
	
	/**
	 * 获取增值服务列表
	 * @param serviceName
	 * @return
	 */
	List<AddServiceBean> getAddservice(WxtbUser wxtbUser, String serviceName);
	
	/**
	 * 启用/禁用服务
	 * @param serviceName
	 * @return
	 */
	boolean operateService(String serviceName);
	
	/**
	 * 获取增值服务下拉框
	 * @return
	 */
	Map<String, Object> getEnableService();
	
	/**
	 * 获取状态下拉框
	 * @return
	 */
	Map<String, Object> getStatus();
}
