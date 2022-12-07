package com.example.common.exception;
/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *      002: 短信发送太频繁
 *  11: 商品
 *  12: 订单
 *  13: 购物车
 *  14: 物流
 *  15: 用户
 *  21: 库存
 *
 */
/**
 * @author sally
 * @date 2022-07-22 5:43 PM
 */
public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"验证码发送请求太频繁，请稍后再试"),
    TOO_MANY_EXCEPTION(10003,"请求太快，请稍后再试"),
    PRODUCT_UP_EXCEPTION(11000, "ES商品上架失败"),
    NO_STOCK_EXCEPTION(21000, "Insufficient stock of goods"),
    USERNAME_EXIST_EXCEPTION(15001, "用户名已存在"),
    PHONE_EXIST_EXCEPTION(15002, "手机号码已存在"),
    ACCOUNT_PASSWORD_INVALID_EXCEPTION(15003, "账号或密码错误");

    private final int code;
    private final String msg;
    BizCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public int getCode(){
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
