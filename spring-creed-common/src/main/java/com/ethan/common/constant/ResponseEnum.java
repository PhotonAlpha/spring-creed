package com.ethan.common.constant;

public enum ResponseEnum {
  /**
   * 0 表示返回成功
   */
  SUCCESS(200,"成功"),

  /**
   * 表示接口调用方异常提示
   */
  ACCESS_TOKEN_INVALID(400,"access_token无效"),
  REFRESH_TOKEN_INVALID(400,"refresh_token无效"),
  INSUFFICIENT_PERMISSIONS(400,"该用户权限不足以访问该资源接口"),
  UNAUTHORIZED(400,"访问此资源需要完全的身份验证"),


  /**
   * 5000 表示用户提示信息
   */
  INCORRECT_PARAMS(400, "参数不正确"),
  ;
  private Integer code;
  private String message;

  ResponseEnum(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  public Integer getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
