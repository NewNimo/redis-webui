package com.nimo.rediswebui.entity;



import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user_info
 */
@Table(name="user_info")
@Entity
@DynamicInsert
@DynamicUpdate
public class UserInfo implements Serializable {
    /**
     * 
     */
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    private Long id;


    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 角色类型
     */
    private Integer roleId;

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

    private static final long serialVersionUID = 1L;

    /**
     * 
     */

    public Long getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(Long id) {
        this.id = id;
    }



    /**
     * 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 用户类型
     */
    public Integer getUserType() {
        return userType;
    }

    /**
     * 用户类型
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * 角色类型
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * 角色类型
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * 默认0有效1删除
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * 默认0有效1删除
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 创建时间
     */
    public Date getRowCreateTime() {
        return rowCreateTime;
    }

    /**
     * 创建时间
     */
    public void setRowCreateTime(Date rowCreateTime) {
        this.rowCreateTime = rowCreateTime;
    }

    /**
     * 修改时间
     */
    public Date getRowUpdateTime() {
        return rowUpdateTime;
    }

    /**
     * 修改时间
     */
    public void setRowUpdateTime(Date rowUpdateTime) {
        this.rowUpdateTime = rowUpdateTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UserInfo other = (UserInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getUserType() == null ? other.getUserType() == null : this.getUserType().equals(other.getUserType()))
            && (this.getRoleId() == null ? other.getRoleId() == null : this.getRoleId().equals(other.getRoleId()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
            && (this.getRowCreateTime() == null ? other.getRowCreateTime() == null : this.getRowCreateTime().equals(other.getRowCreateTime()))
            && (this.getRowUpdateTime() == null ? other.getRowUpdateTime() == null : this.getRowUpdateTime().equals(other.getRowUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getUserType() == null) ? 0 : getUserType().hashCode());
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        result = prime * result + ((getRowCreateTime() == null) ? 0 : getRowCreateTime().hashCode());
        result = prime * result + ((getRowUpdateTime() == null) ? 0 : getRowUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userName=").append(userName);
        sb.append(", password=").append(password);
        sb.append(", userType=").append(userType);
        sb.append(", roleId=").append(roleId);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", rowCreateTime=").append(rowCreateTime);
        sb.append(", rowUpdateTime=").append(rowUpdateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}