package com.whut.ylogin.common.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserResponseVo  implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String profileImageUrl;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 社交id
     */
    private String socialUid;
    /**
     * 社交令牌
     */
    private String accessToken;
}
