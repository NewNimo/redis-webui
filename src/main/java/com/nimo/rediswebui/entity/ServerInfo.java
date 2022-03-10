package com.nimo.rediswebui.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-15 15:24
 * @Description: ClientInfo
 */
@Table(name="server_info")
@Entity
@DynamicInsert
@DynamicUpdate
@Data
public class ServerInfo implements Serializable {
    /**
     *
     */
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     *名称
     */
    private String serverName;

    /**
     *连接
     */
    private String host;
    /**
     *端口
     */
    private int port;
    /**
     *用户
     */
    private String user;
    /**
     *密码
     */
    private String password;

    /**
     * 默认0有效1删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date rowCreateTime;

    /**
     * 修改时间
     */
    private Date rowUpdateTime;
}
