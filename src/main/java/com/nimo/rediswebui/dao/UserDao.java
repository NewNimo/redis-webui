package com.nimo.rediswebui.dao;

import com.nimo.rediswebui.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author nimo
 * @version V1.0
 * @date 2022/2/9 13:54
 * @Description: UserDao
 */
@Repository
public interface UserDao extends JpaRepository<UserInfo,Long> {
    /**
     * 查找用户
     * @param username
     * @return
     */
    UserInfo findByUserName(String username);

    /**
     * 验证用户
     * @param username
     * @param password
     * @return
     */
    UserInfo findByUserNameAndPassword(String username,String password);
}
