package com.github.tinyretry.retry.domain;

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
public class TaskHistoryQuery {
	/**
	 * 自增主键
	 */
	private Long id;

	/**
	 * 任务类型
	 */
	private String type;

	/**
	 * 任务的版本
	 */
	private Short version;

	/**
	 * 任务状态
	 */
	private Short status;

	/**
	 * 记录创建时间
	 */
	private Date beginCreateTime;

	/**
	 * 记录创建时间
	 */
	private Date endCreateTime;

	/**
	 * 记录最后修改时间
	 */
	private Date beginModifiedTime;

	/**
	 * 记录最后修改时间
	 */
	private Date endModifiedTime;

	/**
	 * 记录最后修改时间
	 */
	private Long beginFinishTime;

	/**
	 * 记录最后修改时间
	 */
	private Long endFinishTime;

	/**
	 * 任务对应的应用
	 */
	private String appCode;

	/**
	 * 处理者，可以指机器或者服务等
	 */
	private String processor;

	/**
	 * 翻页参数
	 */
	private Integer offset;

	/**
	 * 翻页参数
	 */
	private Integer length;

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

	public Date getBeginCreateTime() {
		return beginCreateTime;
	}

	public void setBeginCreateTime(Date beginCreateTime) {
		this.beginCreateTime = beginCreateTime;
	}

	public Date getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime;
	}

	public Date getBeginModifiedTime() {
		return beginModifiedTime;
	}

	public void setBeginModifiedTime(Date beginModifiedTime) {
		this.beginModifiedTime = beginModifiedTime;
	}

	public Date getEndModifiedTime() {
		return endModifiedTime;
	}

	public void setEndModifiedTime(Date endModifiedTime) {
		this.endModifiedTime = endModifiedTime;
	}

	public Long getBeginFinishTime() {
		return beginFinishTime;
	}

	public void setBeginFinishTime(Long beginFinishTime) {
		this.beginFinishTime = beginFinishTime;
	}

	public Long getEndFinishTime() {
		return endFinishTime;
	}

	public void setEndFinishTime(Long endFinishTime) {
		this.endFinishTime = endFinishTime;
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

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

}
