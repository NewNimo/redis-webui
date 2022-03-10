package com.nimo.rediswebui.dao;

import com.nimo.rediswebui.entity.ServerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author nimo
 * @version V1.0
 * @date 2022/2/9 13:54
 * @Description: ClientDao
 */
@Repository
public interface ServerDao extends JpaRepository<ServerInfo,Long> {



}
