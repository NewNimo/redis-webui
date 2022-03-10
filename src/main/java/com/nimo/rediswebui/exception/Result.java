package com.nimo.rediswebui.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-01-26 14:10
 * @Description: Result Data
 */
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    //响应码
    private Integer code;
    //消息
    private String message;
    //业务是否成功
    private Boolean ok;
    //数据
    private T data;

    private static final String SUCCESS="success";
    private static final String ERROR="error";

    public static <T> Result<T> build(Code code) {
        return new Result(code.getCode(), code.getMsg(), code.getSuccess(), null);
    }

    public static <T> Result<T> build(Code code, T data) {
        return new Result(code.getCode(), code.getMsg(), code.getSuccess(),data);
    }

    public static <T> Result<T> ok() {
        return new Result(Code.SUCCESS.getCode(), Code.SUCCESS.getMsg(), true, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result(Code.SUCCESS.getCode(), Code.SUCCESS.getMsg(), true, data);
    }

    public static <T> Result<T> error() {
        return new Result(Code.ERROR.getCode(), Code.ERROR.getMsg(), false, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result(Code.ERROR.getCode(), msg, false, null);
    }
    public static <T> Result<T> error(String msg,T data) {
        return new Result(Code.ERROR.getCode(), msg, false, data);
    }
    public static <T> Result<T> error(Exception exception) {
        return new Result(Code.ERROR.getCode(), getRootCauseMessage(exception), false, null);
    }
    public static <T> Result<T> error(Exception exception,T data) {
        return new Result(Code.ERROR.getCode(), getRootCauseMessage(exception), false, data);
    }

    public static <T> Result<T> fail(int code,String msg,T data) {
        return new Result(code, msg, false, data);
    }

    public static String getRootCauseMessage(Throwable throwable) {
        Throwable rootCause = ExceptionUtil.getRootCause(throwable);
        if (null == rootCause) {
            return "";
        } else {
            return null == rootCause.getMessage() ? rootCause.toString() : rootCause.getMessage();
        }
    }
}
