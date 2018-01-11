package com.pcbwx.shiro.exception;

/**
 * 当用户传入不支持的过滤条件时，抛出此异常
 * 
 * @author 王海龙
 *
 */
@SuppressWarnings("serial")
public class UnsupportedFilterException extends RuntimeException {

	/**
	 * 
	 */
	public UnsupportedFilterException() {
	}

	/**
	 * @param message
	 */
	public UnsupportedFilterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnsupportedFilterException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnsupportedFilterException(String message, Throwable cause) {
		super(message, cause);
	}

}
