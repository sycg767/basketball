package com.basketball.controller;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.CourseEnrollmentDTO;
import com.basketball.dto.request.CourseQueryDTO;
import com.basketball.security.JwtTokenProvider;
import com.basketball.service.ICourseEnrollmentService;
import com.basketball.service.ICourseScheduleService;
import com.basketball.service.ICourseService;
import com.basketball.vo.CourseEnrollmentVO;
import com.basketball.vo.CourseScheduleVO;
import com.basketball.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 课程控制器(用户端)
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/api/course")
@Tag(name = "课程管理(用户端)")
public class CourseController {

    @Resource
    private ICourseService courseService;

    @Resource
    private ICourseScheduleService scheduleService;

    @Resource
    private ICourseEnrollmentService enrollmentService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @Operation(summary = "获取课程列表")
    public Result<PageResult<CourseVO>> getCourseList(CourseQueryDTO dto) {
        // 默认只查询上架的课程
        if (dto.getStatus() == null) {
            dto.setStatus(1);
        }
        return Result.success(courseService.getCourseList(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情")
    public Result<CourseVO> getCourseDetail(@PathVariable Long id) {
        // 增加浏览次数
        courseService.incrementViewCount(id);
        return Result.success(courseService.getCourseDetail(id));
    }

    @GetMapping("/{id}/schedules")
    @Operation(summary = "获取课程排期")
    public Result<List<CourseScheduleVO>> getCourseSchedules(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return Result.success(scheduleService.getCourseSchedules(id, startDate, endDate));
    }

    @PostMapping("/enroll")
    @Operation(summary = "报名课程")
    public Result<Long> enrollCourse(
            @Valid @RequestBody CourseEnrollmentDTO dto,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(enrollmentService.createEnrollment(userId, dto));
    }

    @DeleteMapping("/enrollment/{id}")
    @Operation(summary = "取消报名")
    public Result<Void> cancelEnrollment(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        enrollmentService.cancelEnrollment(id, userId);
        return Result.success();
    }

    @GetMapping("/my-enrollments")
    @Operation(summary = "我的课程报名")
    public Result<PageResult<CourseEnrollmentVO>> getMyEnrollments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer payStatus,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(enrollmentService.getUserEnrollments(userId, page, pageSize, payStatus));
    }

    @GetMapping("/enrollment/{id}")
    @Operation(summary = "获取报名详情")
    public Result<CourseEnrollmentVO> getEnrollmentDetail(@PathVariable Long id) {
        return Result.success(enrollmentService.getEnrollmentDetail(id));
    }

    @PutMapping("/enrollment/{id}/rate")
    @Operation(summary = "评价课程")
    public Result<Void> rateCourse(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment
    ) {
        enrollmentService.rateCourse(id, rating, comment);
        return Result.success();
    }
}
