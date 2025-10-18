package com.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basketball.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单Mapper
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
}
