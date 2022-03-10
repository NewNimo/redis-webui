package com.nimo.rediswebui.intercptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

@ControllerAdvice(basePackages = "com.nimo.rediswebui")
@Slf4j
public class LogRequestAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return methodParameter.getParameterAnnotation(RequestBody.class) != null;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return new MappingJacksonInputMessage(httpInputMessage.getBody(), httpInputMessage.getHeaders());
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        String className = methodParameter.getDeclaringClass().getSimpleName();
        String methodName = methodParameter.getMethod().getName();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String arg = JSON.toJSONString(o);
        MDC.put("traceId", uuid);
        log.info("{} 请求方法 {}.{} 参数为:{}", uuid, className, methodName, arg);
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return null;
    }
}