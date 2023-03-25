/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.service.spi.ServiceException;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.rmi.ServerException;
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



    public static <T> R<T> success(T data) {
        R<T> result = new R<>();
        if (Objects.isNull(data)) {
            result.code = 400;
            result.msg = "No data received.";
        } else {
            result.code = 200;
            result.data = data;
            result.msg = "";
        }
        return result;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, 200);
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


    /**
     * 判断是否有异常。如果有，则抛出 {@link ServiceException} 异常
     * 如果没有，则返回 {@link #data} 数据
     */
    @JsonIgnore // 避免 jackson 序列化
    public T getCheckedData() {
        return data;
    }


}
