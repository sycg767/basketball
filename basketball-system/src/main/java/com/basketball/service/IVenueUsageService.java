package com.basketball.service;

import com.basketball.entity.VenueUsageAnalysis;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 场地使用分析服务接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
public interface IVenueUsageService {

    /**
     * 分析每日场地使用情况
     *
     * @param analysisDate 分析日期
     */
    void analyzeDailyVenueUsage(LocalDate analysisDate);

    /**
     * 获取场地使用率排行
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 返回数量
     * @return 使用率排行
     */
    List<Map<String, Object>> getVenueUsageRanking(LocalDate startDate, LocalDate endDate, Integer limit);

    /**
     * 获取场地使用趋势
     *
     * @param venueId 场地ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据
     */
    List<Map<String, Object>> getVenueUsageTrend(Long venueId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取场地详细分析
     *
     * @param venueId 场地ID
     * @param analysisDate 分析日期
     * @return 详细分析数据
     */
    VenueUsageAnalysis getVenueUsageDetail(Long venueId, LocalDate analysisDate);

    /**
     * 获取高使用率场地
     *
     * @param minRate 最低使用率
     * @return 高使用率场地列表
     */
    List<Map<String, Object>> getHighUsageVenues(Integer minRate);

    /**
     * 获取低使用率场地
     *
     * @param maxRate 最高使用率
     * @return 低使用率场地列表
     */
    List<Map<String, Object>> getLowUsageVenues(Integer maxRate);

    /**
     * 获取场地收入分析
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入分析数据
     */
    List<Map<String, Object>> getVenueRevenueAnalysis(LocalDate startDate, LocalDate endDate);

    /**
     * 获取场地高峰时段分析
     *
     * @param venueId 场地ID(可选)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 高峰时段分析
     */
    List<Map<String, Object>> getPeakPeriodAnalysis(Long venueId, LocalDate startDate, LocalDate endDate);
}
