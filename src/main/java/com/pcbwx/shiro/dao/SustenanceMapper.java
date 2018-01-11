package com.pcbwx.shiro.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.pcbwx.shiro.model.Sustenance;

public interface SustenanceMapper extends BaseMapper<Sustenance>{
	
	/**
	 * 按运单id查找
	 * @param expressId
	 * @return
	 */
	Sustenance selectByExpressId(@Param("expressId") Integer expressId);
	
	/**
	 * 按运单id集合查找
	 * @param expressIds
	 * @return
	 */
	List<Sustenance> selectByExpressIds(@Param("expressIds") Set<Integer> expressIds);
}