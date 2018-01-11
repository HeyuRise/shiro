package com.pcbwx.shiro.map;

public class Integer2String extends ChannelDeserialize<String> {

	@Override
	public String deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		Integer value = (Integer) src;
		return String.valueOf(value);
	}
}
