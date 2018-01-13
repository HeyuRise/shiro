package com.pcbwx.shiro.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.exception.ServiceException;

/**
 * 检测服务器网络接口
 * @author 王海龙
 * @version 1.0
 *
 */
@Controller
@Api(tags = "检测服务器网络接口")
public class ReachableController {
	
	private static final Logger logger = LogManager.getLogger(LogContext.class);
	
	/**
	 * 方法名：reachable
	 * 描述：测试服务器的连通性
	 * 
	 * @return 无 ,客户端负责处理200状态码
	 * @throws ServiceException
	 */
	@RequestMapping(value="/reachable",method=RequestMethod.GET)
	@ResponseBody
	@ApiOperation("测试服务器的连通性")
	public String reachable() throws ServiceException{
		
//		LogContext.addLog("测试服务器连通性", "test Server connectivity");
		
		try {
			InetAddress addr = InetAddress.getLocalHost();
			if (addr instanceof java.net.Inet4Address) {
				return testReachable(addr);
			} else if (addr instanceof java.net.Inet6Address) {
				return testReachable(addr);
			} else {
				return "";
			}
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			return "";
		}
	}
		
	private static String testReachable(InetAddress address)  {

		try {
			if (address.isReachable(5000)) {
				return "";
			} else {
				return "";
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			return "";
		}
	}
	
}


