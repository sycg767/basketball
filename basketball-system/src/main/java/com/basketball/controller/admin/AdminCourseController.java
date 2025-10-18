package com.basketball.controller.admin;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.*;
import com.basketball.service.ICourseEnrollmentService;
import com.basketball.service.ICourseScheduleService;
import com.basketball.service.ICourseService;
import com.basketball.vo.CourseEnrollmentVO;
import com.basketball.vo.CourseScheduleVO;
import com.basketball.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理控制器(管理端)
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/api/admin/course")
@Tag(name = "课程管理(管理端)")
public class AdminCourseController {

    @Resource
    private ICourseService courseService;

    @Resource
    private ICourseScheduleService scheduleService;

    @Resource
    private ICourseEnrollmentService enrollmentService;

    // ========== 课程管理 ==========

    @PostMapping
    @Operation(summary = "创建课程")
    public Result<Long> createCourse(@Valid @RequestBody CourseCreateDTO dto) {
        return Result.success(courseService.createCourse(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程")
    public Result<Void> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseUpdateDTO dto
    ) {
        courseService.updateCourse(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }

    @GetMapping
    @Operation(summary = "获取课程列表")
    public Result<PageResult<CourseVO>> getCourseList(CourseQueryDTO dto) {
        return Result.success(courseService.getCourseList(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情")
    public Result<CourseVO> getCourseDetail(@PathVariable Long id) {
        return Result.success(courseService.getCourseDetail(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新课程状态")
    public Result<Void> updateCourseStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        courseService.updateCourseStatus(id, status);
        return Result.success();
    }

    // ========== 课程排期管理 ==========

    @PostMapping("/schedule")
    @Operation(summary = "创建课程排期")
    public Result<Long> createSchedule(@Valid @RequestBody CourseScheduleCreateDTO dto) {
        return Result.success(scheduleService.createSchedule(dto));
    }

    @DeleteMapping("/schedule/{id}")
    @Operation(summary = "删除排期")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return Result.success();
    }

    @GetMapping("/schedule")
    @Operation(summary = "获取排期列表")
    public Result<PageResult<CourseScheduleVO>> getScheduleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer status
    ) {
        return Result.success(scheduleService.getScheduleList(page, pageSize, courseId, status));
    }

    @GetMapping("/schedule/{id}")
    @Operation(summary = "获取排期详情")
    public Result<CourseScheduleVO> getScheduleDetail(@PathVariable Long id) {
        return Result.success(scheduleService.getScheduleDetail(id));
    }

    @PutMapping("/schedule/{id}/cancel")
    @Operation(summary = "取消排期")
    public Result<Void> cancelSchedule(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        scheduleService.cancelSchedule(id, reason);
        return Result.success();
    }

    // ========== 课程报名管理 ==========

    @GetMapping("/enrollment")
    @Operation(summary = "获取报名列表")
    public Result<PageResult<CourseEnrollmentVO>> getEnrollmentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long scheduleId
    ) {
        if (scheduleId != null) {
            return Result.success(enrollmentService.getScheduleEnrollments(scheduleId, page, pageSize));
        }
        return Result.success(PageResult.empty());
    }

    @GetMapping("/enrollment/{id}")
    @Operation(summary = "获取报名详情")
    public Result<CourseEnrollmentVO> getEnrollmentDetail(@PathVariable Long id) {
        return Result.success(enrollmentService.getEnrollmentDetail(id));
    }

    @PutMapping("/enrollment/{id}/check-in")
    @Operation(summary = "学员签到")
    public Result<Void> checkIn(@PathVariable Long id) {
        enrollmentService.checkIn(id);
        return Result.success();
    }
}
