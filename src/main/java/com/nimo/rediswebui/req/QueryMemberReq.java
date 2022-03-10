package com.nimo.rediswebui.req;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-17 10:55
 * @Description: QueryKeysReq
 */
@Data
public class QueryMemberReq {
    private String server;
    private String key;
    private String match;
    private boolean accurate;
    private String cursor;
    private int page;
    private int size =30;
    private int sort;
}
