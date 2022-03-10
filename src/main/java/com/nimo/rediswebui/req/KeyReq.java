package com.nimo.rediswebui.req;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-17 10:55
 * @Description: KeyReq
 */
@Data
public class KeyReq {
    private String key;
    private String server;
    private Long ttl;
}
