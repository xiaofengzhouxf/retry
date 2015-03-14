package com.github.tinyretry.retry.scanner;

import com.github.tinyretry.retry.AsyncProcessor;
import com.github.tinyretry.retry.RetryServer;
import com.github.tinyretry.retry.service.TaskManage;
import com.github.tinyretry.timer.domain.McJob;

/**
 * <pre>
 * desc: 
 * created: 2012-9-11 上午11:00:19
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public abstract class RetryScanner extends McJob {
	/**
	 * 对应的业务码
	 */
	protected String appCode;

	/**
	 * 本scanner重复执行时间
	 */
	protected long repeatInterval;

	protected RetryServer server;

	protected AsyncProcessor asyncProcessor;

	protected TaskManage taskManage;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public RetryServer getServer() {
		return server;
	}

	public void setServer(RetryServer server) {
		this.server = server;
	}

	public AsyncProcessor getAsyncProcessor() {
		return asyncProcessor;
	}

	public void setAsyncProcessor(AsyncProcessor asyncProcessor) {
		this.asyncProcessor = asyncProcessor;
	}

	public TaskManage getTaskManage() {
		return taskManage;
	}

	public void setTaskManage(TaskManage taskManage) {
		this.taskManage = taskManage;
	}
}
