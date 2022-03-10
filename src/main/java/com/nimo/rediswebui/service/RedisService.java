package com.nimo.rediswebui.service;

import com.nimo.rediswebui.req.AddKeyReq;
import com.nimo.rediswebui.req.KeyReq;
import com.nimo.rediswebui.req.MemberReq;
import com.nimo.rediswebui.req.QueryKeyReq;
import com.nimo.rediswebui.rsp.ConnectInfo;
import com.nimo.rediswebui.rsp.keyRsp;

import java.util.List;
import java.util.Map;

/**
 * @author nimo
 * @version V1.0
 * @date 2022/2/15 15:29
 * @Description: RedisService
 */
public interface RedisService {

    /***
     * 添加服务端
     * @param name
     * @param host
     * @param port
     * @param password
     */
    void addServer(String name,String host,Integer port,String user,String password);

    /***
     *服务列表
     * @return
     */
    List<ConnectInfo> getServerList();

    keyRsp keySearch(QueryKeyReq req);

    void addkey(AddKeyReq req);

    void delkey(KeyReq req);

    Long updateKeyExptime(KeyReq req);


    void addValueByKey(MemberReq req);
}
