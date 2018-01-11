package com.pcbwx.shiro.map;

public class Float2Integer extends ChannelDeserialize<Integer> {

	@Override
	public Integer deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		Float value = (Float) src;
		return value.intValue();
	}
}
