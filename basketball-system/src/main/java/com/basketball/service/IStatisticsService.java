package com.basketball.service;

import com.basketball.vo.BookingVO;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface IStatisticsService {

    /**
     * 获取统计数据
     */
    Map<String, Object> getStatistics();

    /**
     * 获取预订趋势数据
     */
    List<Map<String, Object>> getBookingTrend(Integer days);

    /**
     * 获取场地使用率
     */
    List<Map<String, Object>> getVenueUsageRate();

    /**
     * 获取最近预订列表
     */
    List<BookingVO> getRecentBookings(Integer limit);
}
