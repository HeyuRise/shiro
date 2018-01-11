package com.pcbwx.shiro.control;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.service.ExpressService;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.service.RouteService;
import com.pcbwx.shiro.service.SupportService;

@Controller
@RequestMapping("/push")
public class PushController extends BaseController{
	
	@Autowired
	private RouteService routeService;
	@Autowired
	private LogService logService;
	@Autowired
	private SupportService supportService;
	@Autowired
	private ExpressService expressService;
	
	/**
	 * 路由推送接口
	 * @param request
	 * @param xml
	 * @return
	 */
	@RequestMapping( value = {"/route"} , method = RequestMethod.POST)
	@ResponseBody
	public String getRoutePush(HttpServletRequest request, @RequestBody String xml){
		if (xml == null) {
			return supportService.getErrorXML("RoutePushService");
		}
		try {
			xml = URLDecoder.decode(xml, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return supportService.getErrorXML("RoutePushService");
		}
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.ROUTE_PUSH.getCode(), null, xml);
		String xmlOp = xml.substring(xml.indexOf("<"));
		Integer isSuccess = routeService.operateRoutePush(xmlOp);
		if (isSuccess == 0) {
			return supportService.getErrorXML("RoutePushService");
		}
		return supportService.getOkXML("RoutePushService");
	}
	
	/**
	 * 运单人工筛选结果推送接口
	 * @param request
	 * @param xml
	 * @return
	 */
	@RequestMapping( value = {"/orderFilter"}, method = RequestMethod.POST)
	@ResponseBody
	public String personOperate(HttpServletRequest request, @RequestBody String xml){
		if (xml == null) {
			return supportService.getErrorXML("OrderFilterPushService");
		}
		try {
			xml = URLDecoder.decode(xml, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return supportService.getErrorXML("OrderFilterPushService");
		}
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.ORDER_FILTER_PUSH.getCode(), null, xml);
		String xmlOp = xml.substring(xml.indexOf("<"));
		Integer isSuccess = expressService.operateOrderFilterXml(xmlOp);
		if (isSuccess == 0) {
			return supportService.getErrorXML("OrderFilterPushService");
		}
		return supportService.getOkXML("OrderFilterPushService");
	}
}
