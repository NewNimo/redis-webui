package com.nimo.rediswebui.exception;

import lombok.AllArgsConstructor;


/**
 * @author nimo
 * @version V1.0
 * @date 2022/1/26 14:29
 * @Description: Business status code
 */
@AllArgsConstructor
public enum Code {
    SUCCESS(200,"success",true),
    ERROR(500,"unknow error",false),
    FAIL(-100,"error",false),
    DATA_NULL(-200,"数据为空",false),
    DATA_END(-201,"数据终点",false),
    USER_EXIST(-10000,"用户已存在",false),
    LOGIN_FAIL(-10001,"用户名或密码错误",false),
    CONNECT_FAIL(-10002,"连接失败",false),
    KEY_ISNULL(-10003,"key为空",false),
    VALUE_ISNULL(-10004,"value为空",false),
    MEMEBER_ISNULL(-10005,"member为空",false),
    VALUE_NOT_EXIST(-10006,"key不存在",false),
    /**
     * END OF CODE
     */
    END(0,"end",false);


    private final int code;
    private final String msg;
    private final Boolean success;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Boolean getSuccess() {
        return success;
    }
}
