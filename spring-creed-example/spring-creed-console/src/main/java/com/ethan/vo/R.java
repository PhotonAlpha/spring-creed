package com.ethan.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义框架异常信息格式：
 *
 * @param <T>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {
  private static final long serialVersionUID = 2662082887789042860L;
  /**
   * 异常码
   */
  @JsonIgnore
  private Integer code = 20000;

  @JsonProperty("code")
  private Integer responseCode = 20000;

  @JsonProperty("error_description")
  private String errorDescription;

  /**
   * 描述
   */
  private String message;
  private Boolean success = true;

  /**
   * 数据
   */
  private T data;

  public R() {}

  public R(Integer code, String msg) {
    this.responseCode = code;
    this.message = msg;
    this.success = false;
  }

  public R(Integer code, String message, String errorDescription) {
    this.code = code;
    this.message = message;
    this.errorDescription = errorDescription;
    this.success = false;
  }

  public R(Integer code, T data) {
    this.code = code;
    this.data = data;
    this.success = true;
  }



  public static <T> R<T> success(HttpStatus httpStatus, T data){
    return new R<>(httpStatus.value(), data);
  }
  public static <T> R<T> success(T data){
    return new R<>(HttpStatus.OK.value(), data);
  }

  public static R error(int code, String msg){
    return new R<>(code,msg);
  }
  public static R error(int code, List<String> msg){
    return new R<>(code, String.join(",", msg));
  }

}
