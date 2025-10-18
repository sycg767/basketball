package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.entity.*;
import com.basketball.mapper.*;
import com.basketball.service.IMemberActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会员活跃度分析服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Service
public class MemberActivityServiceImpl implements IMemberActivityService {

    @Resource
    private MemberActivityAnalysisMapper activityAnalysisMapper;

    @Resource
    private UserBehaviorLogMapper behaviorLogMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private PaymentOrderMapper paymentOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void analyzeDailyActivity(LocalDate analysisDate) {
        log.info("开始分析会员活跃度: analysisDate={}", analysisDate);

        LocalDateTime startTime = analysisDate.atStartOfDay();
        LocalDateTime endTime = analysisDate.plusDays(1).atStartOfDay();

        // 查询所有用户
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<>());
        log.info("查询到{}个用户", users.size());

        int analyzedCount = 0;
        for (User user : users) {
            try {
                // 查询该用户在指定日期的行为日志
                LambdaQueryWrapper<UserBehaviorLog> logQuery = new LambdaQueryWrapper<>();
                logQuery.eq(UserBehaviorLog::getUserId, user.getId())
                        .between(UserBehaviorLog::getBehaviorTime, startTime, endTime);
                List<UserBehaviorLog> logs = behaviorLogMapper.selectList(logQuery);

                // 统计各类行为次数
                int loginCount = (int) logs.stream().filter(l -> "login".equals(l.getBehaviorType())).count();
                int viewCount = (int) logs.stream().filter(l -> "view".equals(l.getBehaviorType())).count();
                int bookingCount = (int) logs.stream().filter(l -> "booking".equals(l.getBehaviorType())).count();
                int paymentCount = (int) logs.stream().filter(l -> "payment".equals(l.getBehaviorType())).count();
                int cancelCount = (int) logs.stream().filter(l -> "cancel".equals(l.getBehaviorType())).count();

                // 查询该日期的支付金额
                LambdaQueryWrapper<PaymentOrder> paymentQuery = new LambdaQueryWrapper<>();
                paymentQuery.eq(PaymentOrder::getUserId, user.getId())
                        .eq(PaymentOrder::getStatus, 2) // 已支付
                        .between(PaymentOrder::getPaidTime, startTime, endTime);
                List<PaymentOrder> payments = paymentOrderMapper.selectList(paymentQuery);
                BigDecimal paymentAmount = payments.stream()
                        .map(PaymentOrder::getActualAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 获取最近活跃时间
                LocalDateTime lastActiveTime = logs.isEmpty() ? null :
                        logs.stream()
                                .map(UserBehaviorLog::getBehaviorTime)
                                .max(LocalDateTime::compareTo)
                                .orElse(null);

                // 计算连续活跃天数
                int continuousDays = calculateContinuousDays(user.getId(), analysisDate);

                // 计算活跃度得分 (0-100)
                int activityScore = calculateActivityScore(loginCount, viewCount, bookingCount, paymentCount);

                // 计算RFM得分
                Map<String, Object> rfm = calculateRFM(user.getId(), analysisDate);

                // 计算流失风险等级
                int churnRisk = calculateChurnRisk(user.getId(), analysisDate, loginCount, paymentCount);

                // 检查是否已有该日期的分析记录
                LambdaQueryWrapper<MemberActivityAnalysis> existQuery = new LambdaQueryWrapper<>();
                existQuery.eq(MemberActivityAnalysis::getUserId, user.getId())
                        .eq(MemberActivityAnalysis::getAnalysisDate, analysisDate);
                MemberActivityAnalysis existAnalysis = activityAnalysisMapper.selectOne(existQuery);

                MemberActivityAnalysis analysis = existAnalysis != null ? existAnalysis : new MemberActivityAnalysis();
                analysis.setUserId(user.getId());
                analysis.setAnalysisDate(analysisDate);
                analysis.setLoginCount(loginCount);
                analysis.setViewCount(viewCount);
                analysis.setBookingCount(bookingCount);
                analysis.setPaymentCount(paymentCount);
                analysis.setPaymentAmount(paymentAmount);
                analysis.setCancelCount(cancelCount);
                analysis.setActivityScore(activityScore);
                analysis.setLastActiveTime(lastActiveTime);
                analysis.setContinuousDays(continuousDays);
                analysis.setRfmR((Integer) rfm.get("R"));
                analysis.setRfmF((Integer) rfm.get("F"));
                analysis.setRfmM((BigDecimal) rfm.get("M"));
                analysis.setRfmScore((Integer) rfm.get("score"));
                analysis.setChurnRisk(churnRisk);
                analysis.setUpdateTime(LocalDateTime.now());

                if (existAnalysis != null) {
                    activityAnalysisMapper.updateById(analysis);
                } else {
                    analysis.setCreateTime(LocalDateTime.now());
                    activityAnalysisMapper.insert(analysis);
                }

                analyzedCount++;

            } catch (Exception e) {
                log.error("分析用户活跃度失败: userId={}", user.getId(), e);
            }
        }

        log.info("会员活跃度分析完成: analysisDate={}, 成功分析{}个用户", analysisDate, analyzedCount);
    }

    @Override
    public List<Map<String, Object>> identifyChurnRisk() {
        List<Map<String, Object>> riskUsers = new ArrayList<>();

        // 查询所有用户
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<>());

        for (User user : users) {
            try {
                // 查询最近的活跃度记录
                LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();
                query.eq(MemberActivityAnalysis::getUserId, user.getId())
                        .orderByDesc(MemberActivityAnalysis::getAnalysisDate)
                        .last("LIMIT 1");
                MemberActivityAnalysis latestAnalysis = activityAnalysisMapper.selectOne(query);

                if (latestAnalysis != null && latestAnalysis.getChurnRisk() >= 2) {
                    Map<String, Object> riskUser = new HashMap<>();
                    riskUser.put("userId", user.getId());
                    riskUser.put("username", user.getUsername());
                    riskUser.put("realName", user.getRealName());
                    riskUser.put("phone", user.getPhone());
                    riskUser.put("churnRisk", latestAnalysis.getChurnRisk());
                    riskUser.put("activityScore", latestAnalysis.getActivityScore());
                    riskUser.put("lastActiveTime", latestAnalysis.getLastActiveTime());
                    riskUser.put("analysisDate", latestAnalysis.getAnalysisDate());

                    riskUsers.add(riskUser);
                }
            } catch (Exception e) {
                log.error("识别用户流失风险失败: userId={}", user.getId(), e);
            }
        }

        // 按流失风险降序排序
        riskUsers.sort((a, b) -> ((Integer) b.get("churnRisk")).compareTo((Integer) a.get("churnRisk")));

        log.info("识别到{}个流失风险用户", riskUsers.size());
        return riskUsers;
    }

    @Override
    public List<Map<String, Object>> getActivityTrend(LocalDate startDate, LocalDate endDate, Long userId) {
        LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();

        if (userId != null) {
            query.eq(MemberActivityAnalysis::getUserId, userId);
        }

        query.between(MemberActivityAnalysis::getAnalysisDate, startDate, endDate)
                .orderByAsc(MemberActivityAnalysis::getAnalysisDate);

        List<MemberActivityAnalysis> analyses = activityAnalysisMapper.selectList(query);

        // 按日期分组统计
        Map<LocalDate, List<MemberActivityAnalysis>> dateGroup = analyses.stream()
                .collect(Collectors.groupingBy(MemberActivityAnalysis::getAnalysisDate));

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Map.Entry<LocalDate, List<MemberActivityAnalysis>> entry : dateGroup.entrySet()) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", entry.getKey());

            List<MemberActivityAnalysis> dayAnalyses = entry.getValue();
            int avgScore = (int) dayAnalyses.stream()
                    .mapToInt(MemberActivityAnalysis::getActivityScore)
                    .average()
                    .orElse(0);
            int totalLogin = dayAnalyses.stream().mapToInt(MemberActivityAnalysis::getLoginCount).sum();
            int totalBooking = dayAnalyses.stream().mapToInt(MemberActivityAnalysis::getBookingCount).sum();
            BigDecimal totalAmount = dayAnalyses.stream()
                    .map(MemberActivityAnalysis::getPaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dayData.put("avgActivityScore", avgScore);
            dayData.put("activeUserCount", dayAnalyses.size());
            dayData.put("totalLoginCount", totalLogin);
            dayData.put("totalBookingCount", totalBooking);
            dayData.put("totalPaymentAmount", totalAmount);

            trend.add(dayData);
        }

        // 按日期排序
        trend.sort((a, b) -> ((LocalDate) a.get("date")).compareTo((LocalDate) b.get("date")));

        return trend;
    }

    @Override
    public List<Map<String, Object>> getActiveUsers(Integer minScore) {
        // 查询最近7天的活跃度数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);

        LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();
        query.between(MemberActivityAnalysis::getAnalysisDate, startDate, endDate)
                .ge(MemberActivityAnalysis::getActivityScore, minScore)
                .orderByDesc(MemberActivityAnalysis::getActivityScore);

        List<MemberActivityAnalysis> analyses = activityAnalysisMapper.selectList(query);

        // 按用户ID分组,取最新的分析记录
        Map<Long, MemberActivityAnalysis> userLatest = new HashMap<>();
        for (MemberActivityAnalysis analysis : analyses) {
            if (!userLatest.containsKey(analysis.getUserId()) ||
                    userLatest.get(analysis.getUserId()).getAnalysisDate().isBefore(analysis.getAnalysisDate())) {
                userLatest.put(analysis.getUserId(), analysis);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (MemberActivityAnalysis analysis : userLatest.values()) {
            User user = userMapper.selectById(analysis.getUserId());
            if (user != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("userId", user.getId());
                item.put("username", user.getUsername());
                item.put("realName", user.getRealName());
                item.put("phone", user.getPhone());
                item.put("activityScore", analysis.getActivityScore());
                item.put("continuousDays", analysis.getContinuousDays());
                item.put("lastActiveTime", analysis.getLastActiveTime());

                result.add(item);
            }
        }

        // 按活跃度得分降序排序
        result.sort((a, b) -> ((Integer) b.get("activityScore")).compareTo((Integer) a.get("activityScore")));

        return result;
    }

    @Override
    public List<Map<String, Object>> getInactiveUsers(Integer maxScore) {
        // 查询最近7天的活跃度数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);

        LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();
        query.between(MemberActivityAnalysis::getAnalysisDate, startDate, endDate)
                .le(MemberActivityAnalysis::getActivityScore, maxScore)
                .orderByAsc(MemberActivityAnalysis::getActivityScore);

        List<MemberActivityAnalysis> analyses = activityAnalysisMapper.selectList(query);

        // 按用户ID分组,取最新的分析记录
        Map<Long, MemberActivityAnalysis> userLatest = new HashMap<>();
        for (MemberActivityAnalysis analysis : analyses) {
            if (!userLatest.containsKey(analysis.getUserId()) ||
                    userLatest.get(analysis.getUserId()).getAnalysisDate().isBefore(analysis.getAnalysisDate())) {
                userLatest.put(analysis.getUserId(), analysis);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (MemberActivityAnalysis analysis : userLatest.values()) {
            User user = userMapper.selectById(analysis.getUserId());
            if (user != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("userId", user.getId());
                item.put("username", user.getUsername());
                item.put("realName", user.getRealName());
                item.put("phone", user.getPhone());
                item.put("activityScore", analysis.getActivityScore());
                item.put("lastActiveTime", analysis.getLastActiveTime());

                result.add(item);
            }
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getChurnRiskUsers(Integer riskLevel) {
        // 查询最近的活跃度记录
        LocalDate recentDate = LocalDate.now().minusDays(1);

        LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(MemberActivityAnalysis::getAnalysisDate, recentDate)
                .eq(MemberActivityAnalysis::getChurnRisk, riskLevel)
                .orderByDesc(MemberActivityAnalysis::getChurnRisk);

        List<MemberActivityAnalysis> analyses = activityAnalysisMapper.selectList(query);

        List<Map<String, Object>> result = new ArrayList<>();
        for (MemberActivityAnalysis analysis : analyses) {
            User user = userMapper.selectById(analysis.getUserId());
            if (user != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("userId", user.getId());
                item.put("username", user.getUsername());
                item.put("realName", user.getRealName());
                item.put("phone", user.getPhone());
                item.put("churnRisk", analysis.getChurnRisk());
                item.put("activityScore", analysis.getActivityScore());
                item.put("lastActiveTime", analysis.getLastActiveTime());
                item.put("rfmScore", analysis.getRfmScore());

                result.add(item);
            }
        }

        return result;
    }

    @Override
    public MemberActivityAnalysis getUserActivityDetail(Long userId, LocalDate analysisDate) {
        LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(MemberActivityAnalysis::getUserId, userId)
                .eq(MemberActivityAnalysis::getAnalysisDate, analysisDate);

        return activityAnalysisMapper.selectOne(query);
    }

    /**
     * 计算连续活跃天数
     */
    private int calculateContinuousDays(Long userId, LocalDate currentDate) {
        int days = 0;
        LocalDate checkDate = currentDate;

        // 向前查询连续活跃的天数 (最多查询30天)
        for (int i = 0; i < 30; i++) {
            LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();
            query.eq(MemberActivityAnalysis::getUserId, userId)
                    .eq(MemberActivityAnalysis::getAnalysisDate, checkDate)
                    .gt(MemberActivityAnalysis::getActivityScore, 0);

            Long count = activityAnalysisMapper.selectCount(query);
            if (count > 0) {
                days++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return days;
    }

    /**
     * 计算活跃度得分 (0-100)
     */
    private int calculateActivityScore(int loginCount, int viewCount, int bookingCount, int paymentCount) {
        // 登录 10分, 浏览 20分, 预订 30分, 支付 40分
        int score = 0;

        score += Math.min(loginCount * 5, 10);     // 登录最多10分
        score += Math.min(viewCount * 2, 20);      // 浏览最多20分
        score += Math.min(bookingCount * 15, 30);  // 预订最多30分
        score += Math.min(paymentCount * 20, 40);  // 支付最多40分

        return Math.min(score, 100);
    }

    /**
     * 计算RFM得分
     */
    private Map<String, Object> calculateRFM(Long userId, LocalDate analysisDate) {
        Map<String, Object> rfm = new HashMap<>();

        // 查询最近一次支付时间 (R - Recency)
        LambdaQueryWrapper<PaymentOrder> recentPaymentQuery = new LambdaQueryWrapper<>();
        recentPaymentQuery.eq(PaymentOrder::getUserId, userId)
                .eq(PaymentOrder::getStatus, 2)
                .orderByDesc(PaymentOrder::getPaidTime)
                .last("LIMIT 1");
        PaymentOrder recentPayment = paymentOrderMapper.selectOne(recentPaymentQuery);

        int rValue = 0;
        if (recentPayment != null && recentPayment.getPaidTime() != null) {
            long daysSinceLastPayment = ChronoUnit.DAYS.between(
                    recentPayment.getPaidTime().toLocalDate(), analysisDate);
            rValue = (int) (100 - Math.min(daysSinceLastPayment, 100));
        }

        // 查询最近90天的消费频次 (F - Frequency)
        LocalDate ninetyDaysAgo = analysisDate.minusDays(90);
        LambdaQueryWrapper<PaymentOrder> frequencyQuery = new LambdaQueryWrapper<>();
        frequencyQuery.eq(PaymentOrder::getUserId, userId)
                .eq(PaymentOrder::getStatus, 2)
                .ge(PaymentOrder::getPaidTime, ninetyDaysAgo.atStartOfDay());
        int fValue = paymentOrderMapper.selectCount(frequencyQuery).intValue();

        // 查询最近90天的消费总额 (M - Monetary)
        List<PaymentOrder> recentPayments = paymentOrderMapper.selectList(frequencyQuery);
        BigDecimal mValue = recentPayments.stream()
                .map(PaymentOrder::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算RFM综合得分 (0-100)
        int rfmScore = (int) ((rValue * 0.3) + (Math.min(fValue * 10, 100) * 0.3) +
                (Math.min(mValue.intValue() / 10, 100) * 0.4));

        rfm.put("R", rValue);
        rfm.put("F", fValue);
        rfm.put("M", mValue);
        rfm.put("score", rfmScore);

        return rfm;
    }

    /**
     * 计算流失风险等级
     * 0-无风险, 1-低风险, 2-中风险, 3-高风险
     */
    private int calculateChurnRisk(Long userId, LocalDate analysisDate, int loginCount, int paymentCount) {
        // 查询最近30天的活跃记录
        LocalDate thirtyDaysAgo = analysisDate.minusDays(30);
        LambdaQueryWrapper<MemberActivityAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(MemberActivityAnalysis::getUserId, userId)
                .ge(MemberActivityAnalysis::getAnalysisDate, thirtyDaysAgo)
                .orderByDesc(MemberActivityAnalysis::getAnalysisDate);
        List<MemberActivityAnalysis> recentAnalyses = activityAnalysisMapper.selectList(query);

        if (recentAnalyses.isEmpty()) {
            return 0; // 新用户,无风险
        }

        // 计算平均活跃度得分
        double avgScore = recentAnalyses.stream()
                .mapToInt(MemberActivityAnalysis::getActivityScore)
                .average()
                .orElse(0);

        // 计算活跃天数
        int activeDays = (int) recentAnalyses.stream()
                .filter(a -> a.getActivityScore() > 0)
                .count();

        // 查询最后活跃时间
        LocalDateTime lastActiveTime = recentAnalyses.stream()
                .filter(a -> a.getLastActiveTime() != null)
                .map(MemberActivityAnalysis::getLastActiveTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        long daysSinceLastActive = 0;
        if (lastActiveTime != null) {
            daysSinceLastActive = ChronoUnit.DAYS.between(lastActiveTime.toLocalDate(), analysisDate);
        }

        // 判断流失风险
        if (daysSinceLastActive >= 30 || avgScore < 10) {
            return 3; // 高风险: 30天未活跃或平均得分极低
        } else if (daysSinceLastActive >= 14 || avgScore < 30) {
            return 2; // 中风险: 14天未活跃或平均得分较低
        } else if (daysSinceLastActive >= 7 || avgScore < 50) {
            return 1; // 低风险: 7天未活跃或平均得分一般
        } else {
            return 0; // 无风险
        }
    }
}
