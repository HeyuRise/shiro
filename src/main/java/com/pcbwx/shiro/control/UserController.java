package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.request.AddUser;
import com.pcbwx.shiro.bean.user.Role;
import com.pcbwx.shiro.bean.user.UserManageInfo;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.model.WxtbUser;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.service.UserService;

@Controller
@RequestMapping("/user")
@Api(tags = "用户API")
public class UserController extends BaseController {

	private static final String PASSWORD = "123456";
	
	@Autowired
	private UserService userService;
	@Autowired
	private LogService logService;

	@RequestMapping(value = { "/role" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "获取角色列表")
	public List<Role> getRoles(HttpServletRequest request,
			@RequestParam( value = "roleName", required = false) String roleName) {
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.ROLE.getCode(), wxtbUser.getAccount(), roleName);
		List<Role> roles = userService.getRoles(roleName);
		if (roles == null) {
			roles = new ArrayList<Role>();
		} 
		return roles;
	}
	
	@RequestMapping( value = {"/user"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "获取用户列表")
	public List<UserManageInfo> getUsers(HttpServletRequest request,
			@RequestParam( value = "name", required = false) String name,
			@RequestParam( value = "account", required = false) String account,
			@RequestParam( value = "department", required = false) String department,
			@RequestParam( value = "roleName", required = false) String roleName,
			@RequestParam( value = "enable", required = false) String enable){
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		List<UserManageInfo> userManageInfos = userService.getUserInfos(wxtbUser.getAccount(), name, account, department, roleName, enable);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.USERS.getCode(), wxtbUser.getAccount(), null);
		if (userManageInfos == null) {
			userManageInfos = new ArrayList<UserManageInfo>();
		}
		return userManageInfos;
	}
	
	@RequestMapping( value = {"/user"}, method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation("新建用户,department为部门编号,roleIds为角色id集合,enable传汉字'启用','禁用'")
	public Map<String, Object> addUser(HttpServletRequest request,
			@RequestBody AddUser addUser){
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		if (addUser == null) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "数据解析错误");
			return response;
		}
		if (addUser.getAccount() == null || addUser.getAccount().equals("")) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "账号不能为空");
			return response;
		}
		boolean isSuccess = userService.addUser(addUser, PASSWORD);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.USERS_ADD.getCode(), wxtbUser.getAccount(), addUser.getAccount(), addUser.getUsername(), addUser.getDepartmentCode());
		if (!isSuccess) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "账号不能重复");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}
	
	@RequestMapping( value = {"/userEnable"}, method = RequestMethod.PATCH)
	@ResponseBody
	@ApiOperation("启用/禁用")
	public Map<String, Object> operateEnable(HttpServletRequest request,
			@RequestParam("account") String account){
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		if (account.equals("")) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "数据解析错误");
			return response;
		}
		boolean isSuccess = userService.operateEnable(account);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.USER_ENABLE.getCode(), wxtbUser.getAccount(), account);
		if (!isSuccess) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "数据解析错误");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}
	
	@RequestMapping( value = {"/userRole"}, method = RequestMethod.PATCH)
	@ResponseBody
	@ApiOperation("配置用户角色")
	public Map<String, Object> setUserRole(HttpServletRequest request,
			@RequestParam("account") String account,
			@RequestParam( value = "roleId", required = false) List<Integer> roleId){
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		if (account == null || account.equals("")) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "数据解析错误");
			return response;
		}
		boolean isSuccess = userService.operateUserRole(account, roleId);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.USER_ROLE.getCode(), wxtbUser.getAccount(), account, roleId.get(0).toString());
		if (!isSuccess) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "配置失败");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}
	
	@RequestMapping( value = {"/password"}, method = RequestMethod.PATCH)
	@ResponseBody
	@ApiOperation("重置密码")
	public Map<String, Object> verfityPassword(HttpServletRequest request,
			@RequestParam("account") String account){
		WxtbUser wxtbUser = (WxtbUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		Integer result = userService.resetPassword(account, PASSWORD);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.USER_PASSWORD.getCode(), wxtbUser.getAccount(), account);
		if (result == 1) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "系统错误");
			return response;
		}
		response.put("msg", "重置密码成功，密码为" + PASSWORD);
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}
}
