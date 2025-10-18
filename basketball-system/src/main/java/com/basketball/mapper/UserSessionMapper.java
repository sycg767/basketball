package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.UserSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Session Mapper
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Mapper
public interface UserSessionMapper extends BaseMapper<UserSession> {
}
