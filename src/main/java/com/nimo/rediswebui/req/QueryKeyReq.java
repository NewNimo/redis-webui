package com.nimo.rediswebui.req;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-17 10:55
 * @Description: QueryKeysReq
 */
@Data
public class QueryKeyReq {
    private String key;
    private String server;
    private boolean accurate;
    private String cursor;
    private int page;
}
