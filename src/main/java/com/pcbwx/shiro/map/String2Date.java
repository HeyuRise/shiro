package com.pcbwx.shiro.map;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pcbwx.shiro.utils.DateTimeUtil;

public class String2Date extends ChannelDeserialize<Date> {

	@Override
	public Date deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		String value = (String) src;
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return DateTimeUtil.dateStr2date(value);
	}
}
