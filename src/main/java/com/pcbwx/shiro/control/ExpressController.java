package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.request.Order;
import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.service.ExpressService;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.service.SupportService;

@RequestMapping("/sfExpress")
@Controller
@Api(tags = "顺丰接口Api")
public class ExpressController {
	
	@Autowired
	private ExpressService expressService;
	@Autowired
	private SupportService supportService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(value = {"/order"}, method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation("下单接口")
	public Map<String, Object> sfExpressOrder(HttpServletRequest request, 
			@RequestParam("companyId") Integer comapnyId,
			@RequestBody Order order){
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		if (order == null) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "获取信息异常");
			return response;
		}
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.EXPRESS_ORDER.getCode(), wxtbUser.getAccount(), comapnyId.toString());
		String custId = order.getCustId();
		if (custId == null || custId.equals("")) {
			order.setCustId(null);
		}
		order = operateOrder(order);
		String orderId = supportService.getOrderId();
		return expressService.expressOrder(wxtbUser, comapnyId, order, orderId);
	}
	
	@RequestMapping(value = {"expressInfo"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("二维码信息转运单信息")
	public Map<String, Object> opearteQRCode(HttpServletRequest request,
			@RequestParam("text") String text){
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.EXPRESS_INFO.getCode(), wxtbUser.getAccount(), text);
		return expressService.operateQRCode(text);
	}

	
	/**
	 * 处理省直辖县级行政区
	 * @param order
	 * @return
	 */
	private Order operateOrder(Order order){
		String j_county = order.getSendCounty();
		String j_city = order.getSendCity();
		if (j_county != null && j_city != null) {
			if (j_city.contains("直辖")) {
				order.setSendCity(j_county);
				order.setSendCounty(null);
			}
		}
		String d_county = order.getReceiverCounty();
		String d_city = order.getReceiverCity();
		if (d_county != null && d_city != null) {
			if (d_city.contains("直辖")) {
				order.setReceiverCity(d_county);
				order.setReceiverCounty(null);
			}
		}
		return order;
	}
	
//	/**
//	 * 新增子单
//	 * @param request
//	 * @param mailno
//	 * @param number
//	 * @return
//	 */
//	@RequestMapping( value = {"/orderZD"}, method = RequestMethod.POST)
//	@ResponseBody
//	@ApiOperation("新增子单")
//	public Map<String, Object> orderZD(HttpServletRequest request, 
//			@RequestParam("mailno") String mailno,
//			@RequestParam("number") Integer number){
//		Map<String, Object> response = new HashMap<String, Object>();
//		if (mailno == null || number == null) {
//			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
//			response.put("msg", "获取信息异常");
//			return response;
//		}
//		if (number < 1) {
//			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
//			response.put("msg", "新增子订单数量必须大于0");
//			return response;
//		}
//		Integer isSuccess = expressService.goforOrderZD(mailno, number);
//		if (isSuccess == null || isSuccess == 0) {
//			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
//			response.put("msg", "系统发生数据错误或运行时异常");
//			return response;
//		}
//		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
//		return response;
//	}
}
