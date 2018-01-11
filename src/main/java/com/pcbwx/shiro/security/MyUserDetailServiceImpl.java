package com.pcbwx.shiro.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pcbwx.shiro.bean.user.WxtbAuthUser;
import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.component.CacheService;
import com.pcbwx.shiro.dao.WxtbUserMapper;
import com.pcbwx.shiro.enums.ConfigEnum;
import com.pcbwx.shiro.model.UserRole;
import com.pcbwx.shiro.model.WxtbUser;
import com.pcbwx.shiro.utils.DataUtil;

@SuppressWarnings("deprecation")
@Service("myUserDetailServiceImpl")
@Transactional
public class MyUserDetailServiceImpl implements UserDetailsService {
	
	@Autowired
	private CacheService cacheService;	
	
	@Autowired
	private WxtbUserMapper wxtbUserMapper;
	
	Logger logger = Logger.getLogger(MyUserDetailServiceImpl.class);
	
	@Override
	//通过cas返回的用户名，重载为系统中自定义的用户
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if(StringUtils.isEmpty(username)){
			throw new UsernameNotFoundException("用户名不能为空！");	
		}
		WxtbUser wxtbUser = null;
		try {
			wxtbUser = wxtbUserMapper.selectByAccount(username);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		if(wxtbUser == null){
			throw new UsernameNotFoundException("不存在该用户！ ");	
		}
		WxtbAuthUser user = (WxtbAuthUser) DataUtil.fatherToChild(wxtbUser, WxtbAuthUser.class);
		Set<GrantedAuthority> authorities = obtionGrantedAuthority(user);
		user.setAuthorities(authorities);
        return user;
	}
	
	private Set<GrantedAuthority> obtionGrantedAuthority(WxtbAuthUser wxtbUser){
		Set<GrantedAuthority> roleSet = new HashSet<GrantedAuthority>();
		
		Integer debug = ConfigProperties.getPropertyInt(ConfigEnum.DEBUG_WITHOUT_LOGIN);
		if (!DataUtil.equals(debug, 1)) {
			final List<UserRole> userRoles = cacheService.getUserRole(wxtbUser.getAccount());
	        for(UserRole role : userRoles) {
	        	roleSet.add(new GrantedAuthorityImpl(role.getRoleName()));
	        }
			roleSet.add(new GrantedAuthorityImpl("游客"));			
		}
        return roleSet;
	}
}



