package com.ethan.exception;

import com.ethan.handler.ServiceExceptionEnum;

public class ServiceException extends RuntimeException {
	/**
	 * 错误码
	 */
	private final Integer code;

	public ServiceException(ServiceExceptionEnum serviceExceptionEnum) {
		super(serviceExceptionEnum.getMessage());
		this.code = serviceExceptionEnum.getCode();
	}

	public Integer getCode() {
		return code;
	}
}
