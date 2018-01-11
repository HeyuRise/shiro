package com.pcbwx.shiro.exception;

/**
 * 当用户会话超时，抛出此异常
 * 
 * @author 王海龙
 *
 */
@SuppressWarnings("serial")
public class TimeoutException extends RuntimeException {

	/**
	 * 
	 */
	public TimeoutException() {
	}

	/**
	 * @param message
	 */
	public TimeoutException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public TimeoutException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
