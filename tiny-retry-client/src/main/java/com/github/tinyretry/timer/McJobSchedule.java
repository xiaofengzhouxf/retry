package com.github.tinyretry.timer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobFactory;
import org.quartz.spi.JobStore;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tinyretry.timer.domain.McJob;
import com.github.tinyretry.timer.domain.McJobGroup;
import com.github.tinyretry.timer.domain.McJobRepository;
import com.github.tinyretry.timer.ext.McJobDefinition;
import com.github.tinyretry.timer.ext.McJobListener;
import com.github.tinyretry.timer.ext.McJobStore;
import com.github.tinyretry.timer.ext.jobfactory.McJobFactory;

/**
 * <pre>
 * desc: 任务调度器,扩展quartz,尽量单例的方式使用本类
 *   
 * 使用方法：
 * <ul><li>创建McJobGroup,定义cornExpression或者repeatInterval，定义groupId
 * <code>
 *      McJobGroup perTowSec = new McJobGroup();
 * 		towPerSecond.setGroupId("perTwoSec");
 * 		towPerSecond.setRepeatInterval(2000);
 * </code>
 * </li>
 * <li>继承<code>McJob</code>创建job，实现<code>execute()</code>方法
 * <code>
 * class TestJob extends McJob implements McJobNotifyListener {
 * 		public boolean onFile(McJobNotify notify) {
 * 			.....
 * 		}
 * 		public void execute() throws Exception {
 * 			.....
 *          //发送消息给同组group或者指定group的job们
 * 			McJobNotify notify = new McJobNotify(TestJob2.class.getName());
 * 			notify.setFromJobName(this.getName());
 * 			notify.setGroupId(this.getGroupId());
 * 			this.sendNotify(notify);
 * 		}
 * 	}
 * 	</code>
 * </li>
 * <li>创建<code>McJobSchedule</code>类js, <code>McJobSchedule js = new McJobSchedule()</code>   </li>
 *    -调用<code>js.setGroups(perTowSec)</code>方法把第1步骤中创建的McJobGroup添加进去-
 *    -创建job定义<code> McJobDefinition definition = new McJobDefinition();
 *  	//这里注意别有重复的名称同时注册，因为容器中以名称来定位
 *  	definition.setName("TestJob");
 *  	definition.setGroupId("testGroup");
 * 		TestJob testJob= new TestJob()；
 *      testJob.setDefinition(definition);
 *    -调用<code>js.registerJob(testJob)</code>方法把第2步中创建的job注册进去
 *    
 * <li>调用<code>js.init()</code>初始化调度器</li>
 * <li>调用<code>js.start()</code>启动job</li></ul>
 * 
 * 可以调用<code>js.stop()</code>来暂停所有的job
 * 
 * 这边notify消息的作用，可以理解为指令，举例：凌晨1点运行job1，把分析结果提交给1点10分的任务，
 * 这样1点10分跑的任务只需要监听某中事件来执行即可。
 * 
 * created: 2012-5-7 下午02:20:37
 * author: xiaofeng.zhouxf
 * history:
 * </pre>
 */
public class McJobSchedule {
	private static final Logger logger = LoggerFactory.getLogger(McJobSchedule.class);

	private McJobRepository repository;
	private Scheduler scheduler;
	private int maxThreads = 10;
	private String scheduleName = "MC_JOB_SCHEDULE";
	private String scheduleNameId = "MC_JOB_SCHEDULE_INSTANCT";
	private JobStore jobStore;
	private McJobFactory jobFactory;
	private volatile boolean isInited = false;
	private Object lockObj = new Object();

	public McJobSchedule() {
		super();
	}

	public McJobSchedule(int maxThreads) {
		super();
		if (maxThreads > 0) {
			this.maxThreads = maxThreads;
		}
	}

	public McJobSchedule(int maxThreads, String scheduleName, String scheduleNameId) {
		this(null, maxThreads, scheduleName, scheduleNameId, null, null);
	}

	public McJobSchedule(McJobRepository repository, int maxThreads, String scheduleName, String scheduleNameId) {
		this(repository, maxThreads, scheduleName, scheduleNameId, null, null);
	}

	public McJobSchedule(int maxThreads, String scheduleName, String scheduleNameId,
			McJobStore jobStore) {
		this(null, maxThreads, scheduleName, scheduleNameId, jobStore, null);
	}

	public McJobSchedule(McJobRepository repository, int maxThreads, String scheduleName,
			String scheduleNameId, JobStore jobStore) {
		this(repository, maxThreads, scheduleName, scheduleNameId, jobStore, null);
	}

	public McJobSchedule(int maxThreads, String scheduleName, String scheduleNameId, McJobFactory jobFactory) {
		this(null, maxThreads, scheduleName, scheduleNameId, null, jobFactory);
	}

	public McJobSchedule(int maxThreads, String scheduleName, String scheduleNameId, JobStore jobStore,
			McJobFactory jobFactory) {
		this(null, maxThreads, scheduleName, scheduleNameId, jobStore, jobFactory);
	}

	public McJobSchedule(McJobRepository repository, int maxThreads, String scheduleName, String scheduleNameId,
			JobStore jobStore, McJobFactory jobFactory) {
		super();
		if (maxThreads > 0) {
			this.maxThreads = maxThreads;
		}
		if (StringUtils.isNotBlank(scheduleName)) {
			this.scheduleName = scheduleName;
		}
		if (StringUtils.isNotBlank(scheduleNameId)) {
			this.scheduleNameId = scheduleNameId;
		}
		this.repository = repository;
		this.jobStore = jobStore;
		this.jobFactory = jobFactory;
	}

	// ====================================
	public McJobSchedule init() throws McJobScheduleException {
		if (isInited) {
			return this;
		}
		if (repository == null) {
			repository = DefaultMcJobRepository.getInstance();
		}
		synchronized (lockObj) {

			if (isInited) {
				return this;
			}

			SimpleThreadPool threadPool = new SimpleThreadPool(maxThreads, Thread.NORM_PRIORITY);
			try {
				threadPool.initialize();
				if (jobStore == null) {
					jobStore = new RAMJobStore();
				}
				DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();
				scheduler = factory.getScheduler(scheduleName);
				if (scheduler == null) {
					factory.createScheduler(scheduleName, scheduleNameId, threadPool, jobStore);
					scheduler = factory.getScheduler(scheduleName);
				}
				if (jobFactory == null) {
					scheduler.setJobFactory(new JobFactory() {
						public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {
							// job名称
							String name = bundle.getJobDetail().getName();
							if (StringUtils.isNotBlank(name)) {
								// 从资源库中找job
								McJob job = repository.getJob(name);

								// 执行的最后就把job给删了，清除空间
								if (bundle.getNextFireTime() == null) {
									McJobGroup groups = repository.getGroups(job.getDefinition().getGroupId());
									if (groups.getRepeatCount() == 0) {
										repository.removeJob(job.getDefinition().getGroupId(), job);
									}
								}
								return job;
							}
							return null;
						}
					});
				} else {
					jobFactory.setRepository(repository);
					scheduler.setJobFactory(jobFactory);
				}

			} catch (SchedulerConfigException e) {
				logger.error(e.getMessage(), e);
				throw new McJobScheduleException("Init jobSchedule failed.", e);
			} catch (SchedulerException e) {
				logger.error(e.getMessage(), e);
				throw new McJobScheduleException("Init jobSchedule failed.", e);
			}
			isInited = true;
		}
		return this;
	}

	/**
	 * 启动异步任务,允许调用多次
	 * 
	 * @throws McJobScheduleException
	 *             暂停异常
	 */
	public void start() throws McJobScheduleException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		// 启动调度器
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			throw new McJobScheduleException("schudule run exception", e);
		}
	}

	/**
	 * 暂停某个job
	 * 
	 * @param job
	 * @throws McJobScheduleException
	 *             暂停异常
	 * @throws IllegalArgumentException
	 *             当参数不正确时抛出
	 */
	public void pauseJob(McJob job) throws McJobScheduleException, IllegalArgumentException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		if (job == null || StringUtils.isBlank(job.getDefinition().getName())
				|| StringUtils.isBlank(job.getDefinition().getGroupId())) {
			throw new IllegalArgumentException("Job's groupId and name,or jobListenr's name  can't be emtpy.");
		}
		try {
			scheduler.pauseJob(job.getDefinition().getName(), job.getDefinition().getGroupId());
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			throw new McJobScheduleException("schudule run exception", e);
		}
	}

	/**
	 * 暂停某个group
	 * 
	 * @param group
	 * @throws McJobScheduleException
	 *             暂停异常
	 * @throws IllegalArgumentException
	 *             当参数不正确时抛出
	 */
	public void pauseJob(String gourpId) throws McJobScheduleException, IllegalArgumentException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		if (StringUtils.isBlank(gourpId)) {
			throw new IllegalArgumentException("Job's groupId and name,or jobListenr's name  can't be emtpy.");
		}
		try {
			scheduler.pauseJobGroup(gourpId);
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			throw new McJobScheduleException("schudule pause exception", e);
		}
	}

	/**
	 * 停止异步任务服务
	 * 
	 * @throws CloudStoreException
	 */
	public void stop() throws McJobScheduleException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		try {
			scheduler.standby();
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			throw new McJobScheduleException("schudule pause exception", e);
		}
	}

	/**
	 * 停止异步任务服务
	 * 
	 * @throws CloudStoreException
	 */
	public void destroy() throws McJobScheduleException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			throw new McJobScheduleException("schudule scheduler exception", e);
		}
	}

	/**
	 * 注册job
	 * 
	 * @param job
	 * @throws McJobScheduleException
	 *             当注册job失败或者异常时抛出
	 * @throws IllegalArgumentException
	 *             当参数不正确时抛出
	 */
	public synchronized void registerJob(McJob job) throws McJobScheduleException, IllegalArgumentException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		if (job == null || !job.validate()) {
			throw new IllegalArgumentException("Job's groupId and name,or jobListenr's name  can't be emtpy.");
		}

		if (scheduler != null) {

			JobDetail detail = new JobDetail();
			detail.setName(job.getDefinition().getName());
			detail.setGroup(job.getDefinition().getGroupId());
			detail.setJobClass(job.getClass());
			if (job.getDefinition().getJobListeners() != null && job.getDefinition().getJobListeners().size() > 0) {
				for (McJobListener listener : job.getDefinition().getJobListeners())
					if (listener != null) {
						try {
							scheduler.addJobListener(listener);
							detail.addJobListener(listener.getName());
						} catch (SchedulerException e) {
							logger.error(e.getMessage(), e);
							throw new RuntimeException("Add job listener exception", e);
						}

					}
			}

			McJobGroup group = repository.getGroups(job.getDefinition().getGroupId());

			if (group == null || !group.validate()) {
				throw new IllegalArgumentException(
						"Job's group not exist or the params cornExpression is empty, repeatInterval is less than 100ms.");
			}

			if (!repository.addJob(job.getDefinition().getGroupId(), job)) {
				throw new McJobScheduleException("Add job to  repository failed , current job size:"
						+ repository.getJobs().size());
			}

			// repeatInterval 优先级高于 CornExpression;
			Trigger trig = null;
			if (group.getRepeatInterval() >= 100) {
				trig = new SimpleTrigger();
				((SimpleTrigger) trig).setRepeatInterval(group.getRepeatInterval());
				((SimpleTrigger) trig).setRepeatCount(group.getRepeatCount());
				((SimpleTrigger) trig).setStartTime(new Date());
				((SimpleTrigger) trig).setName("trigger_" + job.getDefinition().getGroupId() + "_"
						+ job.getDefinition().getName());
			} else {
				trig = new CronTrigger();
				try {
					((CronTrigger) trig).setCronExpression(group.getCornExpression());
				} catch (ParseException e) {
				}
				((CronTrigger) trig).setStartTime(new Date());
				((CronTrigger) trig).setName("trigger_" + job.getDefinition().getGroupId() + "_"
						+ job.getDefinition().getName());
			}
			job.setRegisterTime(System.currentTimeMillis());
			try {
				scheduler.scheduleJob(detail, trig);
			} catch (SchedulerException e) {
				logger.error(e.getMessage(), e);
				throw new McJobScheduleException("Add job listener exception", e);
			}
		}
	}

	/**
	 * 注销job
	 * 
	 * @param job
	 * @throws McJobScheduleException
	 *             当注册job失败或者异常时抛出
	 * @throws IllegalArgumentException
	 *             当参数不正确时抛出
	 */
	public synchronized void unRegisterJob(McJob job) throws McJobScheduleException, IllegalArgumentException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		if (job == null || !job.validate()) {
			throw new IllegalArgumentException("Job's groupId and name can't be emtpy.");
		}

		repository.removeJob(job.getDefinition().getGroupId(), job);
		try {
			scheduler.pauseJob(job.getDefinition().getName(), job.getDefinition().getGroupId());
			scheduler.deleteJob(job.getDefinition().getName(), job.getDefinition().getGroupId());
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			throw new McJobScheduleException("UnRegister job exception", e);
		}
	}

	/**
	 * 通过job名词和组名来注销一个job
	 * 
	 * @param jobName
	 * @param groupId
	 * @throws McJobScheduleException
	 * @throws IllegalArgumentException
	 */
	public synchronized void unRegisterJob(String groupId, String jobName) throws McJobScheduleException,
			IllegalArgumentException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		if (StringUtils.isBlank(jobName) || StringUtils.isBlank(groupId)) {
			throw new IllegalArgumentException("Job's groupId and name can't be emtpy.");
		}

		repository.removeJob(groupId, jobName);
		try {
			scheduler.pauseJob(jobName, groupId);
			scheduler.deleteJob(jobName, groupId);
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			throw new McJobScheduleException("UnRegister job exception", e);
		}
	}

	public void setGroups(List<McJobGroup> groups) {
		if (groups == null) {
			return;
		}
		if (repository == null) {
			repository = DefaultMcJobRepository.getInstance();
		}

		for (McJobGroup group : groups) {
			repository.addGroups(group);
		}
	}

	/**
	 * 获取当前正在运行的job信息(当前时刻)
	 * 
	 * @return
	 * @throws McJobScheduleException
	 */
	public List<McJobDefinition> getCurrentRunningJob() throws McJobScheduleException {
		if (scheduler == null || !this.isInited) {
			return null;
		}
		try {
			List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
			List<McJobDefinition> jobInfo = null;
			if (jobContexts != null && jobContexts.size() > 0) {
				jobInfo = new ArrayList<McJobDefinition>(jobContexts.size());
				for (JobExecutionContext context : jobContexts) {
					Job jobInstance = context.getJobInstance();
					if (jobInstance != null && jobInstance instanceof McJob) {
						McJob mcjob = (McJob) jobInstance;
						jobInfo.add(mcjob.getDefinition());
					}
				}
			}
			return jobInfo;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			throw new McJobScheduleException("UnRegister job exception", e);
		}
	}

	/**
	 * 获取当前仓库中存储的（也许已经在处理）job列表
	 * 
	 * @return
	 * @throws McJobScheduleException
	 */
	public List<McJobDefinition> getCurrentStoreJob() throws McJobScheduleException {
		if (repository == null || !this.isInited) {
			return null;
		}
		Map<String, McJob> jobs = repository.getJobs();
		List<McJobDefinition> jobInfo = null;
		if (jobs != null && jobs.size() > 0) {
			Collection<McJob> values = jobs.values();
			jobInfo = new ArrayList<McJobDefinition>(jobs.size());
			for (McJob job : values) {
				jobInfo.add(job.getDefinition());
			}
		}
		return jobInfo;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public McJobRepository getRepository() {
		return repository;
	}

	public void setRepository(McJobRepository repository) {
		this.repository = repository;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getScheduleNameId() {
		return scheduleNameId;
	}

	public void setScheduleNameId(String scheduleNameId) {
		this.scheduleNameId = scheduleNameId;
	}

	public JobStore getJobStore() {
		return jobStore;
	}

	public void setJobStore(JobStore jobStore) {
		this.jobStore = jobStore;
	}

	public McJobFactory getJobFactory() {
		return jobFactory;
	}

	public void setJobFactory(McJobFactory jobFactory) {
		this.jobFactory = jobFactory;
	}

	/**
	 * 根据job注册时候的名称，获取job实例
	 * 
	 * @param name
	 * @return McJob
	 * @throws McJobScheduleException
	 */
	public McJob getJobByName(String name) throws McJobScheduleException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		if (StringUtils.isNotBlank(name) && repository != null) {
			return repository.getJob(name);
		}
		return null;
	}

	/**
	 * 根据groupid获取jobGroup
	 * 
	 * @param groupId
	 * @return McJobGroup
	 * @throws McJobScheduleException
	 */
	public McJobGroup getMcJobGroupByGroupId(String groupId) throws McJobScheduleException {
		if (!isInited) {
			throw new McJobScheduleException("The job schedule is not initialized.");
		}

		if (StringUtils.isNotBlank(groupId) && repository != null) {
			return repository.getGroups(groupId);
		}
		return null;
	}
}
