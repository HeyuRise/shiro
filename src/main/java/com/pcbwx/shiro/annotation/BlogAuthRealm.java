package com.pcbwx.shiro.annotation;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private WxtbUserMapper wxtbUserMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    	WxtbUser wxtbUser = (WxtbUser) principalCollection.getPrimaryPrincipal();
        List<String> permissionList=new ArrayList<>();
        List<String> roleList=new ArrayList<>();
        
        
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        info.addStringPermissions(permissionList);
        info.addRoles(roleList);
        System.out.println("获取用户角色权限信息");
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
