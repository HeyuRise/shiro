
package com.pcbwx.shiro.exception;

/**
 * 当配置表中系统是否关闭的value值为true时抛出此异常
 * @author 王海龙
 *
 */
@SuppressWarnings("serial")
public class SystemShutDownException extends RuntimeException {

	/**
	 * 
	 */
	public SystemShutDownException() {
	}

	/**
	 * @param message
	 */
	public SystemShutDownException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SystemShutDownException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SystemShutDownException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
