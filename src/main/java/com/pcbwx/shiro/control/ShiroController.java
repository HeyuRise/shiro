package com.pcbwx.shiro.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.enums.ErrorCodeEnum;

@Controller
public class ShiroController extends BaseController{

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
    public String loginUser(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            HttpSession session, Model model) {
		Map<String, Object> response = new HashMap<String, Object>();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            response.put("result", ErrorCodeEnum.SUCCESS);
            return "";
        } catch (Exception e) {
        	response.put("result", ErrorCodeEnum.SYSTEM_ERROR);
        	response.put("msg", e.getMessage());
        	return "redirect:/login";
        }
    }
}
