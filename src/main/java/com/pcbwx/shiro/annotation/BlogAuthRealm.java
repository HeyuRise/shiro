package com.pcbwx.shiro.annotation;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.pcbwx.shiro.dao.WxtbUserMapper;
import com.pcbwx.shiro.model.WxtbUser;

/**
 * Created by BeautifulSoup on 2017/11/9.
 * 实现用户认证与授权
 */
public class BlogAuthRealm extends AuthorizingRealm {
	
	private static final Logger logger = LogManager.getLogger(BlogAuthRealm.class);
	
    @Autowired
    private WxtbUserMapper wxtbUserMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    	WxtbUser wxtbUser = (WxtbUser) principalCollection.getPrimaryPrincipal();
        logger.info("获取用户" + wxtbUser.getUsername() + "角色权限信息");
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        Set<String> roleList = new HashSet<String>();
        roleList.add("管理员");
        info.setRoles(roleList);
        return info;
    }




    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        if (usernamePasswordToken==null){
            System.out.println("用户信息为空!");
        }
        String username = usernamePasswordToken.getUsername();
        WxtbUser wxtbUser = wxtbUserMapper.selectByAccount(username);
        return new SimpleAuthenticationInfo(wxtbUser,wxtbUser.getHashedPassword(),this.getClass().getName());
    }

}
