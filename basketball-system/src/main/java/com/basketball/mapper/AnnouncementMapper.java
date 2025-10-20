package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}