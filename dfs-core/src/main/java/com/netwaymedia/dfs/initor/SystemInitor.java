package com.netwaymedia.dfs.initor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.netwaymedia.dfs.core.service.AppInfoService;

/**
 * 初始化
 *
 */
@Service
public class SystemInitor implements ApplicationListener<ContextRefreshedEvent> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AppInfoService appInfoService;

	/**
	 * spring初始化完成可以做一些系统相关初始化
	 * 
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		appInfoService.loadAppInfoToCache();
		if (logger.isDebugEnabled()) {
			logger.debug("all appInfo loaded to cache !");
		}
	}

}
