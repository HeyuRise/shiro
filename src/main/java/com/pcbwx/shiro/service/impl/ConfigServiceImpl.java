package com.pcbwx.shiro.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.component.LogContext;
import com.pcbwx.shiro.dao.ConfigMapper;
import com.pcbwx.shiro.dao.RecordUtilsMapper;
import com.pcbwx.shiro.dao.TaskClockMapper;
import com.pcbwx.shiro.enums.ConfigEnum;
import com.pcbwx.shiro.enums.TaskClockEnum;
import com.pcbwx.shiro.model.Config;
import com.pcbwx.shiro.model.RecordUtils;
import com.pcbwx.shiro.model.TaskClock;
import com.pcbwx.shiro.service.ConfigService;
import com.pcbwx.shiro.utils.DateCalcUtil;
import com.pcbwx.shiro.utils.DateTimeUtil;

/**
 * 日志接口实现类
 * 
 * @author 王海龙
 *
 */
@Service("configService")
public class ConfigServiceImpl implements ConfigService {

	private static Logger logger = Logger.getLogger(ConfigServiceImpl.class);

	@Autowired
	private ConfigMapper configMapper;
	@Autowired
	private RecordUtilsMapper recordUtilsMapper;
	@Autowired
	private TaskClockMapper taskClockMapper;

	private Hashtable<String, Config> configCache = new Hashtable<String, Config>();

	@PostConstruct
	public void initLoad() {
		reloadConfig();
	}

	@Override
	public void reloadConfig() {
		logger.info("开始载入配置...");
		configCache.clear();
		List<Config> configs = configMapper.load();
		if (configs != null && !configs.isEmpty()) {
			for (Config record : configs) {
				String key = record.getCfgName();
				configCache.put(key, record);
			}
			logger.info("配置缓存条数:" + configCache.size());
		}
	}
	
	@Override
	public Integer readIntConfig(ConfigEnum option) {
//		Config config = configMapper.selectByCfgName(option.getCode());
		Config config = configCache.get(option.getCode());
		if (config != null) {
			return config.getValueInt();
		}	
		return null;
	}	

	@Override
	public String readStrConfig(ConfigEnum option) {
//		Config config = configMapper.selectByCfgName(option.getCode());
		Config config = configCache.get(option.getCode());
		if (config != null) {
			return config.getValueStr();
		}	
		return null;
	}	

	@Override
	public Date readDateConfig(ConfigEnum option) {
//		Config config = configMapper.selectByCfgName(option.getCode());
		Config config = configCache.get(option.getCode());
		if (config != null) {
			return config.getValueTime();
		}	
		return null;
	}	

	@Override
	public RecordUtils getUtilRecord(ConfigEnum config) {
		return recordUtilsMapper.selectByName(config.getCode());
	}

	@Override
	public Integer getUtilInt(ConfigEnum config) {
		RecordUtils record = this.getUtilRecord(config);
		if (record != null && record.getValueInt() != null) {
			return record.getValueInt();
		}
		return null;
	}
	@Override
	public String getUtilStr(ConfigEnum config) {
		RecordUtils record = this.getUtilRecord(config);
		if (record != null && record.getValueStr() != null) {
			return record.getValueStr();
		}
		return null;
	}
	@Override
	public Date getUtilDate(ConfigEnum config) {
		RecordUtils record = this.getUtilRecord(config);
		if (record != null && record.getValueTime() != null) {
			return record.getValueTime();
		}
		return null;
	}
	
	@Override
	public boolean setUtilRecord(ConfigEnum config, String valueStr, Integer valueInt, Date valueTime, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config.getCode());
		record.setDescription(config.getDescr());
		record.setValueInt(valueInt);
		record.setValueStr(valueStr);
		record.setValueTime(valueTime);
		record.setParam(param);
		record.setDescription(config.getDescr());
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setUtilRecord(ConfigEnum config, String value, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config.getCode());
		record.setDescription(config.getDescr());
		record.setValueStr(value);
		record.setParam(param);
		record.setDescription(config.getDescr());
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setUtilRecord(ConfigEnum config, Integer value, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config.getCode());
		record.setDescription(config.getDescr());
		record.setValueInt(value);
		record.setParam(param);
		record.setDescription(config.getDescr());
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setUtilRecord(ConfigEnum config, Date value, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config.getCode());
		record.setDescription(config.getDescr());
		record.setValueTime(value);
		record.setParam(param);
		record.setDescription(config.getDescr());
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}

	@Override
	public RecordUtils getUtilRecord(String config) {
		return recordUtilsMapper.selectByName(config);
	}

	@Override
	public Integer getUtilInt(String config) {
		RecordUtils record = this.getUtilRecord(config);
		if (record != null && record.getValueInt() != null) {
			return record.getValueInt();
		}
		return null;
	}
	@Override
	public String getUtilStr(String config) {
		RecordUtils record = this.getUtilRecord(config);
		if (record != null && record.getValueStr() != null) {
			return record.getValueStr();
		}
		return null;
	}
	@Override
	public Date getUtilDate(String config) {
		RecordUtils record = this.getUtilRecord(config);
		if (record != null && record.getValueTime() != null) {
			return record.getValueTime();
		}
		return null;
	}
	
	@Override
	public boolean setUtilRecord(String config, String desc, String valueStr, Integer valueInt, Date valueTime, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config);
		record.setDescription(desc);
		record.setValueInt(valueInt);
		record.setValueStr(valueStr);
		record.setValueTime(valueTime);
		record.setParam(param);
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean setUtilRecord(String config, String desc, String value, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config);
		record.setDescription(desc);
		record.setValueStr(value);
		record.setParam(param);
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setUtilRecord(String config, String desc, Integer value, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config);
		record.setDescription(desc);
		record.setValueInt(value);
		record.setParam(param);
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setUtilRecord(String config, String desc, Date value, String param) {
		RecordUtils record = new RecordUtils();
		record.setRecordName(config);
		record.setDescription(desc);
		record.setValueTime(value);
		record.setParam(param);
		
		int updated = recordUtilsMapper.updateByName(record);
		if (updated > 0) {
			return true;
		}
		int added = recordUtilsMapper.insertSelective(record);
		if (added > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isTimeOver(Date now, ConfigEnum lastActionTime) {
		RecordUtils exportTime = this.getUtilRecord(lastActionTime);
		Date date = DateTimeUtil.getFirstTimeOfMonth(now);
		String clock = ConfigProperties.getAddressClock();
		Date clockDate = DateTimeUtil.Str2dateTime(clock, "dd HH:mm:ss");
		Date clockDate2 = DateTimeUtil.getFirstTimeOfMonth(clockDate);
		int extSeconds = (int) ((clockDate.getTime() - clockDate2.getTime()) / 1000);
		date = DateCalcUtil.addTime(date, Calendar.SECOND, extSeconds);
		if (now.compareTo(date) >= 0) {
			if (exportTime == null || exportTime.getValueTime() == null
					|| exportTime.getValueTime().before(date)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isTaskTimeOverClock(Date now, ConfigEnum lastActionTime, TaskClockEnum task) {
		RecordUtils exportTime = this.getUtilRecord(lastActionTime);
		
		String taskName = null;
		if (task != null) {
			taskName = task.getCode();
		}
		List<TaskClock> times = taskClockMapper.selectByTask(taskName);
		if (times == null || times.isEmpty()) {
			if (task != null) {	// 如果是单项配置，找不到，采用默认配置
				times = taskClockMapper.selectByTask(TaskClockEnum.DEFAULT.getCode());
			}			
		}
		if (times == null || times.isEmpty()) {
			LogContext.error("导出时间配置", "没有导出数据任务的时间点配置！！！！！");
			return false;
		}
		for (TaskClock clock : times) {
			Date date = DateTimeUtil.truncateDateTime(now);
			Date clockDate = DateTimeUtil.truncateDateTime(clock.getClock());
			int extSeconds = (int) ((clock.getClock().getTime() - clockDate.getTime()) / 1000);
//			logger.info("clock=" + clock.getClock().toString() + ",seconds=" + extSeconds);
			date = DateCalcUtil.addTime(date, Calendar.SECOND, extSeconds);
			if (now.compareTo(date) >= 0) {
				if (exportTime == null || exportTime.getValueTime() == null || exportTime.getValueTime().before(date)) {
					return true;
				}
			}
		}
		return false;
	}
}
