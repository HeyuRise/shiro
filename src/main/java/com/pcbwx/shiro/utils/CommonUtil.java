package com.pcbwx.shiro.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pcbwx.shiro.annotation.FieldMap;
import com.pcbwx.shiro.map.ChannelDeserialize;

/**
 * 公共通用util工具包
 * 
 * @author 王海龙
 * @version 1.0
 *
 */
public class CommonUtil {

	private static SimpleDateFormat sdf = null;
	private static final Logger logger = Logger.getLogger(CommonUtil.class);

	public static void main(String[] args) {
		// CommonUtil.sendAuthCode("18668187252");
		boolean check = CommonUtil.checkMobileNumber("18668187252");
		System.out.println(check);
	}

	/**
	 * 获取UUID
	 * 
	 * @return UUID值
	 */
	public static String getUuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 检查是否合法的手机号
	 * 
	 * @param mobile
	 *            手机号
	 * @return
	 */
	public static boolean checkMobileNumber(String mobile) {
		if (mobile == null || mobile.length() != 11 || !mobile.startsWith("1")) {
			return false;
		}
		Long number = null;
		try {
			number = Long.valueOf(mobile);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
		}
		if (number == null) {
			return false;
		}
		String __mobile = String.valueOf(number);
		if (!__mobile.equals(mobile)) {
			return false;
		}
		return true;
	}

	/**
	 * 将meetingId转换成显示格式
	 * 
	 * @param meetingId
	 * @return
	 */
	public static String transMeetingId(long meetingId) {
		String suhfix = String.format("%c", ((int) meetingId / 9999) + 0x41);
		String value = String.format("%04d", meetingId);
		return suhfix + value;
	}

	/**
	 * 解escape解码的字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String unEscape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * 去除List中的重复对象
	 * 
	 * @param lists
	 * @return
	 */
	// public static List<Person> RemoveDuplicate(List<Person> lists) {
	//
	// List<Person> results = new ArrayList<Person>();
	// Set<Person> sets = new HashSet<Person>(lists);
	// results.addAll(sets);
	// return results;
	// }

	public static String getCronTime(String time) {
		logger.debug("cron original Time is : " + time);

		StringBuffer cronTime = new StringBuffer();
		if (!StringUtil.isEmpty(time)) {
			String[] strs = time.split(":");
			for (int i = strs.length - 1; i >= 0; i--) {
				if (strs[i].equals("00")) {
					strs[i] = "0";
				}
				cronTime.append(strs[i]).append(" ");
			}

		}
		cronTime = cronTime.append("*").append(" ").append("*").append(" ").append("?");
		logger.debug("cron Time is : " + cronTime);
		return cronTime.toString();
	}

	/**
	 * 将父类的数据通过字段反射拷贝到子类中
	 * 
	 * @param father
	 *            父类实例
	 * @param childClass
	 *            子类类型
	 * @return 子类实例
	 */
	public static Object fatherToChild(Object father, Class childClass) {
		// if(!(child.getClass().getSuperclass()==father.getClass())){
		if (!(childClass.getSuperclass() == father.getClass())) {
			System.err.println("child不是father的子类");
		}
		Class fatherClass = father.getClass();
		// Class childClass= child.getClass();
		Field ff[] = fatherClass.getDeclaredFields();
		Object child = null;
		try {
			child = childClass.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		for (int i = 0; i < ff.length; i++) {
			Field f = ff[i];// 取出每一个属性，如deleteDate
			Class type = f.getType();
			try {
				// 方法getDeleteDate
				String upperFieldName = StringUtil.toUpperFirst(f.getName());
				Method m = fatherClass.getMethod("get" + upperFieldName);
				Object objValue = m.invoke(father);// 取出属性值

				Method mSet = childClass.getMethod("set" + upperFieldName, type);
				mSet.invoke(child, objValue);
			} catch (SecurityException | IllegalArgumentException
					| NoSuchMethodException | IllegalAccessException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return child;
	}

	/**
	 * 将源数据类中的字段拷贝到目标类中相同字段中(针对目标类字段数多于源类，效率较高)
	 * 
	 * @param src
	 *            源数据类 dstClazz 目标类
	 * @return
	 * @throws Exception
	 */
	public static Object fetchMapFields(Object src, Class dstClazz) throws Exception {
		Class srcClazz = src.getClass();
		// Class childClass= child.getClass();
		Field srcFields[] = srcClazz.getDeclaredFields();
		Object child = null;
		try {
			child = dstClazz.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		Field dstFields[] = dstClazz.getDeclaredFields();
		Map<String, Class<?>> fieldTypeMap = new HashMap<String, Class<?>>();
		for (Field dstField : dstFields) {
			fieldTypeMap.put(dstField.getName(), dstField.getType());
		}

		for (int i = 0; i < srcFields.length; i++) {
			Field field = srcFields[i];// 取出每一个属性，如deleteDate
			// Class type = field.getType();
			String dstFieldName = null;
			try {
				// 方法getDeleteDate
				FieldMap annotation = field.getAnnotation(FieldMap.class);
				if (annotation == null) {
					continue;
				}
				dstFieldName = field.getName();
				if (!StringUtils.isBlank(annotation.value())) {
					dstFieldName = annotation.value();
				}
				String srcUpperFieldName = StringUtil.toMethodName(field.getName());
				String dstUpperFieldName = StringUtil.toMethodName(dstFieldName);
				Method mGet = srcClazz.getMethod("get" + srcUpperFieldName);
				if (mGet == null) {
					continue;
				}
				Object srcValue = mGet.invoke(src);// 取出属性值
				if (srcValue == null) {
					continue;
				}
				Object dstValue = srcValue;

				// Class<?> dstType = annotation.type();
				// if (dstType.getName().equals(NoneType.class.getName())) {
				// dstType = type;
				// }
				Class<?> dstType = fieldTypeMap.get(dstFieldName);
				if (dstType == null) {
					continue;
				}
				Method mSet = dstClazz.getMethod("set" + dstUpperFieldName, dstType);
				if (mSet == null) {
					continue;
				}
				Class<? extends ChannelDeserialize<?>> convertMethod = annotation.using();
				if (!convertMethod.getName().equals(ChannelDeserialize.None.class.getName())) {
					ChannelDeserialize<?> instance = convertMethod.newInstance();
					dstValue = instance.deserialize(srcValue);
				}
				mSet.invoke(child, dstValue);
			} catch (SecurityException | IllegalArgumentException | NoSuchMethodException
					| IllegalAccessException | InvocationTargetException | ClassCastException e) {
				logger.info("field copy error! fieldName=" + dstFieldName);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return child;
	}
}
