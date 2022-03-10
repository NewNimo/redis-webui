package com.nimo.rediswebui.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nimo.rediswebui.dao.ServerDao;
import com.nimo.rediswebui.entity.ServerInfo;
import com.nimo.rediswebui.exception.BusinessException;
import com.nimo.rediswebui.exception.Code;
import com.nimo.rediswebui.manager.RedisCahcheManager;
import com.nimo.rediswebui.req.AddKeyReq;
import com.nimo.rediswebui.req.KeyReq;
import com.nimo.rediswebui.req.MemberReq;
import com.nimo.rediswebui.req.QueryKeyReq;
import com.nimo.rediswebui.rsp.ConnectInfo;
import com.nimo.rediswebui.rsp.keyRsp;
import com.nimo.rediswebui.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-15 15:34
 * @Description: RedisServiceImpl
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private ServerDao serverDao;
    @Override
    public void addServer(String name, String host, Integer port,String user, String password) {
        if(StrUtil.isBlank(name)){
            throw new BusinessException("连接名不能为空");
        }
        if(StrUtil.isBlank(host)){
            throw new BusinessException("host不能为空");
        }
        if(port==null){
            throw new BusinessException("port不能为空");
        }
        RedisCahcheManager manager= RedisCahcheManager.inst();
        if(manager.existConnect(name)){
            throw new BusinessException("已经存在的连接名");
        }
        ServerInfo info=new ServerInfo();
        //尝试连接
        try  {
            Jedis jedis=new Jedis(host,port,3000);
            if(StrUtil.isNotBlank(user)&&StrUtil.isNotBlank(password)){
                jedis.auth(user,password);
            }
            jedis.ping();
            jedis.close();
        } catch (Exception e) {
            throw new BusinessException(Code.CONNECT_FAIL);
        }
        //保存
        info.setServerName(name);
        info.setHost(host);
        info.setPort(port);
        info.setUser(user);
        info.setPassword(password);
        serverDao.save(info);
        //初始化连接
        manager.addServer(name,host,port,user,password);
    }

    @Override
    public List<ConnectInfo> getServerList() {
        List<String> server=RedisCahcheManager.inst().getServerList();
        Set<String> set=new HashSet<>(server);
        List<ServerInfo> list=serverDao.findAll();
        if(CollUtil.isNotEmpty(list)){
            for (ServerInfo info : list) {
                if(!set.contains(info.getServerName())){
                    RedisCahcheManager.inst().addServer(info.getServerName(),info.getHost(),info.getPort(),info.getUser(),info.getPassword());
                }
            }
        }
        return RedisCahcheManager.inst().getConnectList();
    }

    @Override
    public keyRsp keySearch(QueryKeyReq req) {
        return RedisCahcheManager.inst().searchKey(req.getKey(),req.getServer(),req.getCursor(),req.isAccurate(),req.getPage(),RedisCahcheManager.SIZE);
    }

    @Override
    public void addkey(AddKeyReq req) {
        RedisCahcheManager.inst().newkey(req.getServer(),req.getKey(),req.getType(),req.getValue(),req.getMember(),req.getTtl());
    }

    @Override
    public void delkey(KeyReq req) {
        RedisCahcheManager.inst().deleteKey(req.getKey(),req.getServer());
    }

    @Override
    public Long updateKeyExptime(KeyReq req) {
       return RedisCahcheManager.inst().updateKeyExptime(req.getServer(),req.getKey(),req.getTtl());
    }

    @Override
    public void addValueByKey(MemberReq req) {
        RedisCahcheManager.inst().addValueByKey(req.getKey(),req.getMember(),req.getValue(),req.getServer(),req.getTtl(),req.isLeft());
    }
}
