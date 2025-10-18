package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.Booking;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预订Mapper接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Mapper
public interface BookingMapper extends BaseMapper<Booking> {
}
