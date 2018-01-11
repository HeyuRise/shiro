package com.pcbwx.shiro.dao;

import com.pcbwx.shiro.model.RecordUtils;

public interface RecordUtilsMapper extends BaseMapper<RecordUtils> {
	int updateByName(RecordUtils record);
	
	RecordUtils selectByName(String recordName);
}