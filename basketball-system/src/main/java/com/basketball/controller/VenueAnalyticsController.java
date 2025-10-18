package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.entity.VenueUsageAnalysis;
import com.basketball.service.IVenueUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 场地分析控制器
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/analytics/venue")
@Tag(name = "场地分析", description = "场地使用率和收入分析相关接口")
public class VenueAnalyticsController {

    @Resource
    private IVenueUsageService venueUsageService;

    @GetMapping("/usage-ranking")
    @Operation(summary = "获取场地使用率排行")
    public Result<List<Map<String, Object>>> getUsageRanking(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("查询场地使用率排行: startDate={}, endDate={}, limit={}", startDate, endDate, limit);

        List<Map<String, Object>> ranking = venueUsageService.getVenueUsageRanking(startDate, endDate, limit);
        return Result.success(ranking);
    }

    @GetMapping("/usage-trend")
    @Operation(summary = "获取场地使用趋势")
    public Result<List<Map<String, Object>>> getUsageTrend(
            @RequestParam Long venueId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        log.info("查询场地使用趋势: venueId={}, startDate={}, endDate={}", venueId, startDate, endDate);

        List<Map<String, Object>> trend = venueUsageService.getVenueUsageTrend(venueId, startDate, endDate);
        return Result.success(trend);
    }

    @GetMapping("/usage-detail")
    @Operation(summary = "获取场地使用详情")
    public Result<VenueUsageAnalysis> getUsageDetail(
            @RequestParam Long venueId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate analysisDate) {

        log.info("查询场地使用详情: venueId={}, analysisDate={}", venueId, analysisDate);

        VenueUsageAnalysis detail = venueUsageService.getVenueUsageDetail(venueId, analysisDate);
        return Result.success(detail);
    }

    @GetMapping("/high-usage")
    @Operation(summary = "获取高使用率场地列表")
    public Result<List<Map<String, Object>>> getHighUsageVenues(
            @RequestParam(defaultValue = "70") Integer minRate) {

        log.info("查询高使用率场地: minRate={}", minRate);

        List<Map<String, Object>> venues = venueUsageService.getHighUsageVenues(minRate);
        return Result.success(venues);
    }

    @GetMapping("/low-usage")
    @Operation(summary = "获取低使用率场地列表")
    public Result<List<Map<String, Object>>> getLowUsageVenues(
            @RequestParam(defaultValue = "30") Integer maxRate) {

        log.info("查询低使用率场地: maxRate={}", maxRate);

        List<Map<String, Object>> venues = venueUsageService.getLowUsageVenues(maxRate);
        return Result.success(venues);
    }

    @GetMapping("/revenue-analysis")
    @Operation(summary = "获取场地收入分析")
    public Result<List<Map<String, Object>>> getRevenueAnalysis(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        log.info("查询场地收入分析: startDate={}, endDate={}", startDate, endDate);

        List<Map<String, Object>> analysis = venueUsageService.getVenueRevenueAnalysis(startDate, endDate);
        return Result.success(analysis);
    }

    @GetMapping("/peak-period")
    @Operation(summary = "获取高峰时段分析")
    public Result<List<Map<String, Object>>> getPeakPeriodAnalysis(
            @RequestParam(required = false) Long venueId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        log.info("查询高峰时段分析: venueId={}, startDate={}, endDate={}", venueId, startDate, endDate);

        List<Map<String, Object>> analysis = venueUsageService.getPeakPeriodAnalysis(venueId, startDate, endDate);
        return Result.success(analysis);
    }

    @PostMapping("/analyze")
    @Operation(summary = "手动触发场地使用分析")
    public Result<Void> analyzeVenueUsage(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate analysisDate) {

        log.info("手动触发场地使用分析: analysisDate={}", analysisDate);

        venueUsageService.analyzeDailyVenueUsage(analysisDate);
        return Result.success();
    }
}
