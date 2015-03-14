package com.github.tinyretry.timer.ext;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tinyretry.timer.domain.McJob;

/**
 * <pre>
 * desc: 监听器，在job执行前后执行一些操作，与job实例关联
 * created: 2012-7-16 上午09:53:01
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public abstract class McJobListener implements JobListener {
	private static final Logger logger = LoggerFactory.getLogger(McJobListener.class);
	/**
	 * 监听器名称
	 */
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		if (context == null) {
			logger.warn("jobToBeExecuted Listener [{0}]'s context is empty", this.getName());
			return;
		}
		before(buildMcJobDefinition(context));
	}

	public void jobExecutionVetoed(JobExecutionContext context) {

	}

	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		if (context == null) {
			logger.warn("jobWasExecuted Listener [{0}]'s context is empty", this.getName());
			return;
		}
		after(buildMcJobDefinition(context));
	}

	private McJobDefinition buildMcJobDefinition(JobExecutionContext context) {
		McJobDefinition definition = new McJobDefinition();
		Job job = context.getJobInstance();

		if (job != null && job instanceof McJob) {
			definition = ((McJob) job).getDefinition();
		} else {
			JobDetail jobDetail = context.getJobDetail();
			if (jobDetail != null) {
				definition.setDescription(jobDetail.getDescription());
				definition.setGroupId(jobDetail.getGroup());
				definition.setJobClass(jobDetail.getJobClass());
				definition.setJobDataMap(jobDetail.getJobDataMap());
				definition.setName(name);
			}
		}
		return definition;
	}

	/**
	 * 在定时任务每一次执行前执行
	 * 
	 * @param definition
	 */
	public abstract void before(McJobDefinition definition);

	/**
	 * 在定时任务每一次执行后执行
	 * 
	 * @param definition
	 */
	public abstract void after(McJobDefinition definition);

}
