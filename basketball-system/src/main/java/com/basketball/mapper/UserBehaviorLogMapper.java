package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.UserBehaviorLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为日志Mapper接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Mapper
public interface UserBehaviorLogMapper extends BaseMapper<UserBehaviorLog> {
}
