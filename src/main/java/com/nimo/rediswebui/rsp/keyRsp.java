package com.nimo.rediswebui.rsp;

import com.nimo.rediswebui.entity.redis.RedisKeys;
import lombok.Data;

import java.util.List;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-03-07 16:14
 * @Description: keyRsp
 */
@Data
public class keyRsp {
    private int page;
    private int size;
    private long count;
    private String cursor;
    private List<RedisKeys> list;
}
