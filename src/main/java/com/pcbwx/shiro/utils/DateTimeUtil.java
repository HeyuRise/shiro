package com.pcbwx.shiro.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 常用工具函数类. 用静态函数的方法定义一些常用的函数，如字符串转数字、字符串转日期等。 *
 * 
 */
public class DateTimeUtil {

	private static final Logger logger = LogManager.getLogger(DateTimeUtil.class);
	
	/**
	 * 将字符串时间转换为Date类型的时间
	 * @param time 字符格式的时间
	 * @param pattern 时间的字符串格式
	 * @return Date类型时间
	 */
//	public static java.util.Date getDateTime(String time, String pattern) {
//		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//		try {
//			return sdf.parse(time);
//		} catch (ParseException e) {
//			logger.error("解析String时间类型为Date时间类型发生错误");
//		}
//		return null;
//
//	}

	/**
	 * 得到String类型的时间
	 * @param source Date时间
	 * @param pattern 时间格式
	 * @return 字符串格式时间
	 */
//	public static String getStringTime(Date source, String pattern) {
//		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//		return sdf.format(source);
//	}

	/**
	 * 按"yyyy-MM-dd HH:mm:ss"格式得到String类型的时间
	 * @param time Date时间
	 * @return 字符串格式时间
	 */
//	public static String getDateStr(Date time){
//		
//		StringBuffer sb = new StringBuffer();
//		if(time!=null){
//			String timeStr = getStringTime(time, "yyyy-MM-dd HH:mm:ss");
//			sb.append(timeStr);
//			sb = sb.insert(8,"T");
//			return sb.toString();
//		}else{
//			return null;
//		}
//		
//	}
	
	/**
	 * 日期转字符串，输出"yyyy-MM-dd HH:mm:ss"的表示形式
	 * @param aDate	需要转换的日期对象
	 * @return 字符串格式时间
	 */
	public static String date2dateTimeStr(Date aDate) {
		if (aDate == null)
			return null;

		SimpleDateFormat theFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return theFormat.format(aDate);
	}

	/**
	 * 日期转字符串
	 * @param aDate	需要转换的日期对象
	 * @param pattern yyyy-MM-dd HH:mm:ss
	 *            	yyyy/MM/dd HH:mm:ss
	 *            	yyyy-MM-dd-HH-mm-ss
	 * @return 字符串格式时间
	 */
	public static String date2dateTimeStr(Date aDate, String pattern) {
		if (aDate == null)
			return null;

		SimpleDateFormat theFormat = new SimpleDateFormat(pattern);
		return theFormat.format(aDate);
	}
	
	public static Date Str2dateTime(String aDate, String pattern){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = dateFormat.parse(aDate);
		} catch (ParseException e) {
			return null;
		}
		return date;
	}
	
	/**
	 * 日期转字符串，输出"yyyy-MM-dd"的表示形式
	 * @param dateValue 需要转换的日期对象
	 * @return
	 */
	public static String date2dateStr(Date dateValue) {
		if (dateValue == null)
			return null;
		SimpleDateFormat theFormat = new SimpleDateFormat("yyyy-MM-dd");
		return theFormat.format(dateValue);
	}

	/**
	 * 日期转字符串，输出"yyyy-MM-dd 00:00:00"的表示形式
	 * @param aDate	需要转换的日期对象
	 * @return String 字符串格式时间
	 */
	public static String date2dateStr_ex(Date aDate) {
		if (aDate == null)
			return null;

		SimpleDateFormat theFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return theFormat.format(aDate);
	}

	/**
	 * 字符串xxxx-xx-xx转换成Date
	 * @param str xxxx-xx-xx格式的时间字符串
	 * @return 日期对象
	 */
	public static Date dateStr2date(String str) {
		if (str == null) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = dateFormat.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 字符串xxxx-xx-xx xx:xx:xx转换成Date
	 * @param str xxxx-xx-xx xx:xx:xx格式的时间字符串
	 * @return 日期对象
	 */
	public static Date dateTimeStr2date(String str) {
		if (str == null) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 截取日期(把时分秒去掉)
	 * @param date 日期对象
	 * @return 不包含时分秒的日期对象
	 */
	public static Date truncateDateTime(Date date) {
		if (date == null) {
			return null;
		}
		String dateStr = date2dateStr(date);
		return dateStr2date(dateStr);
	}

	/**
	 * 获取某月第一天起始时刻
	 * @param date 一月中的某一天
	 * @return 该月的第一天的零点日期
	 */
	public static Date getFirstTimeOfMonth(Date date)   {  
        Calendar cDay1 = Calendar.getInstance();  
        cDay1.setTime(date); 
        Date lastDate = cDay1.getTime();  
        lastDate.setDate(1); 
        String lastTimeStr = date2dateStr(lastDate) + " 00:00:00";
        return dateTimeStr2date(lastTimeStr);  
	} 

	/**
	 * 获取某月最后一天最后时刻
	 * @param date 一月中的某一天
	 * @return 该月的最后一天的23:59:59
	 */
	public static Date getLastTimeOfMonth(Date date)   {  
        Calendar cDay1 = Calendar.getInstance();  
        cDay1.setTime(date);  
        final int lastDay =  cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);  
        Date lastDate = cDay1.getTime();  
        lastDate.setDate(lastDay); 
        String lastTimeStr = date2dateStr(lastDate) + " 23:59:59";
        return dateTimeStr2date(lastTimeStr);  
	} 

	/**
	 * 获取几天之前的日期(0点)
	 * @param date 时间基点
	 * @param days 减去的天数
	 * @return 减去天数后的日期的对象
	 */
	public static Date getLastTimeBeforeDays(Date date, int days)   {  
        Date theDay = truncateDateTime(date);
        Date lastDay = DateCalcUtil.subTime(theDay, Calendar.DATE, days);
        return lastDay;  
	} 

	/**
	 * 获取当天的最后时刻
	 * @param date 当天时间
	 * @return 最后时刻时间对象(23:59:59)
	 */
	public static Date getTheDayLastTime(Date date)   {
		if (date == null) {
			return null;
		}
		String lastTimeStr = date2dateStr(date) + " 23:59:59";
        return dateTimeStr2date(lastTimeStr);  
	} 
	
	public static void main(String args[]) {
//		Date date = DateTimeUtil.truncateDateTime(new Date());
//		System.out.println(DateTimeUtil.date2dateStr(new Date()));
//		String dateTime = "2015-06-17 00:11:12";
//		Date date = DateTimeUtil.dateStr2date(dateTime);
		
//		Date date = DateTimeUtil.getLastTimeOfMonth(new Date());
		Date date = getFirstTimeOfMonth(new Date());
		System.out.println(date);

		Date delayDate = DateTimeUtil.getLastTimeBeforeDays(new Date(), 3);
		System.out.println("last time:" + delayDate);
		
		Date now = new Date();
		String timeStr = DateTimeUtil.date2dateTimeStr(now, "yyyyMMddHHmmss");
		System.out.println(timeStr);
		
		Date dd = DateTimeUtil.dateTimeStr2date("2016-08-01");
	}
	
}
