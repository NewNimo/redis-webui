package com.nimo.rediswebui.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedisKeys {
	private String key;
	private String type;
	private long ttl;

}
