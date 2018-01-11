package com.pcbwx.shiro.map;

public abstract class ChannelDeserialize<DST> {
	public abstract DST deserialize(Object src) throws Exception;
	
	public abstract static class None extends ChannelDeserialize<Object> { }
}
