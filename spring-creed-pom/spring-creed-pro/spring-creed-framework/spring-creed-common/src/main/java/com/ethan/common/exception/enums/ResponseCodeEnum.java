package com.ethan.common.exception.enums;

/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 *
 * 一般情况下，使用 HTTP 响应状态码 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status
 * 虽然说，HTTP 响应状态码作为业务使用表达能力偏弱，但是使用在系统层面还是非常不错的
 */
public enum ResponseCodeEnum {
  /**
   * 0 表示返回成功
   */
  SUCCESS(200,"成功"),

  /**
   * ========== 客户端错误段 ==========
   * 表示接口调用方异常提示
   */
  BAD_REQUEST(400,"请求参数不正确"),
  UNAUTHORIZED(401,"访问此资源需要完全的身份验证"),
  FORBIDDEN(403,"没有该操作权限"),
  NOT_FOUND(404,"请求未找到"),
  METHOD_NOT_ALLOWED(405,"请求方法不正确"),
  ACCESS_TOKEN_INVALID(403,"access_token无效"),
  REFRESH_TOKEN_INVALID(403,"refresh_token无效"),
  INSUFFICIENT_PERMISSIONS(403,"该用户权限不足以访问该资源接口"),
  LOCKED(423,"请求失败，请稍后重试"),
  TOO_MANY_REQUESTS(429,"请求过于频繁，请稍后重试"),

  /**
   * ========== 服务端错误段 ==========
   * 5000 表示用户提示信息
   */
  INTERNAL_SERVER_ERROR(500, "系统异常"),
  REPEATED_REQUESTS(900, "重复请求，请稍后重试"),
  DEMO_DENY(901, "演示模式，禁止写操作"),
  UNKNOWN(999, "未知错误"),
  ;
  private Integer code;
  private String message;

  ResponseCodeEnum(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  public Integer getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public static boolean isServerErrorCode(Integer code) {
    return code != null
            && code >= INTERNAL_SERVER_ERROR.getCode() && code <= INTERNAL_SERVER_ERROR.getCode() + 99;
  }
}
