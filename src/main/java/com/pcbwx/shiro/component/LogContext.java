/**
 * 
 */
package com.pcbwx.shiro.component;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pcbwx.shiro.enums.LogTypeEnum;
import com.pcbwx.shiro.model.Log;
import com.pcbwx.shiro.service.LogService;
import com.pcbwx.shiro.utils.DateTimeUtil;

/**
 * 日志环境类
 * 
 * @author 王海龙
 *
 */
public class LogContext {
	private static final Logger logger = LogManager.getLogger(LogContext.class);
	
	private static ThreadLocal<Log> local = new ThreadLocal<Log>();

	private static LogService logService;

	public static LogService getLogService() {
		return logService;
	}

	public static void setLogService(LogService logService) {
		LogContext.logService = logService;
	}

	public void init() {

	}

	/**
	 * 将日志信息放入LogContext中
	 * 
	 * @param contentCN
	 *            中文日志
	 * @param contentEN
	 *            英文日志
	 * @param logTypeEnum
	 *            日志类型
	 */
	public static int info(String title, String content) {
		return LogContext.addLog(title, content, LogTypeEnum.LOG_NORMAL);
	}

	public static int system(String title, String content) {
		return LogContext.addLog(title, content, LogTypeEnum.LOG_SYSTEM);
	}

	public static int error(String title, String content) {
		return LogContext.addLog(title, content, LogTypeEnum.LOG_ERROR);
	}

	public static int operate(String title, String content) {
		return LogContext.addLog(title, content, LogTypeEnum.LOG_OPERATE);
	}

	public static int warn(String title, String content) {
		return LogContext.addLog(title, content, LogTypeEnum.LOG_WARN);
	}

	private static int addLog(String title, String content, LogTypeEnum type) {
		if (StringUtils.isBlank(content)) {
			return -1;
		}
		Log log = new Log();

		StackTraceElement stack = new Exception().getStackTrace()[2];
		log.setFileName(stack.getFileName()); // 文件名
		log.setClassName(stack.getClassName()); // 类名
		log.setMethodName(stack.getMethodName()); // 函数名
		log.setLineNumber(stack.getLineNumber()); // 行号

		log.setLevel(type.getCode());
		log.setTitle(title);
		if (content.length() > 1000) {
			log.setContent(content.substring(0, 1000));
		} else {
			log.setContent(content);
		}
		log.setRecordTime(new Date());

		logService.addLog(log);

		StringBuilder msg = new StringBuilder();
		msg.append(DateTimeUtil.date2dateTimeStr(new Date())).append(" [").append(log.getFileName()).append("] [")
				.append(log.getLineNumber()).append("] ").append(type.getCode()).append(" - ")
				.append(log.getTitle()).append(" >> ").append(log.getContent());

		System.out.println(msg.toString());

		return 0;
	}

	/**
	 * 清除日志信息
	 */
	public static void clear() {
		local.remove();
	}

	/**
	 * 获取日志
	 * 
	 * @return
	 */
	public static Log get() {
		return local.get();
	}
}
