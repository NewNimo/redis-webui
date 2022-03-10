package com.nimo.rediswebui.entity.redis;

import lombok.Data;

import java.util.List;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-17 10:11
 * @Description: RedisResult
 */
@Data
public class RedisResult<T> {
    private List<T> keys;
    private String cursor;
}
