package com.creed.vo;

import com.creed.constant.ResponseEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseVO<T> implements Serializable {
  private static final long serialVersionUID = 2662082887789042860L;
  /**
   * 异常码
   */
  @JsonIgnore
  private Integer code = 200;

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
  public ResponseVO() {}

  public ResponseVO(Integer code, String msg) {
    this.code = code;
    this.message = msg;
    this.success = false;
  }

  public ResponseVO(Integer code, String message, String errorDescription) {
    this.code = code;
    this.message = message;
    this.errorDescription = errorDescription;
    this.success = false;
  }

  public ResponseVO(ResponseEnum responseEnum) {
    this.code = responseEnum.getCode();
    this.message = responseEnum.getMessage();
    if (responseEnum == ResponseEnum.SUCCESS) {
      this.success = true;
    } else {
      this.success = false;
    }
  }

  public ResponseVO(Integer code, T data) {
    this.code = code;
    this.data = data;
    this.success = true;
  }

  public ResponseVO(ResponseEnum responseEnum, T data) {
    this.code = responseEnum.getCode();
    this.message = responseEnum.getMessage();
    this.data = data;
    if (responseEnum == ResponseEnum.SUCCESS) {
      this.success = true;
    } else {
      this.success = false;
    }
  }

  public static ResponseVO success(){
    return new ResponseVO(ResponseEnum.SUCCESS);
  }

  public static <T> ResponseVO<T> success(T data){
    return new ResponseVO<>(ResponseEnum.SUCCESS, data);
  }
  public static <T> ResponseVO<T> success(HttpStatus httpStatus, T data){
    return new ResponseVO<>(httpStatus.value(), data);
  }

  public static ResponseVO error(int code, String msg){
    return new ResponseVO(code,msg);
  }

  public static ResponseVO error(ResponseEnum responseEnum){
    return new ResponseVO(responseEnum);
  }

  public static ResponseVO error(ResponseEnum responseEnum, Object data){
    return new ResponseVO<>(responseEnum, data);
  }

  public static ResponseVO error(OAuth2Exception oEx){
    return new ResponseVO(HttpStatus.BAD_REQUEST.value(), oEx.getOAuth2ErrorCode(), oEx.getMessage());
  }
  public static ResponseVO errorParams(String msg){
    return new ResponseVO(ResponseEnum.INCORRECT_PARAMS.getCode(), msg);
  }

  public static ResponseVO error(BindingResult result, MessageSource messageSource) {
    StringBuffer msg = new StringBuffer();
    //获取错误字段集合
    List<FieldError> fieldErrors = result.getFieldErrors();
    //获取本地locale,zh_CN
    Locale currentLocale = LocaleContextHolder.getLocale();
    //遍历错误字段获取错误消息
    for (FieldError fieldError : fieldErrors) {
      //获取错误信息
      String errorMessage = messageSource.getMessage(fieldError, currentLocale);
      //添加到错误消息集合内
      msg.append(fieldError.getField() + "：" + errorMessage + " , ");
    }
    return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS, msg.toString());
  }

  public ResponseEntity<ResponseVO> build() {
    return new ResponseEntity<>(this, HttpStatus.resolve(this.getCode()));
  }
}
