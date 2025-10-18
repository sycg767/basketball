package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.MemberCardRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员卡使用记录Mapper
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Mapper
public interface MemberCardRecordMapper extends BaseMapper<MemberCardRecord> {
}
