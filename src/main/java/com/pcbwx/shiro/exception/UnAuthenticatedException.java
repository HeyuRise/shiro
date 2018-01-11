package com.pcbwx.shiro.exception;

/**
 * 用户未登录时，抛出此异常
 * 
 * @author 王海龙
 *
 */
@SuppressWarnings("serial")
public class UnAuthenticatedException extends RuntimeException {

	/**
	 * 
	 */
	public UnAuthenticatedException() {
	}

	/**
	 * @param message
	 */
	public UnAuthenticatedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnAuthenticatedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnAuthenticatedException(String message, Throwable cause) {
		super(message, cause);
	}

}
