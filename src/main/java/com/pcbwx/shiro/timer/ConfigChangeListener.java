/**
 * 
 */
package com.pcbwx.shiro.timer;

/**
 * AD配置项变化监听器
 * 
 * @author 王海龙
 *
 */
public interface ConfigChangeListener {

	/**
	 * 当配置项有变化时，执行此方法
	 * 
	 * @param adConfig
	 */
	void onConfigChange(AdConfig adConfig);
}
