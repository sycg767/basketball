package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.CourseEnrollmentDTO;
import com.basketball.entity.*;
import com.basketball.mapper.*;
import com.basketball.service.ICourseEnrollmentService;
import com.basketball.service.ICourseScheduleService;
import com.basketball.service.INotificationService;
import com.basketball.dto.request.NotificationSendDTO;
import com.basketball.utils.NotificationEventPublisher;
import com.basketball.vo.CourseEnrollmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程报名服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Slf4j
@Service
public class CourseEnrollmentServiceImpl extends ServiceImpl<CourseEnrollmentMapper, CourseEnrollment> implements ICourseEnrollmentService {

    @Resource
    private CourseEnrollmentMapper enrollmentMapper;

    @Resource
    private CourseScheduleMapper scheduleMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private VenueMapper venueMapper;

    @Resource
    private ICourseScheduleService scheduleService;

    @Resource
    private INotificationService notificationService;

    @Resource
    private NotificationEventPublisher notificationEventPublisher;

    @Resource
    private com.basketball.service.IMemberService memberService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEnrollment(Long userId, CourseEnrollmentDTO dto) {
        // 验证排期是否存在
        CourseSchedule schedule = scheduleMapper.selectById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("课程排期不存在");
        }

        // 检查状态
        if (schedule.getStatus() == 2) {
            throw new BusinessException("课程已满员");
        }
        if (schedule.getStatus() == 5) {
            throw new BusinessException("课程已取消");
        }
        if (schedule.getStatus() != 1) {
            throw new BusinessException("课程不在报名中");
        }

        // 检查是否已报名
        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getUserId, userId)
                .eq(CourseEnrollment::getScheduleId, dto.getScheduleId())
                .eq(CourseEnrollment::getStatus, 1);

        if (enrollmentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已报名该课程");
        }

        // 获取课程信息
        Course course = courseMapper.selectById(schedule.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 创建报名记录
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setScheduleId(dto.getScheduleId());
        enrollment.setCourseId(schedule.getCourseId());
        enrollment.setUserId(userId);

        // 生成报名编号
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        enrollment.setEnrollmentNo("ENR" + dateStr + userId % 1000);

        // 设置价格(根据用户会员等级计算折扣)
        User user = userMapper.selectById(userId);
        BigDecimal basePrice = course.getPrice();
        if (user != null && user.getMemberLevel() > 0) {
            BigDecimal discount = getMemberDiscount(user.getMemberLevel());
            enrollment.setPrice(basePrice.multiply(discount));
        } else {
            enrollment.setPrice(basePrice);
        }

        enrollment.setPayStatus(0); // 待支付
        enrollment.setAttendanceStatus(0); // 未签到
        enrollment.setStatus(1); // 正常
        enrollment.setEnrollTime(LocalDateTime.now());

        enrollmentMapper.insert(enrollment);

        // 更新排期的报名人数
        scheduleService.updateEnrolledCount(dto.getScheduleId(), 1);

        // 更新课程的报名人数
        course.setEnrollCount(course.getEnrollCount() + 1);
        courseMapper.updateById(course);

        log.info("创建课程报名成功, 报名ID: {}, 用户ID: {}, 排期ID: {}",
                enrollment.getId(), userId, dto.getScheduleId());

        // 发送报名成功通知
        try {
            NotificationSendDTO notificationDTO = new NotificationSendDTO();
            notificationDTO.setUserId(userId);
            notificationDTO.setTemplateCode("COURSE_ENROLL_SUCCESS");
            notificationDTO.setBizId(String.valueOf(enrollment.getId()));
            notificationDTO.setBizType("course");
            notificationDTO.setNotificationType("system");

            Map<String, Object> params = new HashMap<>();
            params.put("courseName", course.getCourseName());
            params.put("enrollmentNo", enrollment.getEnrollmentNo());
            params.put("scheduleDate", schedule.getScheduleDate().toString());
            notificationDTO.setParams(params);

            notificationEventPublisher.publishNotification(notificationDTO);
        } catch (Exception e) {
            log.error("发送课程报名成功通知失败: enrollmentId={}", enrollment.getId(), e);
        }

        return enrollment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelEnrollment(Long id, Long userId) {
        CourseEnrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("报名记录不存在");
        }

        if (!enrollment.getUserId().equals(userId)) {
            throw new BusinessException("无权取消此报名");
        }

        if (enrollment.getPayStatus() == 1) {
            throw new BusinessException("已支付的报名需要申请退款");
        }

        enrollment.setStatus(0); // 已取消
        enrollment.setUpdateTime(LocalDateTime.now());
        enrollmentMapper.updateById(enrollment);

        // 更新排期的报名人数
        scheduleService.updateEnrolledCount(enrollment.getScheduleId(), -1);

        log.info("取消课程报名成功, 报名ID: {}", id);

        // 发送报名取消通知
        try {
            Course course = courseMapper.selectById(enrollment.getCourseId());

            NotificationSendDTO notificationDTO = new NotificationSendDTO();
            notificationDTO.setUserId(userId);
            notificationDTO.setTemplateCode("COURSE_ENROLL_CANCEL");
            notificationDTO.setBizId(String.valueOf(enrollment.getId()));
            notificationDTO.setBizType("course");
            notificationDTO.setNotificationType("system");

            Map<String, Object> params = new HashMap<>();
            params.put("courseName", course != null ? course.getCourseName() : "课程");
            params.put("enrollmentNo", enrollment.getEnrollmentNo());
            notificationDTO.setParams(params);

            notificationEventPublisher.publishNotification(notificationDTO);
        } catch (Exception e) {
            log.error("发送课程报名取消通知失败: enrollmentId={}", id, e);
        }
    }

    @Override
    public CourseEnrollmentVO getEnrollmentDetail(Long id) {
        CourseEnrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("报名记录不存在");
        }

        return convertToVO(enrollment);
    }

    @Override
    public PageResult<CourseEnrollmentVO> getUserEnrollments(Long userId, Integer page, Integer pageSize, Integer payStatus) {
        Page<CourseEnrollment> pageObj = new Page<>(page, pageSize);

        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getUserId, userId)
                .eq(payStatus != null, CourseEnrollment::getPayStatus, payStatus)
                .orderByDesc(CourseEnrollment::getEnrollTime);

        IPage<CourseEnrollment> result = enrollmentMapper.selectPage(pageObj, wrapper);

        List<CourseEnrollmentVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<CourseEnrollmentVO> getScheduleEnrollments(Long scheduleId, Integer page, Integer pageSize) {
        Page<CourseEnrollment> pageObj = new Page<>(page, pageSize);

        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getScheduleId, scheduleId)
                .eq(CourseEnrollment::getStatus, 1)
                .orderByDesc(CourseEnrollment::getEnrollTime);

        IPage<CourseEnrollment> result = enrollmentMapper.selectPage(pageObj, wrapper);

        List<CourseEnrollmentVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payEnrollment(Long id, Integer paymentMethod, String paymentType) {
        CourseEnrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("报名记录不存在");
        }

        if (enrollment.getPayStatus() == 1) {
            throw new BusinessException("该报名已支付");
        }

        Long userId = enrollment.getUserId();
        BigDecimal payAmount = enrollment.getPrice();

        // 根据支付方式处理扣款
        if (paymentMethod == 2) {
            // 余额支付：扣除用户账户余额
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            if (currentBalance.compareTo(payAmount) < 0) {
                throw new BusinessException("账户余额不足");
            }

            user.setBalance(currentBalance.subtract(payAmount));
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);

            log.info("扣除用户账户余额: userId={}, amount={}, remainingBalance={}",
                    userId, payAmount, user.getBalance());
        } else if (paymentMethod == 3) {
            // 会员卡支付：扣除会员卡余额
            // 这里需要传入会员卡ID，暂时从用户的第一张可用会员卡扣除
            try {
                com.basketball.dto.request.CardConsumeDTO consumeDTO = new com.basketball.dto.request.CardConsumeDTO();
                // 获取用户的第一张可用储值卡
                com.basketball.common.result.PageResult<com.basketball.vo.MemberCardVO> cards =
                        memberService.getMyCards(userId, 1, 1);

                if (cards.getRecords().isEmpty()) {
                    throw new BusinessException("没有可用的会员卡");
                }

                Long cardId = cards.getRecords().get(0).getId();
                consumeDTO.setCardId(cardId);
                consumeDTO.setAmount(payAmount);
                consumeDTO.setOrderNo(enrollment.getEnrollmentNo());
                consumeDTO.setDescription("课程报名支付：" + enrollment.getEnrollmentNo());

                memberService.consumeCard(userId, consumeDTO);

                log.info("扣除会员卡余额: userId={}, cardId={}, amount={}",
                        userId, cardId, payAmount);
            } catch (Exception e) {
                log.error("扣除会员卡余额失败", e);
                throw new BusinessException("会员卡支付失败：" + e.getMessage());
            }
        }

        // 生成支付订单号
        String orderNo = "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + id;

        enrollment.setOrderNo(orderNo);
        enrollment.setPayStatus(1); // 已支付
        enrollment.setPayTime(LocalDateTime.now());
        enrollment.setUpdateTime(LocalDateTime.now());

        enrollmentMapper.updateById(enrollment);

        // 支付成功后增加积分：消费1元送1积分
        try {
            memberService.addPoints(userId, payAmount, orderNo);
            log.info("课程支付成功，增加积分: userId={}, amount={}, points={}",
                    userId, payAmount, payAmount.intValue());
        } catch (Exception e) {
            log.error("增加积分失败，但不影响支付流程", e);
        }

        log.info("支付课程报名成功, 报名ID: {}, 订单号: {}, 支付方式: {}", id, orderNo, paymentMethod);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long id) {
        CourseEnrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("报名记录不存在");
        }

        if (enrollment.getPayStatus() != 1) {
            throw new BusinessException("未支付无法签到");
        }

        enrollment.setAttendanceStatus(1); // 已签到
        enrollment.setCheckInTime(LocalDateTime.now());
        enrollment.setUpdateTime(LocalDateTime.now());

        enrollmentMapper.updateById(enrollment);

        // 更新排期的签到人数
        CourseSchedule schedule = scheduleMapper.selectById(enrollment.getScheduleId());
        if (schedule != null) {
            schedule.setCheckedInCount(schedule.getCheckedInCount() + 1);
            scheduleMapper.updateById(schedule);
        }

        log.info("课程签到成功, 报名ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rateCourse(Long id, Integer rating, String comment) {
        CourseEnrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("报名记录不存在");
        }

        if (enrollment.getAttendanceStatus() != 1) {
            throw new BusinessException("未签到无法评价");
        }

        enrollment.setRating(rating);
        enrollment.setComment(comment);
        enrollment.setCommentTime(LocalDateTime.now());
        enrollment.setUpdateTime(LocalDateTime.now());

        enrollmentMapper.updateById(enrollment);
        log.info("课程评价成功, 报名ID: {}, 评分: {}", id, rating);
    }

    /**
     * 转换为VO
     */
    private CourseEnrollmentVO convertToVO(CourseEnrollment enrollment) {
        CourseEnrollmentVO vo = new CourseEnrollmentVO();
        BeanUtils.copyProperties(enrollment, vo);

        // 获取课程信息
        Course course = courseMapper.selectById(enrollment.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getCourseName());

            // 获取教练名称
            User coach = userMapper.selectById(course.getCoachId());
            if (coach != null) {
                vo.setCoachName(coach.getRealName() != null ? coach.getRealName() : coach.getUsername());
            }
        }

        // 获取排期信息
        CourseSchedule schedule = scheduleMapper.selectById(enrollment.getScheduleId());
        if (schedule != null) {
            vo.setScheduleDate(schedule.getScheduleDate());
            vo.setTimeSlot(schedule.getStartTime().toString() + "-" + schedule.getEndTime().toString());

            // 获取场地名称
            Venue venue = venueMapper.selectById(schedule.getVenueId());
            if (venue != null) {
                vo.setVenueName(venue.getVenueName());
            }
        }

        // 获取用户名称
        User user = userMapper.selectById(enrollment.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName() != null ? user.getRealName() : user.getUsername());
        }

        return vo;
    }

    /**
     * 根据会员等级获取折扣
     */
    private BigDecimal getMemberDiscount(Integer memberLevel) {
        if (memberLevel == null || memberLevel <= 0) {
            return BigDecimal.ONE;
        }
        // 会员等级折扣：1级9.5折，2级9折，3级8.5折，4级8折，5级7.5折
        switch (memberLevel) {
            case 1: return new BigDecimal("0.95");
            case 2: return new BigDecimal("0.90");
            case 3: return new BigDecimal("0.85");
            case 4: return new BigDecimal("0.80");
            case 5: return new BigDecimal("0.75");
            default: return BigDecimal.ONE;
        }
    }
}
