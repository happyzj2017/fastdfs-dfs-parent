package com.netwaymedia.dfs.interceptor;

import com.netwaymedia.dfs.base.BaseConntroller;
import com.netwaymedia.dfs.base.ErrorCode;
import com.netwaymedia.dfs.base.spring.SpringContextUtils;
import com.netwaymedia.dfs.core.service.AppInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * auth包下Controller拦截器
 *
 */
public class AuthControllerInterceptor extends BaseConntroller implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthControllerInterceptor.class);

	private AppInfoService appInfoService = SpringContextUtils.getBean(AppInfoService.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("preHandle");
		}
		String appKey = request.getHeader(HEADER_APP_KEY);
		String timestamp = request.getHeader(HEADER_TIMESTAMP);
		String sign = request.getHeader(HEADER_SIGN);
		response.setContentType(UTF8_JSON);
		if (StringUtils.isEmpty(appKey) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(sign)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getOutputStream().write(getResponseByCode(ErrorCode.AUTH_PARAM_ERROR).getBytes(UTF8));
			response.getOutputStream().flush();
			return false;
		}
		ErrorCode eCode = appInfoService.checkAuth(appKey, timestamp, sign);
		if (eCode != ErrorCode.OK) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getOutputStream().write(getResponseByCode(eCode).getBytes(UTF8));
			response.getOutputStream().flush();
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("postHandle");
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
