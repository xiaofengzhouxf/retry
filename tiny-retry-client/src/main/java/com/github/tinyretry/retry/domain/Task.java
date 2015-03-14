package com.github.tinyretry.retry.domain;

import java.io.Serializable;

/**
 * <pre>
 * desc: 任务定义
 * 
 *  
 *  retryPeriod: 必填项， 重试周期毫秒，不填则使用配置文件默认值	 
 *  retryTime: 必填项，重试次数，不填则使用配置文件默认值
 *  retryFactor: 必填项，重试周期权重，每多一次执行就会乘以该值，不填则使用配置文件默认值
 *  appCode: 必填项，对应的业务码，不填则使用配置文件默认值
 *  processor: 必填项，处理类
 *  taskName: 必填项，task名字， 保证在一个应用中是唯一的，需要定位来查询
 *  
 *  description: 选填项，任务描述 
 *  nextTime: 选填项，下次执行的时间 
 *  currentTime: 选填项，当前执行次数,默认为1 
 *  taskId: 选填项，当前运行的任务id, 无需填写，如果事先已记录数据库，则填写对应记录ID	 
 *  priority: 选填项，任务优先级，越大优先级越高
 * 
 * created: 2012-8-23 下午01:36:26
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class Task implements Serializable {
	private static final long serialVersionUID = -6160900702908678856L;

	/**
	 * 扩展记录信息，增加字符串的记录主键
	 */
	private String taskStrId;

	/**
	 * 选填项，当前运行的任务id, 无需填写，如果事先已记录数据库，则填写对应记录ID
	 */
	private long taskId;

	/**
	 * 必填项， 重试周期毫秒，不填则使用配置文件默认值
	 */
	private long retryPeriod;

	/**
	 * 必填项，重试次数，不填则使用配置文件默认值
	 */
	private short retryTime;

	/**
	 * 必填项，重试周期权重，每多一次执行就会乘以该值，不填则使用配置文件默认值
	 */
	private float retryFactor;

	/**
	 * 必填项，处理类
	 */
	private Processor processor;

	/**
	 * 必填项，task名字， 保证在一个应用中是唯一的，需要定位来查询
	 */
	private String taskName;

	/**
	 * 选填项，任务描述
	 */
	private String description;

	/**
	 * 选填项，下次执行的时间
	 */
	private long nextTime;

	/**
	 * 执行超时时间，单位毫秒,超时后会被重新注册到job中,之前的job会被注销
	 */
	private long executeTimeOut;

	/**
	 * 选填项，当前执行次数,默认为1
	 */
	private int currentTime = 1;

	/**
	 * 必填项，对应的业务码，不填则使用配置文件默认值
	 */
	private String appCode;

	/**
	 * 选填项，任务优先级，越大优先级越高
	 */
	private short priority;

	/**
	 * 扩展字段,用于存储一些特殊信息
	 */
	private String extMsg;

	// =========================

	public Task() {
		super();
	}

	/**
	 * 构建一个新的task
	 * 
	 * @param task
	 */
	public Task(Task task) {
		this.setTaskId(task.getTaskId());
		this.setAppCode(task.getAppCode());
		this.setCurrentTime(task.getCurrentTime());
		this.setDescription(task.getDescription());
		this.setExecuteTimeOut(task.getExecuteTimeOut());
		this.setNextTime(task.getNextTime());
		this.setPriority(task.getPriority());
		this.setProcessor(task.getProcessor());
		this.setRetryFactor(task.getRetryFactor());
		this.setRetryPeriod(task.getRetryPeriod());
		this.setTaskName(task.getTaskName());
		this.setRetryTime(task.getRetryTime());
	}

	public Task(Processor processor, String taskName) {
		super();
		this.processor = processor;
		this.taskName = taskName;
	}

	public Task(long retryPeriod, short retryTime, float retryFactor, Processor process, String taskName) {
		this(process, taskName);
		this.retryPeriod = retryPeriod;
		this.retryTime = retryTime;
		this.retryFactor = retryFactor;
	}

	// =========================
	public long getRetryPeriod() {
		return retryPeriod;
	}

	/**
	 * 必填项， 重试周期毫秒，不填则使用配置文件默认值
	 */
	public void setRetryPeriod(long retryPeriod) {
		this.retryPeriod = retryPeriod;
	}

	public short getRetryTime() {
		return retryTime;
	}

	/**
	 * 必填项，重试次数，不填则使用配置文件默认值
	 */
	public void setRetryTime(short retryTime) {
		this.retryTime = retryTime;
	}

	public float getRetryFactor() {
		return retryFactor;
	}

	/**
	 * 必填项，重试周期权重，每多一次执行就会乘以该值，不填则使用配置文件默认值
	 */
	public void setRetryFactor(float retryFactor) {
		this.retryFactor = retryFactor;
	}

	public Processor getProcessor() {
		return processor;
	}

	/**
	 * 必填项，处理类
	 */
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 选填项，任务描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getTaskName() {
		return taskName;
	}

	/**
	 * 必填项，task名字， 保证在一个应用中是唯一的，需要定位来查询
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public long getNextTime() {
		return nextTime;
	}

	/**
	 * 选填项，下次执行的时间
	 */
	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	/**
	 * 当前执行次数,默认为1
	 */
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	public String getAppCode() {
		return appCode;
	}

	/**
	 * 必填项，对应的业务码，不填则使用配置文件默认值
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public short getPriority() {
		return priority;
	}

	/**
	 * 选填项，任务优先级，越大优先级越高
	 */
	public void setPriority(short priority) {
		this.priority = priority;
	}

	public long getTaskId() {
		return taskId;
	}

	/**
	 * 选填项，当前运行的任务id, 无需填写，如果事先已记录数据库，则填写对应记录ID
	 */
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public long getExecuteTimeOut() {
		return executeTimeOut;
	}

	public void setExecuteTimeOut(long executeTimeOut) {
		this.executeTimeOut = executeTimeOut;
	}

	public String getTaskStrId() {
		return taskStrId;
	}

	public void setTaskStrId(String taskStrId) {
		this.taskStrId = taskStrId;
	}

	public String getExtMsg() {
		return extMsg;
	}

	public void setExtMsg(String extMsg) {
		this.extMsg = extMsg;
	}

}
