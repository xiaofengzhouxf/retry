package com.github.tinyretry.retry.exception;

/**
 * <pre>
 * desc: 
 * created: 2012-9-5 ионГ11:40:04
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class RetryRemoteException extends Exception {

	private static final long serialVersionUID = 5044356216957810620L;

	public RetryRemoteException() {
		super();
	}

	public RetryRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetryRemoteException(String message) {
		super(message);
	}

	public RetryRemoteException(Throwable cause) {
		super(cause);
	}

}
