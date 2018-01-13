package com.pcbwx.shiro.annotation;

import java.net.URI;
import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import com.pcbwx.shiro.common.ConfigProperties;
import com.pcbwx.shiro.enums.ConfigEnum;

@Plugin(name = "CustomConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class CustomConfigurationFactory extends ConfigurationFactory{

	static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(Level.INFO);
        builder.add(getConsoleBuilder(builder));
        builder.add(getFileBuilder(builder));
        builder.add(getFileBuilder1(builder));
        builder.add(getAsyncBuilder(builder));
        if (!Objects.equals(ConfigProperties.getProperty(ConfigEnum.IS_ONLINE), "1")) {
        	addDevLogger(builder);
            builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("Console")));
		}else {
			builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("ASYNC")));
		}
        return builder.build();
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }
    
    // 获取显示台适配器
	public static AppenderComponentBuilder getConsoleBuilder(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder.newAppender(
				"Console", "Console").addAttribute("target",
				ConsoleAppender.Target.SYSTEM_OUT);
		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute(
				"pattern",
				"%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} [%L] - %msg%n"));
		return appenderBuilder;
	}

	// 获取文件日志适配器
	public static AppenderComponentBuilder getFileBuilder(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder
				.newAppender("myInfo", "RollingFile")
				.addAttribute("fileName", "/opt/tbwechat/logs/shiro/info/info.log")
				.addAttribute("filePattern", "/opt/tbwechet/logs/shiro/info/info-%d{yyyy-MM-dd}.log");
		appenderBuilder.add(builder.newLayout("ThresholdFilter")
				.addAttribute("level", "INFO")
				.addAttribute("onMatch", "ACCEPT")
				.addAttribute("onMismatch", "DENY"));
		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute(
				"pattern",
				"%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"));
		appenderBuilder.add(builder.newLayout("Policies").addComponent(
				builder.newLayout("TimeBasedTriggeringPolicy")
						.addAttribute("modulate", true)
						.addAttribute("interval", "1")));
		return appenderBuilder;
	}

	// 获取文件日志适配器
	public static AppenderComponentBuilder getFileBuilder1(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder
				.newAppender("myError", "RollingFile")
				.addAttribute("fileName",
						"/opt/tbwechat/logs/shiro/error/error.log")
				.addAttribute("filePattern",
						"/opt/tbwechet/logs/shiro/error/error-%d{yyyy-MM-dd}.log");
		appenderBuilder.add(builder.newLayout("ThresholdFilter")
				.addAttribute("level", "ERROR")
				.addAttribute("onMatch", "ACCEPT")
				.addAttribute("onMismatch", "DENY"));
		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute(
				"pattern",
				"%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"));
		appenderBuilder.add(builder.newLayout("Policies").addComponent(
				builder.newLayout("TimeBasedTriggeringPolicy")
						.addAttribute("modulate", true)
						.addAttribute("interval", "1")));
		return appenderBuilder;
	}

	// 获取异步写日志适配器
	public static AppenderComponentBuilder getAsyncBuilder(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder
				.newAppender("ASYNC", "Async");
		if (Objects.equals(ConfigProperties.getProperty(ConfigEnum.IS_ONLINE), "1")) {
			appenderBuilder.add(builder.newLayout("AppenderRef")
					.addAttribute("ref", "myInfo"));
			appenderBuilder.add(builder.newLayout("AppenderRef")
					.addAttribute("ref", "myError"));
		} else { 
			appenderBuilder.add(builder.newLayout("AppenderRef")
				.addAttribute("ref", "Console"));
		}
		return appenderBuilder;
	}
	
	// 添加开发环境的支持
	public static void addDevLogger(ConfigurationBuilder<BuiltConfiguration> builder){
		// 添加mybatis支持
		LoggerComponentBuilder loggerMyBatisBuilder = builder
				.newLogger("com.pcbwx.shiro.dao", "debug")
				.addAttribute("additivity", false)
				.add(builder.newAppenderRef("Console"));
		builder.add(loggerMyBatisBuilder);
		
		LoggerComponentBuilder loggerSpringBuilder = builder
				.newLogger("org.springframework", "INFO")
				.addAttribute("additivity", false)
				.add(builder.newAppenderRef("Console"));
		builder.add(loggerSpringBuilder);
	}
}
