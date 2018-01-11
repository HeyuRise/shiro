package com.pcbwx.shiro.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pcbwx.shiro.bean.request.AddUser;
import com.pcbwx.shiro.bean.user.MainMenu;
import com.pcbwx.shiro.bean.user.MenuItem;
import com.pcbwx.shiro.bean.user.Role;
import com.pcbwx.shiro.bean.user.UserManageInfo;
import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.dao.UserRoleMapper;
import com.pcbwx.shiro.dao.UserRoleRelationMapper;
import com.pcbwx.shiro.dao.WxtbUserMapper;
import com.pcbwx.shiro.enums.ClickEnum;
import com.pcbwx.shiro.model.Menu;
import com.pcbwx.shiro.model.UserRole;
import com.pcbwx.shiro.model.UserRoleRelation;
import com.pcbwx.shiro.model.WxtbUser;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.service.UserService;
import com.pcbwx.shiro.utils.BASE64MD5Util;
/**
 * 用户模块接口实现类
 * @author 孙贺宇
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	//private static Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private WxtbUserMapper wxtbUserMapper;
	@Autowired
	private UserRoleRelationMapper userRoleRelationMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private CacheService cacheService;
	@Autowired
	private SupportService supportService;

	@Override
	public Map<String, Object> getUserDetail(WxtbAuthUser wxtbUser) {
		Map<String, Object> response = new HashMap<String, Object>();
		String account = wxtbUser.getAccount();
		String username = wxtbUser.getUsername();
		response.put("account", account);
		response.put("username", username);
		List<String> roleNames = new ArrayList<String>();
		List<UserRole> userRoles = cacheService.getUserRole(wxtbUser.getAccount());
		if (userRoles == null || userRoles.isEmpty()) {
			return response;
		}
		// 添加角色集合
		for (UserRole userRole : userRoles) {
			roleNames.add(userRole.getRoleName());
		}
		response.put("roles", roleNames);
		Set<Integer> authIds = supportService.getUserAuths(account);
		Map<Integer, Menu> menuCache = cacheService.getMenu();
		List<Menu> menus = new ArrayList<Menu>();
		List<Menu> secondMenus = new ArrayList<Menu>();
		for (Integer id : menuCache.keySet()) {
			Menu menu = menuCache.get(id);
			String authTypesStr = menu.getAuthType();
			if (authTypesStr == null) {
				continue;
			}
			// 按钮的权限集合
			String[] sp = authTypesStr.split(",");
			List<String> sps = new ArrayList<String>();
			for (String string : sp) {
				sps.add(string);
			}
			// 查看用户是否存在该权限
			for (Integer authId : authIds) {
				if (sps.contains(authId.toString())) {
					if (menu.getMenuLevel() == 1) {
						menus.add(menu);
					} else {
						secondMenus.add(menu);
					}
					break;
				}
			}
		}
		List<MainMenu> mainMenus = new ArrayList<MainMenu>();
		for (Menu menu : menus) {
			if (menu.getMenuLevel() == 1) {
				MainMenu mainMenu = new MainMenu();
				mainMenu.setTile(menu.getMenu());
				mainMenu.setParam(menu.getParam());
				mainMenu.setColor(menu.getColor());
				List<MenuItem> menuItems = new ArrayList<MenuItem>();
				Integer mainMenuId = menu.getMainMenuId();
				for (Menu menuSecond : secondMenus) {
					if (menuSecond.getMainMenuId() == mainMenuId) {
						MenuItem menuItem = new MenuItem();
						menuItem.setTile(menuSecond.getMenu());
						menuItem.setUrl(menuSecond.getMenuUrl());
						menuItems.add(menuItem);
						mainMenu.setMenuItems(menuItems);
					}
				}
				mainMenus.add(mainMenu);
			}
		}
		response.put("menu", mainMenus);
		return response;
	}

	@Override
	public List<Role> getRoles(String roleName) {
		if (roleName == null || roleName.equals("")) {
			roleName = null;
		}
		List<UserRole> userRoles = userRoleMapper.selectByRoleName(roleName);
		if (userRoles == null || userRoles.isEmpty()) {
			return null;
		}
		List<Role> roles = new ArrayList<Role>();
		for (UserRole userRole : userRoles) {
			Role role = new Role();
			role.setName(userRole.getRoleName());
			roles.add(role);
		}
		return roles;
	}

	@Override
	public List<UserManageInfo> getUserInfos(String realAccount, String name, String account,
			String department, String roleName, String enable) {
		if (name == null || name.equals("")) {
			name = null;
		}
		if (account == null || account.equals("")) {
			account = null;
		}
		if (department == null || department.equals("")) {
			department = null;
		}
		if (roleName == null || roleName.equals("")) {
			roleName = null;
		}
		Integer enableInt = null;
		if (enable == null || enable.equals("")) {
			enableInt = null;
		} else if (enable.equals("启用")) {
			enableInt = 1;
		} else if (enable.equals("禁用")) {
			enableInt = 0;
		}
		List<String> accounts = getAccounts(roleName);
		List<WxtbUser> wxtbUsers = wxtbUserMapper.selectByCondition(account,
				name, enableInt, department, accounts);
		if (wxtbUsers == null || wxtbUsers.isEmpty()) {
			return null;
		}
		List<UserManageInfo> userManageInfos = new ArrayList<UserManageInfo>();
		Integer click = supportService.ebableClick(realAccount, ClickEnum.USER_ENABLE);
		for (WxtbUser wxtbUser : wxtbUsers) {
			String departmentName = wxtbUser.getDepartmentCode();
			String accountItem = wxtbUser.getAccount();
			UserManageInfo userManageInfo = new UserManageInfo();
			userManageInfo.setAccount(wxtbUser.getAccount());
			userManageInfo.setUserName(wxtbUser.getUsername());
			userManageInfo.setDepartment(departmentName);
			enable = wxtbUser.getEnable() == 1 ? "启用" : "禁用";
			userManageInfo.setEnable(enable);
			userManageInfo.setClick(click);
			if (accountItem == null) {
				userManageInfos.add(userManageInfo);
				continue;
			}
			List<UserRole> userRoles = cacheService.getUserRole(accountItem);
			if (userRoles == null || userRoles.size() == 0) {
				userManageInfos.add(userManageInfo);
				continue;
			}
			String userRoleStr = "";
			for (int i = 0; i < userRoles.size(); i++) {
				if (i == 0) {
					userRoleStr = userRoleStr + userRoles.get(i).getRoleName();
				} else {
					userRoleStr = userRoleStr + ","
							+ userRoles.get(i).getRoleName();
				}
			}
			userManageInfo.setRole(userRoleStr);
			userManageInfos.add(userManageInfo);
		}
		return userManageInfos;
	}

	/**
	 * 获取角色对应的用户
	 * @param roleName
	 * @return
	 */
	public List<String> getAccounts(String roleName) {
		if (roleName == null) {
			return null;
		}
		Map<Integer, UserRole> userRoleMap = cacheService.getUserRoles();
		List<String> userRoleNames = new ArrayList<String>();
		for (Integer id : userRoleMap.keySet()) {
			String userRoleName = userRoleMap.get(id).getRoleName();
			userRoleNames.add(userRoleName);
		}
		List<Integer> ids = new ArrayList<Integer>();
		List<String> accounts = null;
		if (roleName != null) {
			for (Integer id : userRoleMap.keySet()) {
				String userRoleName = userRoleMap.get(id).getRoleName();
				if (userRoleName.contains(roleName)) {
					ids.add(id);
				}
			}
			if (ids.size() == 0) {
				ids = null;
			}
			List<UserRoleRelation> userRoleRelations = userRoleRelationMapper
					.selectByIds(ids);
			if (userRoleRelations != null && userRoleRelations.size() != 0) {
				accounts = new ArrayList<String>();
				for (UserRoleRelation userRoleRelation : userRoleRelations) {
					accounts.add(userRoleRelation.getAccount());
				}
			}
		}
		if (accounts == null || accounts.isEmpty()) {
			accounts = new ArrayList<String>();
			accounts.add("张飞站关羽");
		}
		return accounts;
	}

	@Override
	public boolean addUser(AddUser addUser, String password) {
		String account = addUser.getAccount();
		WxtbUser wxtbUser = wxtbUserMapper.selectByAccount(account);
		if (wxtbUser != null) {
			return false;
		}
		String username = addUser.getUsername();
		String departmentCode = addUser.getDepartmentCode();
		String enableStr = addUser.getEnable();
		List<Integer> roleIds = addUser.getRoleIds();
		Integer enable = 0;
		if (enableStr != null) {
			enable = enableStr.equals("启用") ? 1 : 0;
		}
		Date now = new Date();
		wxtbUser = new WxtbUser();
		wxtbUser.setAccount(account);
		wxtbUser.setUsername(username);
		// 使用默认密码
		String passWord = BASE64MD5Util.md5(password);
		wxtbUser.setHashedPassword(passWord);
		wxtbUser.setDepartmentCode(departmentCode);
		wxtbUser.setEnable(enable);
		wxtbUser.setCreateTime(now);
		wxtbUserMapper.insertSelective(wxtbUser);
		if (roleIds == null || roleIds.isEmpty()) {
			return true;
		}
		for (Integer roleId : roleIds) {
			UserRoleRelation userRoleRelation = new UserRoleRelation();
			userRoleRelation.setAccount(account);
			userRoleRelation.setEnable(1);
			userRoleRelation.setRoleId(roleId);
			userRoleRelation.setCreateTime(now);
			userRoleRelationMapper.insertSelective(userRoleRelation);
		}
		reloadUserRealtion();
		return true;
	}

	@Override
	public boolean operateEnable(String account) {
		WxtbUser wxtbUser = wxtbUserMapper.selectByAccount(account);
		if (wxtbUser == null) {
			return false;
		}
		Integer enable = wxtbUser.getEnable();
		if (enable == 0) {
			wxtbUser.setEnable(1);
		} else {
			wxtbUser.setEnable(0);
		}
		wxtbUserMapper.updateByPrimaryKeySelective(wxtbUser);
		return true;
	}

	@Override
	public boolean operateUserRole(String account, List<Integer> roleIds) {
		if (roleIds == null) {
			roleIds = new ArrayList<Integer>();
		}
		Date now = new Date();
		userRoleRelationMapper.deleteByAccount(account);
		for (Integer roleid : roleIds) {
			UserRoleRelation userRoleRelation = new UserRoleRelation();
			userRoleRelation.setAccount(account);
			userRoleRelation.setEnable(1);
			userRoleRelation.setRoleId(roleid);
			userRoleRelation.setCreateTime(now);
			userRoleRelationMapper.insertSelective(userRoleRelation);
		}
		reloadUserRealtion();
		return true;
	}

	@Override
	public Map<String, Object> getUserRoles() {
		Map<Integer, UserRole> userRoleMap = cacheService.getUserRoles();
		JSONArray jsonArray = new JSONArray();
		for (Integer roleId : userRoleMap.keySet()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", roleId);
			jsonObject.put("roleName", userRoleMap.get(roleId)
					.getRoleName());
			jsonArray.add(jsonObject);
		}
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("size", jsonArray.size());
		response.put("roles", jsonArray);
		return response;
	}

	public void reloadUserRealtion() {
		List<UserRoleRelation> userRoleRelationsNew = userRoleRelationMapper.load();
		cacheService.reloadUserRoleRelation(userRoleRelationsNew);
	}

	@Override
	public Integer vertifyPassword(String account, String oldPassword,
			String newPassword) {
		WxtbUser wxtbUser = wxtbUserMapper.selectByAccount(account);
		if (wxtbUser == null) {
			return 1;
		}
		String md5Old = BASE64MD5Util.md5(oldPassword);
		if (!md5Old.equals(wxtbUser.getHashedPassword())) {
			return 2;
		}
		String md5New = BASE64MD5Util.md5(newPassword);
		wxtbUser.setHashedPassword(md5New);
		wxtbUserMapper.updateByPrimaryKeySelective(wxtbUser);
		return 0;
	}

	@Override
	public Integer resetPassword(String account, String password) {
		WxtbUser wxtbUser = wxtbUserMapper.selectByAccount(account);
		if (wxtbUser == null) {
			return 1;
		}
		// 使用默认密码
		String passWord = BASE64MD5Util.md5(password);
		wxtbUser.setHashedPassword(passWord);
		wxtbUserMapper.updateByPrimaryKeySelective(wxtbUser);
		return 0;
	}
}
