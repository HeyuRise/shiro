package com.pcbwx.shiro.common.exception;

/**
 * ajax异步请求
 * @author 王海龙
 *
 */
public class AjaxBusinessException extends BusinessException {

	private static final long serialVersionUID = 734588006872170927L;

	public AjaxBusinessException(Throwable t){
		super(t);
	}
	public AjaxBusinessException(ExceptionType exceptionType, String msg){
		super(exceptionType, msg);
	}
}
