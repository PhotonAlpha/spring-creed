package com.ethan.exception;

import com.ethan.dto.CommonResult;
import com.ethan.handler.ServiceExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebInputException;

@Slf4j
@ControllerAdvice(basePackages = "com.ethan")
public class GlobalExceptionHandler {
	/**
	 * 处理 ServiceException 异常
	 */
	@ResponseBody
	@ExceptionHandler(value = ServiceException.class)
	public CommonResult serviceExceptionHandler(ServiceException ex) {
		log.debug("[serviceExceptionHandler]", ex);
		// 包装 CommonResult 结果
		return CommonResult.error(ex.getCode(), ex.getMessage());
	}

	/**
	 * 处理 ServerWebInputException 异常
	 *
	 * WebFlux 参数不正确
	 */
	@ResponseBody
	@ExceptionHandler(value = ServerWebInputException.class)
	public CommonResult serverWebInputExceptionHandler(ServerWebInputException ex) {
		log.debug("[ServerWebInputExceptionHandler]", ex);
		// 包装 CommonResult 结果
		return CommonResult.error(ServiceExceptionEnum.MISSING_REQUEST_PARAM_ERROR.getCode(),
				ServiceExceptionEnum.MISSING_REQUEST_PARAM_ERROR.getMessage());
	}

	/**
	 * 处理其它 Exception 异常
	 */
	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public CommonResult exceptionHandler(Exception e) {
		// 记录异常日志
		log.error("[exceptionHandler]", e);
		// 返回 ERROR CommonResult
		return CommonResult.error(ServiceExceptionEnum.SYS_ERROR.getCode(),
				ServiceExceptionEnum.SYS_ERROR.getMessage());
	}
}
