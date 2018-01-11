package com.pcbwx.shiro.utils;

/**
 * 判断文件版本工具类
 * 
 * @author 王海龙
 * @version 1.0
 *
 */
public class FileTypeUtil {

	/**
	 * 判断excel是否为2003版本
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	/**
	 * 判断excel是否为2007版本
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}

	/**
	 * 判断word是否为2003版本
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isWord2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(doc)$");
	}

	/**
	 * 判断word是否为2007版本
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isWord2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(docx)$");
	}

	/**
	 * 判断是否为pdf文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isPdf(String filePath) {
		return filePath.matches("^.+\\.(?i)(pdf)$");
	}

}
