package com.pcbwx.shiro.component;


public class ServiceRouter {
	//private static final Logger logger = LogManager.getLogger(ServiceRouter.class);

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
