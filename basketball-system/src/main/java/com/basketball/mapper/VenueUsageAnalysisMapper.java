package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.VenueUsageAnalysis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 场地使用分析Mapper接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Mapper
public interface VenueUsageAnalysisMapper extends BaseMapper<VenueUsageAnalysis> {
}
