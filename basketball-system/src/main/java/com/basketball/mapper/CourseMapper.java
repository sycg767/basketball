package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程Mapper
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
