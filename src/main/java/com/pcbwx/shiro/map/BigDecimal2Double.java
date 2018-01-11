package com.pcbwx.shiro.map;

import java.math.BigDecimal;

public class BigDecimal2Double extends ChannelDeserialize<Double> {

	@Override
	public Double deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		BigDecimal value = (BigDecimal) src;
		return value.doubleValue();
	}
}
