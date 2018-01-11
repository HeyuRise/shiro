/**
 * 
 */
package com.pcbwx.shiro.timer;

/**
 * AD配置类
 * 
 * @author 王海龙
 *
 */
public class AdConfig implements Cloneable {
	private AdConfig memo; // 快照对象，用于保存旧的对象
	private ConfigChangeListener configChangeListener; //配置项变化监听器

	private String adServerAddress;
	private String adAccount;
	private String adPassword;
	private String adBaseDn;
	private String adOu;
	private String adSync;

	/**
	 * @return the memo
	 */
	public AdConfig getMemo() {
		return memo;
	}

	/**
	 * @return the adServerAddress
	 */
	public String getAdServerAddress() {
		return adServerAddress;
	}

	/**
	 * @return the adAccount
	 */
	public String getAdAccount() {
		return adAccount;
	}

	/**
	 * @return the adPassword
	 */
	public String getAdPassword() {
		return adPassword;
	}

	/**
	 * @return the adSuffix
	 */

	/**
	 * @return the adBaseDn
	 */
	public String getAdBaseDn() {
		return adBaseDn;
	}

	/**
	 * @return the adOu
	 */
	public String getAdOu() {
		return adOu;
	}

	/**
	 * @return the adSync
	 */
	public String getAdSync() {
		return adSync;
	}

	/**
	 * 设置AD配置项的值
	 * 
	 * @param adConfig
	 */
	public synchronized void setValue(String adServerAddress, String adAccount,
			String adBaseDn, String adOu, String adPassword, String adSuffix,
			String adSync) {
		this.adServerAddress = adServerAddress;
		this.adAccount = adAccount;
		this.adBaseDn = adBaseDn;
		this.adOu = adOu;
		this.adPassword = adPassword;
		this.adSync = adSync;
		if (memo == null || !memo.equals(this)) {
			if (configChangeListener != null) {
				configChangeListener.onConfigChange(this);
			}

			try {
				memo = (AdConfig) clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置AD配置项变化监听器
	 * 
	 * @param configChangeListener
	 */
	public synchronized void setConfigChangeListener(
			ConfigChangeListener configChangeListener) {
		this.configChangeListener = configChangeListener;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((adAccount == null) ? 0 : adAccount.hashCode());
		result = prime * result
				+ ((adBaseDn == null) ? 0 : adBaseDn.hashCode());
		result = prime * result + ((adOu == null) ? 0 : adOu.hashCode());
		result = prime * result
				+ ((adPassword == null) ? 0 : adPassword.hashCode());
		result = prime * result
				+ ((adServerAddress == null) ? 0 : adServerAddress.hashCode());
		result = prime * result + ((adSync == null) ? 0 : adSync.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdConfig other = (AdConfig) obj;
		if (adAccount == null) {
			if (other.adAccount != null)
				return false;
		} else if (!adAccount.equals(other.adAccount))
			return false;
		if (adBaseDn == null) {
			if (other.adBaseDn != null)
				return false;
		} else if (!adBaseDn.equals(other.adBaseDn))
			return false;
		if (adOu == null) {
			if (other.adOu != null)
				return false;
		} else if (!adOu.equals(other.adOu))
			return false;
		if (adPassword == null) {
			if (other.adPassword != null)
				return false;
		} else if (!adPassword.equals(other.adPassword))
			return false;
		if (adServerAddress == null) {
			if (other.adServerAddress != null)
				return false;
		} else if (!adServerAddress.equals(other.adServerAddress))
			return false;
		if (adSync == null) {
			if (other.adSync != null)
				return false;
		} else if (!adSync.equals(other.adSync))
			return false;
		return true;
	}

}
