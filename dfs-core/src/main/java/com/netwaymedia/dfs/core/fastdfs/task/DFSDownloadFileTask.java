package com.netwaymedia.dfs.core.fastdfs.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

/**
 * 下载任务，异步执行
 *
 */
public class DFSDownloadFileTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(DFSDownloadFileTask.class);

	private String fileId;
	private OutputStream out;
	private boolean isClose;

	public DFSDownloadFileTask(String fileId, OutputStream out, boolean isClose) {
		this.fileId = fileId;
		this.out = out;
		this.isClose = isClose;
	}

	@Override
	public void run() {
	}

}
