package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.CourseEnrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程报名Mapper
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {
}
