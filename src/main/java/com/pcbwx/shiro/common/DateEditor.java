package com.pcbwx.shiro.common;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 用于在String与java.util.date之间进行格式转换
 * 
 * @author 王海龙
 */
public class DateEditor extends PropertyEditorSupport {
	private String format = "yyyy-MM-dd HH:mm:ss";

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {

			setValue(sdf.parse(text));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Date format is not correct!");
		}
	}
}