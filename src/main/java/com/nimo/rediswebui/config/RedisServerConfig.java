package com.nimo.rediswebui.config;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-16 13:55
 * @Description: RedisServerConfig
 */
@Data
public class RedisServerConfig {
    /**
     *名称
     */
    private String ServerName;

    /**
     *连接
     */
    private String host;
    /**
     *端口
     */
    private int port;
    /**
     *用户
     */
    private String user;
    /**
     *密码
     */
    private String password;
    /***
     * 重试次数
     */
    private int maxRetryCount=6;
}
