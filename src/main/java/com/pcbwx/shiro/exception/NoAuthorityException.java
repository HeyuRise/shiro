package com.pcbwx.shiro.exception;

/**
 * 当用户没有相应的操作权限时，抛出此异常
 * 
 * @author 王海龙
 *
 */
@SuppressWarnings("serial")
public class NoAuthorityException extends RuntimeException {

	/**
	 * 
	 */
	public NoAuthorityException() {
	}

	/**
	 * @param message
	 */
	public NoAuthorityException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoAuthorityException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoAuthorityException(String message, Throwable cause) {
		super(message, cause);
	}

}
