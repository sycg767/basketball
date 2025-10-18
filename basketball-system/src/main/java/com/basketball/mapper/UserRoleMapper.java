package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色Mapper
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}