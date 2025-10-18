package com.basketball.controller.admin;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.service.ICoachService;
import com.basketball.vo.CoachVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 教练管理控制器(管理端)
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/api/admin/coach")
@Tag(name = "教练管理(管理端)")
public class AdminCoachController {

    @Resource
    private ICoachService coachService;

    @GetMapping("/list")
    @Operation(summary = "获取教练列表")
    public Result<PageResult<CoachVO>> getCoachList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "999") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        // 如果没有传分页参数或要求获取全部，返回所有在职教练
        if ((page == 1 && pageSize >= 999) && status == null && keyword == null) {
            return Result.success(coachService.getAllActiveCoaches());
        }
        return Result.success(coachService.getCoachList(page, pageSize, keyword, status));
    }
}
