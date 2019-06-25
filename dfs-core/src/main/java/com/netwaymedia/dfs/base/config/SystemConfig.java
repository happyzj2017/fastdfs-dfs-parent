package com.netwaymedia.dfs.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
// @ConfigurationProperties(value = "dfs.thread.num")
public class SystemConfig {
	/**
	 * 上传线程数
	 */
	@Value("${dfs.thread.num.upload:3}")
	private int upload;
	
	/**
	 * 下载线程数
	 */
	@Value("${dfs.thread.num.download:3}")
	private int download;

	/**
	 * tracker服务器列表
	 */
	@Value("${fastdfs.tracker_servers}")
	private String trackers;

	/**
	 * 连接超时
	 */
	@Value("${fastdfs.connect_timeout_in_seconds:5}")
	private String connect_timeout_in_seconds;

	/**
	 * 网络超时
	 */
	@Value("${fastdfs.network_timeout_in_seconds:30}")
	private String network_timeout_in_seconds;

	/**
	 * 网络超时
	 */
	@Value("${fastdfs.charset:utf-8}")
	private String charset;

	public int getUpload() {
		return upload;
	}

	public void setUpload(int upload) {
		this.upload = upload;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	public String getTrackers() {
		return trackers;
	}

	public void setTrackers(String trackers) {
		this.trackers = trackers;
	}

	public String getConnect_timeout_in_seconds() {
		return connect_timeout_in_seconds;
	}

	public void setConnect_timeout_in_seconds(String connect_timeout_in_seconds) {
		this.connect_timeout_in_seconds = connect_timeout_in_seconds;
	}

	public String getNetwork_timeout_in_seconds() {
		return network_timeout_in_seconds;
	}

	public void setNetwork_timeout_in_seconds(String network_timeout_in_seconds) {
		this.network_timeout_in_seconds = network_timeout_in_seconds;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
