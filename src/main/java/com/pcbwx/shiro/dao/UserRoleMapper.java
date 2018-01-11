package com.pcbwx.shiro.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pcbwx.shiro.model.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole> {
	
	@Select("select GREATEST(COALESCE(max(create_time)),COALESCE(max(update_time),0)) from user_role")
	Date selectLastRecordTime();
	
	/**
	 * 获取可用可现实的角色
	 * @return
	 */
	List<UserRole> load();
	
	/**
	 * 获取可用角色
	 * @return
	 */
	List<UserRole> getRoles();
	
	/**
	 * 按角色名字查找
	 * @param roleName
	 * @return
	 */
	List<UserRole> selectByRoleName(@Param("roleName") String roleName);
}