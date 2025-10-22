package com.basketball.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basketball.dto.request.CourseCreateDTO;
import com.basketball.dto.request.CourseQueryDTO;
import com.basketball.dto.request.CourseUpdateDTO;
import com.basketball.entity.Course;
import com.basketball.common.result.PageResult;
import com.basketball.vo.CourseVO;

/**
 * 课程服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface ICourseService extends IService<Course> {

    /**
     * 创建课程
     */
    Long createCourse(CourseCreateDTO dto);

    /**
     * 更新课程
     */
    void updateCourse(Long id, CourseUpdateDTO dto);

    /**
     * 删除课程
     */
    void deleteCourse(Long id);

    /**
     * 获取课程详情
     */
    CourseVO getCourseDetail(Long id);

    /**
     * 分页查询课程列表
     */
    PageResult<CourseVO> getCourseList(CourseQueryDTO dto);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long id);

    /**
     * 更新课程状态
     */
    void updateCourseStatus(Long id, Integer status);

    /**
     * 计算课程价格
     */
    com.basketball.vo.CoursePriceVO calculateCoursePrice(Long userId, com.basketball.dto.request.CoursePriceDTO dto);
}
