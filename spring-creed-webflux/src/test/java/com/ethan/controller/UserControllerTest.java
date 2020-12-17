package com.ethan.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {UserController.class})
public class UserControllerTest {
	@Autowired
	private ApplicationContext context;
	@Autowired
	private WebTestClient  webClient;

	// @MockBean
	@Test
	public void testList() {
		webClient.get().uri("/users/list")
				.exchange() // 执行请求
				.expectStatus().isOk() // 响应状态码 200
				.expectBody().json("[\n" +
				"    {\n" +
				"        \"id\": 1,\n" +
				"        \"username\": \"ming\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": 2,\n" +
				"        \"username\": \"tian\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"id\": 3,\n" +
				"        \"username\": \"zhe\"\n" +
				"    }\n" +
				"]"); // 响应结果
	}
}
