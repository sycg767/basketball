package com.basketball.controller;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.BookingCancelDTO;
import com.basketball.dto.request.BookingCreateDTO;
import com.basketball.dto.request.BookingPayDTO;
import com.basketball.dto.response.PaymentResultVO;
import com.basketball.security.JwtTokenProvider;
import com.basketball.service.IBookingService;
import com.basketball.vo.BookingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 预订控制器
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/api/booking")
@Tag(name = "预订管理", description = "预订相关接口")
public class BookingController {

    @Resource
    private IBookingService bookingService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 创建预订
     */
    @PostMapping
    @Operation(summary = "创建预订")
    public Result<Long> createBooking(@Valid @RequestBody BookingCreateDTO createDTO,
                                      HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        Long bookingId = bookingService.createBooking(userId, createDTO);
        return Result.success(bookingId);
    }

    /**
     * 获取用户预订列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的预订列表")
    public Result<PageResult<BookingVO>> getMyBookingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        PageResult<BookingVO> result = bookingService.getUserBookingList(userId, page, pageSize, status);
        return Result.success(result);
    }

    /**
     * 获取预订详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取预订详情")
    public Result<BookingVO> getBookingDetail(@PathVariable Long id) {
        BookingVO bookingVO = bookingService.getBookingDetail(id);
        return Result.success(bookingVO);
    }

    /**
     * 取消预订
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消预订")
    public Result<Void> cancelBooking(@PathVariable Long id,
                                      @Valid @RequestBody BookingCancelDTO cancelDTO) {
        bookingService.cancelBooking(id, cancelDTO);
        return Result.success();
    }

    /**
     * 支付预订
     */
    @PutMapping("/{id}/pay")
    @Operation(summary = "支付预订", description = "在线支付时返回二维码或跳转URL，其他支付方式直接完成支付")
    public Result<PaymentResultVO> payBooking(@PathVariable Long id,
                                              @Valid @RequestBody BookingPayDTO payDTO) {
        PaymentResultVO paymentResult = bookingService.payBooking(id, payDTO);
        return Result.success(paymentResult);
    }

    /**
     * 检查场地时间段是否可用
     */
    @GetMapping("/check-available")
    @Operation(summary = "检查场地时间段是否可用")
    public Result<Boolean> checkVenueAvailable(
            @RequestParam Long venueId,
            @RequestParam String bookingDate,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        boolean available = bookingService.checkVenueAvailable(
                venueId,
                java.time.LocalDate.parse(bookingDate),
                java.time.LocalTime.parse(startTime),
                java.time.LocalTime.parse(endTime)
        );
        return Result.success(available);
    }

    /**
     * 计算预订价格
     */
    @GetMapping("/calculate-price")
    @Operation(summary = "计算预订价格")
    public Result<java.util.Map<String, Object>> calculatePrice(
            @RequestParam Long venueId,
            @RequestParam String bookingDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        java.util.Map<String, Object> priceInfo = bookingService.calculateBookingPrice(
                userId,
                venueId,
                java.time.LocalDate.parse(bookingDate),
                java.time.LocalTime.parse(startTime),
                java.time.LocalTime.parse(endTime)
        );
        return Result.success(priceInfo);
    }
}
