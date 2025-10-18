package com.basketball.service;

import com.basketball.entity.CoursePopularityAnalysis;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 课程热度分析服务接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
public interface ICoursePopularityService {

    /**
     * 分析每日课程热度
     *
     * @param analysisDate 分析日期
     */
    void analyzeDailyCoursePopularity(LocalDate analysisDate);

    /**
     * 获取课程热度排行榜
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 返回数量
     * @return 排行榜列表
     */
    List<Map<String, Object>> getCoursePopularityRanking(LocalDate startDate, LocalDate endDate, Integer limit);

    /**
     * 获取课程热度趋势
     *
     * @param courseId 课程ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据
     */
    List<Map<String, Object>> getCoursePopularityTrend(Long courseId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取课程详细分析
     *
     * @param courseId 课程ID
     * @param analysisDate 分析日期
     * @return 详细分析数据
     */
    CoursePopularityAnalysis getCoursePopularityDetail(Long courseId, LocalDate analysisDate);

    /**
     * 获取热门课程列表(热度评分 >= minScore)
     *
     * @param minScore 最低评分
     * @return 热门课程列表
     */
    List<Map<String, Object>> getHotCourses(Integer minScore);

    /**
     * 获取冷门课程列表(热度评分 <= maxScore)
     *
     * @param maxScore 最高评分
     * @return 冷门课程列表
     */
    List<Map<String, Object>> getColdCourses(Integer maxScore);

    /**
     * 获取课程转化率分析
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 转化率分析数据
     */
    List<Map<String, Object>> getCourseConversionAnalysis(LocalDate startDate, LocalDate endDate);
}
