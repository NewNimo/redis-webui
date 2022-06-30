package com.nimo.rediswebui.rsp;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-06-23 15:09
 * @Description: 信息
 */
@Data
public class InfoRsp {
    //sever
    private String version;
    private String mode;
    private String os;
    private String uptime;
    //client
    private Integer client;
    private Integer maxClient;
    //memory
    private String userdMemory;//使用
    private String userdMemoryRss;//系统分配
    private String userdMemoryPeak ;//峰值消耗
    private String maxMemory ;//最大内存
    private String systemMemory ;//系统内存
    //统计
    private Long keyspaceHits;
    private Long keyspaceMisses;
    private String hitRate;
}
