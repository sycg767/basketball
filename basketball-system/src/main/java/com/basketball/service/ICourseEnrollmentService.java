package com.basketball.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basketball.dto.request.CourseEnrollmentDTO;
import com.basketball.entity.CourseEnrollment;
import com.basketball.common.result.PageResult;
import com.basketball.vo.CourseEnrollmentVO;

/**
 * 课程报名服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface ICourseEnrollmentService extends IService<CourseEnrollment> {

    /**
     * 创建报名
     */
    Long createEnrollment(Long userId, CourseEnrollmentDTO dto);

    /**
     * 取消报名
     */
    void cancelEnrollment(Long id, Long userId);

    /**
     * 获取报名详情
     */
    CourseEnrollmentVO getEnrollmentDetail(Long id);

    /**
     * 获取用户的报名列表
     */
    PageResult<CourseEnrollmentVO> getUserEnrollments(Long userId, Integer page, Integer pageSize, Integer payStatus);

    /**
     * 获取排期的报名列表
     */
    PageResult<CourseEnrollmentVO> getScheduleEnrollments(Long scheduleId, Integer page, Integer pageSize);

    /**
     * 支付报名
     */
    void payEnrollment(Long id, Integer paymentMethod, String paymentType);

    /**
     * 签到
     */
    void checkIn(Long id);

    /**
     * 评价课程
     */
    void rateCourse(Long id, Integer rating, String comment);
}
