package com.netwaymedia.dfs.base.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netwaymedia.dfs.core.entity.AppInfoEntity;

public class CacheService {

	/**
	 * 缓存应用信息，做定时刷新，添加删除时注间实现更新缓存
	 */
	public final static Map<String, AppInfoEntity> APP_INFO_CACHE = new ConcurrentHashMap<String, AppInfoEntity>();
}
