package com.github.tinyretry.timer;

/**
 * <pre>
 * desc: 定时任务的异常
 * created: 2012-7-20 下午03:50:41
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class McJobScheduleException extends Exception {

	private static final long serialVersionUID = -219401193032551342L;

	public McJobScheduleException() {
		super();
	}

	public McJobScheduleException(String message, Throwable cause) {
		super(message, cause);
	}

	public McJobScheduleException(String message) {
		super(message);
	}

	public McJobScheduleException(Throwable cause) {
		super(cause);
	}

}
