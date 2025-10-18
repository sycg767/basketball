package com.basketball.controller;

import com.basketball.common.result.Result;
import com.basketball.dto.request.VenueCreateDTO;
import com.basketball.dto.request.VenuePriceDTO;
import com.basketball.dto.request.VenueUpdateDTO;
import com.basketball.service.IVenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 场地管理控制器（管理端）
 *
 * @author Basketball Team
 * @date 2025-10-01
 */
@Tag(name = "场地管理（管理员）", description = "管理员场地管理接口")
@RestController
@RequestMapping("/api/admin/venue")
@Validated
public class AdminVenueController {

    @Resource
    private IVenueService venueService;

    /**
     * 添加场地
     */
    @Operation(summary = "添加场地", description = "管理员添加新场地")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody VenueCreateDTO createDTO) {
        venueService.createVenue(createDTO);
        return Result.success();
    }

    /**
     * 修改场地
     */
    @Operation(summary = "修改场地", description = "管理员修改场地信息")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "场地ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody VenueUpdateDTO updateDTO) {
        venueService.updateVenue(id, updateDTO);
        return Result.success();
    }

    /**
     * 删除场地
     */
    @Operation(summary = "删除场地", description = "管理员删除场地")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "场地ID", required = true)
            @PathVariable Long id) {
        venueService.deleteVenue(id);
        return Result.success();
    }

    /**
     * 设置场地价格
     */
    @Operation(summary = "设置场地价格", description = "管理员设置场地价格配置")
    @PostMapping("/price")
    public Result<Void> setPrice(@Valid @RequestBody VenuePriceDTO priceDTO) {
        venueService.setVenuePrice(priceDTO);
        return Result.success();
    }
}
