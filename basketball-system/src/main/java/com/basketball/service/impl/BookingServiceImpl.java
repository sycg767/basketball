package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.exception.ErrorCode;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.BookingCancelDTO;
import com.basketball.dto.request.BookingCreateDTO;
import com.basketball.dto.request.BookingPayDTO;
import com.basketball.entity.Booking;
import com.basketball.entity.User;
import com.basketball.entity.Venue;
import com.basketball.entity.VenuePrice;
import com.basketball.entity.PointsRecord;
import com.basketball.entity.MemberCard;
import com.basketball.entity.MemberCardType;
import com.basketball.entity.MemberCardRecord;
import com.basketball.mapper.BookingMapper;
import com.basketball.mapper.PointsRecordMapper;
import com.basketball.mapper.UserMapper;
import com.basketball.mapper.VenueMapper;
import com.basketball.mapper.VenuePriceMapper;
import com.basketball.mapper.MemberCardMapper;
import com.basketball.mapper.MemberCardTypeMapper;
import com.basketball.mapper.MemberCardRecordMapper;
import com.basketball.service.IBookingService;
import com.basketball.service.INotificationService;
import com.basketball.service.IPaymentService;
import com.basketball.dto.request.NotificationSendDTO;
import com.basketball.dto.request.PaymentCreateDTO;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.vo.BookingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 预订服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Slf4j
@Service
public class BookingServiceImpl extends ServiceImpl<BookingMapper, Booking> implements IBookingService {

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private VenueMapper venueMapper;

    @Resource
    private VenuePriceMapper venuePriceMapper;

    @Resource
    private INotificationService notificationService;

    @Resource
    private IPaymentService paymentService;

    @Resource
    private PointsRecordMapper pointsRecordMapper;

    @Resource
    private MemberCardMapper memberCardMapper;

    @Resource
    private MemberCardTypeMapper memberCardTypeMapper;

    @Resource
    private MemberCardRecordMapper memberCardRecordMapper;

    /**
     * 创建预订
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBooking(Long userId, BookingCreateDTO createDTO) {
        // 1. 校验用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 校验场地
        Venue venue = venueMapper.selectById(createDTO.getVenueId());
        if (venue == null) {
            throw new BusinessException("场地不存在");
        }
        if (venue.getStatus() != 1) {
            throw new BusinessException("场地不可用");
        }

        // 3. 校验预订日期不能是过去
        if (createDTO.getBookingDate().isBefore(LocalDate.now())) {
            throw new BusinessException("预订日期不能是过去的日期");
        }

        // 4. 校验时间段
        if (createDTO.getEndTime().isBefore(createDTO.getStartTime()) ||
            createDTO.getEndTime().equals(createDTO.getStartTime())) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }

        // 5. 检查场地时间段是否可用
        if (!checkVenueAvailable(createDTO.getVenueId(), createDTO.getBookingDate(),
                                  createDTO.getStartTime(), createDTO.getEndTime())) {
            throw new BusinessException("该时间段已被预订");
        }

        // 6. 计算时长和价格
        Duration duration = Duration.between(createDTO.getStartTime(), createDTO.getEndTime());
        int hours = (int) duration.toHours();

        // 获取场地价格 - 查询符合时间段的价格配置
        BigDecimal pricePerHour = getVenuePrice(createDTO.getVenueId(), createDTO.getBookingDate(),
                                                createDTO.getStartTime(), user.getMemberLevel());
        BigDecimal totalPrice = pricePerHour.multiply(new BigDecimal(hours));

        // 7. 检查用户是否有有效的时间卡，如果有则应用折扣
        BigDecimal actualPrice = totalPrice;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // 查询用户有效的时间卡
        LambdaQueryWrapper<MemberCard> cardQuery = new LambdaQueryWrapper<>();
        cardQuery.eq(MemberCard::getUserId, userId)
                .eq(MemberCard::getStatus, 1)
                .ge(MemberCard::getExpireDate, LocalDate.now())
                .orderByDesc(MemberCard::getCreateTime)
                .last("LIMIT 1");
        MemberCard timeCard = memberCardMapper.selectOne(cardQuery);

        if (timeCard != null) {
            MemberCardType cardType = memberCardTypeMapper.selectById(timeCard.getCardTypeId());
            if (cardType != null && cardType.getCardType() == 1) {
                // 时间卡：应用折扣
                BigDecimal discount = cardType.getDiscount() != null ? cardType.getDiscount() : BigDecimal.ONE;
                actualPrice = totalPrice.multiply(discount);
                discountAmount = totalPrice.subtract(actualPrice);
                log.info("应用时间卡折扣: userId={}, cardId={}, 原价={}, 折扣={}折, 实付={}",
                        userId, timeCard.getId(), totalPrice,
                        discount.multiply(new BigDecimal("10")).intValue(), actualPrice);
            }
        }

        // 8. 生成订单号
        String bookingNo = "BK" + System.currentTimeMillis();

        // 9. 创建预订
        Booking booking = new Booking();
        booking.setBookingNo(bookingNo);
        booking.setUserId(userId);
        booking.setVenueId(createDTO.getVenueId());
        booking.setBookingDate(createDTO.getBookingDate());
        booking.setStartTime(createDTO.getStartTime());
        booking.setEndTime(createDTO.getEndTime());
        booking.setDuration(hours);
        booking.setBookingType(createDTO.getBookingType() != null ? createDTO.getBookingType() : 1);
        booking.setTotalPrice(totalPrice);
        booking.setDiscountAmount(discountAmount);
        booking.setActualPrice(actualPrice);
        booking.setStatus(0); // 待支付
        booking.setContactName(createDTO.getContactName());
        booking.setContactPhone(createDTO.getContactPhone());
        booking.setPeopleCount(createDTO.getPeopleCount());
        booking.setRemark(createDTO.getRemark());
        booking.setIsCheckedIn(0);

        // 设置过期时间（30分钟后）
        booking.setExpireTime(LocalDateTime.now().plusMinutes(30));
        booking.setCreateTime(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());

        bookingMapper.insert(booking);
        return booking.getId();
    }

    /**
     * 获取用户预订列表
     */
    @Override
    public PageResult<BookingVO> getUserBookingList(Long userId, Integer page, Integer pageSize, Integer status) {
        LambdaQueryWrapper<Booking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Booking::getUserId, userId);

        if (status != null) {
            queryWrapper.eq(Booking::getStatus, status);
        }

        queryWrapper.orderByDesc(Booking::getCreateTime);

        IPage<Booking> iPage = new Page<>(page, pageSize);
        IPage<Booking> bookingPage = bookingMapper.selectPage(iPage, queryWrapper);

        List<BookingVO> voList = bookingPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.<BookingVO>builder()
                .records(voList)
                .total(bookingPage.getTotal())
                .current(bookingPage.getCurrent())
                .size(bookingPage.getSize())
                .pages(bookingPage.getPages())
                .build();
    }

    @Override
    public PageResult<BookingVO> getBookingList(Integer page, Integer pageSize, String orderNo, String username, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<Booking> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            queryWrapper.like(Booking::getBookingNo, orderNo);
        }

        if (status != null) {
            queryWrapper.eq(Booking::getStatus, status);
        }

        // 如果有用户名条件，需要先查询用户ID
        if (StringUtils.hasText(username)) {
            LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.like(User::getUsername, username)
                    .or().like(User::getRealName, username);
            List<User> users = userMapper.selectList(userQueryWrapper);
            if (users.isEmpty()) {
                return PageResult.<BookingVO>builder()
                        .records(List.of())
                        .total(0L)
                        .current((long) page)
                        .size((long) pageSize)
                        .pages(0L)
                        .build();
            }
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            queryWrapper.in(Booking::getUserId, userIds);
        }

        queryWrapper.orderByDesc(Booking::getCreateTime);

        // 分页查询
        IPage<Booking> iPage = new Page<>(page, pageSize);
        IPage<Booking> bookingPage = bookingMapper.selectPage(iPage, queryWrapper);

        // 转换为VO
        List<BookingVO> voList = bookingPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());

        return PageResult.<BookingVO>builder()
                .records(voList)
                .total(bookingPage.getTotal())
                .current(bookingPage.getCurrent())
                .size(bookingPage.getSize())
                .pages(bookingPage.getPages())
                .build();
    }

    @Override
    public BookingVO getBookingDetail(Long id) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订不存在");
        }
        return convertToVO(booking);
    }

    /**
     * 取消预订
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(Long id, BookingCancelDTO cancelDTO) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订不存在");
        }

        // 只有待支付和已支付的订单可以取消
        if (booking.getStatus() != 0 && booking.getStatus() != 1) {
            throw new BusinessException("当前订单状态不允许取消");
        }

        // 已支付的订单需要退款
        if (booking.getStatus() == 1) {
            // TODO: 实现退款逻辑
            booking.setStatus(4); // 已退款
        } else {
            booking.setStatus(2); // 已取消
        }

        booking.setCancelTime(LocalDateTime.now());
        booking.setCancelReason(cancelDTO.getCancelReason());
        booking.setUpdateTime(LocalDateTime.now());
        bookingMapper.updateById(booking);

        // 发送预订取消通知
        try {
            NotificationSendDTO notificationDTO = new NotificationSendDTO();
            notificationDTO.setUserId(booking.getUserId());
            notificationDTO.setTemplateCode("BOOKING_CANCEL");
            notificationDTO.setBizId(String.valueOf(booking.getId()));
            notificationDTO.setBizType("booking");
            notificationDTO.setNotificationType("system");

            Map<String, Object> params = new HashMap<>();
            params.put("bookingNo", booking.getBookingNo());
            params.put("cancelReason", cancelDTO.getCancelReason() != null ? cancelDTO.getCancelReason() : "用户取消");
            notificationDTO.setParams(params);

            notificationService.sendNotification(notificationDTO);
        } catch (Exception e) {
            log.error("发送预订取消通知失败: bookingId={}", booking.getId(), e);
        }
    }

    /**
     * 支付预订
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO payBooking(Long id, BookingPayDTO payDTO) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订不存在");
        }

        // 只有待支付的订单可以支付
        if (booking.getStatus() != 0) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 检查订单是否过期
        if (booking.getExpireTime() != null && LocalDateTime.now().isAfter(booking.getExpireTime())) {
            booking.setStatus(5); // 超时取消
            booking.setUpdateTime(LocalDateTime.now());
            bookingMapper.updateById(booking);
            throw new BusinessException("订单已过期");
        }

        // 根据支付方式处理
        User user = userMapper.selectById(booking.getUserId());
        PaymentResultVO paymentResult = null;
        
        if (payDTO.getPaymentMethod() == 1) {
            // 在线支付（微信/支付宝）- 创建支付订单
            // 注意：在线支付是异步的，不能立即标记为已支付
            // 需要等待支付回调后再更新状态
            log.info("创建在线支付订单: bookingId={}, bookingNo={}, paymentType={}", 
                    id, booking.getBookingNo(), payDTO.getPaymentType());
            
            // 验证支付类型
            String paymentType = payDTO.getPaymentType();
            if (paymentType == null || paymentType.isEmpty()) {
                throw new BusinessException("请选择支付方式（微信或支付宝）");
            }
            
            // 构建支付请求
            PaymentCreateDTO paymentCreateDTO = new PaymentCreateDTO();
            paymentCreateDTO.setBusinessNo(booking.getBookingNo());
            paymentCreateDTO.setBusinessType("booking");
            paymentCreateDTO.setAmount(booking.getActualPrice());
            paymentCreateDTO.setDescription("场地预订支付 - " + booking.getBookingNo());
            paymentCreateDTO.setPaymentType(paymentType);
            
            // 调用统一支付服务创建支付订单
            paymentResult = paymentService.createPayment(booking.getUserId(), paymentCreateDTO);
            
            // 更新预订订单的支付方式，但不修改状态（等待回调）
            booking.setPaymentMethod(payDTO.getPaymentMethod());
            log.info("在线支付订单创建成功: paymentNo={}, paymentType={}, qrCodeUrl={}", 
                    paymentResult.getPaymentNo(), paymentType, paymentResult.getQrCodeUrl());
            
        } else if (payDTO.getPaymentMethod() == 2) {
            // 余额支付 - 直接扣款
            if (user.getBalance().compareTo(booking.getActualPrice()) < 0) {
                throw new BusinessException("余额不足");
            }
            user.setBalance(user.getBalance().subtract(booking.getActualPrice()));
            userMapper.updateById(user);

            booking.setPaymentMethod(payDTO.getPaymentMethod());
            booking.setStatus(1); // 已支付
            booking.setPayTime(LocalDateTime.now());
            log.info("余额支付成功: userId={}, amount={}", booking.getUserId(), booking.getActualPrice());

            // 增加积分：消费金额的1%转换为积分（1元=1积分）
            try {
                addPointsForPayment(booking.getUserId(), booking.getActualPrice(), booking.getBookingNo());
            } catch (Exception e) {
                log.error("增加积分失败: userId={}, bookingNo={}", booking.getUserId(), booking.getBookingNo(), e);
            }
            
        } else if (payDTO.getPaymentMethod() == 3) {
            // 会员卡支付
            if (payDTO.getCardId() == null) {
                throw new BusinessException("请选择会员卡");
            }

            MemberCard card = memberCardMapper.selectById(payDTO.getCardId());
            if (card == null || !card.getUserId().equals(booking.getUserId())) {
                throw new BusinessException("会员卡不存在");
            }
            if (card.getStatus() != 1) {
                throw new BusinessException("会员卡未激活或已失效");
            }

            MemberCardType cardType = memberCardTypeMapper.selectById(card.getCardTypeId());

            if (cardType.getCardType() == 2) {
                // 次卡：扣减1次，按原价获得积分
                if (card.getRemainingTimes() == null || card.getRemainingTimes() < 1) {
                    throw new BusinessException("会员卡次数不足");
                }
                card.setRemainingTimes(card.getRemainingTimes() - 1);
                memberCardMapper.updateById(card);

                // 记录消费
                MemberCardRecord record = new MemberCardRecord();
                record.setCardId(card.getId());
                record.setUserId(booking.getUserId());
                record.setRecordType(2);
                record.setChangeTimes(-1);
                record.setTimesBefore(card.getRemainingTimes() + 1);
                record.setTimesAfter(card.getRemainingTimes());
                record.setRelatedOrderNo(booking.getBookingNo());
                record.setDescription("预订场地消费（次卡）");
                memberCardRecordMapper.insert(record);

                booking.setPaymentMethod(payDTO.getPaymentMethod());
                booking.setStatus(1);
                booking.setPayTime(LocalDateTime.now());
                log.info("次卡支付成功: cardId={}, 剩余次数={}", card.getId(), card.getRemainingTimes());

                // 次卡按原价获得积分
                try {
                    addPointsForPayment(booking.getUserId(), booking.getTotalPrice(), booking.getBookingNo());
                } catch (Exception e) {
                    log.error("增加积分失败: userId={}, bookingNo={}", booking.getUserId(), booking.getBookingNo(), e);
                }

            } else if (cardType.getCardType() == 3) {
                // 储值卡：扣减余额，按实际扣除金额获得积分
                if (card.getBalance() == null || card.getBalance().compareTo(booking.getActualPrice()) < 0) {
                    throw new BusinessException("会员卡余额不足");
                }
                BigDecimal balanceBefore = card.getBalance();
                card.setBalance(card.getBalance().subtract(booking.getActualPrice()));
                memberCardMapper.updateById(card);

                // 记录消费
                MemberCardRecord record = new MemberCardRecord();
                record.setCardId(card.getId());
                record.setUserId(booking.getUserId());
                record.setRecordType(2);
                record.setChangeAmount(booking.getActualPrice().negate());
                record.setBalanceBefore(balanceBefore);
                record.setBalanceAfter(card.getBalance());
                record.setRelatedOrderNo(booking.getBookingNo());
                record.setDescription("预订场地消费（储值卡）");
                memberCardRecordMapper.insert(record);

                booking.setPaymentMethod(payDTO.getPaymentMethod());
                booking.setStatus(1);
                booking.setPayTime(LocalDateTime.now());
                log.info("储值卡支付成功: cardId={}, 扣除金额={}, 剩余余额={}",
                        card.getId(), booking.getActualPrice(), card.getBalance());

                // 储值卡按实际扣除金额获得积分
                try {
                    addPointsForPayment(booking.getUserId(), booking.getActualPrice(), booking.getBookingNo());
                } catch (Exception e) {
                    log.error("增加积分失败: userId={}, bookingNo={}", booking.getUserId(), booking.getBookingNo(), e);
                }

            } else if (cardType.getCardType() == 1) {
                // 时间卡：应用折扣扣除金额，按原价获得积分
                if (card.getExpireDate() != null && LocalDate.now().isAfter(card.getExpireDate())) {
                    throw new BusinessException("会员卡已过期");
                }

                // 应用会员卡折扣
                BigDecimal discount = cardType.getDiscount() != null ? cardType.getDiscount() : BigDecimal.ONE;
                BigDecimal discountedAmount = booking.getTotalPrice().multiply(discount);

                // 记录消费（包含折扣信息）
                MemberCardRecord record = new MemberCardRecord();
                record.setCardId(card.getId());
                record.setUserId(booking.getUserId());
                record.setRecordType(2);
                record.setChangeAmount(discountedAmount.negate());
                record.setRelatedOrderNo(booking.getBookingNo());
                record.setDescription("时间卡预订场地（原价¥" + booking.getTotalPrice() +
                                    "，折扣" + discount.multiply(new BigDecimal("10")).intValue() +
                                    "折，实付¥" + discountedAmount + "）");
                memberCardRecordMapper.insert(record);

                booking.setPaymentMethod(payDTO.getPaymentMethod());
                booking.setStatus(1);
                booking.setPayTime(LocalDateTime.now());
                log.info("时间卡支付成功: cardId={}, 原价={}, 折扣={}折, 实付={}",
                        card.getId(), booking.getTotalPrice(),
                        discount.multiply(new BigDecimal("10")).intValue(), discountedAmount);

                // 时间卡按原价获得积分
                try {
                    addPointsForPayment(booking.getUserId(), booking.getTotalPrice(), booking.getBookingNo());
                } catch (Exception e) {
                    log.error("增加积分失败: userId={}, bookingNo={}", booking.getUserId(), booking.getBookingNo(), e);
                }

            } else {
                throw new BusinessException("不支持的会员卡类型");
            }

        } else if (payDTO.getPaymentMethod() == 4) {
            // 现场支付 - 只更新支付方式，不修改状态
            booking.setPaymentMethod(payDTO.getPaymentMethod());
            log.info("现场支付订单确认成功，等待现场付款: bookingNo={}", booking.getBookingNo());
            
        } else {
            throw new BusinessException("不支持的支付方式");
        }

        booking.setUpdateTime(LocalDateTime.now());
        bookingMapper.updateById(booking);

        // 发送预订成功通知
        try {
            Venue venue = venueMapper.selectById(booking.getVenueId());

            NotificationSendDTO notificationDTO = new NotificationSendDTO();
            notificationDTO.setUserId(booking.getUserId());
            notificationDTO.setTemplateCode("BOOKING_SUCCESS");
            notificationDTO.setBizId(String.valueOf(booking.getId()));
            notificationDTO.setBizType("booking");
            notificationDTO.setNotificationType("system");

            Map<String, Object> params = new HashMap<>();
            params.put("orderNo", booking.getBookingNo());
            params.put("venueName", venue != null ? venue.getVenueName() : "场地");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String bookingTime = booking.getBookingDate() + " " + booking.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "-" + booking.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            params.put("bookingTime", bookingTime);
            notificationDTO.setParams(params);

            notificationService.sendNotification(notificationDTO);
        } catch (Exception e) {
            // 通知发送失败不影响主流程
            log.error("发送预订成功通知失败: bookingId={}", booking.getId(), e);
        }
        
        // 返回支付结果（在线支付时包含二维码或跳转URL）
        return paymentResult;
    }

    @Override
    public void updateBookingStatus(Long id, Integer status) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订不存在");
        }

        booking.setStatus(status);
        if (status == 1) {
            booking.setPayTime(LocalDateTime.now());
        } else if (status == 2) {
            booking.setCancelTime(LocalDateTime.now());
        }
        bookingMapper.updateById(booking);
    }

    /**
     * 转换为VO
     */
    private BookingVO convertToVO(Booking booking) {
        BookingVO vo = new BookingVO();
        BeanUtils.copyProperties(booking, vo);

        // 查询用户信息
        User user = userMapper.selectById(booking.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
        }

        // 查询场地信息
        Venue venue = venueMapper.selectById(booking.getVenueId());
        if (venue != null) {
            vo.setVenueName(venue.getVenueName());
            vo.setVenueLocation(venue.getLocation());
        }

        // 设置时间段
        if (booking.getStartTime() != null && booking.getEndTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            vo.setTimeSlot(booking.getStartTime().format(formatter) + "-" + booking.getEndTime().format(formatter));
        }

        // 设置支付方式名称
        vo.setPayMethodName(getPayMethodName(booking.getPaymentMethod()));

        // 设置状态名称
        vo.setStatusName(getStatusName(booking.getStatus()));

        // 计算单价
        if (booking.getDuration() != null && booking.getDuration() > 0) {
            vo.setPrice(booking.getTotalPrice().divide(java.math.BigDecimal.valueOf(booking.getDuration()), 2, java.math.RoundingMode.HALF_UP));
        }

        return vo;
    }

    /**
     * 获取支付方式名称
     */
    private String getPayMethodName(Integer paymentMethod) {
        if (paymentMethod == null) {
            return "未支付";
        }
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "微信支付");
        map.put(2, "余额支付");
        map.put(3, "会员卡");
        map.put(4, "现场支付");
        return map.getOrDefault(paymentMethod, "未知");
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "待支付");
        map.put(1, "已完成");
        map.put(2, "已取消");
        map.put(3, "已完成");
        map.put(4, "已退款");
        map.put(5, "超时取消");
        return map.getOrDefault(status, "未知");
    }

    /**
     * 检查场地时间段是否可用
     */
    @Override
    public boolean checkVenueAvailable(Long venueId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
        LambdaQueryWrapper<Booking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Booking::getVenueId, venueId)
                .eq(Booking::getBookingDate, bookingDate)
                .in(Booking::getStatus, java.util.Arrays.asList(0, 1, 3)); // 待支付、已支付、已完成的订单

        List<Booking> bookings = bookingMapper.selectList(queryWrapper);

        // 检查是否有时间冲突
        for (Booking booking : bookings) {
            // 新预订的开始时间在已有预订期间
            if ((startTime.isAfter(booking.getStartTime()) || startTime.equals(booking.getStartTime()))
                && startTime.isBefore(booking.getEndTime())) {
                return false;
            }
            // 新预订的结束时间在已有预订期间
            if (endTime.isAfter(booking.getStartTime())
                && (endTime.isBefore(booking.getEndTime()) || endTime.equals(booking.getEndTime()))) {
                return false;
            }
            // 新预订完全包含已有预订
            if ((startTime.isBefore(booking.getStartTime()) || startTime.equals(booking.getStartTime()))
                && (endTime.isAfter(booking.getEndTime()) || endTime.equals(booking.getEndTime()))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 增加消费积分
     * 规则：消费金额的1%转换为积分（1元=1积分）
     */
    private void addPointsForPayment(Long userId, BigDecimal amount, String orderNo) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在，无法增加积分: userId={}", userId);
            return;
        }

        // 计算积分：1元=1积分
        int pointsToAdd = amount.intValue();
        if (pointsToAdd <= 0) {
            log.info("消费金额太小，不增加积分: amount={}", amount);
            return;
        }

        // 记录变动前积分
        int pointsBefore = user.getPoints() != null ? user.getPoints() : 0;
        int pointsAfter = pointsBefore + pointsToAdd;

        // 更新用户积分
        user.setPoints(pointsAfter);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 创建积分记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(pointsToAdd);
        record.setType(1); // 1-消费赠送
        record.setRelatedOrder(orderNo);
        record.setPointsBefore(pointsBefore);
        record.setPointsAfter(pointsAfter);
        record.setExpireDate(LocalDate.now().plusYears(1)); // 积分1年后过期
        record.setRemark("消费赠送积分：订单" + orderNo + "，消费" + amount + "元");
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("增加消费积分成功: userId={}, points=, orderNo={}, amount={}",
                userId, pointsToAdd, orderNo, amount);
    }

    /**
     * 获取场地价格
     * 根据日期类型(工作日/周末/节假日)和会员等级返回对应价格
     */
    private BigDecimal getVenuePrice(Long venueId, LocalDate bookingDate, LocalTime startTime, Integer memberLevel) {
        // 判断日期类型: 1-工作日, 2-周末, 3-节假日
        int dayOfWeek = bookingDate.getDayOfWeek().getValue();
        int timeType = (dayOfWeek == 6 || dayOfWeek == 7) ? 2 : 1; // 简化处理: 周六日为周末,其他为工作日

        // 查询该场地在指定时间类型和时间段的价格
        LambdaQueryWrapper<VenuePrice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VenuePrice::getVenueId, venueId)
                .eq(VenuePrice::getTimeType, timeType)
                .eq(VenuePrice::getStatus, 1) // 启用状态
                .le(VenuePrice::getStartTime, startTime) // 价格时段开始时间 <= 预订开始时间
                .orderByDesc(VenuePrice::getStartTime)
                .last("LIMIT 1");

        VenuePrice venuePrice = venuePriceMapper.selectOne(queryWrapper);

        if (venuePrice != null) {
            // 如果会员等级大于0且有会员价,返回会员价,否则返回标准价
            if (memberLevel != null && memberLevel > 0 && venuePrice.getMemberPrice() != null) {
                return venuePrice.getMemberPrice();
            }
            return venuePrice.getPrice();
        }

        // 如果没有配置价格,返回默认价格100元/小时
        return new BigDecimal("100.00");
    }
}
