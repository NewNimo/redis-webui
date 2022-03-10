package com.nimo.rediswebui.service;

import com.nimo.rediswebui.entity.UserInfo;
import com.nimo.rediswebui.req.UserLoginReq;

/**
 * @author nimo
 * @version V1.0
 * @date 2022/1/28 10:29
 * @Description: UserService
 */
public interface UserService {

    /**
     * 登录
      * @param req
     */
    UserInfo login(UserLoginReq req);

    /**
     * 注册
     * @param req
     */
    void register(UserLoginReq req);
}
