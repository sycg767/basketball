package com.basketball.service;

import com.basketball.entity.MemberActivityAnalysis;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 会员活跃度分析服务接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
public interface IMemberActivityService {

    /**
     * 分析每日会员活跃度
     * 统计指定日期的所有会员活跃度数据
     *
     * @param analysisDate 分析日期
     */
    void analyzeDailyActivity(LocalDate analysisDate);

    /**
     * 识别流失风险用户
     *
     * @return 流失风险用户列表
     */
    List<Map<String, Object>> identifyChurnRisk();

    /**
     * 获取活跃度趋势
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param userId    用户ID (可选,为null则统计所有用户)
     * @return 趋势数据
     */
    List<Map<String, Object>> getActivityTrend(LocalDate startDate, LocalDate endDate, Long userId);

    /**
     * 获取活跃用户列表
     *
     * @param minScore 最低活跃度得分
     * @return 活跃用户列表
     */
    List<Map<String, Object>> getActiveUsers(Integer minScore);

    /**
     * 获取不活跃用户列表
     *
     * @param maxScore 最高活跃度得分
     * @return 不活跃用户列表
     */
    List<Map<String, Object>> getInactiveUsers(Integer maxScore);

    /**
     * 获取流失风险用户
     *
     * @param riskLevel 风险等级 (1-低风险, 2-中风险, 3-高风险)
     * @return 流失风险用户列表
     */
    List<Map<String, Object>> getChurnRiskUsers(Integer riskLevel);

    /**
     * 获取用户活跃度详情
     *
     * @param userId       用户ID
     * @param analysisDate 分析日期
     * @return 活跃度详情
     */
    MemberActivityAnalysis getUserActivityDetail(Long userId, LocalDate analysisDate);
}
