package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.system.AddressInfo;
import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.service.OrderService;

@Controller
@RequestMapping("/address")
@Api(tags = "全国行政区api")
public class AddressController {
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private LogService logService;
	
	@RequestMapping( value = {"/address"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取行政区列表")
	public List<AddressInfo> getAddressInfos(HttpServletRequest request,
			@RequestParam(value = "province", required = false) String province,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "county", required = false) String county){
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SYSTEM_ADDRESS.getCode(), wxtbUser.getAccount(), province, city, county);
		return orderService.getAddressInfos(province, city, county);
	}
	
	@RequestMapping( value = {"/addressExport"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("导出")
	public void addressExpress(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "province", required = false) String province,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "county", required = false) String county){
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.ADDRESS_EXPORT.getCode(), wxtbUser.getAccount(), province, city, county);
		XSSFWorkbook xssfWorkbook = orderService.addressExport(province, city, county);
		String fileName = "全国行政区列表.xlsx";
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);
			OutputStream stream = response.getOutputStream();
			xssfWorkbook.write(stream);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			LogContext.error("导出错误", "全国行政区列表导出错误");
		}
	}
 }
