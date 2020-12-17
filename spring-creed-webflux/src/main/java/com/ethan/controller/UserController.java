package com.ethan.controller;

import com.ethan.dto.CommonResult;
import com.ethan.dto.UserAddDTO;
import com.ethan.dto.UserUpdateDTO;
import com.ethan.vo.UserVO;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

	/**
	 * 查询用户列表
	 *
	 * @return 用户列表
	 */
	@GetMapping("/list")
	public Flux<UserVO> list() {
		List<UserVO> list = new ArrayList<>();
		list.add(UserVO.builder().id(1L).username("ming").build());
		list.add(UserVO.builder().id(2L).username("tian").build());
		list.add(UserVO.builder().id(3L).username("zhe").build());

		return Flux.fromIterable(list);
	}

	/**
	 * 获得指定用户编号的用户
	 *
	 * @param id 用户编号
	 * @return 用户
	 */
	@GetMapping("/get")
	public Mono<UserVO> get(@RequestParam("id") Integer id) {
		// 查询用户
		UserVO user = UserVO.builder().id(id.longValue()).username("username:" + id).build();;
		// 返回
		return Mono.just(user);
	}

	/**
	 * 添加用户
	 *
	 * @param addDTO 添加用户信息 DTO
	 * @return 添加成功的用户编号
	 */
	@PostMapping("add")
	public Mono<Integer> add(@RequestBody Publisher<UserAddDTO> addDTO) {
		// 插入用户记录，返回编号
		Integer returnId = 1;
		// 返回用户编号
		return Mono.just(returnId);
	}

	/**
	 * 更新指定用户编号的用户
	 *
	 * @param updateDTO 更新用户信息 DTO
	 * @return 是否修改成功
	 */
	@PostMapping("/update")
	public Mono<Boolean> update(@RequestBody Publisher<UserUpdateDTO> updateDTO) {
		// 更新用户记录
		Boolean success = true;
		// 返回更新是否成功
		return Mono.just(success);
	}

	/**
	 * 删除指定用户编号的用户
	 *
	 * @param id 用户编号
	 * @return 是否删除成功
	 */
	@PostMapping("/delete") // URL 修改成 /delete ，RequestMethod 改成 DELETE
	public Mono<Boolean> delete(@RequestParam("id") Integer id) {
		// 删除用户记录
		Boolean success = false;
		// 返回是否更新成功
		return Mono.just(success);
	}

	// for exception test
	/**
	 * 获得指定用户编号的用户
	 *
	 * @param id 用户编号
	 * @return 用户
	 */
	@GetMapping("/get1")
	public Mono<UserVO> get1(@RequestParam("id") Long id) {
		// 查询用户
		UserVO user = UserVO.builder().id(id).username("username:" + id).build();
		// 返回
		return Mono.just(user);
	}

	/**
	 * 获得指定用户编号的用户
	 *
	 * @param id 用户编号
	 * @return 用户
	 */
	@GetMapping("/get2")
	public Mono<CommonResult<UserVO>> get2(@RequestParam("id") Long id) {
		// 查询用户
		UserVO user = UserVO.builder().id(id).username("username:" + id).build();
		// 返回
		return Mono.just(CommonResult.success(user));
	}

	/**
	 * 获得指定用户编号的用户
	 *
	 * @param id 用户编号
	 * @return 用户
	 */
	@GetMapping("/get3")
	public UserVO get3(@RequestParam("id") Long id) {
		// 查询用户
		UserVO user = UserVO.builder().id(id).username("username:" + id).build();
		// 返回
		return user;
	}

	/**
	 * 获得指定用户编号的用户
	 *
	 * @param id 用户编号
	 * @return 用户
	 */
	@GetMapping("/get4")
	public CommonResult<UserVO> get4(@RequestParam("id") Long id) {
		// 查询用户
		UserVO user = UserVO.builder().id(id).username("username:" + id).build();
		// 返回
		return CommonResult.success(user);
	}

}
