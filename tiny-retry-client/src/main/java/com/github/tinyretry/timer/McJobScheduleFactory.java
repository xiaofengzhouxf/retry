package com.github.tinyretry.timer;

import org.quartz.spi.JobStore;

import com.github.tinyretry.timer.domain.McJobRepository;
import com.github.tinyretry.timer.ext.McJobStore;
import com.github.tinyretry.timer.ext.jobfactory.McJobFactory;

/**
 * <pre>
 * desc: 
 * 创建任务调度器.
 * 目前调度器支持定时、定点调度，支持自定义线程数，
 * 支持自定义job存储，只需要实现McJobStore即可，但建议使用默认内存保存job，
 * 因为实现比较复杂，本框架适合各应用单独部署少量定时器，持久化job没有任何优势。
 * 
 * 注意：
 *    1. 创建时scheduleName,schedulerInstanceId在一个jvm中不能重复，否则会报错。
 *    2. 失败重试在模型中不支持，需要job中业务自己来完成。
 * created: 2012-7-16 上午09:12:26
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class McJobScheduleFactory {

	public static McJobSchedule createMcJobSchedule(int maxThreads, String scheduleName, String schedulerInstanceId)
			throws McJobScheduleException {
		return createMcJobSchedule(null, maxThreads, scheduleName, schedulerInstanceId, null, null);
	}

	public static McJobSchedule createMcJobSchedule(int maxThreads) throws McJobScheduleException {
		return createMcJobSchedule(null, maxThreads, null, null, null, null);
	}

	public static McJobSchedule createMcJobSchedule(McJobRepository repository, int maxThreads,
			String scheduleName, String scheduleNameId) throws McJobScheduleException {
		return createMcJobSchedule(repository, maxThreads, scheduleName, scheduleNameId, null, null);
	}

	public static McJobSchedule createMcJobSchedule(int maxThreads, String scheduleName, String scheduleNameId,
			McJobStore jobStore) throws McJobScheduleException {
		return createMcJobSchedule(null, maxThreads, scheduleName, scheduleNameId, jobStore, null);
	}

	public static McJobSchedule createMcJobSchedule(int maxThreads, String scheduleName, String scheduleNameId,
			JobStore jobStore, McJobFactory jobFactory) throws McJobScheduleException {
		return createMcJobSchedule(null, maxThreads, scheduleName, scheduleNameId, jobStore, jobFactory);
	}

	public static McJobSchedule createMcJobSchedule(int maxThreads, McJobFactory jobFactory)
			throws McJobScheduleException {
		return createMcJobSchedule(maxThreads, null, null, null, jobFactory);
	}

	public static McJobSchedule createMcJobSchedule(McJobRepository repository, int maxThreads, String scheduleName,
			String scheduleNameId, JobStore jobStore, McJobFactory jobFactory) throws McJobScheduleException {
		return new McJobSchedule(repository, maxThreads, scheduleName, scheduleNameId, jobStore, jobFactory).init();
	}
}
