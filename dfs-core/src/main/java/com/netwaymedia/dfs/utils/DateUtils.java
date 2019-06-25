package com.netwaymedia.dfs.utils;

public class DateUtils {

	public static final int MILLIS_IN_SECOND = 1000; // 秒
	public static final int MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60; // 分

	private DateUtils() {
		
	}
	/**
	 * 获取指时间戳到与当前时间间隔秒数
	 * 
	 * @param fromMillis
	 * @return
	 */
	public static int getSecondsToNow(long fromMillis) {
		long curMillis = System.currentTimeMillis();
		return (int) (Math.abs(curMillis - fromMillis) / MILLIS_IN_SECOND);
	}

	public static int getSecondsToNow(String strfromMillis) {
		long fromMillis = -1;
		try {
			fromMillis = Long.valueOf(strfromMillis);
		} catch (Exception e) {
		}
		if (fromMillis < 0) {
			return -1;
		} else {
			return getSecondsToNow(fromMillis);
		}
	}
}
