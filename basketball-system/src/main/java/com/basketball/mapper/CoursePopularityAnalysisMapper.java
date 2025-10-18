package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.CoursePopularityAnalysis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程热度分析Mapper接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Mapper
public interface CoursePopularityAnalysisMapper extends BaseMapper<CoursePopularityAnalysis> {
}
