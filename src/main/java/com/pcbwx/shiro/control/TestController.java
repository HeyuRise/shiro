package com.pcbwx.shiro.control;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.service.OrderService;

@Controller
@RequestMapping("/test")
public class TestController {
	
	@Autowired
	private OrderService orderService;
	
	@RequestMapping( value = "/test1", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> test1(){
		orderService.addMailNoInfo();
		return null;
	}

}
