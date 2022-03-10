package com.nimo.rediswebui.rsp;

import lombok.Data;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-16 17:24
 * @Description: ConnectInfo
 */
@Data
public class ConnectInfo {
    /**
     *名称
     */
    private String serverName;

    /**
     *连接
     */
    private String host;
}
