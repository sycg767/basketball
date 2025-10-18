package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程排期Mapper
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {
}
