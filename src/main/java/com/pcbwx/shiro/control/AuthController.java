package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.control.BaseController;
import com.pcbwx.shiro.enums.ActionTypeEnum;
import com.pcbwx.shiro.enums.ErrorCodeEnum;
import com.pcbwx.shiro.service.AccountService;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.service.UserService;

@Controller
@RequestMapping("/auth")
@Api(tags = "权限api")
public class AuthController extends BaseController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private LogService logService;

	@RequestMapping( value = {"/userDetail"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取用户详情")
	public Map<String, Object> getUserAuths(HttpServletRequest request) {
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		Map<String, Object> response = new HashMap<String, Object>();
		if (wxtbUser == null) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			return response;
		}
		response = userService.getUserDetail(wxtbUser);
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}
	
	@RequestMapping( value = {"/password"}, method = RequestMethod.PATCH)
	@ResponseBody
	@ApiOperation("修改密码")
	public Map<String, Object> verfityPassword(HttpServletRequest request,
			@RequestParam("account") String account,
			@RequestParam("oldPassword") String oldpassword,
			@RequestParam("newPassword") String password){
		Map<String, Object> response = new HashMap<String, Object>();
		Integer result = userService.vertifyPassword(account, oldpassword, password);
		logService.addAction(ConfigProperties.getMySystemCode(), ActionTypeEnum.AUTH_PASSWORD.getCode(), account, account, oldpassword, password);
		if (result == 1) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "系统错误");
			return response;
		}else if (result == 2) {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
			response.put("msg", "旧密码错误");
			return response;
		}
		response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		return response;
	}
	
	@RequestMapping( value = {"/button"}, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("获取按钮是否显示")
	public Map<String, Object> buttonAppear(HttpServletRequest request,
			@RequestParam("buttonId") Integer buttonId){
		Map<String, Object> response = new HashMap<String, Object>();
		WxtbAuthUser wxtbUser = (WxtbAuthUser) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		boolean idAppear = accountService.getButtonAppear(wxtbUser.getAccount(), buttonId);
		if (idAppear) {
			response.put("result", ErrorCodeEnum.SUCCESS.getCode());
		}else {
			response.put("result", ErrorCodeEnum.SYSTEM_ERROR.getCode());
		}
		return response;
	}
}
