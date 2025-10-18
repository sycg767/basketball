package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.entity.*;
import com.basketball.mapper.*;
import com.basketball.service.IVenueUsageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场地使用分析服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Service
public class VenueUsageServiceImpl implements IVenueUsageService {

    @Resource
    private VenueUsageAnalysisMapper usageAnalysisMapper;

    @Resource
    private VenueMapper venueMapper;

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private PaymentOrderMapper paymentOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void analyzeDailyVenueUsage(LocalDate analysisDate) {
        log.info("开始分析场地使用情况: analysisDate={}", analysisDate);

        LocalDateTime startDateTime = analysisDate.atStartOfDay();
        LocalDateTime endDateTime = analysisDate.plusDays(1).atStartOfDay();

        // 获取所有有效场地
        LambdaQueryWrapper<Venue> venueQuery = new LambdaQueryWrapper<>();
        venueQuery.eq(Venue::getStatus, 1); // 只分析正常开放的场地
        List<Venue> venues = venueMapper.selectList(venueQuery);

        List<VenueUsageAnalysis> analysisResults = new ArrayList<>();

        for (Venue venue : venues) {
            try {
                VenueUsageAnalysis analysis = analyzeSingleVenue(venue.getId(), analysisDate, startDateTime, endDateTime);
                analysisResults.add(analysis);
            } catch (Exception e) {
                log.error("分析场地使用情况失败: venueId={}", venue.getId(), e);
            }
        }

        // 计算趋势
        for (VenueUsageAnalysis analysis : analysisResults) {
            calculateTrend(analysis);
            generateSuggestion(analysis);
        }

        // 保存分析结果
        for (VenueUsageAnalysis analysis : analysisResults) {
            // 检查是否已存在当天的分析记录
            LambdaQueryWrapper<VenueUsageAnalysis> existQuery = new LambdaQueryWrapper<>();
            existQuery.eq(VenueUsageAnalysis::getVenueId, analysis.getVenueId())
                    .eq(VenueUsageAnalysis::getAnalysisDate, analysisDate);
            VenueUsageAnalysis existing = usageAnalysisMapper.selectOne(existQuery);

            if (existing != null) {
                analysis.setId(existing.getId());
                usageAnalysisMapper.updateById(analysis);
            } else {
                usageAnalysisMapper.insert(analysis);
            }
        }

        log.info("场地使用分析完成: analysisDate={}, venueCount={}", analysisDate, analysisResults.size());
    }

    /**
     * 分析单个场地
     */
    private VenueUsageAnalysis analyzeSingleVenue(Long venueId, LocalDate analysisDate,
                                                   LocalDateTime startDateTime, LocalDateTime endDateTime) {
        VenueUsageAnalysis analysis = new VenueUsageAnalysis();
        analysis.setVenueId(venueId);
        analysis.setAnalysisDate(analysisDate);

        // 1. 统计预订数据
        LambdaQueryWrapper<Booking> bookingQuery = new LambdaQueryWrapper<>();
        bookingQuery.eq(Booking::getVenueId, venueId)
                .between(Booking::getCreateTime, startDateTime, endDateTime);
        List<Booking> bookings = bookingMapper.selectList(bookingQuery);

        analysis.setTotalBookings(bookings.size());

        // 统计成功预订(status=1表示正常)
        long successCount = bookings.stream().filter(b -> b.getStatus() == 1).count();
        analysis.setSuccessBookings((int) successCount);

        // 统计取消预订(status=0表示已取消)
        long cancelledCount = bookings.stream().filter(b -> b.getStatus() == 0).count();
        analysis.setCancelledBookings((int) cancelledCount);

        // 统计预订人数(去重)
        Set<Long> uniqueUsers = bookings.stream().map(Booking::getUserId).collect(Collectors.toSet());
        analysis.setUniqueUsers(uniqueUsers.size());

        // 2. 计算预订转化率(成功预订/总预订)
        if (analysis.getTotalBookings() > 0) {
            BigDecimal conversionRate = BigDecimal.valueOf(analysis.getSuccessBookings())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(analysis.getTotalBookings()), 2, RoundingMode.HALF_UP);
            analysis.setBookingConversionRate(conversionRate);
        } else {
            analysis.setBookingConversionRate(BigDecimal.ZERO);
        }

        // 3. 计算场地使用率(假设每天有10个可用时段)
        int availableSlots = 10; // 假设每天10个时段(9:00-21:00,每2小时一个时段)
        analysis.setAvailableSlots(availableSlots);
        analysis.setBookedSlots(analysis.getSuccessBookings());

        BigDecimal usageRate = BigDecimal.valueOf(analysis.getBookedSlots())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(availableSlots), 2, RoundingMode.HALF_UP);
        analysis.setUsageRate(usageRate);

        // 4. 统计收入数据
        LambdaQueryWrapper<PaymentOrder> paymentQuery = new LambdaQueryWrapper<>();
        paymentQuery.eq(PaymentOrder::getBusinessType, "booking")
                .eq(PaymentOrder::getStatus, 2) // 2表示支付成功
                .between(PaymentOrder::getCreateTime, startDateTime, endDateTime);
        List<PaymentOrder> payments = paymentOrderMapper.selectList(paymentQuery);

        // 过滤出该场地的支付记录(通过关联booking)
        List<String> bookingNos = bookings.stream()
                .filter(b -> b.getStatus() == 1)
                .map(Booking::getBookingNo)
                .collect(Collectors.toList());

        BigDecimal revenue = payments.stream()
                .filter(p -> bookingNos.contains(p.getBusinessNo()))
                .map(PaymentOrder::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        analysis.setRevenue(revenue);

        // 5. 计算平均客单价
        if (analysis.getSuccessBookings() > 0) {
            BigDecimal avgAmount = revenue.divide(BigDecimal.valueOf(analysis.getSuccessBookings()), 2, RoundingMode.HALF_UP);
            analysis.setAvgOrderAmount(avgAmount);
        } else {
            analysis.setAvgOrderAmount(BigDecimal.ZERO);
        }

        // 6. 分析高峰时段
        Map<String, Integer> periodBookings = analyzePeakPeriod(bookings);
        String peakPeriod = periodBookings.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
        analysis.setPeakPeriod(peakPeriod);
        analysis.setPeakBookings(periodBookings.getOrDefault(peakPeriod, 0));

        // 7. 场地评分暂不统计(Booking表暂无rating字段)
        analysis.setVenueRating(BigDecimal.ZERO);
        analysis.setRatingCount(0);

        // 8. 计算使用得分(0-100)
        int usageScore = calculateUsageScore(analysis);
        analysis.setUsageScore(usageScore);

        return analysis;
    }

    /**
     * 分析高峰时段
     */
    private Map<String, Integer> analyzePeakPeriod(List<Booking> bookings) {
        Map<String, Integer> periodCounts = new HashMap<>();
        periodCounts.put("morning", 0);    // 6:00-12:00
        periodCounts.put("afternoon", 0);  // 12:00-18:00
        periodCounts.put("evening", 0);    // 18:00-24:00

        for (Booking booking : bookings) {
            if (booking.getStatus() != 1) continue; // 只统计成功预订

            LocalTime startTime = booking.getStartTime();
            String period = getPeriodByTime(startTime);
            periodCounts.put(period, periodCounts.get(period) + 1);
        }

        return periodCounts;
    }

    /**
     * 根据时间获取时段
     */
    private String getPeriodByTime(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 6 && hour < 12) {
            return "morning";
        } else if (hour >= 12 && hour < 18) {
            return "afternoon";
        } else {
            return "evening";
        }
    }

    /**
     * 计算使用得分
     * 算法(评分功能暂未实现,调整为三项评分):
     * - 使用率(50%): usageRate * 0.5, 上限50分
     * - 预订转化率(25%): conversionRate * 0.25, 上限25分
     * - 收入贡献(25%): revenue / 40, 上限25分
     */
    private int calculateUsageScore(VenueUsageAnalysis analysis) {
        int score = 0;

        // 使用率贡献(最高50分)
        score += Math.min(analysis.getUsageRate().doubleValue() * 0.5, 50);

        // 预订转化率贡献(最高25分)
        score += Math.min(analysis.getBookingConversionRate().doubleValue() * 0.25, 25);

        // 收入贡献(最高25分)
        score += Math.min(analysis.getRevenue().divide(BigDecimal.valueOf(40), 2, RoundingMode.HALF_UP).doubleValue(), 25);

        return Math.min(score, 100);
    }

    /**
     * 计算趋势
     */
    private void calculateTrend(VenueUsageAnalysis current) {
        // 获取前一天的分析数据
        LocalDate previousDate = current.getAnalysisDate().minusDays(1);
        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(VenueUsageAnalysis::getVenueId, current.getVenueId())
                .eq(VenueUsageAnalysis::getAnalysisDate, previousDate);
        VenueUsageAnalysis previous = usageAnalysisMapper.selectOne(query);

        if (previous != null) {
            int scoreDiff = current.getUsageScore() - previous.getUsageScore();
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

    /**
     * 生成运营建议
     */
    private void generateSuggestion(VenueUsageAnalysis analysis) {
        List<String> suggestions = new ArrayList<>();

        if (analysis.getUsageRate().compareTo(BigDecimal.valueOf(30)) < 0) {
            suggestions.add("使用率较低,建议增加营销推广或调整价格策略");
        }

        if (analysis.getBookingConversionRate().compareTo(BigDecimal.valueOf(60)) < 0) {
            suggestions.add("预订转化率偏低,建议优化预订流程或提供优惠");
        }

        if (analysis.getCancelledBookings() > analysis.getSuccessBookings() / 2) {
            suggestions.add("取消率较高,建议分析取消原因并改进服务");
        }

        if ("evening".equals(analysis.getPeakPeriod())) {
            suggestions.add("晚间时段最受欢迎,可考虑增加晚间时段供应");
        } else if ("morning".equals(analysis.getPeakPeriod())) {
            suggestions.add("上午时段最受欢迎,可考虑推出早鸟优惠");
        }

        if (suggestions.isEmpty()) {
            analysis.setSuggestion("场地运营良好,继续保持");
        } else {
            analysis.setSuggestion(String.join("; ", suggestions));
        }
    }

    @Override
    public List<Map<String, Object>> getVenueUsageRanking(LocalDate startDate, LocalDate endDate, Integer limit) {
        log.info("查询场地使用率排行: startDate={}, endDate={}, limit={}", startDate, endDate, limit);

        // 查询时间范围内的所有分析数据
        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.between(VenueUsageAnalysis::getAnalysisDate, startDate, endDate);
        List<VenueUsageAnalysis> analyses = usageAnalysisMapper.selectList(query);

        // 按场地ID分组,计算平均使用率
        Map<Long, Double> avgUsageRates = analyses.stream()
                .collect(Collectors.groupingBy(
                        VenueUsageAnalysis::getVenueId,
                        Collectors.averagingDouble(a -> a.getUsageRate().doubleValue())
                ));

        // 排序并取前N个
        List<Map<String, Object>> ranking = avgUsageRates.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit != null ? limit : 10)
                .map(entry -> {
                    Venue venue = venueMapper.selectById(entry.getKey());
                    Map<String, Object> item = new HashMap<>();
                    item.put("venueId", entry.getKey());
                    item.put("venueName", venue != null ? venue.getVenueName() : "未知场地");
                    item.put("avgUsageRate", BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP));

                    // 获取最新的分析数据
                    Optional<VenueUsageAnalysis> latest = analyses.stream()
                            .filter(a -> a.getVenueId().equals(entry.getKey()))
                            .max(Comparator.comparing(VenueUsageAnalysis::getAnalysisDate));

                    latest.ifPresent(analysis -> {
                        item.put("trend", analysis.getTrend());
                        item.put("usageScore", analysis.getUsageScore());
                        item.put("revenue", analysis.getRevenue());
                    });

                    return item;
                })
                .collect(Collectors.toList());

        return ranking;
    }

    @Override
    public List<Map<String, Object>> getVenueUsageTrend(Long venueId, LocalDate startDate, LocalDate endDate) {
        log.info("查询场地使用趋势: venueId={}, startDate={}, endDate={}", venueId, startDate, endDate);

        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(VenueUsageAnalysis::getVenueId, venueId)
                .between(VenueUsageAnalysis::getAnalysisDate, startDate, endDate)
                .orderByAsc(VenueUsageAnalysis::getAnalysisDate);
        List<VenueUsageAnalysis> analyses = usageAnalysisMapper.selectList(query);

        return analyses.stream().map(analysis -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", analysis.getAnalysisDate().toString());
            item.put("usageRate", analysis.getUsageRate());
            item.put("bookedSlots", analysis.getBookedSlots());
            item.put("revenue", analysis.getRevenue());
            item.put("usageScore", analysis.getUsageScore());
            item.put("trend", analysis.getTrend());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public VenueUsageAnalysis getVenueUsageDetail(Long venueId, LocalDate analysisDate) {
        log.info("查询场地使用详情: venueId={}, analysisDate={}", venueId, analysisDate);

        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(VenueUsageAnalysis::getVenueId, venueId)
                .eq(VenueUsageAnalysis::getAnalysisDate, analysisDate);

        return usageAnalysisMapper.selectOne(query);
    }

    @Override
    public List<Map<String, Object>> getHighUsageVenues(Integer minRate) {
        log.info("查询高使用率场地: minRate={}", minRate);

        // 获取最近一天的分析数据
        LambdaQueryWrapper<VenueUsageAnalysis> dateQuery = new LambdaQueryWrapper<>();
        dateQuery.orderByDesc(VenueUsageAnalysis::getAnalysisDate)
                .last("LIMIT 1");
        VenueUsageAnalysis latestAnalysis = usageAnalysisMapper.selectOne(dateQuery);

        if (latestAnalysis == null) {
            return new ArrayList<>();
        }

        LocalDate latestDate = latestAnalysis.getAnalysisDate();

        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(VenueUsageAnalysis::getAnalysisDate, latestDate)
                .ge(VenueUsageAnalysis::getUsageRate, minRate)
                .orderByDesc(VenueUsageAnalysis::getUsageRate);
        List<VenueUsageAnalysis> analyses = usageAnalysisMapper.selectList(query);

        return analyses.stream().map(analysis -> {
            Venue venue = venueMapper.selectById(analysis.getVenueId());
            Map<String, Object> item = new HashMap<>();
            item.put("venueId", analysis.getVenueId());
            item.put("venueName", venue != null ? venue.getVenueName() : "未知场地");
            item.put("usageRate", analysis.getUsageRate());
            item.put("usageScore", analysis.getUsageScore());
            item.put("revenue", analysis.getRevenue());
            item.put("peakPeriod", analysis.getPeakPeriod());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getLowUsageVenues(Integer maxRate) {
        log.info("查询低使用率场地: maxRate={}", maxRate);

        // 获取最近一天的分析数据
        LambdaQueryWrapper<VenueUsageAnalysis> dateQuery = new LambdaQueryWrapper<>();
        dateQuery.orderByDesc(VenueUsageAnalysis::getAnalysisDate)
                .last("LIMIT 1");
        VenueUsageAnalysis latestAnalysis = usageAnalysisMapper.selectOne(dateQuery);

        if (latestAnalysis == null) {
            return new ArrayList<>();
        }

        LocalDate latestDate = latestAnalysis.getAnalysisDate();

        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.eq(VenueUsageAnalysis::getAnalysisDate, latestDate)
                .le(VenueUsageAnalysis::getUsageRate, maxRate)
                .orderByAsc(VenueUsageAnalysis::getUsageRate);
        List<VenueUsageAnalysis> analyses = usageAnalysisMapper.selectList(query);

        return analyses.stream().map(analysis -> {
            Venue venue = venueMapper.selectById(analysis.getVenueId());
            Map<String, Object> item = new HashMap<>();
            item.put("venueId", analysis.getVenueId());
            item.put("venueName", venue != null ? venue.getVenueName() : "未知场地");
            item.put("usageRate", analysis.getUsageRate());
            item.put("bookingConversionRate", analysis.getBookingConversionRate());
            item.put("suggestion", analysis.getSuggestion());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getVenueRevenueAnalysis(LocalDate startDate, LocalDate endDate) {
        log.info("查询场地收入分析: startDate={}, endDate={}", startDate, endDate);

        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.between(VenueUsageAnalysis::getAnalysisDate, startDate, endDate);
        List<VenueUsageAnalysis> analyses = usageAnalysisMapper.selectList(query);

        // 按场地ID分组
        Map<Long, List<VenueUsageAnalysis>> groupedByVenue = analyses.stream()
                .collect(Collectors.groupingBy(VenueUsageAnalysis::getVenueId));

        return groupedByVenue.entrySet().stream().map(entry -> {
            Long venueId = entry.getKey();
            List<VenueUsageAnalysis> venueAnalyses = entry.getValue();

            Venue venue = venueMapper.selectById(venueId);

            // 统计总收入
            BigDecimal totalRevenue = venueAnalyses.stream()
                    .map(VenueUsageAnalysis::getRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 计算平均客单价
            double avgOrderAmount = venueAnalyses.stream()
                    .mapToDouble(a -> a.getAvgOrderAmount().doubleValue())
                    .average()
                    .orElse(0.0);

            // 统计总预订数
            int totalBookings = venueAnalyses.stream()
                    .mapToInt(VenueUsageAnalysis::getSuccessBookings)
                    .sum();

            Map<String, Object> item = new HashMap<>();
            item.put("venueId", venueId);
            item.put("venueName", venue != null ? venue.getVenueName() : "未知场地");
            item.put("totalRevenue", totalRevenue);
            item.put("avgOrderAmount", BigDecimal.valueOf(avgOrderAmount).setScale(2, RoundingMode.HALF_UP));
            item.put("totalBookings", totalBookings);
            item.put("revenuePerBooking", totalBookings > 0 ?
                    totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO);

            return item;
        })
        .sorted((a, b) -> {
            BigDecimal revA = (BigDecimal) a.get("totalRevenue");
            BigDecimal revB = (BigDecimal) b.get("totalRevenue");
            return revB.compareTo(revA);
        })
        .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getPeakPeriodAnalysis(Long venueId, LocalDate startDate, LocalDate endDate) {
        log.info("查询高峰时段分析: venueId={}, startDate={}, endDate={}", venueId, startDate, endDate);

        LambdaQueryWrapper<VenueUsageAnalysis> query = new LambdaQueryWrapper<>();
        query.between(VenueUsageAnalysis::getAnalysisDate, startDate, endDate);

        if (venueId != null) {
            query.eq(VenueUsageAnalysis::getVenueId, venueId);
        }

        List<VenueUsageAnalysis> analyses = usageAnalysisMapper.selectList(query);

        // 统计各时段的预订数
        Map<String, Integer> periodCounts = new HashMap<>();
        periodCounts.put("morning", 0);
        periodCounts.put("afternoon", 0);
        periodCounts.put("evening", 0);

        for (VenueUsageAnalysis analysis : analyses) {
            String period = analysis.getPeakPeriod();
            if (period != null && !period.equals("unknown")) {
                periodCounts.put(period, periodCounts.get(period) + analysis.getPeakBookings());
            }
        }

        // 转换为结果列表
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : periodCounts.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("period", entry.getKey());
            item.put("periodName", getPeriodName(entry.getKey()));
            item.put("bookingCount", entry.getValue());

            int totalBookings = periodCounts.values().stream().mapToInt(Integer::intValue).sum();
            BigDecimal percentage = totalBookings > 0 ?
                    BigDecimal.valueOf(entry.getValue())
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;
            item.put("percentage", percentage);

            result.add(item);
        }

        // 按预订数降序排序
        result.sort((a, b) -> {
            Integer countA = (Integer) a.get("bookingCount");
            Integer countB = (Integer) b.get("bookingCount");
            return countB.compareTo(countA);
        });

        return result;
    }

    /**
     * 获取时段中文名称
     */
    private String getPeriodName(String period) {
        switch (period) {
            case "morning":
                return "上午(6:00-12:00)";
            case "afternoon":
                return "下午(12:00-18:00)";
            case "evening":
                return "晚上(18:00-24:00)";
            default:
                return "未知";
        }
    }
}
