package com.nimo.rediswebui.req;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-02-09 14:07
 * @Description: 用户登录
 */
@Data
public class UserLoginReq {
    @NotNull(message = "用户名不能为空")
    @Pattern(regexp = "^.{2,20}$",message = "用户名长度2-20位")
    private String username;
    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "^\\w{6,20}$",message = "密码长度6-20位")
    private String password;
}
