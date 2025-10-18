package com.basketball.controller;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.VenueQueryDTO;
import com.basketball.service.IVenueService;
import com.basketball.vo.VenuePriceVO;
import com.basketball.vo.VenueVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 场地控制器（用户端）
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Tag(name = "场地管理", description = "用户端场地查询接口")
@RestController
@RequestMapping("/api/venue")
@Validated
public class VenueController {

    @Resource
    private IVenueService venueService;

    /**
     * 场地列表
     */
    @Operation(summary = "场地列表", description = "分页查询场地列表，支持筛选")
    @GetMapping("/list")
    public Result<PageResult<VenueVO>> list(VenueQueryDTO queryDTO) {
        PageResult<VenueVO> result = venueService.listVenues(queryDTO);
        return Result.success(result);
    }

    /**
     * 场地详情
     */
    @Operation(summary = "场地详情", description = "根据ID查询场地详细信息")
    @GetMapping("/{id}")
    public Result<VenueVO> getById(
            @Parameter(description = "场地ID", required = true)
            @PathVariable Long id) {
        VenueVO venueVO = venueService.getVenueById(id);
        return Result.success(venueVO);
    }

    /**
     * 可用场地列表
     */
    @Operation(summary = "可用场地列表", description = "查询所有可用的场地")
    @GetMapping("/available")
    public Result<List<VenueVO>> listAvailable() {
        List<VenueVO> list = venueService.listAvailableVenues();
        return Result.success(list);
    }

    /**
     * 场地价格列表
     */
    @Operation(summary = "场地价格列表", description = "查询指定场地的价格配置")
    @GetMapping("/{id}/prices")
    public Result<List<VenuePriceVO>> getPrices(
            @Parameter(description = "场地ID", required = true)
            @PathVariable Long id) {
        List<VenuePriceVO> prices = venueService.getVenuePrices(id);
        return Result.success(prices);
    }
}
