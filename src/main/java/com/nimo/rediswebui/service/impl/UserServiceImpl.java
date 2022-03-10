package com.nimo.rediswebui.service.impl;

import com.nimo.rediswebui.dao.UserDao;
import com.nimo.rediswebui.entity.UserInfo;
import com.nimo.rediswebui.exception.BusinessException;
import com.nimo.rediswebui.exception.Code;
import com.nimo.rediswebui.req.UserLoginReq;
import com.nimo.rediswebui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-01-28 10:30
 * @Description: 描述
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserInfo login(UserLoginReq req) {
        UserInfo user=userDao.findByUserNameAndPassword(req.getUsername(),req.getPassword());
        if(user==null){
            throw new BusinessException(Code.LOGIN_FAIL);
        }
        return user;
    }

    @Override
    public void register(UserLoginReq req) {
        UserInfo user=userDao.findByUserName(req.getUsername());
        if(user!=null){
            throw new BusinessException(Code.USER_EXIST);
        }
        //重复校验
        user=new UserInfo();
        user.setUserName(req.getUsername());
        user.setPassword(req.getPassword());
        userDao.save(user);
    }
}
