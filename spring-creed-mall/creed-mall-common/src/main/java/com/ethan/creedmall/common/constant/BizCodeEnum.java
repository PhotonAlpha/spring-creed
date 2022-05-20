package com.ethan.creedmall.common.constant;

/**
 * @className: BizCodeEnum
 * @author: Ethan
 * @date: 25/3/2022
 *
 * 错误码列表：
 * 10 通用
 *      001： 参数格式校验
 * 11 商品
 * 12 订单
 * 13 购物车
 * 14 物流
 *
 **/
public enum BizCodeEnum implements ICode {
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    UNKNOW_EXCEPTION(10000, "未知异常");

    private Integer statusCode;
    private String msg;

    BizCodeEnum(Integer statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
    }

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
