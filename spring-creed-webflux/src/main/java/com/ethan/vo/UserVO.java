package com.ethan.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {
	private Long id;
	private String username;
}
