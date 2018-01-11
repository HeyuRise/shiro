package com.pcbwx.shiro.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcbwx.shiro.bean.ExpressThread;
import com.pcbwx.shiro.bean.response.ResponseRoute;
import com.pcbwx.shiro.bean.response.ResponseRoutePush;
import com.pcbwx.shiro.bean.response.Route;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.dao.ExpressMapper;
import com.pcbwx.shiro.dao.RoutePushMapper;
import com.pcbwx.shiro.dao.SustenanceMapper;
import com.pcbwx.shiro.enums.DictionaryEnum;
import com.pcbwx.shiro.enums.ExpressStatusEnum;
import com.pcbwx.shiro.model.Dictionary;
import com.pcbwx.shiro.model.Express;
import com.pcbwx.shiro.model.RoutePush;
import com.pcbwx.shiro.service.RouteService;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.utils.DateTimeUtil;
import com.pcbwx.shiro.utils.HttpUtil;
import com.pcbwx.shiro.utils.XMLUtil;
/**
 * 路由接口实现类
 * @author 孙贺宇
 *
 */
@Service("routeService")
public class RouteServiceImpl implements RouteService{

	@Autowired
	private RoutePushMapper routePushMapper;
	@Autowired
	private ExpressMapper expressMapper;
	@Autowired
	private SustenanceMapper sustenanceMapper;
	@Autowired
	private SupportService supportService;
	@Autowired
	private CacheService cacheService;
	
	@Override
	public Integer operateRoutePush(String xml) {
		// 解析推送消息
		List<ResponseRoutePush> routeInfos = XMLUtil.getResponseRoutePush(xml);
		if (routeInfos == null || routeInfos.isEmpty()) {
			return 0;
		}
		Date now = new Date();
		RoutePush routePush = new RoutePush();
		Map<Integer, Express> expressMap = new HashMap<Integer, Express>();
		for (ResponseRoutePush routeInfo : routeInfos) {
			routePush.setAcceptAddress(routeInfo.getAcceptAddress());
			routePush.setAcceptTime(routeInfo.getAcceptTime());
			routePush.setCreateTime(now);
			routePush.setMailNo(routeInfo.getMailNo());
			routePush.setOpCode(routeInfo.getOpCode());
			routePush.setOrderId(routeInfo.getOrderId());
			routePush.setRemark(routeInfo.getRemark());
			String mailno = routeInfo.getMailNo();
			String orderId = routeInfo.getOrderId();
			Express express = null;
			if (mailno != null && !mailno.equals("")) {
				express =  expressMapper.selectByMailno(mailno);
				if (express == null) {
					express = expressMapper.selectByMailnoChild(mailno);
				}
			}
			if (express == null && orderId != null && !orderId.equals("")) {
				express = expressMapper.selectByOrderId(orderId);
			}
			if (express == null) {
				return 1;
			}
			routePush.setExpressId(express.getExpressId());
			routePushMapper.insertSelective(routePush);
			expressMap.put(express.getExpressId(), express);
			String opCode = routeInfo.getOpCode();
			String status = null;
			// 根据路由操作码来改变当前运单的状态
			Dictionary dictionaryDel = cacheService.getDictionary(DictionaryEnum.EXPRESS_STATUS, ExpressStatusEnum.DELEIVER.getCode());
			Dictionary dictionaryRec = cacheService.getDictionary(DictionaryEnum.EXPRESS_STATUS, ExpressStatusEnum.RECEIVED.getCode());
			String paramDel = dictionaryDel.getParamStr1();
			String paramRec = dictionaryRec.getParamStr1();
			if (paramDel != null && paramDel.contains(opCode)) {
				status = dictionaryDel.getInnerCode();
			}else if (paramRec != null && paramRec.contains(opCode)) {
				status = dictionaryRec.getInnerCode();
			}
			express.setStatus(status);
			expressMapper.updateByPrimaryKeySelective(express);
		}
		// 开起新的线程，调取运单运费和重量等信息
		if (!expressMap.isEmpty()) {
			ExpressThread thread = new ExpressThread(expressMap, sustenanceMapper, expressMapper);
			thread.start();
		}
		return 1;
	}
	
	@Override
	public List<ResponseRoute> goforExpressRoute(Integer type, String mailno) {
		// 默认按运单查找
		if (type == null) {
			type = 1;
		}
		String xml = supportService.getQueryRouteXML(type, mailno);
		String response = HttpUtil.postXML(xml);
		if (response == null) {
			return new ArrayList<ResponseRoute>();
		}
		List<ResponseRoute> responseRoutes = XMLUtil.operateRouteResponse(response);
		if (responseRoutes == null || responseRoutes.isEmpty()) {
			return responseRoutes;
		}
		for (ResponseRoute responseRoute : responseRoutes) {
			List<Route> routes = responseRoute.getRoutes();
			if (routes == null || routes.isEmpty()) {
				continue;
			}
			List<Route> routesOrder = new ArrayList<Route>();
			// 按时间倒叙排列
			for (int i = routes.size() - 1; i >= 0; i--) {
				routesOrder.add(routes.get(i));
			}
			responseRoute.setRoutes(routesOrder);
		}
		return responseRoutes;
	}

	@Override
	public List<ResponseRoute> goforRoutePushInfo(String mailno) {
		List<RoutePush> routePushs = routePushMapper.selectByMailno(mailno);
		if (routePushs == null || routePushs.isEmpty()) {
			return null;
		}
		ResponseRoute responseRoute = new ResponseRoute();
		responseRoute.setMailno(mailno);
		List<Route> routePushInfos = new ArrayList<Route>();
		for (RoutePush routePush : routePushs) {
			Date accept = routePush.getAcceptTime();
			String acceptDateStr = DateTimeUtil.date2dateTimeStr(accept, "yyyy-MM-dd");
			String acceptTimeStr = DateTimeUtil.date2dateTimeStr(accept, "HH:mm:ss");
			Route route = new Route();
			route.setRemark(routePush.getRemark());
			route.setOpcode(routePush.getOpCode());
			route.setAccrptAddress(routePush.getAcceptAddress());
			route.setAcceptDate(acceptDateStr);
			route.setAcceptTime(acceptTimeStr);
			routePushInfos.add(route);
		}
		responseRoute.setRoutes(routePushInfos);
		List<ResponseRoute> responseRoutes = new ArrayList<ResponseRoute>();
		responseRoutes.add(responseRoute);
		return responseRoutes;
	}
}
