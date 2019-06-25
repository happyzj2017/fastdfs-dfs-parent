package com.netwaymedia.dfs.app.api;

import com.google.gson.Gson;

public class GsonUtils {
	private static final Gson GSON = new Gson();

	// 将Json数据解析成相应的映射对象
	public static <T> T parseObject(String jsonData, Class<T> type) {
		T result = GSON.fromJson(jsonData, type);
		return result;
	}
}
