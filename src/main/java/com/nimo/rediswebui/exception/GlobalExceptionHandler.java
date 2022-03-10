package com.nimo.rediswebui.exception;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        fieldErrors.forEach(n -> errorMsg.append(n.getDefaultMessage()).append(","));
        String msg = errorMsg.toString();
        Result<Object> result = Result.error(msg.substring(0, msg.length() - 1));
        log.error("{} 参数错误,返回值为:{}", MDC.get("traceId"), result);
        return result;
    }

    /**
     * Validator 参数校验异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Result<Object> handleMethodVoArgumentNotValidException(BindException ex) {
        FieldError err = ex.getFieldError();
        String message = "参数{".concat(err.getField()).concat("}").concat(err.getDefaultMessage());
        return Result.error(message);
    }

    /**
     * Validator 参数校验异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public Result<Object> handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            String paramName = constraintViolation.getPropertyPath().toString();
            String message = "参数{".concat(paramName).concat("}").concat(StrUtil.emptyToDefault(constraintViolation.getMessage(), "参数错误"));
            return Result.error(message);
        }
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result BusinessException(BusinessException e) {
        Result<Object> result = Result.error(e.getMessage());
        if(e.getError()!=null){
            result =Result.build(e.getError());
        }
        if(e.getMsg()!=null){
            result.setMessage(e.getMsg());
        }
        if(e.getData()!=null){
            result.setData(e.getData());
        }
        log.error("{} 自定义异常,返回值为:{}", MDC.get("traceId"), result);
        return result;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, InvalidFormatException.class})
    @ResponseBody
    public Result HttpMessageNotReadableException(Exception e) {
        return Result.error("参数错误");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result Exception(Exception e) {
        log.error("{} 系统异常", MDC.get("traceId"), e);
        return Result.error("系统异常");
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public Result httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.error("当前请求不支持" + e.getMethod() + "方法");
    }
}
