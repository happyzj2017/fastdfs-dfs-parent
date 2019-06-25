package com.netwaymedia.dfs.base.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * 用来手动获取spring bean
 * 
 *
 */
@Service
public class SpringContextUtils implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	/**
	 * 通过class获取Bean.
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	/**
	 * 通过beanId获bean
	 * 
	 * @param beanId
	 * @return
	 */
	public static Object getBean(String beanId) {
		return applicationContext.getBean(beanId);
	}

}