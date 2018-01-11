package com.pcbwx.shiro.map;

import org.apache.commons.lang.StringUtils;

public class String2Integer extends ChannelDeserialize<Integer> {

	@Override
	public Integer deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		String value = (String) src;
		if (StringUtils.isBlank(value)) {
			return null;
		}
		Integer dst = null;
		try {
			dst = Integer.valueOf(value);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return dst;
	}
}
