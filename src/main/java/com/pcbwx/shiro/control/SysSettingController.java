package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.system.AddServiceBean;
import com.pcbwx.shiro.bean.system.ExpressCompanyInfo;
import com.pcbwx.shiro.bean.system.ProductInfo;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.model.WxtbUser;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.service.SystemService;

@Controller
@RequestMapping("/sysSetting")
@Api(tags = "系统配置Api")
public class SysSettingController extends BaseController {

	@Autowired
	private SystemService systemService;
	@Autowired
	private LogService logService;

	@RequestMapping(value = { "/product" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取业务类型列表")
	public List<ProductInfo> getExpressTypeInfos(
			HttpServletRequest request,
			@RequestParam(value = "productName", required = false) String productName) {
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SYSTEM_PRODUCT.getCode(), wxtbUser.getAccount(), productName);
		return systemService.getProductInfos(wxtbUser, productName);
	}

	@RequestMapping(value = { "/product" }, method = RequestMethod.PATCH)
	@ResponseBody
	@ApiOperation("启用/禁用")
	public Map<String, Object> operateProduct(
			HttpServletRequest request,
			@RequestParam("productId") Integer productId) {
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		Integer isSuccess = systemService.enableProduct(productId);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.PRODUCT_ENABLE.getCode(), wxtbUser.getAccount(), productId.toString());
		if (isSuccess == 1) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "系统错误");
			return response;
		}else if (isSuccess == 2) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "相同公司下，不能有相同的启用的业务类型Code");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}

	@RequestMapping(value = { "/product" }, method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation("新增快递业务类型，enable传汉字'启用'/'禁用' ")
	public Map<String, Object> addProduct(HttpServletRequest request,
			@RequestParam("companyId") Integer companyId,
			@RequestParam("productCode") String productCode,
			@RequestParam("productName") String productName,
			@RequestParam("enable") String enable) {
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		if (productCode.equals("") || productName.equals("")
				|| enable.equals("")) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "字段名不能为空");
			return response;
		}
		boolean isSuccess = systemService.addProduct(companyId, productCode,
				productName, enable);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.PRODUCT_ADD.getCode(), wxtbUser.getAccount(), companyId.toString(), productCode, productName);
		if (!isSuccess) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "同一快递公司不允许有相同的启用的业务Code");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}

	@RequestMapping(value = { "/expressCompany" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取快递公司列表,companyId不需要显示")
	public List<ExpressCompanyInfo> getCompanyInfos(
			HttpServletRequest request,
			@RequestParam(value = "companyName", required = false) String companyName) {
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SYSTEM_COMPANY.getCode(), wxtbUser.getAccount(), companyName);
		return systemService.getExpressCompanyInfos(companyName);
	}

	@RequestMapping(value = { "/service" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("增值服务列表")
	public List<AddServiceBean> getAddServiceBeans(
			HttpServletRequest request,
			@RequestParam(value = "serviceName", required = false) String serviceName) {
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SYSTEM_SERVICE.getCode(), wxtbUser.getAccount(), serviceName);
		return systemService.getAddservice(wxtbUser, serviceName);
	}

	@RequestMapping(value = {"/serviceEnable"}, method = RequestMethod.PATCH)
	@ResponseBody
	@ApiOperation("启用/禁用服务")
	public Map<String, Object> operateAddService(HttpServletRequest request,
			@RequestParam("serviceName") String serviceName) {
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		if (serviceName == null || serviceName.equals("")) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "系统错误");
			return response;
		}
		boolean isSuccess = systemService.operateService(serviceName);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SERVICE_ENABLE.getCode(), wxtbUser.getAccount(), serviceName);
		if (!isSuccess) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "修改失败");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}

}
