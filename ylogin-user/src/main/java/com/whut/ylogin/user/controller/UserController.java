package com.whut.ylogin.user.controller;

import java.util.Arrays;
import java.util.Map;

import com.whut.ylogin.common.exception.BizCodeEnume;
import com.whut.ylogin.common.to.SocialUserTo;
import com.whut.ylogin.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.whut.ylogin.user.entity.UserEntity;
import com.whut.ylogin.user.service.UserService;
import com.whut.ylogin.common.utils.PageUtils;




/**
 * 
 *
 * @author fangyun
 * @email fangyun@gmail.com
 * @date 2021-03-20 15:08:33
 */
@RestController
@RequestMapping("user/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 社交用户登陆
     */
    @PostMapping("/oauth/login")
    public R socialLogin(@RequestBody SocialUserTo socialUserTo) throws Exception {
        UserEntity userEntity = userService.login(socialUserTo);
        if (userEntity != null){
            return R.ok().setData(userEntity);
        }else {
            return R.error(BizCodeEnume.LOGINACCOUNT_PASSWORD_INVALID_EXCEPTION.getCode(),BizCodeEnume.LOGINACCOUNT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		UserEntity user = userService.getById(id);

        return R.ok().put("user", user);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UserEntity user){
		userService.save(user);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UserEntity user){
		userService.updateById(user);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
