package com.pcbwx.shiro.map;

public class Float2Double extends ChannelDeserialize<Double> {

	@Override
	public Double deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		Float value = (Float) src;
		return value.doubleValue();
	}
}
