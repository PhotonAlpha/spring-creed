package com.ethan.common.exception;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorCode implements Serializable {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

}
