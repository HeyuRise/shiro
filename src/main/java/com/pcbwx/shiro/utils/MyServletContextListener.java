package com.pcbwx.shiro.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 应用启动监听器类
 * 
 * @author 王海龙
 * @since 1.0.0
 *
 */
public class MyServletContextListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(MyServletContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sce
				.getServletContext());

		// 删除会话表记录
//		AccountSessionService accountSessionService = applicationContext.getBean(AccountSessionService.class);
//		accountSessionService.deleteAll();

		// 定期扫描AD同步参数变化
//		ConfigService configService = applicationContext.getBean(ConfigService.class);
//		PersonService personService = applicationContext.getBean(PersonService.class);
//		QuartzTask task = (QuartzTask) applicationContext.getBean("quartzTask");
//		task.executeTimeTask(configService, personService);

		logger.info("server is started.");

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("server is closed.");

	}

}
