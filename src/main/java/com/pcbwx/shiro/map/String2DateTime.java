package com.pcbwx.shiro.map;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pcbwx.shiro.utils.DateTimeUtil;

public class String2DateTime extends ChannelDeserialize<Date> {

	@Override
	public Date deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		String value = (String) src;
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return DateTimeUtil.dateTimeStr2date(value);
	}
}
