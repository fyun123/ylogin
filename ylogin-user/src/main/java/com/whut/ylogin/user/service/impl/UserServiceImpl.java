package com.whut.ylogin.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.whut.ylogin.common.to.SocialUserTo;
import com.whut.ylogin.common.to.UserLoginTo;
import com.whut.ylogin.common.utils.HttpUtils;
import com.whut.ylogin.common.utils.PageUtils;
import com.whut.ylogin.common.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.whut.ylogin.user.dao.UserDao;
import com.whut.ylogin.user.entity.UserEntity;
import com.whut.ylogin.user.service.UserService;

@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 社交登录
     * @param socialUserTo 社交登录成功返回的数据
     * @return UserEntity 数据库保存的用户
     */
    @Override
    public UserEntity SocialLogin(SocialUserTo socialUserTo) {
        // 登陆和注册合并逻辑
        String uid = socialUserTo.getUid();
        UserDao userDao = this.baseMapper;
        UserEntity member = userDao.selectOne(new QueryWrapper<UserEntity>().eq("social_uid", uid));
        if (member != null){
            UserEntity updateMember = new UserEntity();
            updateMember.setId(member.getId());
            updateMember.setAccessToken(socialUserTo.getAccessToken());
            userDao.updateById(updateMember);
            member.setAccessToken(socialUserTo.getAccessToken());
            return member;
        } else {
            // 没有查到当前社交用户，需要注册
            UserEntity register = new UserEntity();
            try {
                // 查询当前社交用户的信息
                Map<String, String> query= new HashMap<>();
                query.put("access_token",socialUserTo.getAccessToken());
                query.put("uid",socialUserTo.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "GET", new HashMap<String, String>(), query);
                if (response.getStatusLine().getStatusCode() == 200) {
                    // 查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    // 获取用户信息
                    register.setNickname(jsonObject.getString("name"));
                    register.setGender("m".equals(jsonObject.getString("gender")) ?1:0);
                    register.setProfileImageUrl(jsonObject.getString("profile_image_url"));
                }
            }catch (Exception e){
                log.error("调用微博API服务失败");
            }
            register.setSocialUid(socialUserTo.getUid());
            register.setAccessToken(socialUserTo.getAccessToken());
            register.setCreateTime(new Date());
            userDao.insert(register);
            return register;
        }
    }

    @Override
    public UserEntity login(UserLoginTo userLoginTo) {
        String loginAccount = userLoginTo.getLoginAccount();
        String password = userLoginTo.getPassword();
        // 1. 去数据库查询
        UserEntity userEntity = this.baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", loginAccount));
        if (userEntity == null){
            return null;
        } else {
            String passwordDb = userEntity.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, passwordDb);
            if (matches){
                return userEntity;
            } else {
                return  null;
            }
        }
    }

}