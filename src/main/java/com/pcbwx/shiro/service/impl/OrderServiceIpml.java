package com.pcbwx.shiro.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcbwx.shiro.bean.ExpressDetail;
import com.pcbwx.shiro.bean.ExpressItem;
import com.pcbwx.shiro.bean.ExpressOrder;
import com.pcbwx.shiro.bean.ExpressOrderExport;
import com.pcbwx.shiro.bean.PackageInfo;
import com.pcbwx.shiro.bean.PayInfo;
import com.pcbwx.shiro.bean.ReceiveItem;
import com.pcbwx.shiro.bean.SenderItem;
import com.pcbwx.shiro.bean.ServiceItem;
import com.pcbwx.shiro.bean.request.WaybillService;
import com.pcbwx.shiro.bean.response.QueryWayBill;
import com.pcbwx.shiro.bean.system.AddressInfo;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.dao.AddressMapper;
import com.pcbwx.shiro.dao.ExpressMapper;
import com.pcbwx.shiro.dao.ExpressProductMapper;
import com.pcbwx.shiro.dao.RoutePushMapper;
import com.pcbwx.shiro.dao.ServiceMapper;
import com.pcbwx.shiro.dao.ServiceRelationMapper;
import com.pcbwx.shiro.dao.SustenanceMapper;
import com.pcbwx.shiro.enums.DictionaryEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.enums.ExpressStatusEnum;
import com.pcbwx.shiro.model.Address;
import com.pcbwx.shiro.model.Dictionary;
import com.pcbwx.shiro.model.Express;
import com.pcbwx.shiro.model.ExpressProduct;
import com.pcbwx.shiro.model.RoutePush;
import com.pcbwx.shiro.model.ServiceRelation;
import com.pcbwx.shiro.model.Sustenance;
import com.pcbwx.shiro.service.OrderService;
import com.pcbwx.shiro.utils.DataUtil;
import com.pcbwx.shiro.utils.DateTimeUtil;
import com.pcbwx.shiro.utils.ExcelUtil;
import com.pcbwx.shiro.utils.HttpUtil;
import com.pcbwx.shiro.utils.XMLUtil;
/**
 * 订单接口实现类
 * @author 孙贺宇
 *
 */
@Service("orderservice")
public class OrderServiceIpml implements OrderService {

	//private static final Logger logger = LogManager.getLogger(OrderServiceIpml.class);
	
	@Autowired
	private CacheService cacheService;
	@Autowired
	private AddressMapper addressMapper;
	@Autowired
	private RoutePushMapper routePushMapper;
	@Autowired
	private ExpressMapper expressMapper;
	@Autowired
	private ExpressProductMapper expressProductMapper;
	@Autowired
	private SustenanceMapper SustenanceMapper;
	@Autowired
	private ServiceRelationMapper serviceRelationMapper;
	@Autowired
	private ServiceMapper serviceMapper;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getExpressOrders(String orderId, String mailno,
			String mailnoChild, String sendCompany, String receiveCompany,
			String sendContact, String receiveContact, String sendDateBegin,
			String sendDateEnd, String receiveDateBegin, String receiveDateEnd,
			String status, Integer page, Integer size) {
		Integer start = null;
		if (page == null || size == null) {
			size = null;
		}else {
			start = size * (page - 1);
		}
		Map<String, Object> expressMap = getExpressList(orderId, mailno, mailnoChild,
				sendCompany, receiveCompany, sendContact, receiveContact,
				sendDateBegin, sendDateEnd, receiveDateBegin, receiveDateEnd,
				status, start, size);
		List<Express> expresses = (List<Express>) expressMap.get("rows");
		if (expresses == null) {
			return expressMap;
		}
		Set<Integer> expressIds = new HashSet<Integer>();
		for (Express express : expresses) {
			expressIds.add(express.getExpressId());
		}
		List<ExpressOrder> expressOrders = new ArrayList<ExpressOrder>();
		/*
		 * 状态map
		 */
		List<Dictionary> statusDList = cacheService
				.getDictionarys(DictionaryEnum.EXPRESS_STATUS);
		if (statusDList == null || statusDList.isEmpty()) {
			statusDList = new ArrayList<Dictionary>();
		}
		Map<String, String> statusMap = new HashMap<String, String>();
		for (Dictionary dictionary : statusDList) {
			statusMap.put(dictionary.getInnerCode(), dictionary.getValueStr());
		}
		/*
		 * 获取收件时间map
		 */
		Map<Integer, Date> receiveDateMap = getReceiveDateMap(expressIds, expresses);
		
		for (Express express : expresses) {
			ExpressOrder expressOrder = new ExpressOrder(express);
			Date receiveDate = receiveDateMap.get(express.getExpressId());
			String receiveDateStr = DateTimeUtil.date2dateStr(receiveDate);
			expressOrder.setReceiveDate(receiveDateStr);
			ExpressProduct expressProduct = cacheService.getExpressProduct(express
					.getProductId());
			if (expressProduct != null) {
				expressOrder.setExpressType(expressProduct.getProductName());
			}
			expressOrder.setStatus(statusMap.get(express.getStatus()));
			Date sendDate = express.getCreateTime();
			String sendDateStr = DateTimeUtil.date2dateStr(sendDate);
			expressOrder.setSendDate(sendDateStr);
			expressOrders.add(expressOrder);
		}
		expressMap.put("rows", expressOrders);
		return expressMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SXSSFWorkbook expressExport(String orderId, String mailno, String mailnoChild,
			String sendCompany, String receiveCompany, String sendContact,
			String receiveContact, String sendDateBegin, String sendDateEnd,
			String receiveDateBegin, String receiveDateEnd, String status) {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		Sheet xssfSheet = workbook.createSheet();
		// 生成标题行样式
		CellStyle cellStyle = ExcelUtil.generateColumnTitleStyle(workbook);
		String[] titles = { "订单号", "运单号", "子单号", "寄件日期", "收件状态", "收件时间",
				"收方地址", "收方联系电话", "收方联系人", "收方公司", "收方手机号码", "托寄物内容", "托寄物数量",
				"寄件公司", "寄件联系地址", "寄件联系电话", "寄件联系人", "原寄地", "目的地", "计费重量",
				"实际重量", "运费", "备注", "业务类型", "付款方式", "件数", "增值服务" };
		// 生成标题行
		ExcelUtil.setColumnTitle(xssfSheet, titles, cellStyle, 0);
		// 设置列宽度
		ExcelUtil.setColumnWidth(xssfSheet, 3600);
		Map<String, Object> expressMap = getExpressList(orderId, mailno, mailnoChild,
				sendCompany, receiveCompany, sendContact, receiveContact,
				sendDateBegin, sendDateEnd, receiveDateBegin, receiveDateEnd,
				status, null, null);
		List<Express> expresses = (List<Express>) expressMap.get("rows");
		if (expresses.isEmpty()) {
			return workbook;
		}
		Set<Integer> expressIds = new HashSet<Integer>();
		for (Express express : expresses) {
			expressIds.add(express.getExpressId());
		}
		/*
		 * 状态map
		 */
		List<Dictionary> statusDList = cacheService
				.getDictionarys(DictionaryEnum.EXPRESS_STATUS);
		if (statusDList == null || statusDList.isEmpty()) {
			statusDList = new ArrayList<Dictionary>();
		}
		Map<String, String> statusMap = new HashMap<String, String>();
		for (Dictionary dictionary : statusDList) {
			statusMap.put(dictionary.getInnerCode(), dictionary.getValueStr());
		}
		/*
		 * 获取收件时间map
		 */
		Map<Integer, Date> receiveDateMap = getReceiveDateMap(expressIds, expresses);
		/*
		 * 获取增值服务map
		 */
		Map<Integer, String> serviceNameMap = getServiceMap(expressIds);
		/*
		 *获取包裹内容map
		 */
		List<Sustenance> sustenances = SustenanceMapper.selectByExpressIds(expressIds);
		Map<Integer, Sustenance> susMap = new HashMap<Integer, Sustenance>();
		try {
			susMap = DataUtil.list2map(sustenances, Sustenance.class, "expressId");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		List<ExpressOrderExport> expressOrderExports = new ArrayList<ExpressOrderExport>();
		for (Express express : expresses) {
			ExpressOrderExport expressOrderExport = new ExpressOrderExport(express);
			String statusCode = express.getStatus();
			if (statusCode != null) {
				expressOrderExport.setStatus(statusMap.get(statusCode));
			}
			Date receiveDate = receiveDateMap.get(express.getExpressId());
			String receiveDateStr = DateTimeUtil.date2dateStr(receiveDate);
			expressOrderExport.setReceiveDate(receiveDateStr);
			Sustenance sustenance = susMap.get(express.getExpressId());
			if (sustenance != null) {
				expressOrderExport.setCargoName(sustenance.getName());
				String count = sustenance.getNumber();
				if (count != null && !count.equals("")) {
					expressOrderExport.setCargoCount(count);
				}
				expressOrderExport.setRealWeight(sustenance.getWeight());
				expressOrderExport.setPayWeight(sustenance.getPayWeight());
			}
			ExpressProduct expressProduct = cacheService.getExpressProduct(express.getProductId());
			if (expressProduct != null) {
				expressOrderExport.setExpressType(expressProduct.getProductName());
			}
			expressOrderExport.setAddServices(serviceNameMap.get(express.getExpressId()));
			expressOrderExports.add(expressOrderExport);
		}
		getworkbook(xssfSheet, expressOrderExports);
		return workbook;
	}
	

	@Override
	public Map<String, Object> getExpressDetail(String mailno, String orderId) {
		Map<String, Object> response = new HashMap<String, Object>();
		if ((mailno == null || mailno.equals("")) && (orderId == null || orderId.equals(""))) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", ErrorCodeEnum.SYSTEM_ERROR.getDescr());
		}
		Express express = null;
		if (mailno != null && !mailno.equals("")) {
			express = expressMapper.selectByMailno(mailno);
		}else {
			express = expressMapper.selectByOrderId(orderId);
		}	
		if (express == null) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "系统错误");
			return response;
		}
		Integer expressId = express.getExpressId();
		List<String> opCodes = getCodeList(ExpressStatusEnum.DELEIVER, null);
		List<RoutePush> routePushs = routePushMapper
				.selectByExpressId(expressId);
		Date shouDate = null;
		if (opCodes != null && !opCodes.isEmpty()) {
			for (RoutePush routePush : routePushs) {
				if (opCodes.contains(routePush.getOpCode())) {
					shouDate = routePush.getAcceptTime();
					break;
				}
			}
		}
		ExpressDetail expressDetail = new ExpressDetail();
		expressDetail.setDestCode(express.getDestCode());
		expressDetail.setCustId(express.getPayAccount());
		mailno = express.getMailNo();
		List<String> mailnos = new ArrayList<String>();
		mailnos.add(mailno);
		List<String> mailnoChilds = new ArrayList<String>();
		String mailnoChild = express.getMailNoChild();
		if (mailnoChild != null) {
			String[] items = mailnoChild.split(",");
			for (String string : items) {
				mailnos.add(string);
				mailnoChilds.add(string);
			}
		}
		expressDetail.setMailnos(mailnos);
		/*
		 * 业务类型
		 */
		Integer productId = express.getProductId();
		ExpressProduct expressProduct = expressProductMapper
				.selectByPrimaryKey(productId);
		if (expressProduct != null) {
			expressDetail.setExpressType(expressProduct.getProductName());
		}
		/*
		 * 快递单详情
		 */
		ExpressItem expressItem = new ExpressItem();
		expressItem.setMailno(mailno);
		expressItem.setMailnoChild(mailnoChilds);
		expressItem.setReceiver(express.getRecipientsName());
		String shouDateStr = DateTimeUtil.date2dateStr(shouDate);
		expressItem.setReceiveDate(shouDateStr);
		Dictionary dictionary = cacheService.getDictionary(
				DictionaryEnum.EXPRESS_STATUS, express.getStatus());
		if (dictionary != null) {
			expressItem.setStatus(dictionary.getValueStr());
		}
		expressItem.setPrintCount(express.getCount());
		expressDetail.setExpress(expressItem);
		/*
		 * 寄件方信息
		 */
		SenderItem senderItem = new SenderItem();
		senderItem.setCompany(express.getSenderCompany());
		senderItem.setContact(express.getSenderName());
		senderItem.setTel(express.getSenderTel());
		senderItem.setMobile(express.getSenderMobile());
		String senderAddress = (express.getSenderProvince() == null ? "" : express.getSenderProvince())
				+ (express.getSenderCity() == null ? "" : express.getSenderCity())
				+ (express.getSenderCounty() == null ? "" : express.getSenderCounty())
				+ (express.getSenderAddress() == null ? "" : express.getSenderAddress());
		senderItem.setAddress(senderAddress);
		expressDetail.setSender(senderItem);
		/*
		 * 收件方信息
		 */
		ReceiveItem receiveItem = new ReceiveItem();
		receiveItem.setCompany(express.getRecipientsCompany());
		receiveItem.setContact(express.getRecipientsName());
		receiveItem.setTel(express.getRecipientsTel());
		receiveItem.setMobile(express.getRecipientsMobile());
		String receiveAddress = (express.getRecipientsProvince() == null ? "" : express.getRecipientsProvince())
				+ (express.getRecipientsCity() == null ? "" : express.getRecipientsCity())
				+ (express.getRecipientsCounty() == null ? "" : express.getRecipientsCounty())
				+ (express.getAddress() == null ? "" : express.getAddress());
		receiveItem.setAddress(receiveAddress);
		expressDetail.setReceiver(receiveItem);
		/*
		 * 付款方式信息
		 */
		PayInfo payInfo = new PayInfo();
		payInfo.setCustId(express.getPayAccount());
		payInfo.setPayTypeName(express.getPayTypeName());
		expressDetail.setPayInfo(payInfo);
		/*
		 * 包裹信息
		 */
		PackageInfo packageInfo = new PackageInfo();
		Sustenance sustenance = SustenanceMapper.selectByExpressId(expressId);
		if (sustenance == null) {
			sustenance = new Sustenance();
		}
		packageInfo.setWeight(sustenance.getWeight());
		packageInfo.setPayWeight(sustenance.getPayWeight());
		packageInfo.setCount(sustenance.getNumber());
		packageInfo.setName(sustenance.getName());
		packageInfo.setParcelQuantity(express.getParcelNumber());
		packageInfo.setMoney(express.getMoney());
		packageInfo.setOrderId(express.getOrderId());
		expressDetail.setPackageInfo(packageInfo);
		/*
		 *增值服务 
		 */
		List<ServiceItem> serviceItems = new ArrayList<ServiceItem>();
		List<ServiceRelation> serviceRelations = serviceRelationMapper.selectByExpressId(expressId);
		if (serviceRelations == null || serviceRelations.isEmpty()) {
			expressDetail.setServiceInfos(serviceItems);
			response.put("result", ErrorCodeEnum.SUCCESS.getCode());
			response.put("detail", expressDetail);
			return response;
		}
		for (ServiceRelation serviceRelation : serviceRelations) {
			Integer serviceId = serviceRelation.getServiceId();
			if (serviceId == null) {
				continue;
			}
			com.pcbwx.shiro.model.Service service = serviceMapper.selectByPrimaryKey(serviceId);
			if (service == null) {
				continue;
			}
			ServiceItem serviceItem = new ServiceItem();
			serviceItem.setServiceName(service.getName());
			String key = service.getKey1();
			List<String> keyBeans = new ArrayList<String>();
			if (key != null && !key.equals("")) {
				String[] keys = key.split(",");
				for (String string : keys) {
					keyBeans.add(string);
				}
			}
			serviceItem.setKey(keyBeans);
			String value = serviceRelation.getValueShow();
			List<String> valueBeans = new ArrayList<String>();
			if (value == null) {
				valueBeans.add(null);
			}else {
				value = " " + value + " ";
				String[] values = value.split(",");
				for (String string : values) {
					valueBeans.add(string.trim());
				}
			}
			serviceItem.setValue(valueBeans);
			serviceItems.add(serviceItem);
		}
		expressDetail.setServiceInfos(serviceItems);
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		response.put("detail", expressDetail);
		return response;
	}
	
	@Override
	public void addMailNoInfo() {
		Set<String> statues = new HashSet<String>();
		statues.add(ExpressStatusEnum.DELEIVER.getCode());
		statues.add(ExpressStatusEnum.RECEIVED.getCode());
		List<Express> expresses = expressMapper.getNoMailnoInfos(statues);
		if (expresses == null || expresses.isEmpty()) {
			return;
		}
		Set<Integer> expressIds = new HashSet<Integer>();
		Map<Integer, Express> expressMap = new HashMap<Integer, Express>();
		for (Express express : expresses) {
			Integer expressId = express.getExpressId();
			expressIds.add(expressId);
			expressMap.put(expressId, express);
		}
		List<Sustenance> sustenances = SustenanceMapper.selectByExpressIds(expressIds);
		for (Sustenance sustenance : sustenances) {
			Express express = expressMap.get(sustenance.getExpressId());
			if (express == null) {
				continue;
			}
			String mailno = express.getMailNo();
			if (mailno == null) {
				continue;
			}
			QueryWayBill queryWayBill = getMailFeeInfo(mailno);
			if (queryWayBill == null) {
				continue;
			}
			BigDecimal payWeightDec = queryWayBill.getWeight();
			BigDecimal moneyDec = queryWayBill.getExpressCost();
			String payWeight = payWeightDec == null ? null : payWeightDec.toString();
			String money = moneyDec == null ? null : moneyDec.toString();
			sustenance.setPayWeight(payWeight);
			express.setMoney(money);
			SustenanceMapper.updateByPrimaryKeySelective(sustenance);
			expressMapper.updateByPrimaryKeySelective(express);
		}
	}
	
	private void getworkbook(Sheet xssfSheet, List<ExpressOrderExport> expressOrderExports){
		Integer count = 1;
		Row row = null;
		Cell cell = null;
		for (ExpressOrderExport express : expressOrderExports) {
			row = xssfSheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(express.getOrderId());
			cell = row.createCell(1);
			cell.setCellValue(express.getMailno());
			cell = row.createCell(2);
			cell.setCellValue(express.getMailnoChild());
			cell = row.createCell(3);
			cell.setCellValue(express.getSendDate());
			cell = row.createCell(4);
			cell.setCellValue(express.getStatus());
			cell = row.createCell(5);
			cell.setCellValue(express.getReceiveDate());
			cell = row.createCell(6);
			cell.setCellValue(express.getReceiveAddress());
			cell = row.createCell(7);
			cell.setCellValue(express.getReceiveTel());
			cell = row.createCell(8);
			cell.setCellValue(express.getReceiveContact());
			cell = row.createCell(9);
			cell.setCellValue(express.getReceiveCompany());
			cell = row.createCell(10);
			cell.setCellValue(express.getReceiveMobile());
			cell = row.createCell(11);
			cell.setCellValue(express.getCargoName());
			cell = row.createCell(12);
			cell.setCellValue(express.getCargoCount());
			cell = row.createCell(13);
			cell.setCellValue(express.getSendCompany());
			cell = row.createCell(14);
			cell.setCellValue(express.getSendAddress());
			cell = row.createCell(15);
			cell.setCellValue(express.getSendTel());
			cell = row.createCell(16);
			cell.setCellValue(express.getSendContact());
			cell = row.createCell(17);
			cell.setCellValue(express.getSendCity());
			cell = row.createCell(18);
			cell.setCellValue(express.getReceiveCity());
			cell = row.createCell(19);
			cell.setCellValue(express.getPayWeight());
			cell = row.createCell(20);
			cell.setCellValue(express.getRealWeight());
			cell = row.createCell(21);
			cell.setCellValue(express.getMoney());
			cell = row.createCell(22);
			cell.setCellValue(express.getNote());
			cell = row.createCell(23);
			cell.setCellValue(express.getExpressType());
			cell = row.createCell(24);
			cell.setCellValue(express.getPayType());
			cell = row.createCell(25);
			cell.setCellValue(express.getParcelQuantity());
			cell = row.createCell(26);
			cell.setCellValue(express.getAddServices());
			count++;
		}
	}
	
	/**
	 * 获取运单id与运单揽件时间的map
	 */
	private Map<Integer, Date> getReceiveDateMap(Set<Integer> expressIds, List<Express> expresses){
		Map<Integer, Date> receiveDateMap = new HashMap<Integer, Date>();
		if (expressIds.isEmpty()) {
			return receiveDateMap;
		}
		// 获取配送中状态下的路由码集合
		List<String> codeList = getCodeList(ExpressStatusEnum.DELEIVER, null);
		List<RoutePush> routePushs = routePushMapper
				.selectByOPCodesAndExpressIds(codeList, expressIds);
		if (routePushs == null) {
			routePushs = new ArrayList<RoutePush>();
		}
		for (RoutePush routePush : routePushs) {
			Date acceptDate = routePush.getAcceptTime();
			Date beforeDate = receiveDateMap.get(routePush.getExpressId());
			if (beforeDate != null && beforeDate.before(acceptDate)) {
				continue;
			}
			receiveDateMap.put(routePush.getExpressId(), acceptDate);
		}
		return receiveDateMap;
	}
	
	/**
	 * 获取运单id与增值服务的map
	 * @param expressIds
	 * @return
	 */
	private Map<Integer, String> getServiceMap(Set<Integer> expressIds){
		List<ServiceRelation> serviceRelations = serviceRelationMapper.selectByExpressIds(expressIds);
		if (serviceRelations == null || serviceRelations.isEmpty()) {
			return new HashMap<Integer, String>();
		}
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		for (ServiceRelation serviceRelation : serviceRelations) {
			List<String> names = map.get(serviceRelation.getExpressId());
			com.pcbwx.shiro.model.Service service = cacheService.getService(serviceRelation.getServiceId());
			if (service == null) {
				continue;
			}
			if (names == null) {
				names = new ArrayList<String>();
			}
			names.add(service.getName());
			map.put(serviceRelation.getExpressId(), names);
		}
		Map<Integer, String> serviceNameMap = new HashMap<Integer, String>();
		for (Integer expressId : map.keySet()) {
			List<String> names = map.get(expressId);
			String name = null;
			for (String string : names) {
				if (name == null) {
					name = string;
				}else {
					name = name + "," + string;
				}
			}
			serviceNameMap.put(expressId, name);
		}
		return serviceNameMap;
	}


	/**
	 * 根据条件获取运单数据库列表集合
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
	private Map<String, Object> getExpressList(String orderId, String mailno, String mailnoChild,
			String sendCompany, String receiveCompany, String sendContact,
			String receiveContact, String sendDateBegin, String sendDateEnd,
			String receiveDateBegin, String receiveDateEnd, String status, Integer start, Integer size) {
		List<String> statusList = null;
		if (status != null) {
			List<Dictionary> statusDList = cacheService
					.getDictionarys(DictionaryEnum.EXPRESS_STATUS);
			if (statusDList != null && !statusDList.isEmpty()) {
				statusList = new ArrayList<String>();
			}
			if (statusList != null) {
				for (Dictionary dictionary : statusDList) {
					if (dictionary.getValueStr().contains(status)) {
						statusList.add(dictionary.getInnerCode());
					}
				}
			}
		}
		Set<Integer> expressIds = getExpressIdsByTime(receiveDateBegin,
				receiveDateEnd);
		Date sendBegin = DateTimeUtil.dateStr2date(sendDateBegin);
		Date sendEnd = DateTimeUtil.dateStr2date(sendDateEnd);
		sendEnd = DateTimeUtil.getTheDayLastTime(sendEnd);
		// 获取总个数
		Integer total = expressMapper.getSelectConditionSize(orderId, mailno,
				mailnoChild, sendCompany, receiveCompany, sendContact,
				receiveContact, sendBegin, sendEnd, statusList,
				expressIds);
		List<Express> expresss = expressMapper.selectByCondition(orderId, mailno,
				mailnoChild, sendCompany, receiveCompany, sendContact,
				receiveContact, sendBegin, sendEnd, statusList, expressIds, start, size);
		if (expresss == null || expresss.isEmpty()) {
			expresss = new ArrayList<Express>();
		}
		Map<String, Object> expressMap = new HashMap<String, Object>();
		expressMap.put("total", total);
		expressMap.put("rows", expresss);
		return expressMap;
	}

	@Override
	public Map<String, Object> getProvinces() {
		Map<Integer, Address> addressMap = cacheService.getAddressMap();
		if (addressMap == null || addressMap.isEmpty()) {
			return null;
		}
		Set<String> pronvinceSet = new HashSet<String>();
		for (Map.Entry<Integer, Address> entry : addressMap.entrySet()) {
			pronvinceSet.add(entry.getValue().getProvince());
		}
		Integer a = 0;
		JSONArray jsonArray = new JSONArray();
		for (String string : pronvinceSet) {
			a = a + 1;
			JSONObject json = new JSONObject();
			json.put("id", a);
			json.put("province", string);
			jsonArray.add(json);
		}
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("size", jsonArray.size());
		response.put("items", jsonArray);
		return response;
	}

	@Override
	public Map<String, Object> getCitys(String province) {
		Map<Integer, Address> addressMap = cacheService.getAddressMap();
		if (addressMap == null || addressMap.isEmpty()) {
			return null;
		}
		Set<String> city = new HashSet<String>();
		for (Map.Entry<Integer, Address> entry : addressMap.entrySet()) {
			Address address = entry.getValue();
			if (address.getProvince().equals(province)) {
				city.add(address.getCity());
			}
		}
		Integer a = 0;
		JSONArray jsonArray = new JSONArray();
		for (String string : city) {
			a = a + 1;
			JSONObject json = new JSONObject();
			json.put("id", a);
			json.put("city", string);
			jsonArray.add(json);
		}
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("size", jsonArray.size());
		response.put("items", jsonArray);
		return response;
	}

	@Override
	public Map<String, Object> getCountys(String province, String city) {
		Map<Integer, Address> addressMap = cacheService.getAddressMap();
		if (addressMap == null || addressMap.isEmpty()) {
			return null;
		}
		Set<String> county = new HashSet<String>();
		for (Map.Entry<Integer, Address> entry : addressMap.entrySet()) {
			Address address = entry.getValue();
			if (address.getProvince().equals(province) && address.getCity().equals(city)) {
				county.add(address.getCounty());
			}
		}
		Integer a = 0;
		JSONArray jsonArray = new JSONArray();
		for (String string : county) {
			a = a + 1;
			JSONObject json = new JSONObject();
			json.put("id", a);
			json.put("county", string);
			jsonArray.add(json);
		}
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("size", jsonArray.size());
		response.put("items", jsonArray);
		return response;
	}

	@Override
	public List<AddressInfo> getAddressInfos(String province, String city,
			String county) {
		List<Address> addresses = addressMapper.selectByCondition(province,
				city, county);
		if (addresses == null) {
			addresses = new ArrayList<Address>();
		}
		List<AddressInfo> addressInfos = new ArrayList<AddressInfo>();
		for (Address address : addresses) {
			AddressInfo addressInfo = new AddressInfo();
			addressInfo.setProvince(address.getProvince());
			addressInfo.setCity(address.getCity());
			addressInfo.setCounty(address.getCounty());
			addressInfos.add(addressInfo);
		}
		return addressInfos;
	}
	
	@Override
	public XSSFWorkbook addressExport(String province, String city,
			String county) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet xssfSheet = workbook.createSheet();
		CellStyle cellStyle = ExcelUtil.generateColumnTitleStyle(workbook);
		String[] titles = { "省", "市", "区/县"};
		ExcelUtil.setColumnTitle(xssfSheet, titles, cellStyle, 0);
		ExcelUtil.setColumnWidth(xssfSheet, 3600);
		List<Address> addresses = addressMapper.selectByCondition(province,
				city, county);
		if (addresses == null) {
			addresses = new ArrayList<Address>();
		}
		Integer count = 1;
		Row row = null;
		Cell cell = null;
		for (Address address : addresses) {
			row = xssfSheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(address.getProvince());
			cell = row.createCell(1);
			cell.setCellValue(address.getCity());
			cell = row.createCell(2);
			cell.setCellValue(address.getCounty());
			count++;
		}
		return workbook;
	}

	/**
	 * 根据取件时间获取相应路由的运单id集合
	 * @param beginTime
	 * @param endTime
	 * @param status 路由状态
	 * @return
	 */
	private Set<Integer> getExpressIdsByTime(String beginTime, String endTime) {
		Date receiveDateB = DateTimeUtil.dateStr2date(beginTime);
		Date receiveDateE = DateTimeUtil.dateStr2date(endTime);
		if (receiveDateB == null && receiveDateE == null) {
			return null;
		}
		receiveDateE = DateTimeUtil.getTheDayLastTime(receiveDateE);
		List<String> codeList = getCodeList(ExpressStatusEnum.DELEIVER, 2);
		if (codeList == null) {
			return null;
		}
		Set<Integer> expressIds = new HashSet<Integer>();
		List<Map<String, Object>> expressIdMap = routePushMapper.selectExpressIdsByAcceptTime(receiveDateB, receiveDateE);
		if (expressIdMap == null || expressIdMap.isEmpty()) {
			expressIds.add(-2);
			return expressIds;
		}
		for (Map<String, Object> map : expressIdMap) {
			Integer expressId = (Integer) map.get("express_id");
			if (expressId != null) {
				expressIds.add(expressId);
			}
		}
//		List<RoutePush> routePushs = routePushMapper.selectByOpcodesAndTime(
//		codeList, receiveDateB, receiveDateE);
//		if (routePushs == null || routePushs.isEmpty()) {
//			expressIds.add(-2);
//			return expressIds;
//		}
//		for (RoutePush routePush : routePushs) {
//			expressIds.add(routePush.getExpressId());
//		}
		return expressIds;
	}

	/**
	 * 获取路由状态对应的操作码集合
	 * @param status状态枚举
	 * @param type param的位置
	 * @return
	 */
	private List<String> getCodeList(ExpressStatusEnum status, Integer type) {
		Dictionary dictionaryRoute = cacheService.getDictionary(
				DictionaryEnum.EXPRESS_STATUS, status.getCode());
		if (dictionaryRoute == null) {
			return null;
		}
		String codeStr = null;
		if (Objects.equals(type, 2)) {
			codeStr = dictionaryRoute.getParamStr2();
		}else {
			codeStr = dictionaryRoute.getParamStr1();
		}
		if (codeStr == null) {
			return null;
		}
		List<String> codeList = new ArrayList<String>();
		String[] codes = codeStr.split(",");
		for (String string : codes) {
			codeList.add(string);
		}
		return codeList;
	}
	
	/**
	 * 按运单号查询运单运费重量等详情
	 * @param mailno 运单号
	 * @return
	 */
	private QueryWayBill getMailFeeInfo(String mailno){
		WaybillService waybillService = new WaybillService();
		waybillService.setMailno(mailno);
		String xml = HttpUtil.postXML(waybillService.toXml());
		QueryWayBill queryWayBill = XMLUtil.getQueryWayBill(xml);
		return queryWayBill;
	}

}
