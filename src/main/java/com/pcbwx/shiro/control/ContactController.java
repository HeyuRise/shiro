package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.contact.Recipient;
import com.pcbwx.shiro.bean.contact.SenderAddressBean;
import com.pcbwx.shiro.bean.contact.SenderBean;
import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.service.ContactService;
import com.pcbwx.shiro.service.LogService;

@Controller
@RequestMapping("contact")
@Api(tags = "通讯录Api")
public class ContactController {

	private static Logger logger = Logger.getLogger(ContactController.class);

	@Autowired
	private ContactService contactService;
	@Autowired
	private LogService logService;

	@RequestMapping(value = { "/recipient" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("收件人信息列表,innerId不显示")
	public List<Recipient> getRecipients(
			HttpServletRequest request,
			@RequestParam(value = "company", required = false) String company,
			@RequestParam(value = "contact", required = false) String contact,
			@RequestParam(value = "province", required = false) String province,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "county", required = false) String county,
			@RequestParam(value = "address", required = false) String address) {
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.CONTACT_RECIPIENT.getCode(), wxtbUser.getAccount(), null);
		if (company == null || company.equals("")) {
			company = null;
		}
		if (contact == null || contact.equals("")) {
			contact = null;
		}
		if (province == null || province.equals("")) {
			province = null;
		}
		if (city == null || city.equals("")) {
			city = null;
		}
		if (county == null || county.equals("")) {
			county = null;
		}
		if (address == null || address.equals("")) {
			address = null;
		}
		return contactService.getRecipients(company, contact, province, city, county, address);
	}

	@RequestMapping(value = { "/recipientExport" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("收件人信息列表导出")
	public void getRecipentsOutPut(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "company", required = false) String company,
			@RequestParam(value = "contact", required = false) String contact,
			@RequestParam(value = "province", required = false) String province,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "county", required = false) String county,
			@RequestParam(value = "address", required = false) String address) {
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.RECIPIENT_EXPORT.getCode(), wxtbUser.getAccount(), null);
		if (company == null || company.equals("")) {
			company = null;
		}
		if (contact == null || contact.equals("")) {
			contact = null;
		}
		if (province == null || province.equals("")) {
			province = null;
		}
		if (city == null || city.equals("")) {
			city = null;
		}
		if (county == null || county.equals("")) {
			county = null;
		}
		if (address == null || address.equals("")) {
			address = null;
		}
		SXSSFWorkbook xssfWorkbook = contactService.getRecipientsExport(company,
				contact, province, city, county, address);
		String fileName = "收件人信息列表.xlsx";
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream stream = response.getOutputStream();
			xssfWorkbook.write(stream);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			logger.error(e);
			LogContext.error("导出错误", "收件人信息列表导出错误");
		}
	}

	@RequestMapping(value = { "/sender" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("寄件人信息列表")
	public List<SenderBean> getSenders(HttpServletRequest request,
			@RequestParam(value = "contact", required = false) String contact) {
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.CONTACT_SENDER.getCode(), wxtbUser.getAccount(), contact);
		if (contact == null || contact.equals("")) {
			contact = null;
		}
		return contactService.getSenderBeans(contact);
	}

	@RequestMapping(value = { "/senderExport" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("寄件人信息列表导出")
	public void senderExport(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "contact", required = false) String contact) {
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SENDER_EXPORT.getCode(), wxtbUser.getAccount(), contact);
		if (contact == null || contact.equals("")) {
			contact = null;
		}
		XSSFWorkbook xssfWorkbook = contactService
				.getSenderBeansExport(contact);
		String fileName = "寄件人信息列表.xlsx";
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream stream = response.getOutputStream();
			xssfWorkbook.write(stream);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			logger.error(e);
			LogContext.error("导出错误", "寄件人信息列表导出错误");
		}
	}

	@RequestMapping(value = { "/senderAddress" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("寄件人地址列表")
	public List<SenderAddressBean> getSenderAddresses(
			HttpServletRequest request,
			@RequestParam(value = "province", required = false) String province,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "county", required = false) String county,
			@RequestParam(value = "address", required = false) String address) {
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SENDER_ADDRESS.getCode(), wxtbUser.getAccount(), null);
		if (province == null || province.equals("")) {
			province = null;
		}
		if (city == null || city.equals("")) {
			city = null;
		}
		if (county == null || county.equals("")) {
			county = null;
		}
		if (address == null || address.equals("")) {
			address = null;
		}
		return contactService.getSenderAddresses(address, province, city,
				county);
	}
	
	@RequestMapping(value = { "/senderAddressExport" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("寄件人地址列表导出")
	public void senderAddressExport(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "province", required = false) String province,
			@RequestParam(value = "city", required = false) String city,
			@RequestParam(value = "county", required = false) String county,
			@RequestParam(value = "address", required = false) String address){
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.SENDER_ADDRESS_EXPORT.getCode(), wxtbUser.getAccount(), null);
		if (province == null || province.equals("")) {
			province = null;
		}
		if (city == null || city.equals("")) {
			city = null;
		}
		if (county == null || county.equals("")) {
			county = null;
		}
		if (address == null || address.equals("")) {
			address = null;
		}
		XSSFWorkbook xssfWorkbook = contactService.getSenderAddressExport(address, province, city, county);
		String fileName = "寄件人地址列表.xlsx";
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream stream = response.getOutputStream();
			xssfWorkbook.write(stream);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			logger.error(e);
			LogContext.error("导出错误", "寄件人地址列表导出错误");
		}
	}
}
