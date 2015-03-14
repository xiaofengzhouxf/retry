package com.github.tinyretry.retry.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 异步任务表
 * 
 * @author xiaofeng Date 2012-09-05
 */
public class McTaskDO implements Serializable {

	private static final long serialVersionUID = 6612913770737906515L;

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
	 * 优先级，值越大优先级越高，默认9, 最大99
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
	 * setter for column 自增主键
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * getter for column 自增主键
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * setter for column 任务类型
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * getter for column 任务类型
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * setter for column 任务的上下文信息
	 */
	public void setContext(byte[] context) {
		this.context = context;
	}

	/**
	 * getter for column 任务的上下文信息
	 */
	public byte[] getContext() {
		return this.context;
	}

	/**
	 * setter for column 任务的版本
	 */
	public void setVersion(Short version) {
		this.version = version;
	}

	/**
	 * getter for column 任务的版本
	 */
	public Short getVersion() {
		return this.version;
	}

	/**
	 * setter for column 任务状态
	 */
	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * getter for column 任务状态
	 */
	public Short getStatus() {
		return this.status;
	}

	/**
	 * setter for column 初始值是0，每重试一次加1
	 */
	public void setRetryTime(Short retryTime) {
		this.retryTime = retryTime;
	}

	/**
	 * getter for column 初始值是0，每重试一次加1
	 */
	public Short getRetryTime() {
		return this.retryTime;
	}

	/**
	 * setter for column 下一次执行的时间，当前时间与January 1, 1970 UTC 之间的差值
	 */
	public void setNextTime(Long nextTime) {
		this.nextTime = nextTime;
	}

	/**
	 * getter for column 下一次执行的时间，当前时间与January 1, 1970 UTC 之间的差值
	 */
	public Long getNextTime() {
		return this.nextTime;
	}

	/**
	 * setter for column 本任务记录最后一次执行失败原因
	 */
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	/**
	 * getter for column 本任务记录最后一次执行失败原因
	 */
	public String getFailReason() {
		return this.failReason;
	}

	/**
	 * setter for column 记录创建时间
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * getter for column 记录创建时间
	 */
	public Date getGmtCreate() {
		return this.gmtCreate;
	}

	/**
	 * setter for column 记录最后修改时间
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	/**
	 * getter for column 记录最后修改时间
	 */
	public Date getGmtModified() {
		return this.gmtModified;
	}

	/**
	 * setter for column 优先级，值越大优先级越高，默认9
	 */
	public void setPriority(Short priority) {
		this.priority = priority;
	}

	/**
	 * getter for column 优先级，值越大优先级越高，默认9
	 */
	public Short getPriority() {
		return this.priority;
	}

	/**
	 * setter for column 任务对应的应用
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	/**
	 * getter for column 任务对应的应用
	 */
	public String getAppCode() {
		return this.appCode;
	}

	/**
	 * setter for column 处理者，可以指机器或者服务等
	 */
	public void setProcessor(String processor) {
		this.processor = processor;
	}

	/**
	 * getter for column 处理者，可以指机器或者服务等
	 */
	public String getProcessor() {
		return this.processor;
	}
}
