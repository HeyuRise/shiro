package com.pcbwx.shiro.quartz;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.enums.ConfigEnum;
import com.pcbwx.shiro.enums.TaskClockEnum;
import com.pcbwx.shiro.service.ConfigService;
import com.pcbwx.shiro.service.SupportService;
import com.pcbwx.shiro.utils.DateTimeUtil;

/**
 * QuartzJob Demo类
 * @author 王海龙
 * @version 1.0
 *
 */
public class QuartzJob {

	private static final Logger logger = LogManager.getLogger(QuartzJob.class);

	@Autowired
	private SupportService supportService;
	@Autowired
	private ConfigService configService;

	@Autowired
	private ImportTask importTask;

	private static AtomicInteger reloadFlag = new AtomicInteger();	
	public void reloadCache() {
		logger.info("reloadCache的任务调度！！！");
		Date now = new Date();
		
		if (reloadFlag.incrementAndGet() > 1) {
			reloadFlag.decrementAndGet();
			return;
		}
		// -------------------重载内存--------------------------------
		try {
			supportService.doReloadCache();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
		// -------------------重载行政区------------------------------
		boolean isAddressFlash = configService.isTimeOver(now, ConfigEnum.LAST_ADRESS_REFLASH_TIME);
		if (isAddressFlash) {
			Integer count = configService.getUtilInt(ConfigEnum.LAST_ADRESS_REFLASH_TIME);
			boolean isCount = false;
			if (count == null) {
				count = 1;
			}else {
				if (count%6 == 0) {
					isCount = true;
				}
				count ++;
			}
			if (isCount) {
				LogContext.info("重载", "重载行政区");
				supportService.reloadInternetAddress();
				configService.setUtilRecord(ConfigEnum.LAST_ADRESS_REFLASH_TIME, DateTimeUtil.date2dateStr(now), count,now, null);
			}else {
				configService.setUtilRecord(ConfigEnum.LAST_ADRESS_REFLASH_TIME, null, count, now, null);
			}
		}
		
		reloadFlag.decrementAndGet();		
		logger.info("reloadCache的任务调度结束！！！");
	}

	private static AtomicInteger dataSyncFlag = new AtomicInteger();	
	public void dataSync() {
		logger.info("dataSync的任务调度！！！");
		if (dataSyncFlag.incrementAndGet() > 1) {
			dataSyncFlag.decrementAndGet();
			return;
		}
		Date now = new Date();
		// ------------同步联系人--------------------
		
		Boolean isSuccess = importTask.contactSyn();
		if (!isSuccess) {
			LogContext.error("同步失败", "同步联系人失败");
		}
		
		// ------------同步运单号--------------------
		try {
			boolean isBegin = configService.isTaskTimeOverClock(now, ConfigEnum.LAST_MAILNO_SYN_TIME, TaskClockEnum.MAILNO_SYNCHRONIZE);
			if (isBegin) {
				LogContext.info("同步运单号", "运单号开始同步");
				boolean success = importTask.mailnoSyn();
				if (success) {
					configService.setUtilRecord(ConfigEnum.LAST_MAILNO_SYN_TIME, now, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogContext.error("同步失败", "运单号同步失败");
		}
		
		dataSyncFlag.decrementAndGet();		
		logger.info("dataSync的任务调度结束！！！");
	}
	
//	private static AtomicInteger mailInfoResponseFlag = new AtomicInteger();
//	public void mailInfoResponse(){
//		logger.info("运单信息回调的任务调度！！！");
//		if (mailInfoResponseFlag.incrementAndGet() > 1) {
//			mailInfoResponseFlag.decrementAndGet();
//			return;
//		}
//
//		mailInfoResponseFlag.decrementAndGet();		
//		logger.info("运单信息回调的任务调度结束！！！");
//	}
//	
}
