package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.entity.MemberActivityAnalysis;
import com.basketball.service.IMemberActivityService;
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
 * 会员活跃度分析控制器
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/analytics/member")
@Tag(name = "会员活跃度分析", description = "会员活跃度分析相关接口")
public class MemberActivityController {

    @Resource
    private IMemberActivityService memberActivityService;

    @GetMapping("/activity-trend")
    @Operation(summary = "获取活跃度趋势")
    public Result<List<Map<String, Object>>> getActivityTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long userId) {

        log.info("查询活跃度趋势: startDate={}, endDate={}, userId={}", startDate, endDate, userId);

        List<Map<String, Object>> trend = memberActivityService.getActivityTrend(startDate, endDate, userId);
        return Result.success(trend);
    }

    @GetMapping("/active-users")
    @Operation(summary = "获取活跃用户列表")
    public Result<List<Map<String, Object>>> getActiveUsers(
            @RequestParam(defaultValue = "60") Integer minScore) {

        log.info("查询活跃用户列表: minScore={}", minScore);

        List<Map<String, Object>> users = memberActivityService.getActiveUsers(minScore);
        return Result.success(users);
    }

    @GetMapping("/inactive-users")
    @Operation(summary = "获取不活跃用户列表")
    public Result<List<Map<String, Object>>> getInactiveUsers(
            @RequestParam(defaultValue = "30") Integer maxScore) {

        log.info("查询不活跃用户列表: maxScore={}", maxScore);

        List<Map<String, Object>> users = memberActivityService.getInactiveUsers(maxScore);
        return Result.success(users);
    }

    @GetMapping("/churn-risk")
    @Operation(summary = "获取流失风险用户")
    public Result<List<Map<String, Object>>> getChurnRiskUsers(
            @RequestParam(required = false) Integer riskLevel) {

        log.info("查询流失风险用户: riskLevel={}", riskLevel);

        List<Map<String, Object>> users;
        if (riskLevel != null) {
            users = memberActivityService.getChurnRiskUsers(riskLevel);
        } else {
            users = memberActivityService.identifyChurnRisk();
        }

        return Result.success(users);
    }

    @GetMapping("/detail")
    @Operation(summary = "获取用户活跃度详情")
    public Result<MemberActivityAnalysis> getUserActivityDetail(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate analysisDate) {

        log.info("查询用户活跃度详情: userId={}, analysisDate={}", userId, analysisDate);

        MemberActivityAnalysis detail = memberActivityService.getUserActivityDetail(userId, analysisDate);
        return Result.success(detail);
    }

    @PostMapping("/analyze")
    @Operation(summary = "手动触发活跃度分析")
    public Result<Void> analyzeDailyActivity(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate analysisDate) {

        log.info("手动触发活跃度分析: analysisDate={}", analysisDate);

        memberActivityService.analyzeDailyActivity(analysisDate);
        return Result.success();
    }
}
