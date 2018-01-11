package com.pcbwx.shiro.utils;

/**
 * 随机数工具包
 * 
 * @author 王海龙
 * 
 */
public class RandomUtil {

	/**
	 * 生成随机数
	 * 
	 * @param length
	 *            随机数长度
	 * @return
	 */
	public static String createRandom(int length) {
		return createRandom(true, length);
	}

	/**
	 * 生成随机数
	 * 
	 * @param numberFlag
	 *            是否纯数字
	 * @param length
	 *            随机数长度
	 * @return
	 */
	public static String createRandom(boolean numberFlag, int length) {
		String retStr = "";
		String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}
}
