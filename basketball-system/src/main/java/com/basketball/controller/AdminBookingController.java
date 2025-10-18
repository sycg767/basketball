package com.basketball.controller;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.BookingCancelDTO;
import com.basketball.service.IBookingService;
import com.basketball.vo.BookingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 预订管理控制器（管理端）
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Tag(name = "预订管理（管理员）", description = "管理员预订管理接口")
@RestController
@RequestMapping("/api/admin/booking")
@Validated
public class AdminBookingController {

    @Resource
    private IBookingService bookingService;

    /**
     * 获取预订列表（分页）
     */
    @Operation(summary = "获取预订列表", description = "分页查询预订列表")
    @GetMapping("/list")
    public Result<PageResult<BookingVO>> getBookingList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        PageResult<BookingVO> result = bookingService.getBookingList(page, pageSize, orderNo, username, status);
        return Result.success(result);
    }

    /**
     * 获取预订详情
     */
    @Operation(summary = "获取预订详情", description = "根据ID获取预订详细信息")
    @GetMapping("/{id}")
    public Result<BookingVO> getBookingDetail(
            @Parameter(description = "预订ID", required = true) @PathVariable Long id) {
        BookingVO booking = bookingService.getBookingDetail(id);
        return Result.success(booking);
    }

    /**
     * 取消预订
     */
    @Operation(summary = "取消预订", description = "管理员取消预订")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelBooking(
            @Parameter(description = "预订ID", required = true) @PathVariable Long id,
            @Valid @RequestBody BookingCancelDTO cancelDTO) {
        bookingService.cancelBooking(id, cancelDTO);
        return Result.success();
    }

    /**
     * 更新预订状态
     */
    @Operation(summary = "更新预订状态", description = "管理员更新预订状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateBookingStatus(
            @Parameter(description = "预订ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态", required = true) @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        bookingService.updateBookingStatus(id, status);
        return Result.success();
    }
}
