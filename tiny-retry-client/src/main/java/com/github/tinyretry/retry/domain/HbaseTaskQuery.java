package com.github.tinyretry.retry.domain;

/**
 * <pre>
 * desc: 
 * created: Apr 7, 2013 4:47:44 PM
 * author: xiangfeng
 * todo: 
 * history:
 * </pre>
 */
public class HbaseTaskQuery extends TaskQuery {

	private String rowkeyBegin;

	private String rowkeyEnd;

	public String getRowkeyBegin() {
		return rowkeyBegin;
	}

	public void setRowkeyBegin(String rowkeyBegin) {
		this.rowkeyBegin = rowkeyBegin;
	}

	public String getRowkeyEnd() {
		return rowkeyEnd;
	}

	public void setRowkeyEnd(String rowkeyEnd) {
		this.rowkeyEnd = rowkeyEnd;
	}

}
