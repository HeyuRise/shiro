package com.pcbwx.shiro.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.dao.AddressMapper;
import com.pcbwx.shiro.enums.DictionaryEnum;
import com.pcbwx.shiro.model.Address;
import com.pcbwx.shiro.model.Dictionary;
import com.pcbwx.shiro.service.AccountService;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.utils.DateTimeUtil;

/**
 * 用户会话模块业务接口实现类
 * 
 * @author 王海龙
 *
 */

@Service("accountService")
public class AccountServiceImpl implements AccountService {
	
	private static final Logger logger = LogManager.getLogger(AccountServiceImpl.class);
	
	@Autowired
	private AddressMapper addressMapper;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private SupportService supportService;

	@Override
	public ByteArrayOutputStream trans(Integer height, Integer width, String format, Integer type, String text) {
		height = height == null ? 1000 : height;
		width = width == null ? 1000 : width;
		if (format == null) {
			format = "png";
		}
		BarcodeFormat barcodeFormat = getBarcodeFormat(type);
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix bitMatrix = null;
		try {
			bitMatrix = new MultiFormatWriter().encode(text, barcodeFormat, width, height, hints);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
		try {
			MatrixToImageWriter.writeToStream(bitMatrix, format, bout);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bout;

	}
	
	@Override
	public XSSFWorkbook outputAddressXlsx() {
		List<Address> addresses = addressMapper.selectByCondition(null, null, null);
		if (addresses == null || addresses.isEmpty()) {
			return null;
		}
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
		XSSFSheet xssfSheet = xssfWorkbook.createSheet("sheet1");
		Row row = xssfSheet.createRow(0);
		Cell cellId = row.createCell(0);
		cellId.setCellValue("id");
		Cell cellpro = row.createCell(1);
		cellpro.setCellValue("province");
		Cell cellCity = row.createCell(2);
		cellCity.setCellValue("city");
		Cell cellArea = row.createCell(3);
		cellArea.setCellValue("area");
		Cell cellCreate = row.createCell(4);
		cellCreate.setCellValue("created_at");
		Cell cellUpdate = row.createCell(5);
		cellUpdate.setCellValue("updated_at");
		Integer count = 1;
		for (Address address : addresses) {
			row = xssfSheet.createRow(count);
			cellId = row.createCell(0);
			cellId.setCellValue(address.getId());
			cellpro = row.createCell(1);
			cellpro.setCellValue(address.getProvince());
			cellCity = row.createCell(2);
			cellCity.setCellValue(address.getCity());
			cellArea = row.createCell(3);
			cellArea.setCellValue(address.getCounty());
			cellCreate = row.createCell(4);
			String dateCreate = DateTimeUtil.date2dateTimeStr(
					address.getCreatTime(), "yyyy-MM-dd");
			cellCreate.setCellValue(dateCreate);
			String dateUpdate = DateTimeUtil.date2dateTimeStr(
					address.getUpdateTime(), "yyyy-MM-dd");
			if (dateUpdate != null) {
				cellUpdate = row.createCell(5);
				cellUpdate.setCellValue(dateUpdate);
			}
			count++;
		}
		return xssfWorkbook;
	}
	
	@Override
	public Boolean getButtonAppear(String account, Integer buttonId) {
		Dictionary dictionary = cacheService.getDictionary(DictionaryEnum.BUTTON, buttonId);
		if (dictionary == null) {
			return true;
		}
		
		// 获取按钮对应的权限
		Integer auth = dictionary.getParamInt1();
		// 获取用户所有权限
		Set<Integer> authIds = supportService.getUserAuths(account);
		// 查看用户权限是否包含这个按钮的权限
		if (authIds.contains(auth)) {
			return true;
		}
		return false;
	}
	
	private BarcodeFormat getBarcodeFormat(Integer type){
		BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
		if (type == null) {
			return barcodeFormat;
		}
		switch (type) {
		case 1:
			barcodeFormat = BarcodeFormat.CODE_128;
			break;
		case 2:
			barcodeFormat = BarcodeFormat.CODE_39;
			break;
		case 3:
			barcodeFormat = BarcodeFormat.CODE_93;
			break;
		default:
			barcodeFormat = BarcodeFormat.QR_CODE;
			break;
		}
		return barcodeFormat;
	}
}
