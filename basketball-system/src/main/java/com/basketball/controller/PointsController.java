package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.PointsDeductDTO;
import com.basketball.dto.response.PointsDeductResultVO;
import com.basketball.security.JwtTokenProvider;
import com.basketball.service.IPointsService;
import com.basketball.vo.PointsRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 积分控制器
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
@RestController
@RequestMapping("/api/points")
@Tag(name = "积分管理", description = "积分相关接口")
public class PointsController {

    @Resource
    private IPointsService pointsService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 计算积分抵扣
     */
    @GetMapping("/calculate-deduct")
    @Operation(summary = "计算积分抵扣")
    public Result<PointsDeductResultVO> calculateDeduct(
            @RequestParam Integer pointsToUse,
            @RequestParam BigDecimal orderAmount,
            HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        PointsDeductResultVO result = pointsService.calculatePointsDeduct(userId, pointsToUse, orderAmount);
        return Result.success(result);
    }

    /**
     * 使用积分抵扣
     */
    @PostMapping("/use-deduct")
    @Operation(summary = "使用积分抵扣")
    public Result<PointsDeductResultVO> useDeduct(
            @Valid @RequestBody PointsDeductDTO deductDTO,
            HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        PointsDeductResultVO result = pointsService.usePointsDeduct(userId, deductDTO);
        return Result.success(result);
    }

    /**
     * 获取用户积分信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户积分信息")
    public Result<Map<String, Object>> getPointsInfo(HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        Map<String, Object> info = pointsService.getUserPointsInfo(userId);
        return Result.success(info);
    }

    /**
     * 获取用户积分记录
     */
    @GetMapping("/records")
    @Operation(summary = "获取用户积分记录")
    public Result<List<PointsRecordVO>> getPointsRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        List<PointsRecordVO> records = pointsService.getUserPointsRecords(userId, page, pageSize);
        return Result.success(records);
    }

    /**
     * 获取积分使用规则
     */
    @GetMapping("/rules")
    @Operation(summary = "获取积分使用规则")
    public Result<Map<String, Object>> getPointsRules() {
        Map<String, Object> rules = pointsService.getPointsRules();
        return Result.success(rules);
    }
}
