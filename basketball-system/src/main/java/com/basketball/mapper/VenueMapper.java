package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.Venue;
import org.apache.ibatis.annotations.Mapper;

/**
 * 场地Mapper接口
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Mapper
public interface VenueMapper extends BaseMapper<Venue> {
}
