package com.basketball.service;

import com.basketball.dto.request.PointsDeductDTO;
import com.basketball.dto.response.PointsDeductResultVO;
import com.basketball.vo.PointsRecordVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 积分服务接口
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
public interface IPointsService {

    /**
     * 计算积分抵扣
     * @param userId 用户ID
     * @param pointsToUse 要使用的积分
     * @param orderAmount 订单金额
     * @return 抵扣结果
     */
    PointsDeductResultVO calculatePointsDeduct(Long userId, Integer pointsToUse, BigDecimal orderAmount);

    /**
     * 使用积分抵扣
     * @param userId 用户ID
     * @param deductDTO 抵扣信息
     * @return 抵扣结果
     */
    PointsDeductResultVO usePointsDeduct(Long userId, PointsDeductDTO deductDTO);

    /**
     * 获取用户积分信息
     * @param userId 用户ID
     * @return 积分信息
     */
    Map<String, Object> getUserPointsInfo(Long userId);

    /**
     * 获取用户积分记录
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 积分记录列表
     */
    List<PointsRecordVO> getUserPointsRecords(Long userId, Integer page, Integer pageSize);

    /**
     * 获取积分使用规则
     * @return 规则说明
     */
    Map<String, Object> getPointsRules();
}
