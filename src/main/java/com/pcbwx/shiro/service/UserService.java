package com.pcbwx.shiro.service;

import java.util.List;
import java.util.Map;

import com.pcbwx.shiro.bean.request.AddUser;
import com.pcbwx.shiro.bean.user.Role;
import com.pcbwx.shiro.bean.user.UserManageInfo;
import com.pcbwx.shiro.bean.user.WxtbAuthUser;

/**
 * 用户模块接口
 * 
 * @author 孙贺宇
 *
 */
public interface UserService {
	/**
	 * 获取用户详细·信息
	 * 
	 * @param wxtbUser
	 * @return
	 */
	Map<String, Object> getUserDetail(WxtbAuthUser wxtbUser);

	/**
	 * 获取角色列表
	 * 
	 * @param roleName
	 * @return
	 */
	List<Role> getRoles(String roleName);

	/**
	 * 获取用户列表
	 * 
	 * @param name
	 * @param account
	 * @param department
	 * @param roleName
	 * @param enable
	 * @return
	 */
	List<UserManageInfo> getUserInfos(String realAccount, String name, String account, String department,
			String roleName, String enable);

	/**
	 * 新增用户
	 * 
	 * @param addUser
	 * @return
	 */
	boolean addUser(AddUser addUser, String password);

	/**
	 * 启用/禁用
	 * 
	 * @param account
	 * @return
	 */
	boolean operateEnable(String account);

	/**
	 * 编辑用户角色
	 * 
	 * @param roles
	 * @return
	 */
	boolean operateUserRole(String account, List<Integer> roleIds);

	/**
	 * 修改密码
	 * 
	 * @param account
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	Integer vertifyPassword(String account, String oldPassword, String newPassword);

	/**
	 * 重置密码
	 * 
	 * @param account
	 * @return
	 */
	Integer resetPassword(String account, String password);

	/**
	 * 获取角色下拉框
	 * 
	 * @return
	 */
	Map<String, Object> getUserRoles();
}
