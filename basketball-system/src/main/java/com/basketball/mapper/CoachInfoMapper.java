package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.CoachInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教练信息Mapper
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Mapper
public interface CoachInfoMapper extends BaseMapper<CoachInfo> {
}
