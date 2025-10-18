package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.entity.CoursePopularityAnalysis;
import com.basketball.service.ICoursePopularityService;
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
 * 课程分析控制器
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/analytics/course")
@Tag(name = "课程分析", description = "课程热度和转化率分析相关接口")
public class CourseAnalyticsController {

    @Resource
    private ICoursePopularityService coursePopularityService;

    @GetMapping("/popularity-ranking")
    @Operation(summary = "获取课程热度排行榜")
    public Result<List<Map<String, Object>>> getPopularityRanking(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("查询课程热度排行榜: startDate={}, endDate={}, limit={}", startDate, endDate, limit);

        List<Map<String, Object>> ranking = coursePopularityService.getCoursePopularityRanking(startDate, endDate, limit);
        return Result.success(ranking);
    }

    @GetMapping("/popularity-trend")
    @Operation(summary = "获取课程热度趋势")
    public Result<List<Map<String, Object>>> getPopularityTrend(
            @RequestParam Long courseId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        log.info("查询课程热度趋势: courseId={}, startDate={}, endDate={}", courseId, startDate, endDate);

        List<Map<String, Object>> trend = coursePopularityService.getCoursePopularityTrend(courseId, startDate, endDate);
        return Result.success(trend);
    }

    @GetMapping("/popularity-detail")
    @Operation(summary = "获取课程热度详情")
    public Result<CoursePopularityAnalysis> getPopularityDetail(
            @RequestParam Long courseId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate analysisDate) {

        log.info("查询课程热度详情: courseId={}, analysisDate={}", courseId, analysisDate);

        CoursePopularityAnalysis detail = coursePopularityService.getCoursePopularityDetail(courseId, analysisDate);
        return Result.success(detail);
    }

    @GetMapping("/hot-courses")
    @Operation(summary = "获取热门课程列表")
    public Result<List<Map<String, Object>>> getHotCourses(
            @RequestParam(defaultValue = "60") Integer minScore) {

        log.info("查询热门课程: minScore={}", minScore);

        List<Map<String, Object>> courses = coursePopularityService.getHotCourses(minScore);
        return Result.success(courses);
    }

    @GetMapping("/cold-courses")
    @Operation(summary = "获取冷门课程列表")
    public Result<List<Map<String, Object>>> getColdCourses(
            @RequestParam(defaultValue = "30") Integer maxScore) {

        log.info("查询冷门课程: maxScore={}", maxScore);

        List<Map<String, Object>> courses = coursePopularityService.getColdCourses(maxScore);
        return Result.success(courses);
    }

    @GetMapping("/conversion-analysis")
    @Operation(summary = "获取课程转化率分析")
    public Result<List<Map<String, Object>>> getConversionAnalysis(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        log.info("查询课程转化率分析: startDate={}, endDate={}", startDate, endDate);

        List<Map<String, Object>> analysis = coursePopularityService.getCourseConversionAnalysis(startDate, endDate);
        return Result.success(analysis);
    }

    @PostMapping("/analyze")
    @Operation(summary = "手动触发课程热度分析")
    public Result<Void> analyzeCoursePopularity(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate analysisDate) {

        log.info("手动触发课程热度分析: analysisDate={}", analysisDate);

        coursePopularityService.analyzeDailyCoursePopularity(analysisDate);
        return Result.success();
    }
}
