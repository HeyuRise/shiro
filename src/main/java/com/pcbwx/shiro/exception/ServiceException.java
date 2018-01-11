package com.pcbwx.shiro.exception;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 6648644306413417295L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	 
}
