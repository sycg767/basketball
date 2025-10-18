package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.entity.Booking;
import com.basketball.entity.User;
import com.basketball.entity.Venue;
import com.basketball.mapper.BookingMapper;
import com.basketball.mapper.UserMapper;
import com.basketball.mapper.VenueMapper;
import com.basketball.service.IBookingService;
import com.basketball.service.IStatisticsService;
import com.basketball.vo.BookingVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {

    @Resource
    private VenueMapper venueMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private IBookingService bookingService;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 场地总数
        Long totalVenues = venueMapper.selectCount(new LambdaQueryWrapper<Venue>()
                .ne(Venue::getStatus, 3)); // 排除已删除
        stats.put("totalVenues", totalVenues);

        // 用户总数
        Long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1)); // 只统计启用用户
        stats.put("totalUsers", totalUsers);

        // 预订总数
        Long totalBookings = bookingMapper.selectCount(new LambdaQueryWrapper<Booking>());
        stats.put("totalBookings", totalBookings);

        // 总收入（已支付的订单）
        List<Booking> paidBookings = bookingMapper.selectList(new LambdaQueryWrapper<Booking>()
                .in(Booking::getStatus, Arrays.asList(1, 3))); // 已支付、已完成
        BigDecimal totalRevenue = paidBookings.stream()
                .map(Booking::getActualPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getBookingTrend(Integer days) {
        List<Map<String, Object>> trend = new ArrayList<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            Long count = bookingMapper.selectCount(new LambdaQueryWrapper<Booking>()
                    .eq(Booking::getBookingDate, date));

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(formatter));
            dayData.put("count", count);
            trend.add(dayData);
        }

        return trend;
    }

    @Override
    public List<Map<String, Object>> getVenueUsageRate() {
        List<Map<String, Object>> usageList = new ArrayList<>();

        // 获取所有可用场地
        List<Venue> venues = venueMapper.selectList(new LambdaQueryWrapper<Venue>()
                .eq(Venue::getStatus, 1));

        for (Venue venue : venues) {
            // 统计该场地的预订次数
            Long bookingCount = bookingMapper.selectCount(new LambdaQueryWrapper<Booking>()
                    .eq(Booking::getVenueId, venue.getId())
                    .in(Booking::getStatus, Arrays.asList(1, 3))); // 已支付、已完成

            Map<String, Object> data = new HashMap<>();
            data.put("venueName", venue.getVenueName());
            data.put("bookingCount", bookingCount);
            // 简化的使用率计算（实际应该根据时间段计算）
            data.put("usageRate", bookingCount > 0 ? Math.min(bookingCount * 5, 100) : 0);
            usageList.add(data);
        }

        return usageList;
    }

    @Override
    public List<BookingVO> getRecentBookings(Integer limit) {
        List<Booking> bookings = bookingMapper.selectList(new LambdaQueryWrapper<Booking>()
                .orderByDesc(Booking::getCreateTime)
                .last("LIMIT " + limit));

        return bookings.stream()
                .map(booking -> bookingService.getBookingDetail(booking.getId()))
                .collect(Collectors.toList());
    }
}
