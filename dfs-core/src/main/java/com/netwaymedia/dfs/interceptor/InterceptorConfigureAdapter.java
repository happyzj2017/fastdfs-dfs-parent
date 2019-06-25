package com.netwaymedia.dfs.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 添加自定义拦截器
 */
@Configuration
public class InterceptorConfigureAdapter extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
		// 所有/dfs/auth下的请求都会被拦截
//		registry.addInterceptor(new AuthControllerInterceptor()).addPathPatterns("/dfs/auth/**");
		//先不做鉴权操作
	}

}
