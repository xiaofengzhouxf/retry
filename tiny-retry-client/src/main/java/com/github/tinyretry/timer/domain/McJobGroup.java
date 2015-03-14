package com.github.tinyretry.timer.domain;

import org.apache.commons.lang.StringUtils;
import org.quartz.SimpleTrigger;

/**
 * <pre>
 * desc: 定时任务组
 * created: 2012-5-7 下午02:09:19
 * author: xiaofeng.zhouxf
 * todo: 定时任务分组的抽象，描述定时任务组的运行特性，如运行时间点，重复次数等。
 * history:
 * </pre>
 */
public class McJobGroup {
	/**
	 * 分组ID，用于区别其他组，不同组的ID必须不同
	 */
	private String groupId;
	/**
	 * 定时表达式
	 * 
	 * @see org.quartz.CronExpression
	 */
	private String cornExpression;
	/**
	 * 至少大于100毫秒
	 */
	private long repeatInterval;

	/**
	 * 重试次数
	 */
	private int repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;

	public McJobGroup() {
		super();
	}

	
	
	public McJobGroup(String groupId, int repeatInterval, int repeatCount) {
		super();
		this.groupId = groupId;
		this.repeatInterval = repeatInterval;
		this.repeatCount = repeatCount;
	}

	public McJobGroup(String groupId, String cornExpression) {
		super();
		this.groupId = groupId;
		this.cornExpression = cornExpression;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCornExpression() {
		return cornExpression;
	}

	public void setCornExpression(String cornExpression) {
		this.cornExpression = cornExpression;
	}

	public long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public boolean validate() {
		if (StringUtils.isBlank(groupId) || (StringUtils.isBlank(cornExpression) && this.repeatInterval < 100)) {
			return false;
		}
		return true;
	}
}
