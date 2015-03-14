package com.github.tinyretry.retry.constants;

/**
 * <pre>
 * desc: 任务状态
 * created: 2012-6-13 下午02:37:46
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public enum TaskStatus {
	// 待处理
	WAITING((short) 1),
	// 处理中
	DEALING((short) 2),
	// 处理失败
	FAIL((short) 3),
	// 处理成功
	SUCCESS((short) 4),
	// 清理中
	REMOVING((short) 5),
	// 等待人工干预
	MANUAL((short) 6),
	// 超时处理中
	TIMEOUT_DEAILING((short) 7);

	private short status;

	private TaskStatus(short status) {
		this.status = status;
	}

	public short getStatus() {
		return status;
	}
}
