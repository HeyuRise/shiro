package com.pcbwx.shiro.utils;

import org.apache.log4j.Logger;

/**
 * 公共通用util工具包
 * 
 * @author 王海龙
 * @version 1.0
 *
 */
public class DisplayTransUtil {

	private static final Logger logger = Logger.getLogger(DisplayTransUtil.class);
	
	public static void main(String[] args) {
//		CommonUtil.sendAuthCode("18668187252");
		Double db = (double) 10;
		System.out.println(">>>" + db);
		System.out.println(">>>" + db.intValue());
//		System.out.println(">>>" + null2string(null));
	}

	/**
	 * 将数字转换为字符串(若为null,转换为空字符串)
	 * @param value
	 * @return
	 */
	public static String nullOrZero2string(Integer value) {
		return value == null || value == 0 ? "" : String.valueOf(value);
	}
	/**
	 * 将为null或为"0"的字符串转换为空字符串
	 * @param value
	 * @return
	 */
	public static String nullOrZero2string(String value) {
		return value == null || value.equals("0") ? "" : String.valueOf(value);
	}
	/**
	 * 将Double数字转换为字符串(若为null,转换为空字符串，若没有小数，把.00去掉)
	 * @param value
	 * @return
	 */
	public static String double2string(Double value) {
		if (value == null) {
			return "";
		}
		if (value == value.intValue()) {
			return String.valueOf(value.intValue());
		}
		return String.valueOf(value);
	}
	/**
	 * 将null转换成空字符串
	 * @param value
	 * @return
	 */
	public static String null2string(Object value) {
		return value == null ? "" : String.valueOf(value);
	}
	/**
	 * 将null转换成默认字符串,不为空转换为字符串(带单位)
	 * @param value 源数据
	 * @param unit 单位
	 * @param def 默认字符串
	 * @return
	 */
	public static String null2string(Object value, String unit, String def) {
		return value == null ? def : String.valueOf(value) + unit;
	}
	/**
	 * 判断数字对象是否为零
	 * @param obj 数字对象
	 * @return 布尔结果
	 */
	public static boolean isBlank(Object obj) {
		if (obj == null) {
			return false;
		}
		Class<? extends Object> clazz = obj.getClass();
		if (clazz.getName().equals("java.lang.Byte")) {
			Byte value = (Byte)obj;
			return value.equals((byte)0);
		} else if (clazz.getName().equals("java.lang.Short")) {
			Short value = (Short)obj;
			return value.equals((short)0);
		} else if (clazz.getName().equals("java.lang.Integer")) {
			Integer value = (Integer)obj;
			return value.equals((int)0);
		} else if (clazz.getName().equals("java.lang.Long")) {
			Long value = (Long)obj;
			return value.equals((long)0);
		} else if (clazz.getName().equals("java.lang.Double")) {
			Double value = (Double)obj;
			return value.equals((double)0);
		} else if (clazz.getName().equals("java.lang.Float")) {
			Float value = (Float)obj;
			return value.equals((float)0);
		} else if (clazz.getName().equals("java.lang.String")) {
			String value = (String)obj;
			return value.equals("0") || value.equals("");
		}
		return false;
	}
	/**
	 * 根据状态判断，将null转换成默认字符串,不为空转换为字符串(带单位)
	 * @param orderState 订单状态
	 * @param value 源数据
	 * @param unit 单位
	 * @param def 默认字符串
	 * @return
	 */
	public static String null2stringWithState(String orderState, Object value, String unit, String def) {
		if (OrderUtil.isFinished(orderState)) {
			if (value == null || DisplayTransUtil.isBlank(value)) {
				return "0" + unit;
			}
			return String.valueOf(value) + unit;
		} else {
			return value == null || DisplayTransUtil.isBlank(value) ? def : String.valueOf(value) + unit;
		}
	}
	
	/**
	 * 将印制板的长宽尺寸转换为字符串(若无信息转为暂无，没有小数，截去.00)
	 * @param orderState 订单状态
	 * @param length 板长
	 * @param width 板宽
	 * @return 字符串"长*宽"
	 */
	public static String boardSize2String(String orderState, Double length, Double width) {
		if (length == null || width == null) {
			if (OrderUtil.isFinished(orderState)) {
				return "无";
			}
			return "暂无";
		}
		StringBuilder sb = new StringBuilder();
		if (length == length.intValue()) {
			sb.append(length.intValue());
		} else {
			sb.append(length);
		}
		sb.append("mm * ");
		if (width == width.intValue()) {
			sb.append(width.intValue());
		} else {
			sb.append(width);
		}
		sb.append("mm");
		return sb.toString();
	}
}
