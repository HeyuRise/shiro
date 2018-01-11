package com.pcbwx.shiro.map;

import org.apache.commons.lang.StringUtils;

public class String2IntegerWithSign extends ChannelDeserialize<Integer> {

	private int signNum = -123456;
	
	@Override
	public Integer deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		String value = (String) src;
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (StringUtils.equals(value, "-")) {
			return signNum;
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
