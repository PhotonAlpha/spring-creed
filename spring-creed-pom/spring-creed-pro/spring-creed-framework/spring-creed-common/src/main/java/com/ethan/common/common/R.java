/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.common.common;

import com.ethan.common.exception.ServerException;
import com.ethan.common.exception.ServiceException;
import com.ethan.common.exception.enums.ResponseCodeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.Assert;

import com.ethan.common.exception.ErrorCode;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description: spring-creed-pro
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/24/2022 6:08 PM
 * 通用返回
 */
@Data
public class R<T> implements Serializable {
    private static final String CODE_MUST_BE_ERROR = "code 必须是错误的！";
    /**
     * 错误码
     *
     * @see ErrorCode#getCode()
     */
    private Integer code;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 错误提示，用户可阅读
     *
     * @see ErrorCode#getMsg() ()
     */
    private String msg;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     *
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T> 返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> R<T> error(R<?> result) {
        return error(result.getCode(), result.getMsg());
    }
    public static <T> R<T> of(Integer code, String msg) {
        R<T> result = new R<>();
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static <T> R<T> error(Integer code, String message) {
        Assert.isTrue(!ResponseCodeEnum.SUCCESS.getCode().equals(code), CODE_MUST_BE_ERROR);
        return R.of(code, message);
    }
    public static <T> R<T> error(ResponseCodeEnum code) {
        Assert.isTrue(!ResponseCodeEnum.SUCCESS.getCode().equals(code), CODE_MUST_BE_ERROR);
        return R.of(code.getCode(), code.getMessage());
    }
    public static <T> R<T> error(ResponseCodeEnum code, String message) {
        Assert.isTrue(!ResponseCodeEnum.SUCCESS.getCode().equals(code), CODE_MUST_BE_ERROR);
        return R.of(code.getCode(), message);
    }

    public static <T> R<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> R<T> success(T data) {
        R<T> result = new R<>();
        if (Objects.isNull(data)) {
            result.code = ResponseCodeEnum.BAD_REQUEST.getCode();
            result.msg = "No data received.";
        } else {
            result.code = ResponseCodeEnum.SUCCESS.getCode();
            result.data = data;
            result.msg = "";
        }
        return result;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, ResponseCodeEnum.SUCCESS.getCode());
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isSuccess() {
        return isSuccess(code);
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isError() {
        return !isSuccess();
    }

    // ========= 和 Exception 异常体系集成 =========

    /**
     * 判断是否有异常。如果有，则抛出 {@link ServiceException} 异常
     */
    public void checkError() throws ServiceException {
        if (isSuccess()) {
            return;
        }
        // 服务端异常
        if (ResponseCodeEnum.isServerErrorCode(code)) {
            throw new ServerException(code, msg);
        }
        // 业务异常
        throw new ServiceException(code, msg);
    }

    /**
     * 判断是否有异常。如果有，则抛出 {@link ServiceException} 异常
     * 如果没有，则返回 {@link #data} 数据
     */
    @JsonIgnore // 避免 jackson 序列化
    public T getCheckedData() {
        checkError();
        return data;
    }

    public static <T> R<T> error(ServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage());
    }
}
