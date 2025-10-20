package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.FinancialRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FinancialRecordMapper extends BaseMapper<FinancialRecord> {
}