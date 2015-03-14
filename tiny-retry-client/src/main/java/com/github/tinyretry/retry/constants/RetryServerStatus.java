package com.github.tinyretry.retry.constants;

/**
 * <pre>
 * desc: 
 * created: 2012-9-3 обнГ04:52:37
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public enum RetryServerStatus {
	BORN(0), INITED(1), RUNNING(2), STOPED(3), DESTROYED(4);

	private int status;

	private RetryServerStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
