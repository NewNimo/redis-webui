package com.nimo.rediswebui.req;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-17 10:55
 * @Description: MemberDelReq
 */
@Data
public class MemberDelReq {
    private String key;
    private String server;
    private String member;
    private Integer count;
}
