package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.Dictionary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {
}