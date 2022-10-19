package com.ethan.controller;

import com.ethan.dto.CommonResult;
import com.ethan.dto.UserAddDTO;
import com.ethan.dto.UserUpdateDTO;
import com.ethan.vo.UserVO;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class ConnectionTestController {
	private static final Logger log = LoggerFactory.getLogger(ConnectionTestController.class);
	@Resource
	private RestTemplate restTemplate;
	/**
	 * 查询用户列表
	 *
	 * @return 用户列表
	 */
	@GetMapping("/list")
	public Mono<UserVO> list() {
		ExecutorService executorService = Executors.newFixedThreadPool(500);
		List<CompletableFuture> list = new ArrayList<>();
		try {
			for (int i = 0; i < 1200; i++) {
				int finalI = i;
				CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
					try {
						log.info(String.format("--start--%s:%s", Thread.currentThread().getName(), finalI));
						ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8088/api/v1/conn", String.class);
						String body = responseEntity.getBody();
						log.info(String.format("%sget %s response:%s", Thread.currentThread().getName(), finalI, body));
					} catch (Exception e) {
						log.error(String.format("%s:%s exception %s", Thread.currentThread().getName(), finalI, e.getMessage()));
					}
				}, executorService);
				list.add(runAsync);
			}
			list.forEach(CompletableFuture::join);
		} finally {
			executorService.shutdown();
		}
		return Mono.just(UserVO.builder().id(1L).username("ming").build());
	}
	@GetMapping("/sleep")
	public Mono<UserVO> sleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return Mono.just(UserVO.builder().id(1L).username("ming").build());
	}



}
