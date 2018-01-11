package com.pcbwx.shiro.common.exception;

/**
 * 页面跳转
 * @author 王海龙
 *
 */
public class ViewBusinessException extends BusinessException {

	private static final long serialVersionUID = -706410443306288157L;
	
	public ViewBusinessException(Throwable t){
		super(t);
	}
	
	public ViewBusinessException(ExceptionType exceptionType, String msg){
		super(exceptionType, msg);
	}
}
