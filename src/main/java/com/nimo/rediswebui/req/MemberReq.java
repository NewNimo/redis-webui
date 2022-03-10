package com.nimo.rediswebui.req;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-17 10:55
 * @Description: MemberReq
 */
@Data
public class MemberReq {
    private String key;
    private String value;
    private String server;
    private long ttl;
    private String member;
    private boolean left;
}
