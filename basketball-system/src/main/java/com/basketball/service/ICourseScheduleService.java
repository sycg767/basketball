package com.basketball.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basketball.dto.request.CourseScheduleCreateDTO;
import com.basketball.entity.CourseSchedule;
import com.basketball.common.result.PageResult;
import com.basketball.vo.CourseScheduleVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 课程排期服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface ICourseScheduleService extends IService<CourseSchedule> {

    /**
     * 创建课程排期
     */
    Long createSchedule(CourseScheduleCreateDTO dto);

    /**
     * 删除排期
     */
    void deleteSchedule(Long id);

    /**
     * 获取排期详情
     */
    CourseScheduleVO getScheduleDetail(Long id);

    /**
     * 获取课程的排期列表
     */
    List<CourseScheduleVO> getCourseSchedules(Long courseId, LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询排期列表
     */
    PageResult<CourseScheduleVO> getScheduleList(Integer page, Integer pageSize, Long courseId, Integer status);

    /**
     * 取消排期
     */
    void cancelSchedule(Long id, String reason);

    /**
     * 更新已报名人数
     */
    void updateEnrolledCount(Long scheduleId, Integer increment);
}
