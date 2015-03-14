package com.github.tinyretry.timer.ext;

import java.util.List;

import org.quartz.JobDataMap;

/**
 * <pre>
 * desc: job定义类
 * created: 2012-7-16 上午09:32:01
 * author: xiaofeng.zhouxf
 * todo: 对任务的抽象，描述任务信息，包括任务名称，组ID,监听器列表等信息
 * history:
 * </pre>
 */
public class McJobDefinition {

	/**
	 * job名字
	 */
	private String name;

	/**
	 * 组名
	 */
	private String groupId;

	/**
	 * job描述
	 */
	private String description;

	/**
	 * 当前job的class信息
	 */
	private Class jobClass;

	/**
	 * 该job的关联数据
	 */
	private JobDataMap jobDataMap;

	/**
	 * 关联的监听器
	 */
	private List<McJobListener> jobListeners;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class jobClass) {
		this.jobClass = jobClass;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	public List<McJobListener> getJobListeners() {
		return jobListeners;
	}

	public void setJobListeners(List<McJobListener> jobListeners) {
		this.jobListeners = jobListeners;
	}
}
