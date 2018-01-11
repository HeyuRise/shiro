package com.pcbwx.shiro.map;

public class Boolean2Integer extends ChannelDeserialize<Integer> {

	@Override
	public Integer deserialize(Object src) throws Exception {
		if (src == null) {
			return 0;
		}
		Boolean value = (Boolean) src;
		return value == true ? 1 : 0;
	}
}
