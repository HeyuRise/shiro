package com.pcbwx.shiro.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcbwx.shiro.bean.contact.Recipient;
import com.pcbwx.shiro.bean.contact.SenderAddressBean;
import com.pcbwx.shiro.bean.contact.SenderBean;
import com.pcbwx.shiro.dao.RecipientsAddressMapper;
import com.pcbwx.shiro.dao.RecipientsMapper;
import com.pcbwx.shiro.dao.SenderAddressMapper;
import com.pcbwx.shiro.dao.SenderMapper;
import com.pcbwx.shiro.model.Recipients;
import com.pcbwx.shiro.model.RecipientsAddress;
import com.pcbwx.shiro.model.Sender;
import com.pcbwx.shiro.model.SenderAddress;
import com.pcbwx.shiro.service.ContactService;
import com.pcbwx.shiro.utils.ExcelUtil;
/**
 * 联系人接口实现类
 * @author 孙贺宇
 *
 */
@Service("contactService")
public class ContactServiceImpl implements ContactService {
	
	@Autowired
	private RecipientsMapper recipientsMapper;
	@Autowired
	private RecipientsAddressMapper recipientsAddressMapper;
	@Autowired
	private SenderAddressMapper senderAddressMapper;
	@Autowired
	private SenderMapper senderMapper;

	@Override
	public List<Recipient> getRecipients(String company, String contact, String province, String city, String county,
			String address) {
		List<String> recipientCodes = getRecipients(company, contact);
		List<RecipientsAddress> recipientsAddresses = recipientsAddressMapper.selectByCodesAndAddress(province, city, county, address, recipientCodes);
		if (recipientsAddresses == null || recipientsAddresses.isEmpty()) {
			return new ArrayList<Recipient>();
		}
		List<Recipient> recipientBeans = new ArrayList<Recipient>();
		List<String> codes = new ArrayList<String>(8000);
		for (RecipientsAddress recipientsAddress : recipientsAddresses) {
			String innerCode = recipientsAddress.getRecipientsCode();
			codes.add(innerCode);
			Recipient recipient = new Recipient();
			recipient.setInnerCode(innerCode);
			recipient.setAddress(recipientsAddress.getAddress());
			recipient.setCity(recipientsAddress.getCity());
			recipient.setCounty(recipientsAddress.getDistrict());
			recipient.setProvince(recipientsAddress.getProvince());
			recipientBeans.add(recipient);
		}
		// 为了提高性能，在全部查找的时候，使用load,在条件查找的时候使用收件人code集合
		List<Recipients> recipients = null;
		if (company == null && contact == null && province == null && city == null && county == null) {
			recipients = recipientsMapper.load();
		}else {
			recipients = recipientsMapper.listByCodes(codes);
		}
		if (recipients == null) {
			recipients = new ArrayList<Recipients>();
		}
		// 把联系人集合根据code封装到map
		Map<String, Recipients> recipientMap = new HashMap<String, Recipients>();
		for (Recipients recipient : recipients) {
			recipientMap.put(recipient.getInnerCode(), recipient);
		}
		for (Recipient recipientBean : recipientBeans) {
			String innerCode = recipientBean.getInnerCode();
			Recipients recipients3 = recipientMap.get(innerCode);
			if (recipients3 == null) {
				continue;
			}
			recipientBean.setCompany(recipients3.getCompany());
			recipientBean.setContact(recipients3.getName());
			recipientBean.setMobile(recipients3.getMobile());
			recipientBean.setTelephone(recipients3.getTelephone());
		}
		return recipientBeans;
	}
	
	@Override
	public SXSSFWorkbook getRecipientsExport(String company, String contact, String province, String city,
			String county, String address) {
		List<Recipient> recipients = this.getRecipients(company, contact, province, city, county, address);
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		Sheet xssfSheet = workbook.createSheet();
		CellStyle cellStyle = ExcelUtil.generateColumnTitleStyle(workbook);
		String[] titles = { "收件公司", "联系人", "联系电话", "手机", "省", "市", "区/县", "详细地址"};
		ExcelUtil.setColumnTitle(xssfSheet, titles, cellStyle, 0);
		ExcelUtil.setColumnWidth(xssfSheet, 3600);
		Integer count = 1;
		Row row = null;
		Cell cell = null;
		for (Recipient recipient : recipients) {
			row = xssfSheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(recipient.getCompany());
			cell = row.createCell(1);
			cell.setCellValue(recipient.getContact());
			cell = row.createCell(2);
			cell.setCellValue(recipient.getTelephone());
			cell = row.createCell(3);
			cell.setCellValue(recipient.getMobile());
			cell = row.createCell(4);
			cell.setCellValue(recipient.getProvince());
			cell = row.createCell(5);
			cell.setCellValue(recipient.getCity());
			cell = row.createCell(6);
			cell.setCellValue(recipient.getCounty());
			cell = row.createCell(7);
			cell.setCellValue(recipient.getAddress());
			count++;
		}
		return workbook;
	}
	
	@Override
	public List<SenderBean> getSenderBeans(String contact) {
		List<Sender> senders = senderMapper.selectByContact(contact);
		if (senders == null || senders.isEmpty()) {
			return new ArrayList<SenderBean>();
		}
		List<SenderBean> senderBeans = new ArrayList<SenderBean>();
		for (Sender sender : senders) {
			SenderBean senderBean = new SenderBean();
			senderBean.setContact(sender.getName());
			senderBean.setMobile(sender.getMobile());
			senderBean.setTelephone(sender.getTelephone());
			senderBeans.add(senderBean);
		}
		return senderBeans;
	}
	
	@Override
	public XSSFWorkbook getSenderBeansExport(String contact) {
		List<SenderBean> senderBeans = this.getSenderBeans(contact);
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet xssfSheet = workbook.createSheet();
		CellStyle cellStyle = ExcelUtil.generateColumnTitleStyle(workbook);
		String[] titles = { "联系人", "联系电话", "手机"};
		ExcelUtil.setColumnTitle(xssfSheet, titles, cellStyle, 0);
		ExcelUtil.setColumnWidth(xssfSheet, 3600);
		Integer count = 1;
		Row row = null;
		Cell cell = null;
		for (SenderBean senderBean : senderBeans) {
			row = xssfSheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(senderBean.getContact());
			cell = row.createCell(1);
			cell.setCellValue(senderBean.getTelephone());
			cell = row.createCell(2);
			cell.setCellValue(senderBean.getMobile());
			count++;
		}
		return workbook;
	}

	@Override
	public List<SenderAddressBean> getSenderAddresses(String address, String province, String city, String county) {
		List<SenderAddress> senderAddresses = senderAddressMapper.selectByAddress(province, city, county, address);
		if (senderAddresses == null || senderAddresses.isEmpty()) {
			return new ArrayList<SenderAddressBean>();
		}
		List<SenderAddressBean> senderAddressBeans = new ArrayList<SenderAddressBean>();
		for (SenderAddress senderAddress : senderAddresses) {
			SenderAddressBean senderAddressBean = new SenderAddressBean();
			senderAddressBean.setAddress(senderAddress.getAddress());
			senderAddressBean.setCity(senderAddress.getCity());
			senderAddressBean.setCounty(senderAddress.getDistrict());
			senderAddressBean.setProvince(senderAddress.getProvince());
			senderAddressBeans.add(senderAddressBean);
		}
		return senderAddressBeans;
	}
	
	@Override
	public XSSFWorkbook getSenderAddressExport(String address, String province,
			String city, String county) {
		List<SenderAddressBean> senderAddressBeans = this.getSenderAddresses(address, province, city, county);
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet xssfSheet = workbook.createSheet();
		CellStyle cellStyle = ExcelUtil.generateColumnTitleStyle(workbook);
		String[] titles = {"省", "市", "区/县", "详细地址"};
		ExcelUtil.setColumnTitle(xssfSheet, titles, cellStyle, 0);
		ExcelUtil.setColumnWidth(xssfSheet, 3600);
		Integer count = 1;
		Row row = null;
		Cell cell = null;
		for (SenderAddressBean senderAddressBean : senderAddressBeans) {
			row = xssfSheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(senderAddressBean.getProvince());
			cell = row.createCell(1);
			cell.setCellValue(senderAddressBean.getCity());
			cell = row.createCell(2);
			cell.setCellValue(senderAddressBean.getCounty());
			cell = row.createCell(3);
			cell.setCellValue(senderAddressBean.getAddress());
			count++;
		}
		return workbook;
	}
	
	/**
	 * 按公司和联系人名字查找，返回查找到的收件人code集合
	 * @param company
	 * @param contact
	 * @return
	 */
	private List<String> getRecipients(String company, String contact) {
		if (company == null && contact == null) {
			return null;
		}
		List<Recipients> recipients = recipientsMapper.selectByCompanyAndName(company, contact);
		List<String> codes = new ArrayList<String>();
		for (Recipients recipients2 : recipients) {
			codes.add(recipients2.getInnerCode());
		}
		// 如果没有查到任何的数据，返回一个随机值
		if (codes.isEmpty()) {
			codes.add("张飞");
		}
		return codes;
	}
}
