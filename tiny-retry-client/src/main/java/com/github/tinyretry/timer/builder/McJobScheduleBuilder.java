package com.github.tinyretry.timer.builder;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tinyretry.timer.McJobSchedule;
import com.github.tinyretry.timer.McJobScheduleException;
import com.github.tinyretry.timer.domain.McJob;
import com.github.tinyretry.timer.domain.McJobGroup;

/**
 * <pre>
 * desc: 独立的定时任务构建方式
 * created: 2012-9-20 下午01:21:56
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public abstract class McJobScheduleBuilder {
	private static final Logger logger = LoggerFactory.getLogger(McJobScheduleBuilder.class);

	/**
	 * 任务调度器
	 */
	protected McJobSchedule schedule;

	/**
	 * 任务实例在调度器中名称的后缀
	 */
	protected final String INSTANT_SUFFIX = "_INSTANT";

	/**
	 * 创建调度容器
	 * 
	 * @throws McJobScheduleException
	 * 
	 */
	public abstract McJobScheduleBuilder createMcJobSchedule(String scheduleName, int maxThreads)
			throws McJobScheduleException;

	/**
	 * 运行job
	 * 
	 * @param job
	 * @return
	 */
	public McJobScheduleBuilder executeJob(McJob job) {
		if (job != null && schedule != null) {
			try {
				schedule.init();
				schedule.registerJob(job);
				schedule.start();
			} catch (McJobScheduleException e) {
				logger.error("ExecuteJob exception.", e);
			}
		}
		return this;
	}

	public McJobScheduleBuilder addGroups(McJobGroup... groups) {
		if (groups != null && schedule != null) {
			schedule.setGroups(Arrays.asList(groups));
		}
		return this;
	}

	public McJobSchedule getSchedule() {
		return schedule;
	}

}
