package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.ScheduledReminder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时提醒Mapper
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Mapper
public interface ScheduledReminderMapper extends BaseMapper<ScheduledReminder> {
}
