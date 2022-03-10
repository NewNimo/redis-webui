package com.nimo.rediswebui.exception;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-01-26 14:30
 * @Description: BusinessException
 */
@Data
public class BusinessException extends RuntimeException {

    private Code error;
    private Object data;
    private String msg;

    public BusinessException(Code error){
        this.error = error;
    }

    public BusinessException(String msg){
        this.error = Code.FAIL;
        this.msg=msg;
    }
    public BusinessException(String msg,Object data){
        this.error = Code.FAIL;
        this.msg=msg;
        this.data = data;
    }

    public BusinessException(Code error,Object data){
        this.error = error;
        this.data = data;
    }



}
