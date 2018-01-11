package com.pcbwx.shiro.component;

import java.util.List;
import java.util.Map;

import com.pcbwx.shiro.enums.DictionaryEnum;
import com.pcbwx.shiro.model.Address;
import com.pcbwx.shiro.model.Dictionary;
import com.pcbwx.shiro.model.ExpressProduct;
import com.pcbwx.shiro.model.Menu;
import com.pcbwx.shiro.model.RoleAuth;
import com.pcbwx.shiro.model.Service;
import com.pcbwx.shiro.model.UserAuth;
import com.pcbwx.shiro.model.UserRole;
import com.pcbwx.shiro.model.UserRoleRelation;


public interface CacheService {
	void reloadDictionary(List<Dictionary> dictionarys);

	void reloadUserRole(List<UserRole> records);
	void reloadUserRoleRelation(List<UserRoleRelation> records);
	void reloadUserAuth(List<UserAuth> records);
	void reloadRoleAuth(List<RoleAuth> records);
	void reloadAddress(List<Address> records);
	void reloadMenu(List<Menu> records);
	void reloadExpressProduct(List<ExpressProduct> records);
	void reloadService(List<Service> records);
	//--------------------------------------------------
	Dictionary getDictionary(DictionaryEnum type, Integer innerId);
	Dictionary getDictionary(DictionaryEnum type, String innerCode);
	
	List<Dictionary> getDictionarys(DictionaryEnum type);

	List<UserRole> getUserRole(String userCode);
	Map<Integer, UserRole> getUserRoles();
	Map<Integer, Address> getAddressMap();
	Map<Integer, RoleAuth> getRoleAuth();
	Map<Integer, Menu> getMenu();
	Service getService(Integer serviceId);
 	ExpressProduct getExpressProduct(Integer productId);
} 
