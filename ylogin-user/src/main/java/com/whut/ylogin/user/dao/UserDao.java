package com.whut.ylogin.user.dao;

import com.whut.ylogin.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author fangyun
 * @email fangyun@gmail.com
 * @date 2021-03-20 15:08:33
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
	
}
