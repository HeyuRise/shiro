package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.response.ResponseRoute;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.model.WxtbUser;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.service.OrderService;
import com.pcbwx.shiro.service.RouteService;

@Controller
@RequestMapping("/order")
@Api(tags = "快递单管理Api")
public class OrderController extends BaseController {
	
	private static final Logger logger = LogManager.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private LogService logService;

	@RequestMapping(value = { "/express" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取快递单列表")
	public Map<String, Object> getExpressOrder(
			HttpServletRequest request,
			@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "mailno", required = false) String mailno,
			@RequestParam(value = "mailnoChild", required = false) String mailnoChild,
			@RequestParam(value = "sendCompany", required = false) String sendCompany,
			@RequestParam(value = "receiveCompany", required = false) String receiveCompany,
			@RequestParam(value = "sendContact", required = false) String sendContact,
			@RequestParam(value = "receiveContact", required = false) String receiveContact,
			@RequestParam(value = "sendDateBegin", required = false) String sendDateBegin,
			@RequestParam(value = "sendDateEnd", required = false) String sendDateEnd,
			@RequestParam(value = "receiveDateBegin", required = false) String receiveDateBegin,
			@RequestParam(value = "receiveDateEnd", required = false) String receiveDateEnd,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "pageNumber", required = false) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer size) {
		if (orderId == null || orderId.equals("")) {
			orderId = null;
		}
		if (mailno == null || mailno.equals("")) {
			mailno = null;
		}
		if (mailnoChild == null || mailnoChild.equals("")) {
			mailnoChild = null;
		}
		if (sendCompany == null || sendCompany.equals("")) {
			sendCompany = null;
		}
		if (receiveCompany == null || receiveCompany.equals("")) {
			receiveCompany = null;
		}
		if (sendContact == null || sendContact.equals("")) {
			sendContact = null;
		}
		if (receiveContact == null || receiveContact.equals("")) {
			receiveContact = null;
		}
		if (sendDateBegin == null || sendDateBegin.equals("")) {
			sendDateBegin = null;
		}
		if (sendDateEnd == null || sendDateEnd.equals("")) {
			sendDateEnd = null;
		}
		if (receiveDateBegin == null || receiveDateBegin.equals("")) {
			receiveDateBegin = null;
		}
		if (receiveDateEnd == null || receiveDateEnd.equals("")) {
			receiveDateEnd = null;
		}
		if (status == null || status.equals("")) {
			status = null;
		}
		return orderService.getExpressOrders(orderId, mailno, mailnoChild, sendCompany,
				receiveCompany, sendContact, receiveContact, sendDateBegin,
				sendDateEnd, receiveDateBegin, receiveDateEnd, status, page, size);
	}

	@RequestMapping(value = { "/detail" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取运单详情")
	public Map<String, Object> getExpressDetail(HttpServletRequest request,
			@RequestParam( value = "mailno", required = false) String mailno,
			@RequestParam( value = "orderId", required = false) String orderId) {
		return orderService.getExpressDetail(mailno, orderId);
	}

	@RequestMapping(value = { "/route" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取物流信息")
	public Map<String, Object> getRoutePushInfo(HttpServletRequest request,
			@RequestParam("mailno") String mailno) {
		Map<String, Object> response = new HashMap<String, Object>();
		if (mailno == null) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "信息填写不完整");
			return response;
		}
		List<ResponseRoute> responseRoutes = routeService.goforRoutePushInfo(mailno);
		if (responseRoutes == null) {
			responseRoutes = routeService.goforExpressRoute(1, mailno);
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		response.put("expressRoute", responseRoutes);
		return response;
	}

	@RequestMapping(value = { "/expressExport" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("导出快递单")
	public void expressExport(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "mailno", required = false) String mailno,
			@RequestParam(value = "mailnoChild", required = false) String mailnoChild,
			@RequestParam(value = "sendCompany", required = false) String sendCompany,
			@RequestParam(value = "receiveCompany", required = false) String receiveCompany,
			@RequestParam(value = "sendContact", required = false) String sendContact,
			@RequestParam(value = "receiveContact", required = false) String receiveContact,
			@RequestParam(value = "sendDateBegin", required = false) String sendDateBegin,
			@RequestParam(value = "sendDateEnd", required = false) String sendDateEnd,
			@RequestParam(value = "receiveDateBegin", required = false) String receiveDateBegin,
			@RequestParam(value = "receiveDateEnd", required = false) String receiveDateEnd,
			@RequestParam(value = "status", required = false) String status) {
		if (orderId == null || orderId.equals("")) {
			orderId = null;
		}
		if (mailno == null || mailno.equals("")) {
			mailno = null;
		}
		if (mailnoChild == null || mailnoChild.equals("")) {
			mailnoChild = null;
		}
		if (sendCompany == null || sendCompany.equals("")) {
			sendCompany = null;
		}
		if (receiveCompany == null || receiveCompany.equals("")) {
			receiveCompany = null;
		}
		if (sendContact == null || sendContact.equals("")) {
			sendContact = null;
		}
		if (receiveContact == null || receiveContact.equals("")) {
			receiveContact = null;
		}
		if (sendDateBegin == null || sendDateBegin.equals("")) {
			sendDateBegin = null;
		}
		if (sendDateEnd == null || sendDateEnd.equals("")) {
			sendDateEnd = null;
		}
		if (receiveDateBegin == null || receiveDateBegin.equals("")) {
			receiveDateBegin = null;
		}
		if (receiveDateEnd == null || receiveDateEnd.equals("")) {
			receiveDateEnd = null;
		}
		if (status == null || status.equals("")) {
			status = null;
		}
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.ORDER_EXPRESS_EXPORT.getCode(), wxtbUser.getAccount(), null);
		SXSSFWorkbook workbook = orderService.expressExport(orderId, mailno, mailnoChild, sendCompany,
				receiveCompany, sendContact, receiveContact, sendDateBegin,
				sendDateEnd, receiveDateBegin, receiveDateEnd, status);
		String fileName = "历史快递单.xlsx";
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);
			OutputStream stream = response.getOutputStream();
			workbook.write(stream);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			logger.error(e);
			LogContext.error("导出错误", "历史快递单列表导出错误");
		}
	}
}
