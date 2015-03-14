package com.github.tinyretry.timer.domain;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tinyretry.timer.event.McJobNotifySupport;
import com.github.tinyretry.timer.event.ReportAble;
import com.github.tinyretry.timer.ext.McJobDefinition;
import com.github.tinyretry.timer.ext.McJobListener;

/**
 * <pre>
 * desc: 定时任务基类，实现自{@code org.quartz.Job}
 * created: 2012-5-7 下午02:06:57
 * author: xiaofeng.zhouxf
 * todo: 对定时任务的抽象，描述执行的内容
 * history:
 * </pre>
 * 
 * @see org.quartz.Job
 * @see McJobNotifySupport
 */
public abstract class McJob extends McJobNotifySupport implements ReportAble, Job {
	private static final Logger log = LoggerFactory.getLogger(McJob.class);
	protected McJobDefinition definition;
	private boolean success = true;
	private long registerTime;
	private long startTime;
	private long finlshedTime;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			beforeExec();
			success = execute();
			afterExec();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			doException();
			throw new JobExecutionException("Run job exception," + e.getMessage());
		}
	}

	public long getElapsedTime() {
		return finlshedTime - registerTime;
	}

	public long getActualRunTime() {
		return finlshedTime - startTime;
	}

	public float successRate() {
		return 1;
	}

	public abstract boolean execute() throws Exception;

	public void doException() {
	}

	public void beforeExec() throws Exception {
		startTime = System.currentTimeMillis();
		if (registerTime == 0) {
			registerTime = startTime;
		}
	}

	public void afterExec() throws Exception {
		finlshedTime = System.currentTimeMillis();
	}

	public McJobDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(McJobDefinition definition) {
		this.definition = definition;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getFinlshedTime() {
		return finlshedTime;
	}

	public void setFinlshedTime(long finlshedTime) {
		this.finlshedTime = finlshedTime;
	}

	public long getRegisterTime() {
		return registerTime;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean validate() {
		if (this.getDefinition() == null || StringUtils.isBlank(getDefinition().getGroupId())
				|| StringUtils.isBlank(getDefinition().getName())) {
			return false;
		}

		if (getDefinition().getJobListeners() != null) {
			for (McJobListener listener : getDefinition().getJobListeners()) {
				if (listener != null && StringUtils.isBlank(listener.getName())) {
					return false;
				}
			}
		}

		return true;
	}

}
