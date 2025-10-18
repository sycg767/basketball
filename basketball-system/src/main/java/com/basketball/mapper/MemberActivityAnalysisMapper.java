package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.MemberActivityAnalysis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员活跃度分析Mapper接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Mapper
public interface MemberActivityAnalysisMapper extends BaseMapper<MemberActivityAnalysis> {
}
