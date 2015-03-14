package com.github.tinyretry.retry.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * desc: 
 * created: 2012-9-5 上午10:48:18
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class TaskUpdate implements Serializable {
	private static final long serialVersionUID = -364248461076192575L;

	/**
	 * 自增主键
	 */
	private Long id;

	/**
	 * 任务类型
	 */
	private String type;

	/**
	 * 任务的上下文信息
	 */
	private byte[] context;

	/**
	 * 任务的版本
	 */
	private Short version;

	/**
	 * 任务状态
	 */
	private Short status;

	/**
	 * 初始值是0，每重试一次加1
	 */
	private Short retryTime;

	/**
	 * 下一次执行的时间，当前时间与January 1, 1970 UTC 之间的差值
	 */
	private Long nextTime;

	/**
	 * 本任务记录最后一次执行失败原因
	 */
	private String failReason;

	/**
	 * 记录创建时间
	 */
	private Date gmtCreate;

	/**
	 * 记录最后修改时间
	 */
	private Date gmtModified;

	/**
	 * 优先级，值越大优先级越高，默认9
	 */
	private Short priority;

	/**
	 * 任务对应的应用
	 */
	private String appCode;

	/**
	 * 处理者，可以指机器或者服务等
	 */
	private String processor;

	/**
	 * 老的状态
	 */
	private Short oldStatus;

	/**
	 * 老的版本
	 */
	private Short oldVersion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getContext() {
		return context;
	}

	public void setContext(byte[] context) {
		this.context = context;
	}

	public Short getVersion() {
		return version;
	}

	public void setVersion(Short version) {
		this.version = version;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Short getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(Short retryTime) {
		this.retryTime = retryTime;
	}

	public Long getNextTime() {
		return nextTime;
	}

	public void setNextTime(Long nextTime) {
		this.nextTime = nextTime;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public Short getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(Short oldStatus) {
		this.oldStatus = oldStatus;
	}

	public Short getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(Short oldVersion) {
		this.oldVersion = oldVersion;
	}
}
