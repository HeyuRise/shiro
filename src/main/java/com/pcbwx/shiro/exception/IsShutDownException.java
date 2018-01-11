package com.pcbwx.shiro.exception;

/**
 * 当配置表中系统是否关闭的value值为true时抛出此异常
 * @author 王海龙
 *
 */
@SuppressWarnings("serial")
public class IsShutDownException extends RuntimeException {

	/**
	 * 
	 */
	public IsShutDownException() {
	}

	/**
	 * @param message
	 */
	public IsShutDownException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public IsShutDownException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IsShutDownException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
