package com.pcbwx.shiro.map;

public class Integer2Float extends ChannelDeserialize<Float> {

	@Override
	public Float deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		Integer value = (Integer) src;
		float dstValue = value;
		return dstValue;
	}
}
