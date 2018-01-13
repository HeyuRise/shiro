package com.pcbwx.shiro.service.impl;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pcbwx.shiro.bean.OrderZd;
import com.pcbwx.shiro.bean.QRCodeInfo;
import com.pcbwx.shiro.bean.QRReceiveInfo;
import com.pcbwx.shiro.bean.QRSenderInfo;
import com.pcbwx.shiro.bean.request.AddedService;
import com.pcbwx.shiro.bean.request.Order;
import com.pcbwx.shiro.bean.request.OrderFilter;
import com.pcbwx.shiro.bean.request.RequestOrderSearch;
import com.pcbwx.shiro.bean.request.RequestOrderZD;
import com.pcbwx.shiro.bean.response.ResponseOrder;
import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.dao.ExpressMapper;
import com.pcbwx.shiro.dao.ExpressProductMapper;
import com.pcbwx.shiro.dao.RecipientsAddressMapper;
import com.pcbwx.shiro.dao.RecipientsMapper;
import com.pcbwx.shiro.dao.SenderAddressMapper;
import com.pcbwx.shiro.dao.SenderMapper;
import com.pcbwx.shiro.dao.ServiceMapper;
import com.pcbwx.shiro.dao.ServiceRelationMapper;
import com.pcbwx.shiro.dao.SustenanceMapper;
import com.pcbwx.shiro.enums.ClickEnum;
import com.pcbwx.shiro.enums.DictionaryEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.enums.ExpressStatusEnum;
import com.pcbwx.shiro.model.Dictionary;
import com.pcbwx.shiro.model.Express;
import com.pcbwx.shiro.model.ExpressProduct;
import com.pcbwx.shiro.model.Recipients;
import com.pcbwx.shiro.model.RecipientsAddress;
import com.pcbwx.shiro.model.Sender;
import com.pcbwx.shiro.model.SenderAddress;
import com.pcbwx.shiro.model.ServiceRelation;
import com.pcbwx.shiro.model.Sustenance;
import com.pcbwx.shiro.service.ExpressService;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.utils.BASE64MD5Util;
import com.pcbwx.shiro.utils.DateTimeUtil;
import com.pcbwx.shiro.utils.HttpUtil;
import com.pcbwx.shiro.utils.JsonUtil;
import com.pcbwx.shiro.utils.StringUtil;
import com.pcbwx.shiro.utils.XMLUtil;
/**
 * 运单请求接口实现类
 * @author 孙贺宇
 *
 */
@Service("expressService")
public class ExpressServiceImpl implements ExpressService{
	
	private static final Logger logger = LogManager.getLogger(ExpressServiceImpl.class);
	
	// 匹配14位数字
	private String reg = "[0-9]{11}";
	private final Pattern pattern = Pattern.compile(reg);
	
	@Autowired
	private ExpressProductMapper expressProductMapper;
	@Autowired
	private ExpressMapper expressMapper;
	@Autowired
	private SustenanceMapper SustenanceMapper;
	@Autowired
	private ServiceMapper serviceMapper;
	@Autowired
	private ServiceRelationMapper serviceRelationMapper;
	@Autowired
	private SenderMapper senderMapper;
	@Autowired
	private SenderAddressMapper senderAddressMapper;
	@Autowired
	private RecipientsMapper recipientsMapper;
	@Autowired
	private RecipientsAddressMapper recipientsAddressMapper;
	
	@Autowired
	private CacheService cacheService;
	@Autowired
	private SupportService supportService;
	
	@Override
	public Integer goforOrderZD(String mailno, Integer number) {
		Express express = expressMapper.selectByMailno(mailno);
		if (express == null) {
			return 0;
		}
		String orderId = express.getOrderId();
		RequestOrderZD requestOrderZD = new RequestOrderZD(orderId, number);
		String response = HttpUtil.postXML(requestOrderZD.toXml());
		if (response == null) {
			return 0;
		}
		logger.info(response);
		OrderZd orderZD = XMLUtil.getOrderZD(response);
		String mailnoChildSqlSer = express.getMailNoChild();
		if (mailnoChildSqlSer == null) {
			express.setMailNoChild(orderZD.getMailnoChild());
		}else{
			express.setMailNoChild(mailnoChildSqlSer + "," + orderZD.getMailnoChild());
		}
		Integer parcelQ = express.getParcelNumber() + number;
		express.setParcelNumber(parcelQ);
		expressMapper.updateByPrimaryKeySelective(express);
		return 1;
	}
	
	@Override
	public Map<String, Object> expressOrder(WxtbAuthUser wxtbUser, Integer companyId, Order order, String orderId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Date now = new Date();
		String innerOrderId = order.getOrderId();
		String receiveTel = order.getReceiverTel();
		String receiveMobile = order.getReceiverMobile();
		String sendTel = order.getSendTel();
		String sendMobile = order.getSendMobile();
		// 整理收发件人联系方式
		if (receiveMobile != null) {
			Matcher receiveMatcher = pattern.matcher(receiveMobile);
			if (receiveMatcher.find()) {
				order.setReceiverMobile(receiveMatcher.group());
			}else {
				order.setReceiverMobile(null);
			}
		}
		if (receiveTel == null || receiveTel.equals("") || receiveTel.equals("-") ) {
			order.setReceiverTel(order.getReceiverMobile());
		}
		if (sendMobile != null) {
			Matcher sendMatcher = pattern.matcher(sendMobile);
			if (sendMatcher.find()) {
				order.setSendMobile(sendMatcher.group());
			}else {
				order.setSendMobile(null);
			}
		}
		if (sendTel == null || sendTel.equals("") || sendTel.equals("-")) {
			order.setSendTel(order.getSendMobile());
		}
		//----------------------------------------------
		if (innerOrderId == null || innerOrderId.equals("")) {
			innerOrderId = null;
		}
		order.setOrderId(orderId);
		String response = HttpUtil.postXML(order.toXml());
		logger.info(order.toXml());
		logger.info(response);
		String mainMailno = null;
		String mailChild = null;
		String destCode = null;
		String status = null;
		Integer filterResult = null;
		String remarkCode = null;
		String backReason = null;
		if (response == null) {
			result.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			result.put("msg", "请求超时，请重试");
			return result;
		}if (response.equals("")) {
			result.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			result.put("msg", "顺丰系统维护中");
			return result;
		}
		ResponseOrder responseOrder = null;
		try {
			responseOrder = XMLUtil.getOrderReponse(response);
		} catch (Exception e) {
			result.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			result.put("msg", "获取快递信息失败");
			return result;
		}
		if (responseOrder == null) {
			result.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			result.put("msg", "获取快递信息失败");
			return result;
		}
		// 如果顺丰返回错误信息，把错误信息返回前台
		Integer isOk = responseOrder.getIsOk();
		if (isOk != null && isOk == 0) {
			String errCode = responseOrder.getErrCode();
			result.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			String errorMsg = responseOrder.getErrMsg();
			backReason = errorMsg == null ? "系统错误" : errorMsg;
			if (errCode != null && errCode.startsWith("40")) {
				backReason = "顺丰系统维护中";
			}
			result.put("msg", backReason);
			return result;
		}
		filterResult = responseOrder.getFilterResult();
		if (filterResult == 3) {
			result.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			result.put("msg", "快件不可收派");
			return result;
		} else if (filterResult == 2) {
			status = ExpressStatusEnum.WAIT_PICK_UP.getCode();
		} else if (filterResult == 1) {
			status = ExpressStatusEnum.WAIT_PUSH.getCode();
		}
		logger.info(JsonUtil.obj2json(responseOrder));
		destCode = responseOrder.getDestcode();
		remarkCode = responseOrder.getRemark();
		String mailNo = responseOrder.getMailno();
		if (mailNo != null) {
			String[] mailNos = mailNo.split(",");
			mainMailno = mailNos[0];
			for (int i = 0; i < mailNos.length; i++) {
				if (i == 0) {
					continue;
				}
				if (mailChild == null) {
					mailChild = mailNos[i];
				} else {
					mailChild = mailChild + "," + mailNos[i];
				}
			}
		}
		Express express = new Express();
		express.setOrderId(orderId);
		express.setInnerOrderId(innerOrderId);
		express.setMailNo(mainMailno);
		express.setMailNoChild(mailChild);
		express.setDestCode(destCode);
		express.setSenderName(order.getSendContact());
		express.setSenderCompany(order.getSendCompany());
		express.setSenderTel(sendTel);
		express.setSenderMobile(sendMobile);
		express.setSenderProvince(order.getSendProvince());
		express.setSenderCity(order.getSendCity());
		express.setSenderCounty(order.getSendCounty());
		express.setSenderAddress(order.getSendAddress());
		express.setRecipientsCompany(order.getReceiverCompany());
		express.setRecipientsName(order.getReceiverContact());
		express.setRecipientsTel(receiveTel);
		express.setRecipientsMobile(receiveMobile);
		express.setRecipientsProvince(order.getReceiverProvince());
		express.setRecipientsCity(order.getReceiverCity());
		express.setRecipientsCounty(order.getReceiverCounty());
		express.setAddress(order.getReceiverAddress());
		Integer payType = order.getPayMethod();
		if (payType == null) {
			payType = 1;
		}
		String custId = order.getCustId();
		// 付款方式为寄方付，并且无月结卡号，付款方式为寄付月结
		if (payType == 1 && custId != null) {
			payType = 4;
		}
		Dictionary dictionary = cacheService.getDictionary(DictionaryEnum.PAY_METHOD, payType);
		if (dictionary != null) {
			express.setPayTypeName(dictionary.getValueStr());
		}
		express.setPayAccount(order.getCustId());
		Integer expressType = order.getExpressType();
		if (expressType == null) {
			expressType = 1;
		}
		List<ExpressProduct> expressProducts = expressProductMapper.selectEnableByCompanyIdAndProductCode(companyId, expressType.toString());
		if (expressProducts != null && !expressProducts.isEmpty()) {
			express.setProductId(expressProducts.get(0).getProductId());
		}
		express.setParcelNumber(order.getParcelQuantity() == null ? 1 : order.getParcelQuantity());
		express.setCreator(wxtbUser.getAccount());
		express.setFilterResult(filterResult);
		express.setBackReason(backReason);
		express.setRemarkCode(remarkCode);
		express.setStatus(status);
		express.setCreateTime(now);
		expressMapper.insertSelective(express);
		express = expressMapper.selectByOrderId(orderId);
		Integer expressId = express.getExpressId();
		/*
		 * 插入包裹信息
		 */
		Sustenance sustenance = new Sustenance();
		sustenance.setExpressId(expressId);
		sustenance.setName(order.getCargo().getName());
		sustenance.setNumber(order.getCargo().getCount());
		sustenance.setStatus(1);
		sustenance.setWeight(order.getCargo().getWeight());
		sustenance.setCreateTime(now);
		SustenanceMapper.insertSelective(sustenance);
		// 查看是否有打印权限
		Integer appear = ErrorCodeEnum.SUCCESS.getCode();
		Set<Integer> auths = supportService.getUserAuths(wxtbUser.getAccount());
		Dictionary dictionary2 = cacheService.getDictionary(DictionaryEnum.BUTTON, ClickEnum.PRINT.getCode());
		if (dictionary2 != null && dictionary2.getParamInt1() != null) {
			if (!auths.contains(dictionary2.getParamInt1())) {
				appear = ErrorCodeEnum.SYSTEM_ERROR.getCode();
			}
		}
		result.put("parcelQuantity", order.getParcelQuantity() == null ? 1 : order.getParcelQuantity());
		List<AddedService> addedServices = order.getAddedServices();
		if (addedServices == null || addedServices.isEmpty()) {
			if (filterResult == 1) {
				result.put("result", ErrorCodeEnum.ORDER_FILTER.getCode());
				result.put("msg", "顺丰人工筛单中，请等待");
				return result;
			}
			result.put("resultAppear", appear);
			result.put("result", ErrorCodeEnum.SUCCESS.getCode());
			result.put("mailno", mainMailno);
			return result;
		}
		/*
		 * 插入增值服务信息
		 */
		List<com.pcbwx.shiro.model.Service> services = serviceMapper.selectByCondition(null);
		Map<String, Integer> serviceMap = new HashMap<String, Integer>();
		for (com.pcbwx.shiro.model.Service service : services) {
			serviceMap.put(service.getCode(), service.getServiceId());
		}
		for (AddedService addedService : addedServices) {
			ServiceRelation serviceRelation = new ServiceRelation();
			serviceRelation.setCreateTime(now);
			serviceRelation.setExpressId(expressId);
			Integer serviceId = serviceMap.get(addedService.getName());
			if (serviceId == null) {
				continue;
			}
			serviceRelation.setServiceId(serviceId);
			serviceRelation.setStatus(1);
			String value = addedService.toValue();
			String appearValue = addedService.toAppearValue();
			if (value == null || value.equals("")) {
				value = null;
			}
			if (appearValue == null || appearValue.equals("")) {
				appearValue = value;
			}
			serviceRelation.setValue(value);
			serviceRelation.setValueShow(appearValue);
			serviceRelationMapper.insertSelective(serviceRelation);
		}
		if (filterResult == 1) {
			result.put("result", ErrorCodeEnum.ORDER_FILTER.getCode());
			result.put("msg", "顺丰人工筛单中，请等待");
			return result;
		}
		result.put("resultAppear", appear);
		result.put("mailno", mainMailno);
		result.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return result;
	}
	
	@Override
	public Map<String, Object> operateQRCode(String text) {
		Map<String, Object> response = new HashMap<String, Object>();
		Date now = new Date();
		List<String> result = null;
		// 处理重复提交的情况
		Integer count = text.indexOf("]");
		text = text.substring(0, count + 1);
		try {
			Gson g = new Gson();
			Type type = (Type) new TypeToken<List<String>>() {}.getType();
			result = g.fromJson(text, type);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			response.put("msg", "二维码解析失败");
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			return response;
		}
		if (result == null || result.isEmpty()) {
			response.put("msg", "二维码解析失败");
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			return response;
		}
		if (result.size() != 10) {
			response.put("msg", "二维码格式错误");
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			return response;
		}
		QRCodeInfo qrCodeInfo = new QRCodeInfo();
		QRSenderInfo senderInfo = new QRSenderInfo();
		QRReceiveInfo receiveInfo = new QRReceiveInfo();
		String msg = null;
		qrCodeInfo.setOrderId(result.get(0));
		String cargoName = result.get(1);
		if (cargoName != null && !cargoName.equals("")) {
			Dictionary dictionary = cacheService.getDictionary(DictionaryEnum.CARGO_NAME, cargoName);
			if (dictionary != null) {
				cargoName = dictionary.getValueStr();
			}
		}
		qrCodeInfo.setCargoName(cargoName);
		/*
		 * 寄件人信息
		 */
		String  senderCode = result.get(2);
		String senderInnerMD5 = result.get(3);
		if (senderCode == null || senderCode.equals("") || senderInnerMD5 == null || senderInnerMD5.equals("")) {
			msg = "二维码缺少寄件人信息";
		}else {
			Sender sender = senderMapper.selectByInnerCode(senderCode);
			if (sender != null) {
				String name = sender.getName();
				String tel = sender.getTelephone();
				String mobile = sender.getMobile();
				senderInfo.setContact(name);
				senderInfo.setTel(tel);
				senderInfo.setMoblie(mobile);
				name = name == null ? "" : name.trim();
				tel = tel == null ? "" : tel;
				mobile = mobile == null ? "" : mobile;
				String md5 = BASE64MD5Util.md5_16(name + mobile + tel);
				if (!senderInnerMD5.equals(md5)) {
					msg = "内外网寄件人信息不同步";
				}
			}else {
				msg = "外网缺少该寄件人信息";
			}
		}
		
		/*
		 * 寄件人地址信息
		 */
		String senderAddressIdStr = result.get(4);
		String senderAddressMD5 = result.get(5);
		if (senderAddressIdStr == null || senderAddressIdStr.equals("") || senderAddressMD5 == null || senderAddressMD5.equals("")) {
			String senderAddressMsg = "二维码缺少寄件人地址信息";
			msg = msg == null ? senderAddressMsg : msg + "," + senderAddressMsg ;
		}else {
			Integer senderAddressId = Integer.parseInt(senderAddressIdStr);
			SenderAddress senderAddress = senderAddressMapper.selectByInnerId(senderAddressId);
			if (senderAddress != null) {
				String province = senderAddress.getProvince();
				String city = senderAddress.getCity();
				String county = senderAddress.getDistrict();
				String address = senderAddress.getAddress();
				senderInfo.setProvince(province);
				senderInfo.setCity(city);
				senderInfo.setCounty(county);
				senderInfo.setAddress(address);
				province = province == null ? "" : province;
				city = city == null ? "" : city;
				county = county == null ? "" : county;
				address = address == null ? "" : address;
				String md5 = BASE64MD5Util.md5_16(province + city + county + address);
				if (!senderAddressMD5.equals(md5)) {
					String senderAddressMsg = "内外网寄件人地址信息不同步";
					msg = msg == null ? senderAddressMsg : msg + "," + senderAddressMsg ;
				}
			}else {
				String senderAddressMsg = "外网缺少该寄件人地址信息";
				msg = msg == null ? senderAddressMsg : msg + "," + senderAddressMsg ;
			}
		}
		/*
		 * 收件人信息
		 */
		String receiveCode = result.get(6);
		String receiveCodeMD5 = result.get(7);
		String tpa = "无锡市同步电子有限公司";
		String tpk = "无锡市同步电子科技有限公司";
		if (receiveCode == null || receiveCode.equals("") || receiveCodeMD5 == null || receiveCodeMD5.equals("")) {
			String senderAddressMsg = "二维码缺少收件人信息";
			msg = msg == null ? senderAddressMsg : msg + "," + senderAddressMsg ;
		}else {
			Recipients recipients = recipientsMapper.selectByInnerCode(receiveCode);
			if (recipients != null) {
				String name = recipients.getName();
				String company = recipients.getCompany();
				String tel = recipients.getTelephone();
				String mobile = recipients.getMobile();
				String senderCompany = recipients.getSendCompany();
				recipients.setUpdateTime(now);
				receiveInfo.setContact(name);
				receiveInfo.setCompany(company);
				receiveInfo.setTel(tel);
				receiveInfo.setMoblie(mobile);
				senderInfo.setCompany(senderCompany);
				name = name == null ? "" : name.trim();
				company = company == null ? "" : company.trim();
				tel = tel == null ? "" : tel;
				mobile = mobile == null ? "" : mobile;
				senderCompany = senderCompany == null ? "" : senderCompany;
				String md5 = BASE64MD5Util.md5_16(name + mobile + tel + company + senderCompany);
				if (!receiveCodeMD5.equals(md5)) {
					// 去除发件公司不同步问题，因为只有两个选项
					senderCompany = tpk;
					md5 = BASE64MD5Util.md5_16(name + mobile + tel + company + senderCompany);
					if (!receiveCodeMD5.equals(md5)) {
						senderCompany = tpa;
						md5 = BASE64MD5Util.md5_16(name + mobile + tel + company + senderCompany);
						if (!receiveCodeMD5.equals(md5)) {
							String receiveMsg = "内外网收件人信息不同步";
							msg = msg == null ? receiveMsg : msg + "," + receiveMsg ;
						}else {
							recipients.setSendCompany(senderCompany);
							recipientsMapper.updateByPrimaryKeySelective(recipients);
						}
					}else {
						recipients.setSendCompany(senderCompany);
						recipientsMapper.updateByPrimaryKeySelective(recipients);
					}
				}
				senderInfo.setCompany(senderCompany);
			}else{
				String receiveMsg = "外网缺少该收件人信息";
				msg = msg == null ? receiveMsg : msg + "," + receiveMsg ;
			}
		}
		/*
		 * 收件人地址信息
		 */
		String receiveAddressIdStr = result.get(8);
		String receiveAddressMD5 = result.get(9);
		if (receiveAddressIdStr == null || receiveAddressIdStr.equals("") || receiveAddressMD5 == null || receiveAddressMD5.equals("")) {
			String senderAddressMsg = "二维码缺少收件人地址信息";
			msg = msg == null ? senderAddressMsg : msg + "," + senderAddressMsg ;
		}else {
			Integer receiveAddressId = Integer.parseInt(receiveAddressIdStr);
			RecipientsAddress recipientsAddress = recipientsAddressMapper.selectByInnerId(receiveAddressId);
			if (recipientsAddress != null) {
				String province = recipientsAddress.getProvince();
				String city = recipientsAddress.getCity();
				String county = recipientsAddress.getDistrict();
				String address = recipientsAddress.getAddress();
				receiveInfo.setProvince(province);
				receiveInfo.setCity(city);
				receiveInfo.setCounty(county);
				receiveInfo.setAddress(address);
				province = province == null ? "" : province;
				city = city == null ? "" : city;
				county = county == null ? "" : county;
				address = address == null ? "" : address;
				String md5 = BASE64MD5Util.md5_16(province + city + county + address);
				if (!receiveAddressMD5.equals(md5)) {
					String receiveAddressMsg = "内外网收件人地址信息不同步";
					msg = msg == null ? receiveAddressMsg : msg + "," + receiveAddressMsg ;
				}
			}else {
				String receiveAddressMsg = "外网缺少该收件人地址信息";
				msg = msg == null ? receiveAddressMsg : msg + "," + receiveAddressMsg ;
			}
		}
		// -----------------------------
		qrCodeInfo.setReceiveInfo(receiveInfo);
		qrCodeInfo.setSenderInfo(senderInfo);
		if (!StringUtil.isEmpty(msg)) {
			LogContext.error("不同步", msg + "-----------" + text);
		}
		response.put("msg", msg);
		response.put("expressInfo", qrCodeInfo);
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}

	@Override
	public void operateTimeOutExpress() {
		Date todayStart = DateTimeUtil.truncateDateTime(new Date());
		List<Express> expresses = expressMapper.selectTimeOutExpress(todayStart);
		if (expresses == null) {
			return;
		}
		for (Express express : expresses) {
			String status = null;
			Integer filterResult = null;
			String remark = null;
			String orderId = express.getOrderId();
			RequestOrderSearch requestOrderSearch = new RequestOrderSearch(
					orderId);
			String response = HttpUtil.postXML(requestOrderSearch.toXml());
			if (response == null) {
				continue;
			}
			ResponseOrder responseOrder = XMLUtil.getOrderReponse(response);
			if (responseOrder == null) {
				continue;
			}
			if (responseOrder.getIsOk() == 0) {
				express.setMailNo("获取运单失败");
			} else {
				String mailNo = responseOrder.getMailno();
				String destCode = responseOrder.getDestcode();
				String[] mailNos = mailNo.split(",");
				String mainMailno = mailNos[0];
				String mailChild = null;
				for (int i = 0; i < mailNos.length; i++) {
					if (i == 0) {
						continue;
					}
					if (mailChild == null) {
						mailChild = mailNos[i];
					} else {
						mailChild = mailChild + "," + mailNos[i];
					}
				}
				filterResult = responseOrder.getFilterResult();
				if (filterResult == 2) {
					status = ExpressStatusEnum.WAIT_PICK_UP.getCode();
				}else if (filterResult == 3) {
					status = ExpressStatusEnum.ERROR.getCode();
				}
				String remarkCode = responseOrder.getRemark();
				if (remarkCode != null) {
					if (remarkCode.equals("1")) {
						remark = "收方超范围";
					}else if (remarkCode.equals("2")) {
						remark = "派方超范围";
					}else if (remarkCode.equals("3")) {
						remark = "其它原因";
					}else {
						remark = remarkCode;
					}
				}
				express.setMailNo(mainMailno);
				express.setMailNoChild(mailChild);
				express.setDestCode(destCode);
				express.setFilterResult(filterResult);
				express.setRemarkCode(remarkCode);
				express.setBackReason(remark);
				express.setStatus(status);
			}
			expressMapper.updateByPrimaryKeySelective(express);
		}
	}

	@Override
	public Integer operateOrderFilterXml(String xml) {
		OrderFilter orderFilter = XMLUtil.operateOrderFilter(xml);
		if (orderFilter == null) {
			return 0;
		}
		logger.info(JsonUtil.obj2json(orderFilter));
		String orderId = orderFilter.getOrderId();
		Express express = expressMapper.selectByOrderId(orderId);
		if (express == null) {
			return 1;
		}
		Integer filterResult = orderFilter.getFilterResult();
		if (filterResult == null) {
			return 0;
		}
		String remark = null;
		String remarkCode = orderFilter.getRemark();
		if (filterResult == 3) {
			if (remarkCode.equals("1")) {
				remark = "收方超范围";
			}else if (remarkCode.equals("2")) {
				remark = "派方超范围";
			}else if (remarkCode.equals("3")) {
				remark = "其它原因";
			}else {
				remark = remarkCode;
			}
		}
		String mailno = orderFilter.getMailno();
		String mainMailno = null;
		String mailChild = null;
		if (mailno != null && !mailno.equals("")) {
			String[] mailNos = mailno.split(",");
			mainMailno = mailNos[0];
			for (int i = 0; i < mailNos.length; i++) {
				if (i == 0) {
					continue;
				}
				if (mailChild == null) {
					mailChild = mailNos[i];
				} else {
					mailChild = mailChild + "," + mailNos[i];
				}
			}
		}
		String destCode = orderFilter.getDestcode();
		express.setMailNo(mainMailno);
		express.setMailNoChild(mailChild);
		if (destCode != null && !destCode.equals("")) {
			express.setDestCode(destCode);
		}
		express.setRemarkCode(remarkCode);
		express.setBackReason(remark);
		String status = null;
		if (filterResult == 2) {
			status = ExpressStatusEnum.WAIT_PICK_UP.getCode();
		}else {
			status = ExpressStatusEnum.ERROR.getCode();
		}
		express.setStatus(status);
		express.setFilterResult(filterResult);
		expressMapper.updateByPrimaryKeySelective(express);
		return 1;
	}

}
