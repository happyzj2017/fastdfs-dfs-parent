package com.netwaymedia.dfs.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netwaymedia.dfs.base.BaseService;
import com.netwaymedia.dfs.base.ErrorCode;
import com.netwaymedia.dfs.base.cache.CacheService;
import com.netwaymedia.dfs.core.entity.AppInfoEntity;
import com.netwaymedia.dfs.core.mapper.AppInfoMapper;
import com.netwaymedia.dfs.utils.DateUtils;
import com.netwaymedia.dfs.utils.MD5Utils;

/**
 * 
 * 应用服务类
 *
 */
@Service
public class AppInfoService extends BaseService {

	@Autowired
	private AppInfoMapper appInfoMapper;

	/**
	 * 将应用信息载入缓存
	 * 
	 * @return
	 */
	public void loadAppInfoToCache() {
		List<AppInfoEntity> lstApps = appInfoMapper.getAllAppInfo();
		if (lstApps != null && !lstApps.isEmpty()) {
			for (AppInfoEntity app : lstApps) {
				CacheService.APP_INFO_CACHE.put(app.getAppKey(), app);
			}
		} else {
			CacheService.APP_INFO_CACHE.clear();
		}
	}

	/**
	 * 获取应用
	 * 
	 * @param appKey
	 * @return
	 */
	public AppInfoEntity getAppInfo(String appKey) {
		if (appKey == null) {
			return null;
		}
		AppInfoEntity app = CacheService.APP_INFO_CACHE.get(appKey);
		if (app == null) {
			app = appInfoMapper.getAppInfoByAppKey(appKey); // 缓存没有，尝试查一下数据库
			if (app != null) {
				CacheService.APP_INFO_CACHE.put(app.getAppKey(), app);
			}
		}
		return app;
	}

	/**
	 * 应用信息校验
	 * 
	 * @param appKey
	 *            应用编码
	 * @param timestamp
	 *            时间戳
	 * @param sign
	 *            MD5(appKey + '$' + appSecret + '$' + 时间戳)
	 * @return ErrorCode
	 */
	public ErrorCode checkAuth(String appKey, String timestamp, String sign) {
		ErrorCode result = ErrorCode.OK;
		AppInfoEntity app = getAppInfo(appKey);

		if (app == null) {
			logger.warn("app info not found! appKey:{}", appKey);
			return ErrorCode.APP_NOT_EXIST;
		}

		if (app.getStatus() == null || app.getStatus().intValue() != AppInfoEntity.APP_STATUS_OK) {
			logger.warn("app stopped ! appKey:{}", appKey);
			return ErrorCode.APP_STOPPED; // 应用已停用
		}

		StringBuilder seq = new StringBuilder(256);
		seq.append(appKey);
		seq.append(SIGN_SPLIT);
		seq.append(app.getAppSecret());
		seq.append(SIGN_SPLIT);
		seq.append(timestamp);
		String md5Sign = MD5Utils.md5(seq.toString());
		if (!sign.equalsIgnoreCase(md5Sign)) {
			logger.warn("sign check error ! appKey:{}, expect:{},but get:{}", appKey, md5Sign, sign);
			return ErrorCode.APP_AUTH_FAILURE; // 签名校验失败
		}

		int timestampCheck = DateUtils.getSecondsToNow(timestamp);
		if (timestampCheck < 0 || timestampCheck > TIMSTAMP_ERROR_SECONDS) {
			logger.warn("timestamp error ! appKey:{}, timstamp:{}, timestampCheck:{}", appKey, timestamp,
					timestampCheck);
			return ErrorCode.TIMESTAMP_ERROR;
		}
		return result;
	}
}
