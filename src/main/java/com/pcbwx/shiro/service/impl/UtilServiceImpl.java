package com.pcbwx.shiro.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pcbwx.shiro.bean.request.WaybillService;
import com.pcbwx.shiro.bean.response.QueryWayBill;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.service.UtilService;
import com.pcbwx.shiro.utils.HttpUtil;
import com.pcbwx.shiro.utils.XMLUtil;
/**
 * 
 * @author 孙贺宇
 *
 */
@Service("工具service")
public class UtilServiceImpl implements UtilService{

	@Override
	public Map<String, Object> getMailNo(String orderId) {
		Map<String, Object> response = new HashMap<String, Object>();
		WaybillService waybillService = new WaybillService();
		waybillService.setOrderId(orderId);
		String xml = HttpUtil.postXML(waybillService.toXml());
		QueryWayBill queryWayBill = XMLUtil.getQueryWayBill(xml);
		if (queryWayBill == null) {
			response.put("result", ErrorCodeEnum.SUCCESS.getCode());
			response.put("msg", "该运单没有运费和计费重量等信息");
			return response;
		}
		BigDecimal cost = queryWayBill.getExpressCost();
		String expressCost = cost == null ? null : "￥" + cost;
		BigDecimal weight = queryWayBill.getWeight();
		String payWeight = weight == null ? null : weight + "kg";
		response.put("运费", expressCost);
		response.put("计费重量", payWeight);
		return response;
	}

}
