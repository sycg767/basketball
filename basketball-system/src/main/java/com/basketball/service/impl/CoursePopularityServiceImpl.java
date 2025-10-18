package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.entity.*;
import com.basketball.mapper.*;
import com.basketball.service.ICoursePopularityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程热度分析服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Service
public class CoursePopularityServiceImpl implements ICoursePopularityService {

    @Resource
    private CoursePopularityAnalysisMapper popularityAnalysisMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseEnrollmentMapper enrollmentMapper;

    @Resource
    private UserBehaviorLogMapper behaviorLogMapper;

    @Resource
    private PaymentOrderMapper paymentOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void analyzeDailyCoursePopularity(LocalDate analysisDate) {
        log.info("开始分析课程热度: analysisDate={}", analysisDate);

        LocalDateTime startDateTime = analysisDate.atStartOfDay();
        LocalDateTime endDateTime = analysisDate.plusDays(1).atStartOfDay();

        // 获取所有有效课程
        LambdaQueryWrapper<Course> courseQuery = new LambdaQueryWrapper<>();
        courseQuery.eq(Course::getStatus, 1); // 只分析上架课程
        List<Course> courses = courseMapper.selectList(courseQuery);

        List<CoursePopularityAnalysis> analysisResults = new ArrayList<>();

        for (Course course : courses) {
            try {
                CoursePopularityAnalysis analysis = analyzeSingleCourse(course.getId(), analysisDate, startDateTime, endDateTime);
                analysisResults.add(analysis);
            } catch (Exception e) {
                log.error("分析课程热度失败: courseId={}", course.getId(), e);
            }
        }

        // 计算排名
        calculateRankings(analysisResults);

        // 计算趋势
        for (CoursePopularityAnalysis analysis : analysisResults) {
            calculateTrend(analysis);
        }

        // 保存分析结果
        for (CoursePopularityAnalysis analysis : analysisResults) {
            // 检查是否已存在当天的分析记录
            LambdaQueryWrapper<CoursePopularityAnalysis> existQuery = new LambdaQueryWrapper<>();
            existQuery.eq(CoursePopularityAnalysis::getCourseId, analysis.getCourseId())
                    .eq(CoursePopularityAnalysis::getAnalysisDate, analysisDate);
            CoursePopularityAnalysis existing = popularityAnalysisMapper.selectOne(existQuery);

            if (existing != null) {
                analysis.setId(existing.getId());
                popularityAnalysisMapper.updateById(analysis);
            } else {
                popularityAnalysisMapper.insert(analysis);
            }
        }

        log.info("课程热度分析完成: analysisDate={}, courseCount={}", analysisDate, analysisResults.size());
    }

    /**
     * 分析单个课程
     */
    private CoursePopularityAnalysis analyzeSingleCourse(Long courseId, LocalDate analysisDate,
                                                         LocalDateTime startDateTime, LocalDateTime endDateTime) {
        CoursePopularityAnalysis analysis = new CoursePopularityAnalysis();
        analysis.setCourseId(courseId);
        analysis.setAnalysisDate(analysisDate);

        // 1. 统计浏览数据(从用户行为日志)
        LambdaQueryWrapper<UserBehaviorLog> viewQuery = new LambdaQueryWrapper<>();
        viewQuery.eq(UserBehaviorLog::getModule, "course")
                .eq(UserBehaviorLog::getBehaviorType, "view")
                .eq(UserBehaviorLog::getBusinessId, courseId.toString())
                .between(UserBehaviorLog::getBehaviorTime, startDateTime, endDateTime);
        List<UserBehaviorLog> viewLogs = behaviorLogMapper.selectList(viewQuery);

        analysis.setViewCount(viewLogs.size());
        Set<Long> viewUsers = viewLogs.stream().map(UserBehaviorLog::getUserId).collect(Collectors.toSet());
        analysis.setViewUserCount(viewUsers.size());

        // 2. 统计报名数据
        LambdaQueryWrapper<CourseEnrollment> enrollQuery = new LambdaQueryWrapper<>();
        enrollQuery.eq(CourseEnrollment::getCourseId, courseId)
                .between(CourseEnrollment::getCreateTime, startDateTime, endDateTime);
        List<CourseEnrollment> enrollments = enrollmentMapper.selectList(enrollQuery);

        analysis.setEnrollCount(enrollments.size());
        Set<Long> enrollUsers = enrollments.stream().map(CourseEnrollment::getUserId).collect(Collectors.toSet());
        analysis.setEnrollUserCount(enrollUsers.size());

        // 3. 统计完成人数(出勤状态为已签到的,且有评价的视为完成)
        LambdaQueryWrapper<CourseEnrollment> completionQuery = new LambdaQueryWrapper<>();
        completionQuery.eq(CourseEnrollment::getCourseId, courseId)
                .eq(CourseEnrollment::getAttendanceStatus, 1) // 已签到
                .isNotNull(CourseEnrollment::getRating) // 已评价视为完成
                .le(CourseEnrollment::getCommentTime, endDateTime);
        Long completionCount = enrollmentMapper.selectCount(completionQuery);
        analysis.setCompletionCount(completionCount.intValue());

        // 4. 统计评分数据(假设CourseEnrollment有评分字段)
        List<CourseEnrollment> ratedEnrollments = enrollments.stream()
                .filter(e -> e.getRating() != null && e.getRating() > 0)
                .collect(Collectors.toList());

        if (!ratedEnrollments.isEmpty()) {
            double avgRating = ratedEnrollments.stream()
                    .mapToInt(CourseEnrollment::getRating)
                    .average()
                    .orElse(0.0);
            analysis.setAvgRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
            analysis.setRatingCount(ratedEnrollments.size());
        } else {
            analysis.setAvgRating(BigDecimal.ZERO);
            analysis.setRatingCount(0);
        }

        // 5. 统计收入数据
        LambdaQueryWrapper<PaymentOrder> paymentQuery = new LambdaQueryWrapper<>();
        paymentQuery.eq(PaymentOrder::getBusinessType, "course")
                .eq(PaymentOrder::getBusinessNo, courseId.toString())
                .eq(PaymentOrder::getStatus, 2) // 2表示支付成功
                .between(PaymentOrder::getCreateTime, startDateTime, endDateTime);
        List<PaymentOrder> payments = paymentOrderMapper.selectList(paymentQuery);

        BigDecimal revenue = payments.stream()
                .map(PaymentOrder::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        analysis.setRevenue(revenue);

        // 6. 计算转化率和完成率
        if (analysis.getViewUserCount() > 0) {
            BigDecimal conversionRate = BigDecimal.valueOf(analysis.getEnrollUserCount())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(analysis.getViewUserCount()), 2, RoundingMode.HALF_UP);
            analysis.setConversionRate(conversionRate);
        } else {
            analysis.setConversionRate(BigDecimal.ZERO);
        }

        if (analysis.getEnrollCount() > 0) {
            BigDecimal completionRate = BigDecimal.valueOf(analysis.getCompletionCount())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(analysis.getEnrollCount()), 2, RoundingMode.HALF_UP);
            analysis.setCompletionRate(completionRate);
        } else {
            analysis.setCompletionRate(BigDecimal.ZERO);
        }

        // 7. 计算热度评分(0-100)
        int popularityScore = calculatePopularityScore(analysis);
        analysis.setPopularityScore(popularityScore);

        return analysis;
    }

    /**
     * 计算热度评分
     * 算法:
     * - 浏览量(20%): viewUserCount * 0.5, 上限20分
     * - 报名量(30%): enrollCount * 3, 上限30分
     * - 完成率(20%): completionRate * 0.2, 上限20分
     * - 评分(15%): avgRating * 3, 上限15分
     * - 收入(15%): revenue / 100, 上限15分
     */
    private int calculatePopularityScore(CoursePopularityAnalysis analysis) {
        int score = 0;

        // 浏览量贡献(最高20分)
        score += Math.min(analysis.getViewUserCount() * 0.5, 20);

        // 报名量贡献(最高30分)
        score += Math.min(analysis.getEnrollCount() * 3, 30);

        // 完成率贡献(最高20分)
        score += Math.min(analysis.getCompletionRate().doubleValue() * 0.2, 20);

        // 评分贡献(最高15分)
        score += Math.min(analysis.getAvgRating().doubleValue() * 3, 15);

        // 收入贡献(最高15分)
        score += Math.min(analysis.getRevenue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).doubleValue(), 15);

        return Math.min(score, 100);
    }

    /**
     * 计算排名
     */
    private void calculateRankings(List<CoursePopularityAnalysis> analyses) {
        // 按热度评分降序排序
        analyses.sort((a, b) -> b.getPopularityScore().compareTo(a.getPopularityScore()));

        // 分配排名
        for (int i = 0; i < analyses.size(); i++) {
            analyses.get(i).setRanking(i + 1);
        }
    }

    /**
     * 计算趋势
     */
    private void calculateTrend(CoursePopularityAnalysis current) {
        // 获取前一天的分析数据
        LocalDate previousDate = current.getAnalysisDate().minusDays(1);
        LambdaQueryWrapper<CoursePopularityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(CoursePopularityAnalysis::getCourseId, current.getCourseId())
                .eq(CoursePopularityAnalysis::getAnalysisDate, previousDate);
        CoursePopularityAnalysis previous = popularityAnalysisMapper.selectOne(query);

        if (previous != null) {
            int scoreDiff = current.getPopularityScore() - previous.getPopularityScore();
            current.setTrendChange(scoreDiff);

            if (scoreDiff > 5) {
                current.setTrend("up");
            } else if (scoreDiff < -5) {
                current.setTrend("down");
            } else {
                current.setTrend("stable");
            }
        } else {
            current.setTrend("stable");
            current.setTrendChange(0);
        }
    }

    @Override
    public List<Map<String, Object>> getCoursePopularityRanking(LocalDate startDate, LocalDate endDate, Integer limit) {
        log.info("查询课程热度排行榜: startDate={}, endDate={}, limit={}", startDate, endDate, limit);

        // 查询时间范围内的所有分析数据
        LambdaQueryWrapper<CoursePopularityAnalysis> query = new LambdaQueryWrapper<>();
        query.between(CoursePopularityAnalysis::getAnalysisDate, startDate, endDate);
        List<CoursePopularityAnalysis> analyses = popularityAnalysisMapper.selectList(query);

        // 按课程ID分组,计算平均热度
        Map<Long, Double> avgScores = analyses.stream()
                .collect(Collectors.groupingBy(
                        CoursePopularityAnalysis::getCourseId,
                        Collectors.averagingInt(CoursePopularityAnalysis::getPopularityScore)
                ));

        // 排序并取前N个
        List<Map<String, Object>> ranking = avgScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit != null ? limit : 10)
                .map(entry -> {
                    Course course = courseMapper.selectById(entry.getKey());
                    Map<String, Object> item = new HashMap<>();
                    item.put("courseId", entry.getKey());
                    item.put("courseName", course != null ? course.getCourseName() : "未知课程");
                    item.put("avgPopularityScore", BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP));

                    // 获取最新的分析数据
                    Optional<CoursePopularityAnalysis> latest = analyses.stream()
                            .filter(a -> a.getCourseId().equals(entry.getKey()))
                            .max(Comparator.comparing(CoursePopularityAnalysis::getAnalysisDate));

                    latest.ifPresent(analysis -> {
                        item.put("trend", analysis.getTrend());
                        item.put("trendChange", analysis.getTrendChange());
                        item.put("enrollCount", analysis.getEnrollCount());
                        item.put("viewCount", analysis.getViewCount());
                    });

                    return item;
                })
                .collect(Collectors.toList());

        return ranking;
    }

    @Override
    public List<Map<String, Object>> getCoursePopularityTrend(Long courseId, LocalDate startDate, LocalDate endDate) {
        log.info("查询课程热度趋势: courseId={}, startDate={}, endDate={}", courseId, startDate, endDate);

        LambdaQueryWrapper<CoursePopularityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(CoursePopularityAnalysis::getCourseId, courseId)
                .between(CoursePopularityAnalysis::getAnalysisDate, startDate, endDate)
                .orderByAsc(CoursePopularityAnalysis::getAnalysisDate);
        List<CoursePopularityAnalysis> analyses = popularityAnalysisMapper.selectList(query);

        return analyses.stream().map(analysis -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", analysis.getAnalysisDate().toString());
            item.put("popularityScore", analysis.getPopularityScore());
            item.put("viewCount", analysis.getViewCount());
            item.put("enrollCount", analysis.getEnrollCount());
            item.put("revenue", analysis.getRevenue());
            item.put("conversionRate", analysis.getConversionRate());
            item.put("trend", analysis.getTrend());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public CoursePopularityAnalysis getCoursePopularityDetail(Long courseId, LocalDate analysisDate) {
        log.info("查询课程热度详情: courseId={}, analysisDate={}", courseId, analysisDate);

        LambdaQueryWrapper<CoursePopularityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(CoursePopularityAnalysis::getCourseId, courseId)
                .eq(CoursePopularityAnalysis::getAnalysisDate, analysisDate);

        return popularityAnalysisMapper.selectOne(query);
    }

    @Override
    public List<Map<String, Object>> getHotCourses(Integer minScore) {
        log.info("查询热门课程: minScore={}", minScore);

        // 获取最近一天的分析数据
        LambdaQueryWrapper<CoursePopularityAnalysis> dateQuery = new LambdaQueryWrapper<>();
        dateQuery.orderByDesc(CoursePopularityAnalysis::getAnalysisDate)
                .last("LIMIT 1");
        CoursePopularityAnalysis latestAnalysis = popularityAnalysisMapper.selectOne(dateQuery);

        if (latestAnalysis == null) {
            return new ArrayList<>();
        }

        LocalDate latestDate = latestAnalysis.getAnalysisDate();

        LambdaQueryWrapper<CoursePopularityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(CoursePopularityAnalysis::getAnalysisDate, latestDate)
                .ge(CoursePopularityAnalysis::getPopularityScore, minScore)
                .orderByDesc(CoursePopularityAnalysis::getPopularityScore);
        List<CoursePopularityAnalysis> analyses = popularityAnalysisMapper.selectList(query);

        return analyses.stream().map(analysis -> {
            Course course = courseMapper.selectById(analysis.getCourseId());
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", analysis.getCourseId());
            item.put("courseName", course != null ? course.getCourseName() : "未知课程");
            item.put("popularityScore", analysis.getPopularityScore());
            item.put("ranking", analysis.getRanking());
            item.put("trend", analysis.getTrend());
            item.put("enrollCount", analysis.getEnrollCount());
            item.put("viewCount", analysis.getViewCount());
            item.put("avgRating", analysis.getAvgRating());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getColdCourses(Integer maxScore) {
        log.info("查询冷门课程: maxScore={}", maxScore);

        // 获取最近一天的分析数据
        LambdaQueryWrapper<CoursePopularityAnalysis> dateQuery = new LambdaQueryWrapper<>();
        dateQuery.orderByDesc(CoursePopularityAnalysis::getAnalysisDate)
                .last("LIMIT 1");
        CoursePopularityAnalysis latestAnalysis = popularityAnalysisMapper.selectOne(dateQuery);

        if (latestAnalysis == null) {
            return new ArrayList<>();
        }

        LocalDate latestDate = latestAnalysis.getAnalysisDate();

        LambdaQueryWrapper<CoursePopularityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(CoursePopularityAnalysis::getAnalysisDate, latestDate)
                .le(CoursePopularityAnalysis::getPopularityScore, maxScore)
                .orderByAsc(CoursePopularityAnalysis::getPopularityScore);
        List<CoursePopularityAnalysis> analyses = popularityAnalysisMapper.selectList(query);

        return analyses.stream().map(analysis -> {
            Course course = courseMapper.selectById(analysis.getCourseId());
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", analysis.getCourseId());
            item.put("courseName", course != null ? course.getCourseName() : "未知课程");
            item.put("popularityScore", analysis.getPopularityScore());
            item.put("enrollCount", analysis.getEnrollCount());
            item.put("viewCount", analysis.getViewCount());
            item.put("conversionRate", analysis.getConversionRate());
            item.put("suggestions", generateImprovementSuggestions(analysis));
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getCourseConversionAnalysis(LocalDate startDate, LocalDate endDate) {
        log.info("查询课程转化率分析: startDate={}, endDate={}", startDate, endDate);

        LambdaQueryWrapper<CoursePopularityAnalysis> query = new LambdaQueryWrapper<>();
        query.between(CoursePopularityAnalysis::getAnalysisDate, startDate, endDate);
        List<CoursePopularityAnalysis> analyses = popularityAnalysisMapper.selectList(query);

        // 按课程ID分组
        Map<Long, List<CoursePopularityAnalysis>> groupedByCourse = analyses.stream()
                .collect(Collectors.groupingBy(CoursePopularityAnalysis::getCourseId));

        return groupedByCourse.entrySet().stream().map(entry -> {
            Long courseId = entry.getKey();
            List<CoursePopularityAnalysis> courseAnalyses = entry.getValue();

            Course course = courseMapper.selectById(courseId);

            // 计算平均转化率
            double avgConversion = courseAnalyses.stream()
                    .mapToDouble(a -> a.getConversionRate().doubleValue())
                    .average()
                    .orElse(0.0);

            // 计算平均完成率
            double avgCompletion = courseAnalyses.stream()
                    .mapToDouble(a -> a.getCompletionRate().doubleValue())
                    .average()
                    .orElse(0.0);

            // 统计总数
            int totalViews = courseAnalyses.stream().mapToInt(CoursePopularityAnalysis::getViewCount).sum();
            int totalEnrolls = courseAnalyses.stream().mapToInt(CoursePopularityAnalysis::getEnrollCount).sum();

            Map<String, Object> item = new HashMap<>();
            item.put("courseId", courseId);
            item.put("courseName", course != null ? course.getCourseName() : "未知课程");
            item.put("avgConversionRate", BigDecimal.valueOf(avgConversion).setScale(2, RoundingMode.HALF_UP));
            item.put("avgCompletionRate", BigDecimal.valueOf(avgCompletion).setScale(2, RoundingMode.HALF_UP));
            item.put("totalViews", totalViews);
            item.put("totalEnrolls", totalEnrolls);
            item.put("conversionLevel", getConversionLevel(avgConversion));

            return item;
        })
        .sorted((a, b) -> {
            BigDecimal convA = (BigDecimal) a.get("avgConversionRate");
            BigDecimal convB = (BigDecimal) b.get("avgConversionRate");
            return convB.compareTo(convA);
        })
        .collect(Collectors.toList());
    }

    /**
     * 生成改进建议
     */
    private String generateImprovementSuggestions(CoursePopularityAnalysis analysis) {
        List<String> suggestions = new ArrayList<>();

        if (analysis.getViewCount() < 10) {
            suggestions.add("增加课程曝光度和宣传");
        }
        if (analysis.getConversionRate().compareTo(BigDecimal.valueOf(10)) < 0) {
            suggestions.add("优化课程介绍和详情页");
        }
        if (analysis.getCompletionRate().compareTo(BigDecimal.valueOf(50)) < 0) {
            suggestions.add("提升课程内容质量和趣味性");
        }
        if (analysis.getAvgRating().compareTo(BigDecimal.valueOf(3.5)) < 0) {
            suggestions.add("改进教学方法和课程体验");
        }

        return suggestions.isEmpty() ? "暂无建议" : String.join("; ", suggestions);
    }

    /**
     * 获取转化率等级
     */
    private String getConversionLevel(double conversionRate) {
        if (conversionRate >= 20) {
            return "优秀";
        } else if (conversionRate >= 10) {
            return "良好";
        } else if (conversionRate >= 5) {
            return "一般";
        } else {
            return "较低";
        }
    }
}
