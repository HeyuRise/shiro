/**
 * 
 */
package com.pcbwx.shiro.timer;

/**
 * AdConfig的单例工厂类
 * 
 * @author 王海龙
 *
 */
public class AdConfigFactory {

	private static AdConfig adConfig = new AdConfig();

	/**
	 * 获取AdConfig的实例
	 * 
	 * @return
	 */
	public static AdConfig getInstance() {
		return adConfig;
	}
}
