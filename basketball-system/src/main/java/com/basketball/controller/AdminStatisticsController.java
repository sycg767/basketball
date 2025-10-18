package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.service.IStatisticsService;
import com.basketball.vo.BookingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 统计数据控制器（管理端）
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Tag(name = "统计数据（管理员）", description = "管理员统计数据接口")
@RestController
@RequestMapping("/api/admin/statistics")
@Validated
public class AdminStatisticsController {

    @Resource
    private IStatisticsService statisticsService;

    /**
     * 获取统计数据
     */
    @Operation(summary = "获取统计数据", description = "获取系统统计数据（场地总数、用户总数、预订总数、总收入）")
    @GetMapping
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = statisticsService.getStatistics();
        return Result.success(stats);
    }

    /**
     * 获取预订趋势数据
     */
    @Operation(summary = "获取预订趋势", description = "获取最近一段时间的预订趋势数据")
    @GetMapping("/booking-trend")
    public Result<List<Map<String, Object>>> getBookingTrend(
            @Parameter(description = "天数") @RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> trend = statisticsService.getBookingTrend(days);
        return Result.success(trend);
    }

    /**
     * 获取场地使用率
     */
    @Operation(summary = "获取场地使用率", description = "获取各场地的使用率统计")
    @GetMapping("/venue-usage")
    public Result<List<Map<String, Object>>> getVenueUsageRate() {
        List<Map<String, Object>> usage = statisticsService.getVenueUsageRate();
        return Result.success(usage);
    }

    /**
     * 获取最近预订列表
     */
    @Operation(summary = "获取最近预订", description = "获取最近的预订记录")
    @GetMapping("/recent-bookings")
    public Result<List<BookingVO>> getRecentBookings(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<BookingVO> bookings = statisticsService.getRecentBookings(limit);
        return Result.success(bookings);
    }
}
