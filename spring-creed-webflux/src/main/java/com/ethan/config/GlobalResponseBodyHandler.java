package com.ethan.config;

import com.ethan.dto.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;

public class GlobalResponseBodyHandler extends ResponseBodyResultHandler {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(GlobalResponseBodyHandler.class)

	private static MethodParameter METHOD_PARAMETER_MONO_COMMON_RESULT;

	private static final CommonResult COMMON_RESULT_SUCCESS = CommonResult.success(null);

	static {
		try {
			// <1> 获得 METHOD_PARAMETER_MONO_COMMON_RESULT 。其中 -1 表示 `#methodForParams()` 方法的返回值
			METHOD_PARAMETER_MONO_COMMON_RESULT = new MethodParameter(
					GlobalResponseBodyHandler.class.getDeclaredMethod("methodForParams"), -1);
		} catch (NoSuchMethodException e) {
			log.error("[static][获取 METHOD_PARAMETER_MONO_COMMON_RESULT 时，找不都方法");
			throw new RuntimeException(e);
		}
	}

	public GlobalResponseBodyHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
		super(writers, resolver);
	}

	public GlobalResponseBodyHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver, ReactiveAdapterRegistry registry) {
		super(writers, resolver, registry);
	}

	@Override
	public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
		log.info("handleResult ==>");
		Object returnValue = result.getReturnValue();
		Object body;
		// <1.1> 处理返回结果为 Mono 的情况
		if (returnValue instanceof Mono) {
			body = ((Mono<Object>) returnValue)
					.map(GlobalResponseBodyHandler::wrapCommonResult)
					.defaultIfEmpty(COMMON_RESULT_SUCCESS);
			// <1.2> 处理返回结果为 Flux 的情况
		} else if (returnValue instanceof Flux) {
			body = ((Flux<Object>) returnValue)
					.collectList()
					.map(GlobalResponseBodyHandler::wrapCommonResult)
					.defaultIfEmpty(COMMON_RESULT_SUCCESS);
			// <1.3> 处理结果为其它类型
		} else {
			body = wrapCommonResult(returnValue);
		}
		// <2>
		return writeBody(body, METHOD_PARAMETER_MONO_COMMON_RESULT, exchange);
	}

	private static Mono<CommonResult> methodForParams() {
		return null;
	}

	private static CommonResult<?> wrapCommonResult(Object body) {
		// 如果已经是 CommonResult 类型，则直接返回
		if (body instanceof CommonResult) {
			return (CommonResult<?>) body;
		}
		// 如果不是，则包装成 CommonResult 类型
		return CommonResult.success(body);
	}

}
