package com.whut.ylogin.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author fangyun
 * @email fangyun@gmail.com
 * @date 2021-03-20 15:08:33
 */
@Data
@TableName("user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
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
	 * 邮箱
	 */
	private String mail;
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
