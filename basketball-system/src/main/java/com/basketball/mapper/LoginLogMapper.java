package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}