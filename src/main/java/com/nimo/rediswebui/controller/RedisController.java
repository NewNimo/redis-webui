package com.nimo.rediswebui.controller;

import cn.hutool.core.util.StrUtil;
import com.nimo.rediswebui.exception.Result;
import com.nimo.rediswebui.manager.RedisCahcheManager;
import com.nimo.rediswebui.req.*;
import com.nimo.rediswebui.rsp.ConnectInfo;
import com.nimo.rediswebui.rsp.InfoRsp;
import com.nimo.rediswebui.rsp.MemberRsp;
import com.nimo.rediswebui.rsp.keyRsp;
import com.nimo.rediswebui.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-01-26 13:42
 * @Description: RedisController
 */
@RestController
@Slf4j
@RequestMapping(value = "/redis")
public class RedisController {
    @Autowired
    private RedisService redisService;

    @GetMapping(value = "/index")
    public ModelAndView redis() {
        return new ModelAndView("redis-index");
    }

    @GetMapping(value = "/server/list")
    public Result<List<ConnectInfo>> serverList() {
        return Result.ok(redisService.getServerList());
    }


    @PostMapping(value = "/add/server")
    public Result<Void> addServer(String name,String host,Integer port,String user,String password) {
        redisService.addServer(name,host,port,user,password);
        return Result.ok();
    }
    //查询key
    @PostMapping(value = "/key/search")
    public Result<keyRsp> keySearch(@RequestBody QueryKeyReq req) {
        return Result.ok(redisService.keySearch(req));
    }


    @PostMapping(value = "/add/key")
    public Result<Void> addkey(@RequestBody AddKeyReq req) {
        redisService.addkey(req);
        return Result.ok();
    }


    @PostMapping(value = "/del/key")
    public Result<Void> delkey(@RequestBody KeyReq req) {
        redisService.delkey(req);
        return Result.ok();
    }


    //首页
    @RequestMapping(value = "/key/index")
    public ModelAndView  keyindex(HttpServletRequest request, String key, String server) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("key", key);
            data.put("server", server);
//            if(RedisManager.keyIsNULL(key, client)) {
//                throw new YzException(CODE.KEYISNULL);
//            }
            RedisCahcheManager redisCahcheManager=RedisCahcheManager.inst();
            String type=redisCahcheManager.getKeyType(server,key);
            data.put("ttl", redisCahcheManager.getKeyTtl(server,key));
            switch (type) {
                case RedisCahcheManager.TypeString:
                    data.put("value",redisCahcheManager.getKeyValue(server,key));
                    return new ModelAndView("redis-string",data);
                case RedisCahcheManager.TypeSet:
                    return new ModelAndView("redis-set",data);
                case RedisCahcheManager.TypeZset:
                    return new ModelAndView("redis-zset",data);
                case RedisCahcheManager.TypeHash:
                    return new ModelAndView("redis-hash",data);
                case RedisCahcheManager.TypeList:
                    return new ModelAndView("redis-list",data);
            }
        return new ModelAndView("redis-nil",data);
    }

    //ttl
    @PostMapping(value = "/key/ttl")
    public Result<Long> keyttl(@RequestBody KeyReq req) {
        return Result.ok(redisService.updateKeyExptime(req));
    }

    @PostMapping(value = "/member/new")
    public Result<Void> memberNew(@RequestBody MemberReq req) {
        redisService.addValueByKey(req);
        return Result.ok();
    }

    @PostMapping(value = "/member/del")
    public Result<Void> memberDelete(@RequestBody MemberDelReq req) {
        RedisCahcheManager.inst().deleteValueByKey(req.getKey(),req.getMember(),req.getServer(),req.getCount());
        return Result.ok();
    }


    @PostMapping(value = "/member/search")
    public Result<MemberRsp> memberSearch(@RequestBody QueryMemberReq req) {
        if(StrUtil.isBlank(req.getMatch())) {
            req.setMatch("*");
        }
        MemberRsp rsp=RedisCahcheManager.inst().scanKeyMember(req.getServer(),req.getKey(),req.getMatch(),
                req.getCursor(),req.isAccurate(),req.getPage(),req.getSize());
        return Result.ok(rsp);
    }


    @PostMapping(value = "/member/list")
    public Result<MemberRsp> memberList(@RequestBody QueryMemberReq req) {
        MemberRsp rsp=RedisCahcheManager.inst().listKeyMember(req.getKey(),req.getServer(),req.getPage(),req.getSort(),req.getSize());
        return Result.ok(rsp);
    }


    @PostMapping(value = "/member/set")
    public Result<String> listMemberSet(@RequestBody ListMemberReq req) {
        String rsp=RedisCahcheManager.inst().listSetMember(req.getKey(),req.getMember(),req.getServer(),req.getIndex());
        return Result.ok(rsp);
    }


    @PostMapping(value = "/info")
    public Result<InfoRsp> listMemberSet(@RequestBody InfoReq req) {
        return Result.ok(RedisCahcheManager.inst().info(req.getServer()));
    }

}
