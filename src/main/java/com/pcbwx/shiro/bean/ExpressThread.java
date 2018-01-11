package com.pcbwx.shiro.bean;

import java.math.BigDecimal;
import java.util.Map;

import com.pcbwx.shiro.bean.request.WaybillService;
import com.pcbwx.shiro.bean.response.QueryWayBill;
import com.pcbwx.shiro.dao.ExpressMapper;
import com.pcbwx.shiro.dao.SustenanceMapper;
import com.pcbwx.shiro.model.Express;
import com.pcbwx.shiro.model.Sustenance;
import com.pcbwx.shiro.utils.HttpUtil;
import com.pcbwx.shiro.utils.StringUtil;
import com.pcbwx.shiro.utils.XMLUtil;

public class ExpressThread extends Thread{

	private Map<Integer, Express> expressMap;
	
	private SustenanceMapper sustenanceMapper;
	
	private ExpressMapper expressMapper;
	
	public ExpressThread(Map<Integer, Express> expressMap,
			SustenanceMapper sustenanceMapper, ExpressMapper expressMapper) {
		super();
		this.expressMap = expressMap;
		this.sustenanceMapper = sustenanceMapper;
		this.expressMapper = expressMapper;
	}

	@Override
	public void run() {
		for ( Integer key : expressMap.keySet()) {
			Express express = expressMap.get(key);
			String mailno = express.getMailNo();
			String orderId = express.getOrderId();
			if (!StringUtil.isEmpty(express.getMoney())) {
				continue;
			}
			Sustenance sustenance = sustenanceMapper.selectByExpressId(key);
			if (sustenance == null) {
				continue;
			}
			WaybillService waybillService = new WaybillService();
			waybillService.setMailno(mailno);
			waybillService.setOrderId(orderId);
			String xml = HttpUtil.postXML(waybillService.toXml());
			QueryWayBill queryWayBill = XMLUtil.getQueryWayBill(xml);
			if (queryWayBill == null) {
				continue;
			}
			BigDecimal expressCost = queryWayBill.getExpressCost();
			BigDecimal payWeight = queryWayBill.getWeight();
			express.setMoney(expressCost == null ? null : expressCost.toString());
			sustenance.setPayWeight(payWeight == null ? null : payWeight.toString());
			expressMapper.updateByPrimaryKeySelective(express);
			sustenanceMapper.updateByPrimaryKeySelective(sustenance);
		}
	}

}
