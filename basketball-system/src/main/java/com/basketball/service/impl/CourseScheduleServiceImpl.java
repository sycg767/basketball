package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.CourseScheduleCreateDTO;
import com.basketball.entity.Course;
import com.basketball.entity.CourseSchedule;
import com.basketball.entity.Venue;
import com.basketball.mapper.CourseMapper;
import com.basketball.mapper.CourseScheduleMapper;
import com.basketball.mapper.VenueMapper;
import com.basketball.service.ICourseScheduleService;
import com.basketball.vo.CourseScheduleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程排期服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Slf4j
@Service
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleMapper, CourseSchedule> implements ICourseScheduleService {

    @Resource
    private CourseScheduleMapper scheduleMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private VenueMapper venueMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(CourseScheduleCreateDTO dto) {
        // 验证课程是否存在
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 验证场地是否存在
        Venue venue = venueMapper.selectById(dto.getVenueId());
        if (venue == null) {
            throw new BusinessException("场地不存在");
        }

        // 检查时间冲突
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getVenueId, dto.getVenueId())
                .eq(CourseSchedule::getScheduleDate, dto.getScheduleDate())
                .ne(CourseSchedule::getStatus, 5) // 排除已取消的
                .and(w -> w
                        .between(CourseSchedule::getStartTime, dto.getStartTime(), dto.getEndTime())
                        .or()
                        .between(CourseSchedule::getEndTime, dto.getStartTime(), dto.getEndTime())
                );

        if (scheduleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该场地在此时间段已被预约");
        }

        // 创建排期
        CourseSchedule schedule = new CourseSchedule();
        BeanUtils.copyProperties(dto, schedule);

        // 生成排期编号
        String dateStr = dto.getScheduleDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        schedule.setScheduleNo("SCH" + dateStr + System.currentTimeMillis() % 100000);

        schedule.setEnrolledCount(0);
        schedule.setCheckedInCount(0);
        schedule.setStatus(1); // 报名中
        schedule.setCreateTime(LocalDateTime.now());

        scheduleMapper.insert(schedule);
        log.info("创建课程排期成功, 排期ID: {}", schedule.getId());

        return schedule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long id) {
        CourseSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("排期不存在");
        }

        if (schedule.getEnrolledCount() > 0) {
            throw new BusinessException("已有学员报名,无法删除");
        }

        scheduleMapper.deleteById(id);
        log.info("删除课程排期成功, 排期ID: {}", id);
    }

    @Override
    public CourseScheduleVO getScheduleDetail(Long id) {
        CourseSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("排期不存在");
        }

        return convertToVO(schedule);
    }

    @Override
    public List<CourseScheduleVO> getCourseSchedules(Long courseId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getCourseId, courseId)
                .ge(startDate != null, CourseSchedule::getScheduleDate, startDate)
                .le(endDate != null, CourseSchedule::getScheduleDate, endDate)
                .ne(CourseSchedule::getStatus, 5) // 排除已取消的
                .orderByAsc(CourseSchedule::getScheduleDate, CourseSchedule::getStartTime);

        List<CourseSchedule> schedules = scheduleMapper.selectList(wrapper);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<CourseScheduleVO> getScheduleList(Integer page, Integer pageSize, Long courseId, Integer status) {
        Page<CourseSchedule> pageObj = new Page<>(page, pageSize);

        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(courseId != null, CourseSchedule::getCourseId, courseId)
                .eq(status != null, CourseSchedule::getStatus, status)
                .orderByDesc(CourseSchedule::getScheduleDate, CourseSchedule::getStartTime);

        IPage<CourseSchedule> result = scheduleMapper.selectPage(pageObj, wrapper);

        List<CourseScheduleVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSchedule(Long id, String reason) {
        CourseSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("排期不存在");
        }

        schedule.setStatus(5); // 已取消
        schedule.setCancelReason(reason);
        schedule.setUpdateTime(LocalDateTime.now());

        scheduleMapper.updateById(schedule);
        log.info("取消课程排期成功, 排期ID: {}, 原因: {}", id, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnrolledCount(Long scheduleId, Integer increment) {
        CourseSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("排期不存在");
        }

        int newCount = schedule.getEnrolledCount() + increment;
        if (newCount < 0) {
            throw new BusinessException("报名人数不能为负数");
        }

        schedule.setEnrolledCount(newCount);

        // 如果达到最大人数,更新状态为已满员
        if (newCount >= schedule.getMaxStudents()) {
            schedule.setStatus(2);
        } else if (schedule.getStatus() == 2 && newCount < schedule.getMaxStudents()) {
            schedule.setStatus(1); // 恢复为报名中
        }

        schedule.setUpdateTime(LocalDateTime.now());
        scheduleMapper.updateById(schedule);
    }

    /**
     * 转换为VO
     */
    private CourseScheduleVO convertToVO(CourseSchedule schedule) {
        CourseScheduleVO vo = new CourseScheduleVO();
        BeanUtils.copyProperties(schedule, vo);

        // 获取课程信息
        Course course = courseMapper.selectById(schedule.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getCourseName());
        }

        // 获取场地信息
        Venue venue = venueMapper.selectById(schedule.getVenueId());
        if (venue != null) {
            vo.setVenueName(venue.getVenueName());
            vo.setVenueLocation(venue.getLocation());
        }

        // 生成时间段
        vo.setTimeSlot(schedule.getStartTime().toString() + "-" + schedule.getEndTime().toString());

        // 计算剩余名额
        vo.setRemainingSeats(schedule.getMaxStudents() - schedule.getEnrolledCount());

        return vo;
    }
}
