package com.pcbwx.shiro.map;

import com.pcbwx.shiro.utils.DateCalcUtil;

public class MinuteInteger2HourFloat extends ChannelDeserialize<Float> {

	@Override
	public Float deserialize(Object src) throws Exception {
		if (src == null) {
			return null;
		}
		Integer value = (Integer) src;
		return DateCalcUtil.minuteInteger2HourFloat(value);
	}
}
