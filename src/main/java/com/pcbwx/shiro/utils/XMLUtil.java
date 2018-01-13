package com.pcbwx.shiro.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.pcbwx.shiro.bean.OrderZd;
import com.pcbwx.shiro.bean.request.OrderFilter;
import com.pcbwx.shiro.bean.response.QueryWayBill;
import com.pcbwx.shiro.bean.response.ResponseOrder;
import com.pcbwx.shiro.bean.response.ResponseRoute;
import com.pcbwx.shiro.bean.response.ResponseRoutePush;
import com.pcbwx.shiro.bean.response.Route;


public class XMLUtil {
	
	private static final Logger logger = LogManager.getLogger(XMLUtil.class);
	
	/**
	 * 解析下单返回数据
	 * @param response
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static ResponseOrder getOrderReponse(String response){
		StringBuffer sb = new StringBuffer(response);
		XmlPullParser parser = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			return null;
		}
		String service = null;			// 下单请求service
		String orderid = null;			// 客户订单号
		String mailno = null;			// 顺丰运单号
		String origincode = null;		// 原寄地区域代码
		String destcode = null;			// 目的地区域代码
		Integer filter_result = null;	// 筛单结果： 1：人工确认,2：可收派,3：不可以收派
		String remark = null;			// 不可以收派的原因代码
		String errCode = null;			// 错误代码
		String errMsg = null;			// 错误信息
		ResponseOrder responseOrder = new ResponseOrder();
		int type = 0;
		try {
			parser.setInput(new ByteArrayInputStream(sb.substring(0)
					.getBytes("UTF-8")), "UTF-8");
			type = parser.getEventType();
		} catch (Exception e1) {
			return null;
		}
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				String start_tag = parser.getName();
				if (start_tag.equals("Response")) {
					service = parser.getAttributeValue(0);
					responseOrder.setService(service);
				}
				if (start_tag.equals("Head")) {
					String head = null;
						try {
							head = parser.nextText();
						} catch (XmlPullParserException e) {
							return null;
						} catch (IOException e) {
							return null;
						}
					if (head != null && head.equals("OK")) {
						responseOrder.setIsOk(1);
					}else if (head != null && head.equals("ERR")) {
						responseOrder.setIsOk(0);
					}
				}
				if (start_tag.equals("OrderResponse")) {
					orderid = parser.getAttributeValue(null, "orderid");
					mailno = parser.getAttributeValue(null, "mailno");
					origincode = parser.getAttributeValue(null, "origincode");
					destcode = parser.getAttributeValue(null, "destcode");
					String filter_resultStr = parser.getAttributeValue(null, "filter_result");
					filter_result = Integer.parseInt(filter_resultStr);
					if (filter_result != null && filter_result == 3) {
						remark = parser.getAttributeValue(null, "remark");
					}
					responseOrder.setOrderId(orderid);
					responseOrder.setMailno(mailno);
					responseOrder.setOrigincode(origincode);
					responseOrder.setDestcode(destcode);
					responseOrder.setFilterResult(filter_result);
					responseOrder.setRemark(remark);
				}
				if (start_tag.equals("ERROR")) {
					errCode = parser.getAttributeValue(0);
					try {
						errMsg = parser.nextText();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					responseOrder.setErrCode(errCode);
					responseOrder.setErrMsg(errMsg);
				}
				break;	
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			try {
				type = parser.next();
			} catch (Exception e) {
				break;
			}
		}	
		return responseOrder;
	}
	
	/**
	 * 解析路由推送数据
	 * @param response
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException 
	 */
	public static List<ResponseRoutePush> getResponseRoutePush(String response){
		List<ResponseRoutePush> responseRoutePushs = new ArrayList<ResponseRoutePush>();
		StringBuffer sb = new StringBuffer(response);
		XmlPullParserFactory factory;
		XmlPullParser parser;
		try {
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			return null;
		}
		String service = null;				// 确定下单请求service
		String orderId = null;				// 客户订单号
		String mailNo = null;				// 顺丰运单号
		String id = null;
		Date acceptTime = null;
		String acceptAddress = null;
		String remark = null;
		String opCode = null;
		ResponseRoutePush routeInfo = new ResponseRoutePush();
		int type = 0;
		try {
			parser.setInput(new ByteArrayInputStream(sb.substring(0)
					.getBytes("UTF-8")), "UTF-8");
			type = parser.getEventType();
		} catch (UnsupportedEncodingException e1) {
			return null;
		} catch (XmlPullParserException e1) {
			return null;
		}
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				String start_tag = parser.getName();
				if (start_tag.equals("Request")) {
					service = parser.getAttributeValue(0);
				}
				if (start_tag.equals("WaybillRoute")) {
					routeInfo = new ResponseRoutePush();
					id = parser.getAttributeValue(null, "id");
					mailNo = parser.getAttributeValue(null, "mailno");
					orderId = parser.getAttributeValue(null, "orderid");
					String acceptTimeStr = parser.getAttributeValue(null, "acceptTime");
					acceptTime = DateTimeUtil.dateTimeStr2date(acceptTimeStr);
					acceptAddress = parser.getAttributeValue(null, "acceptAddress");
					remark = parser.getAttributeValue(null, "remark");
					try {
						opCode = parser.getAttributeValue(null, "opCode");
					} catch (Exception e) {
					}
					routeInfo.setService(service);
					routeInfo.setId(id);
					routeInfo.setMailNo(mailNo);
					routeInfo.setOrderId(orderId);
					routeInfo.setAcceptTime(acceptTime);
					routeInfo.setAcceptAddress(acceptAddress);
					routeInfo.setRemark(remark);
					routeInfo.setOpCode(opCode);
					responseRoutePushs.add(routeInfo);
				}
				break;	
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			try {
				type = parser.next();
			} catch (Exception e) {
				break;
			}
		}
		return responseRoutePushs;
	}
	
	/**
	 * 解析路由查询数据
	 * @param response
	 * @return
	 */
	public static List<ResponseRoute> operateRouteResponse(String response){
		StringBuffer sb = new StringBuffer(response);
		XmlPullParserFactory factory;
		XmlPullParser parser;
		try {
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			return null;
		}
		List<ResponseRoute> responseRoutes = new ArrayList<ResponseRoute>();
		ResponseRoute responseRoute = null;
		List<Route> routes = null;
		int type = 0;
		try {
			parser.setInput(new ByteArrayInputStream(sb.substring(0)
					.getBytes("UTF-8")), "UTF-8");
			type = parser.getEventType();
		} catch (UnsupportedEncodingException e1) {
			return null;
		} catch (XmlPullParserException e1) {
			return null;
		}
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				String start_tag = parser.getName();
				if (start_tag.equals("Head")) {
					String head = null;
						try {
							head = parser.nextText();
						} catch (XmlPullParserException e) {
							return null;
						} catch (IOException e) {
							return null;
						}
					if (head != null && head.equals("ERR")) {
						return null;
					}
				}
				if (start_tag.equals("RouteResponse")) {
					if (routes != null) {
						responseRoute.setRoutes(routes);
					}
					if (responseRoute != null) {
						responseRoutes.add(responseRoute);
					}
					String mailno = parser.getAttributeValue(0);
					responseRoute = new ResponseRoute();
					responseRoute.setMailno(mailno);
					routes = new ArrayList<Route>();
				}
				if (start_tag.equals("Route")) {
					Route route = new Route();
					String remark = parser.getAttributeValue(null, "remark");
					String accept_time = parser.getAttributeValue(null, "accept_time");
					String accept_address = parser.getAttributeValue(null, "accept_address");
					String opcode = parser.getAttributeValue(null, "opcode");
					String[] accept =  accept_time.split(" ");
					route.setRemark(remark);
					route.setAcceptDate(accept[0]);
					route.setAcceptTime(accept[accept.length-1]);
					route.setAccrptAddress(accept_address);
					route.setOpcode(opcode);
					routes.add(route);
				}
				break;	
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			try {
				type = parser.next();
			} catch (Exception e) {
				break;
			}
		}	
		if (routes != null) {
			responseRoute.setRoutes(routes);
		}
		if (responseRoute != null) {
			responseRoutes.add(responseRoute);
		}
		return responseRoutes;
	}
	
	public static OrderZd getOrderZD(String response){
		StringBuffer sb = new StringBuffer(response);
		XmlPullParserFactory factory;
		XmlPullParser parser;
		try {
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			return null;
		}
		OrderZd orderZD = null;
		String orderid = null;
		String mailno = null;
		String mailnoChild = null;
		int type = 0;
		try {
			parser.setInput(new ByteArrayInputStream(sb.substring(0)
					.getBytes("UTF-8")), "UTF-8");
			type = parser.getEventType();
		} catch (UnsupportedEncodingException e1) {
			return null;
		} catch (XmlPullParserException e1) {
			return null;
		}
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				String start_tag = parser.getName();
				if (start_tag.equals("Head")) {
					String head = null;
						try {
							head = parser.nextText();
						} catch (XmlPullParserException e) {
							return null;
						} catch (IOException e) {
							return null;
						}
					if (head != null && head.equals("ERR")) {
						return null;
					}
				}
				if (start_tag.equals("OrderZDResponse")) {
					try {
						orderid = parser.getAttributeValue(null, "orderid");
					} catch (Exception e) {
						logger.info("跳过--------------");
					}
					if (orderid != null) {
						mailno = parser.getAttributeValue(null, "main_mailno");
						mailnoChild = parser.getAttributeValue(null, "mailno_zd");
					}
				}
				if (start_tag.equals("OrderZDResponse")) {
					try {
						orderid = parser.getAttributeValue(null, "orderid");
					} catch (Exception e) {
						logger.info("跳过--------------");
					}
					if (orderid != null) {
						mailno = parser.getAttributeValue(null, "main_mailno");
						mailnoChild = parser.getAttributeValue(null, "mailno_zd");
					}
				}
				break;	
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			try {
				type = parser.next();
			} catch (Exception e) {
				break;
			}
		}
		orderZD = new OrderZd();
		if (orderid == null) {
			return null;
		}
		orderZD.setMailno(mailno);
		orderZD.setOrderId(orderid);
		orderZD.setMailnoChild(mailnoChild);
		return orderZD;
	}
	
	public static OrderFilter operateOrderFilter(String response){
		StringBuffer sb = new StringBuffer(response);
		XmlPullParser parser = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			return null;
		}
		String orderid = null;			// 客户订单号
		String mailno = null;			// 顺丰运单号
		String destCode = null;			// 目的地code
		Integer filter_result = null;	// 筛单结果： 1：人工确认,2：可收派,3：不可以收派
		String remark = null;			// 不可以收派的原因代码
		String returnTrackingNo = null; // 签回单号，当筛单结果为可到，且下单时申请生成签回单号，则会推送该属性。
		OrderFilter orderFilter = new OrderFilter();
		int type = 0;
		try {
			parser.setInput(new ByteArrayInputStream(sb.substring(0)
					.getBytes("UTF-8")), "UTF-8");
			type = parser.getEventType();
		} catch (Exception e1) {
			return null;
		}
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				String start_tag = parser.getName();
				if (start_tag.equals("OrderFilterResult")) {
					orderid = parser.getAttributeValue(null, "orderid");
					mailno = parser.getAttributeValue(null, "mailno");
					destCode = parser.getAttributeValue(null, "destCode");
					String filter_resultStr = parser.getAttributeValue(null, "filterResult");
					filter_result = Integer.parseInt(filter_resultStr);
					remark = parser.getAttributeValue(null, "remark");
					returnTrackingNo = parser.getAttributeValue(null, "returnTrackingNo");
					orderFilter.setOrderId(orderid);
					orderFilter.setMailno(mailno);
					orderFilter.setDestcode(destCode);
					orderFilter.setFilterResult(filter_result);
					orderFilter.setRemark(remark);
					orderFilter.setReturnTrackingNo(returnTrackingNo);
				}
				break;	
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			try {
				type = parser.next();
			} catch (Exception e) {
				break;
			}
		}	
		return orderFilter;
	}
	
	/**
	 * 解析重量运费查询返回数据
	 * @param response
	 * @return
	 */
	public static QueryWayBill getQueryWayBill(String response){
		StringBuffer sb = new StringBuffer(response);
		XmlPullParserFactory factory;
		XmlPullParser parser;
		try {
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
		} catch (XmlPullParserException e1) {
			return null;
		}
		String orderId = null;
		String mailno = null;
		String custId = null;
		BigDecimal weight = null;
		String limitType = null;
		BigDecimal expressCost = null;
		BigDecimal insure = null;
		BigDecimal pkfee = null;
		int type = 0;
		try {
			parser.setInput(new ByteArrayInputStream(sb.substring(0)
					.getBytes("UTF-8")), "UTF-8");
			type = parser.getEventType();
		} catch (UnsupportedEncodingException e1) {
			return null;
		} catch (XmlPullParserException e1) {
			return null;
		}
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				String start_tag = parser.getName();
				if (start_tag.equals("Head")) {
					String head = null;
						try {
							head = parser.nextText();
						} catch (XmlPullParserException e) {
							return null;
						} catch (IOException e) {
							return null;
						}
					if (head != null && head.equals("ERR")) {
						return null;
					}
				}
				if (start_tag.equals("Waybill")) {
					orderId = parser.getAttributeValue(null, "orderId");
					mailno = parser.getAttributeValue(null, "waybillNo");
					custId = parser.getAttributeValue(null, "customerAcctCode");
					String weigthStr = parser.getAttributeValue(null, "meterageWeightQty");
					if (!StringUtil.isEmpty(weigthStr)) {
						weight = new BigDecimal(weigthStr);
					}
					limitType = parser.getAttributeValue(null, "limitTypeCode");
				}
				if (start_tag.equals("Fee")) {
					String name = parser.getAttributeValue(null, "name");
					if (name.contains("运费")) {
						String CostStr = parser.getAttributeValue(null, "value");
						expressCost = new BigDecimal(CostStr);
					}
					if (name.contains("保价")) {
						String CostStr = parser.getAttributeValue(null, "value");
						insure = new BigDecimal(CostStr);
					}
					if (name.contains("包装")) {
						String CostStr = parser.getAttributeValue(null, "value");
						pkfee = new BigDecimal(CostStr);
					}
				}
				break;	
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			try {
				type = parser.next();
			} catch (Exception e) {
				break;
			}
		}
		QueryWayBill queryWayBill = new QueryWayBill();
		queryWayBill.setCustId(custId);
		queryWayBill.setExpressCost(expressCost);
		queryWayBill.setInsure(insure);
		queryWayBill.setLimitType(limitType);
		queryWayBill.setMailno(mailno);
		queryWayBill.setOrderId(orderId);
		queryWayBill.setPkfee(pkfee);
		queryWayBill.setWeight(weight);
		return queryWayBill;
	}
}
