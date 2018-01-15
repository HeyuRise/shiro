package com.pcbwx.shiro.annotation;

import java.util.LinkedHashMap;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;

import com.pcbwx.shiro.service.ShiroService;

public class ShiroPermissionFactory extends ShiroFilterFactoryBean {

	private static ShiroService shiroService;

    /**
     * 初始化设置过滤链
     */
    @Override
    public void setFilterChainDefinitions(String definitions) {
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/auth/**", "anon");
        filterChainDefinitionMap.put("/util/**","anon");
        filterChainDefinitionMap.put("/script/**","anon");

        filterChainDefinitionMap.put("/index", "authc");
        filterChainDefinitionMap.put("/order/**", "roles[管理员]");
        filterChainDefinitionMap.put("/html/**", "roles[管理员]");
        filterChainDefinitionMap.put("/**", "user");
        setFilterChainDefinitionMap(filterChainDefinitionMap);
    }
	
	public static ShiroService getShiroService() {
		return shiroService;
	}

	public static void setShiroService(ShiroService shiroService) {
		ShiroPermissionFactory.shiroService = shiroService;
	}
	
	
}
