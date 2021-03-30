package com.whut.ylogin.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whut.ylogin.common.to.SocialUserTo;
import com.whut.ylogin.common.to.UserLoginTo;
import com.whut.ylogin.common.utils.PageUtils;
import com.whut.ylogin.user.entity.UserEntity;

import java.util.Map;

/**
 * 
 *
 * @author fangyun
 * @email fangyun@gmail.com
 * @date 2021-03-20 15:08:33
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    UserEntity SocialLogin(SocialUserTo socialUserTo);

    UserEntity login(UserLoginTo userLoginTo);
}

