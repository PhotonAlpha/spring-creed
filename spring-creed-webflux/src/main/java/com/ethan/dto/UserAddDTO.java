package com.ethan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddDTO {
	/**
	 * 账号
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
}
