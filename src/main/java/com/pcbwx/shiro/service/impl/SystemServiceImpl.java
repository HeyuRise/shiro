package com.pcbwx.shiro.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcbwx.shiro.bean.system.AddServiceBean;
import com.pcbwx.shiro.bean.system.ExpressCompanyInfo;
import com.pcbwx.shiro.bean.system.ProductInfo;
import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.dao.ExpressCompanyMapper;
import com.pcbwx.shiro.dao.ExpressProductMapper;
import com.pcbwx.shiro.dao.ServiceMapper;
import com.pcbwx.shiro.enums.ClickEnum;
import com.pcbwx.shiro.enums.DictionaryEnum;
import com.pcbwx.shiro.model.Dictionary;
import com.pcbwx.shiro.model.ExpressCompany;
import com.pcbwx.shiro.model.ExpressProduct;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.service.SystemService;
/**
 * 系统设置接口实现类
 * @author 孙贺宇
 *
 */
@Service("systemService")
public class SystemServiceImpl implements SystemService{

	@Autowired
	private ExpressCompanyMapper expressCompanyMapper;
	@Autowired
	private ExpressProductMapper expressProductMapper;
	@Autowired
	private ServiceMapper serviceMapper;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private SupportService supportService;
	
	@Override
	public List<ProductInfo> getProductInfos(WxtbAuthUser wxtbUser, String productName) {
		List<ExpressCompany> expressCompanies = expressCompanyMapper.load(null);
		if (expressCompanies == null) {
			expressCompanies = new ArrayList<ExpressCompany>();
		}
		Map<Integer, String> expressComMap = new HashMap<Integer, String>();
		for (ExpressCompany expressCompany : expressCompanies) {
			expressComMap.put(expressCompany.getExpressCompanyId(), expressCompany.getExpressCompanyName());
		}
		List<ExpressProduct> products = expressProductMapper.selectByProductName(productName);
		if (products == null) {
			return new ArrayList<ProductInfo>();
		}
		List<ProductInfo> productInfos = new ArrayList<ProductInfo>();
		Integer click = supportService.ebableClick(wxtbUser.getAccount(), ClickEnum.PRODUCT_ENABLE);
		for (ExpressProduct expressProduct : products) {
			Integer status = expressProduct.getStatus();
			ProductInfo productInfo = new ProductInfo();
			productInfo.setProductId(expressProduct.getProductId());
			String enable = status == 1 ? "启用" : "禁用";
			productInfo.setEnable(enable);
			productInfo.setProductCode(expressProduct.getProductCode());
			productInfo.setProductName(expressProduct.getProductName());
			Integer companyId = expressProduct.getExpressCompanyId();
			String companyName = expressComMap.get(companyId);
			productInfo.setExpressCompany(companyName);
			productInfo.setClick(click);
			productInfos.add(productInfo);
		}
		return productInfos;
	}


	@Override
	public Integer enableProduct(Integer productId) {
		ExpressProduct expressProduct = expressProductMapper.selectByPrimaryKey(productId);
		if (expressProduct == null) {
			return 1;
		}
		Integer enableInt = expressProduct.getStatus() == 1 ? 0 : 1;
		Integer companyId = expressProduct.getExpressCompanyId();
		String productCode = expressProduct.getProductCode();
		// 如果是启用服务，判断数据库中是否有快递公司和业务Code都相同的启用数据，如果有，把错误信息返回前台
		if (enableInt == 1) {
			List<ExpressProduct> expressProducts = expressProductMapper.selectEnableByCompanyIdAndProductCode(companyId, productCode);
			if (expressProducts.size() > 0) {
				return 2;
			}
		}
		expressProduct.setStatus(enableInt);
		expressProductMapper.updateByPrimaryKeySelective(expressProduct);
		// 重载服务器内存中的业务类型数据
		List<ExpressProduct> expressProducts = expressProductMapper.selectByProductName(null);
		cacheService.reloadExpressProduct(expressProducts);
		return 0;
	}
	
	@Override
	public boolean addProduct(Integer companyId, String productCode,
			String productName, String enable) {
		Integer enableInt = enable.equals("启用") ? 1 : 0;
		// 判断数据库中是否有快递公司和业务Code都相同的启用数据，如果有，把错误信息返回前台
		if (enableInt == 1) {
			List<ExpressProduct> expressProducts = expressProductMapper.selectEnableByCompanyIdAndProductCode(companyId, productCode);
			if (expressProducts != null && expressProducts.size() > 0) {
				return false;
			}
		}
		Date now = new Date();
		ExpressProduct expressProduct = new ExpressProduct();
		expressProduct.setExpressCompanyId(companyId);
		expressProduct.setProductCode(productCode);
		expressProduct.setProductName(productName);
		expressProduct.setStatus(enableInt);
		expressProduct.setCreateTime(now);
		expressProductMapper.insertSelective(expressProduct);
		List<ExpressProduct> expressProducts = expressProductMapper.selectByProductName(null);
		cacheService.reloadExpressProduct(expressProducts);
		return true;
	}
	
	@Override
	public boolean deleteProduct(Integer companyId, String productCode) {
		Integer count = expressProductMapper.deleteByCompanyIdAndProductCode(companyId, productCode);
		if (count == null || count == 0) {
			return false;
		}
		List<ExpressProduct> expressProducts = expressProductMapper.selectByProductName(null);
		cacheService.reloadExpressProduct(expressProducts);
		return true;
	}
	
	@Override
	public Map<String, Object> getEnableExpressCompany() {
		Map<String, Object> response = new HashMap<String, Object>();
		List<ExpressCompany> expressCompanies = expressCompanyMapper.load(1);
		if (expressCompanies == null) {
			expressCompanies = new ArrayList<ExpressCompany>();
		}
		JSONArray jsonArray = new JSONArray();
		for (ExpressCompany expressCompany : expressCompanies) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", expressCompany.getExpressCompanyId());
			jsonObject.put("name", expressCompany.getExpressCompanyName());
			jsonArray.add(jsonObject);
		}
		response.put("size", jsonArray.size());
		response.put("items", jsonArray);
		return response;
	}


	@Override
	public Integer companyName2Id(String companyName) {
		List<ExpressCompany> expressCompanies = expressCompanyMapper.load(null);
		if (expressCompanies == null) {
			expressCompanies = new ArrayList<ExpressCompany>();
		}
		Map<String, Integer> expressComMap = new HashMap<String, Integer>();
		for (ExpressCompany expressCompany : expressCompanies) {
			expressComMap.put(expressCompany.getExpressCompanyName(), expressCompany.getExpressCompanyId());
		}
		return expressComMap.get(companyName);
	}


	@Override
	public List<ExpressCompanyInfo> getExpressCompanyInfos(String companyName) {
		if (companyName == null || companyName.equals("")) {
			companyName = null;
		}
		List<ExpressCompany> expressCompanies = expressCompanyMapper.selectByCompanyName(companyName);
		if (expressCompanies == null) {
			expressCompanies = new ArrayList<ExpressCompany>();
		}
		List<ExpressCompanyInfo> expressCompanyInfos = new ArrayList<ExpressCompanyInfo>();
		for (ExpressCompany expressCompany : expressCompanies) {
			Integer enable = expressCompany.getStatus();
			String enableStr = enable == 1 ? "启用" : "禁用"; 
			ExpressCompanyInfo expressCompanyInfo = new ExpressCompanyInfo();
			expressCompanyInfo.setCompanyId(expressCompany.getExpressCompanyId());
			expressCompanyInfo.setCompanyName(expressCompany.getExpressCompanyName());
			expressCompanyInfo.setEnable(enableStr);
			expressCompanyInfos.add(expressCompanyInfo);
		}
		return expressCompanyInfos;
	}


	@Override
	public List<AddServiceBean> getAddservice(WxtbAuthUser wxtbUser, String serviceName) {
		List<com.pcbwx.shiro.model.Service> services = serviceMapper.selectByCondition(serviceName);
		if (services == null || services.isEmpty()) {
			return new ArrayList<AddServiceBean>();
		}
		List<AddServiceBean> addServiceBeans = new ArrayList<AddServiceBean>();
		Integer click = supportService.ebableClick(wxtbUser.getAccount(), ClickEnum.SERVICE_ENABLE);
		for (com.pcbwx.shiro.model.Service service : services) {
			AddServiceBean addServiceBean = new AddServiceBean();
			addServiceBean.setServiceName(service.getName());
			String enable = service.getStatus() == 1 ? "启用" : "禁用";
			addServiceBean.setEnable(enable);
			addServiceBean.setClick(click);
			addServiceBeans.add(addServiceBean);
		}
		return addServiceBeans;
	}


	@Override
	public boolean operateService(String serviceName) {
		com.pcbwx.shiro.model.Service service = serviceMapper.selectByService(serviceName);
		if (service == null) {
			return false;
		}
		Integer status = service.getStatus();
		if (status == 1) {
			service.setStatus(0);
		}else {
			service.setStatus(1);
		}
		Integer count = serviceMapper.updateByPrimaryKeySelective(service);
		if (count == null || count == 0) {
			return false;
		}
		List<com.pcbwx.shiro.model.Service> services = serviceMapper.selectByCondition(null);
		cacheService.reloadService(services);
		return true;
	}


	@Override
	public Map<String, Object> getEnableService() {
		Map<String, Object> repsonse = new HashMap<String, Object>();
		List<com.pcbwx.shiro.model.Service> services = serviceMapper.selectByStatus();
		JSONArray jsonArray = new JSONArray();
		if (services == null || services.isEmpty()) {
			repsonse.put("size", jsonArray.size());
			repsonse.put("items", jsonArray);
			return repsonse;
		}
		for (com.pcbwx.shiro.model.Service service : services) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", service.getCode());
			jsonObject.put("name", service.getName());
			jsonArray.add(jsonObject);
		}
		repsonse.put("size", jsonArray.size());
		repsonse.put("items", jsonArray);
		return repsonse;
	}


	@Override
	public Map<String, Object> getEnableProduct() {
		Map<String, Object> response = new HashMap<String, Object>();
		List<ExpressProduct> expressProducts = expressProductMapper.selectByStatus();
		JSONArray jsonArray = new JSONArray();
		if (expressProducts == null || expressProducts.isEmpty()) {
			response.put("size", jsonArray.size());
			response.put("items", jsonArray);
			return response;
		}
		for (ExpressProduct expressProduct : expressProducts) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", expressProduct.getProductCode());
			jsonObject.put("name", expressProduct.getProductName());
			jsonArray.add(jsonObject);
		}
		response.put("size", jsonArray.size());
		response.put("items", jsonArray);
		return response;
	}


	@Override
	public Map<String, Object> getStatus() {
		Map<String, Object> response = new HashMap<String, Object>();
		List<Dictionary> dictionaries = cacheService.getDictionarys(DictionaryEnum.EXPRESS_STATUS);
		JSONArray jsonArray = new JSONArray();
		if (dictionaries == null || dictionaries.isEmpty()) {
			response.put("size", jsonArray.size());
			response.put("items", jsonArray);
			return response;
		}
		Integer a = 1;
		for (Dictionary dictionary : dictionaries) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", a);
			jsonObject.put("name", dictionary.getValueStr());
			jsonArray.add(jsonObject);
			a++;
		}
		response.put("size", jsonArray.size());
		response.put("items", jsonArray);
		return response;
	}
}
