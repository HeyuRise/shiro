package com.pcbwx.shiro.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.dao.RoleAuthMapper;
import com.pcbwx.shiro.dao.UserAuthMapper;
import com.pcbwx.shiro.dao.UserRoleMapper;
import com.pcbwx.shiro.enums.ConfigEnum;
import com.pcbwx.shiro.model.RoleAuth;
import com.pcbwx.shiro.model.UserAuth;
import com.pcbwx.shiro.model.UserRole;
import com.pcbwx.shiro.utils.DataUtil;


/**
 * 该过滤器的主要作用就是通过spring著名的IoC生成securityMetadataSource。
 * securityMetadataSource相当于本包中自定义的MyInvocationSecurityMetadataSourceService。
 * 该MyInvocationSecurityMetadataSourceService的作用提从数据库提取权限和资源，装配到HashMap中，
 * 供Spring Security使用，用于权限校验。
 * 
 */
@Service("mySecurityMetadataSource")
@Transactional
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
	
	private static final Logger logger = LogManager.getLogger(MySecurityMetadataSource.class);
	
	private static Map<String, Collection<ConfigAttribute>> resourceMap = null;  

	private RequestMatcher pathMatcher;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RoleAuthMapper roleAuthMapper;
	@Autowired
	private UserAuthMapper userAuthMapper;
	
	

	public MySecurityMetadataSource(){
		
	}
	
	// 由spring调用
	public MySecurityMetadataSource(UserRoleMapper userRoleMapper) {
		loadResourceDefine();
	}

	
	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return new ArrayList<ConfigAttribute>();
	}

	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	public void getNewResourceMap(){
		loadResourceDefine();
	}

	// 拦截的url和角色名之间的关系
	private void loadResourceDefine(){
		/*url，角色集合*/
		resourceMap = new HashMap<String, Collection<ConfigAttribute>>();
		
		Integer debug = ConfigProperties.getPropertyInt(ConfigEnum.DEBUG_WITHOUT_LOGIN);
		if (!DataUtil.equals(debug, 1)) {
			List<UserRole> userRoles = userRoleMapper.getRoles();
			List<RoleAuth> roleAuths = roleAuthMapper.load();
			for (UserRole userRole : userRoles) {
				String roleName = userRole.getRoleName();
				List<Integer> authIds = new ArrayList<Integer>();
				for (RoleAuth roleAuth : roleAuths) {
					if (userRole.getId() == roleAuth.getRoleId()) {
						authIds.add(roleAuth.getAuthId());
					}
				}
				List<String> urls = new ArrayList<String>();
				for (Integer authId : authIds) {
					List<UserAuth> userAuths = userAuthMapper.selectByAuthType(authId);
					if (userAuths == null || userAuths.size() == 0) {
						continue;
					}
					for (UserAuth userAuth : userAuths) {
						urls.add(userAuth.getUrl());
					}
				}
				for(String url : urls){
					Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
					ConfigAttribute ca = new SecurityConfig(roleName);
					atts.add(ca);
					logger.info(roleAuths + "----------------" + url);
					if (url == null || url.equals("")) {
						continue;
					}
					if(resourceMap.containsKey(url)){
						resourceMap.get(url).add(ca);
					}else{
						resourceMap.put(url, atts);
					}
				}
			}			
		}
	}
	
    //返回所请求资源所需要的权限  
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
    	if(resourceMap == null) {  
			loadResourceDefine();
        }
    	FilterInvocation fi = ((FilterInvocation)object);
    	//资源url遍历
    	Iterator<String> it = resourceMap.keySet().iterator();
    	Collection<ConfigAttribute> returnList = new ArrayList<ConfigAttribute>();
		while (it.hasNext()) {
			String resURL = it.next();
			pathMatcher = new AntPathRequestMatcher(resURL);
			if (pathMatcher.matches(fi.getRequest())) {
				Collection<ConfigAttribute> returnCollection = resourceMap.get(resURL);
				returnList.addAll(returnCollection);
			}
		}
		if(returnList.size()!=0){
			return returnList;
		}else{
			return null;
		}
    } 
}