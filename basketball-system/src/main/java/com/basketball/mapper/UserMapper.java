package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}