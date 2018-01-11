package com.pcbwx.shiro.map;

public class Double2Float extends ChannelDeserialize<Float> {

	@Override
	public Float deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		Double value = (Double) src;
		return value.floatValue();
	}
}
