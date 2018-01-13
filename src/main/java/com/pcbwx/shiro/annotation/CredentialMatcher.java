package com.pcbwx.shiro.annotation;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * Created by BeautifulSoup on 2017/11/9.
 * 完成账户信息的校验
 */
public class CredentialMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken usernamePasswordToken= (UsernamePasswordToken) token;
        String rawPassword=new String(usernamePasswordToken.getPassword());

        String dbPassword= (String) info.getCredentials();
        return this.equals(rawPassword,dbPassword);
    }
}
