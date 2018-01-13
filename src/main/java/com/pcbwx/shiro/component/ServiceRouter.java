package com.pcbwx.shiro.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceRouter {
	private static final Logger logger = LogManager.getLogger(ServiceRouter.class);

	private static CacheService cacheService;

	public void init() {

	}

	public static CacheService getCacheService() {
		return cacheService;
	}
	public static void setCacheService(CacheService cacheService) {
		ServiceRouter.cacheService = cacheService;
	}
}
