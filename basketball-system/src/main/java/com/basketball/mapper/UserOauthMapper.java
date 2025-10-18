package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.UserOauth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户第三方登录绑定Mapper
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Mapper
public interface UserOauthMapper extends BaseMapper<UserOauth> {
}
