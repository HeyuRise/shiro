package com.pcbwx.shiro.service;

import java.io.ByteArrayOutputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 用户会话模块业务接口
 * @author 王海龙
 */

public interface AccountService {
	
	/**
	 * 获取图片流
	 * @param height
	 * @param width
	 * @param format
	 * @param type
	 * @param text
	 * @return
	 */
	ByteArrayOutputStream trans(Integer height, Integer width, String format, Integer type, String text);

	/**
	 * 获取行政区列表导出的实体类
	 * @return
	 */
	XSSFWorkbook outputAddressXlsx();
	
	/**
	 * 获取按钮是否显示，是否能点击
	 * @param account
	 * @param buttonId
	 * @return
	 */
	Boolean getButtonAppear(String account, Integer buttonId);
}
