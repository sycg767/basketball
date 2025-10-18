package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}