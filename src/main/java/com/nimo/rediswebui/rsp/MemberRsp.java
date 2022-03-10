package com.nimo.rediswebui.rsp;

import com.nimo.rediswebui.entity.redis.KeyMember;
import lombok.Data;

import java.util.List;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-03-07 16:14
 * @Description: 描述
 */
@Data
public class MemberRsp {
    private int page;
    private int size;
    private long count;
    private String cursor;
    private List<KeyMember> list;
}
