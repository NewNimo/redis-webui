package com.nimo.rediswebui.req;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-17 10:55
 * @Description: AddKeyReq
 */
@Data
public class AddKeyReq {
    private String key;
    private String type;
    private String value;
    private String member;
    private int ttl;
    private String server;
}
