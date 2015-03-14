package com.github.tinyretry.timer.ext.jobfactory;

import org.apache.commons.lang.ClassUtils;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tinyretry.timer.domain.McJob;
import com.github.tinyretry.timer.domain.McJobRepository;

/**
 * <pre>
 * desc: 任务创建工厂，实现自{@code org.quartz.spi.JobFactory}
 * created: 2012-7-16 上午09:24:35
 * author: xiaofeng.zhouxf
 * todo: 用于创建任务，在任务到达运行时间点时，将促发本类的createJob方法，子类只需要实现createJob即可
 * history:
 * </pre>
 */
public abstract class McJobFactory implements JobFactory {
	private static final Logger logger = LoggerFactory.getLogger(McJobFactory.class);

	/**
	 * 任务资源库
	 */
	protected McJobRepository repository;

	public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {

		JobDetail jobDetail = bundle.getJobDetail();
		if (jobDetail != null && ClassUtils.isAssignable(jobDetail.getJobClass(), McJob.class)) {
			return createJob(bundle);
		}
		try {
			return (jobDetail != null && jobDetail.getJobClass() != null ? (Job) jobDetail.getJobClass().newInstance()
					: null);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public abstract McJob createJob(TriggerFiredBundle bundle);

	public McJobRepository getRepository() {
		return repository;
	}

	public void setRepository(McJobRepository repository) {
		this.repository = repository;
	}

}
