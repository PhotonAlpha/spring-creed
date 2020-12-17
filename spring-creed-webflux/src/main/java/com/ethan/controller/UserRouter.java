package com.ethan.controller;

import com.ethan.vo.UserVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

// @Configuration
public class UserRouter {
	@Bean
	public void userListRouterFunction() {
		route(GET("/user2/list"), request -> {
			// 查询列表
			List<UserVO> result = new ArrayList<>();
			result.add(UserVO.builder().id(1L).username("yudaoyuanma").build());
			result.add(UserVO.builder().id(2L).username("woshiyutou").build());
			result.add(UserVO.builder().id(3L).username("chifanshuijiao").build());
			// 返回列表
			return ok().bodyValue(result);
		});
	}

	@Bean
	public RouterFunction<ServerResponse> userGetRouterFunction() {
		return route(GET("/users2/get"), new HandlerFunction<ServerResponse>() {
			@Override
			public Mono<ServerResponse> handle(ServerRequest request) {
					// 获得编号
					Long id = request.queryParam("id")
							.map(s -> StringUtils.isEmpty(s) ? null : Long.valueOf(s)).get();
					// 查询用户
					UserVO user = UserVO.builder().id(id).username(UUID.randomUUID().toString()).build();
					// 返回列表
					return ok().bodyValue(user);
			}
		});
	}

	@Bean
	public RouterFunction<ServerResponse> demoRouterFunction() {
		return route(GET("/users2/demo"), request -> ok().bodyValue("demo"));
	}
}
