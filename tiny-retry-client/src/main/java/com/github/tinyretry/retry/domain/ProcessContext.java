package com.github.tinyretry.retry.domain;

import java.io.Serializable;

/**
 * <pre>
 * desc: 
 * created: 2012-8-23 обнГ04:51:36
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class ProcessContext implements Serializable {

	private static final long serialVersionUID = 2712233650540554124L;

	private Serializable data;

	private Task task;

	public Serializable getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
