package com.basketball.controller;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.CardTypeDTO;
import com.basketball.entity.MemberCardType;
import com.basketball.service.IAdminMemberService;
import com.basketball.vo.MemberCardTypeVO;
import com.basketball.vo.MemberCardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 管理员会员管理控制器
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/api/admin/member")
@Tag(name = "管理员会员管理")
public class AdminMemberController {

    @Resource
    private IAdminMemberService adminMemberService;

    /**
     * 获取会员卡类型列表
     */
    @GetMapping("/card-types")
    @Operation(summary = "获取会员卡类型列表")
    public Result<PageResult<MemberCardTypeVO>> getCardTypes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return Result.success(adminMemberService.listCardTypes(page, pageSize));
    }

    /**
     * 创建会员卡类型
     */
    @PostMapping("/card-type")
    @Operation(summary = "创建会员卡类型")
    public Result<Long> createCardType(@Valid @RequestBody CardTypeDTO dto) {
        return Result.success(adminMemberService.createCardType(dto));
    }

    /**
     * 更新会员卡类型
     */
    @PutMapping("/card-type/{id}")
    @Operation(summary = "更新会员卡类型")
    public Result<Void> updateCardType(
            @PathVariable Long id,
            @Valid @RequestBody CardTypeDTO dto
    ) {
        adminMemberService.updateCardType(id, dto);
        return Result.success();
    }

    /**
     * 删除会员卡类型
     */
    @DeleteMapping("/card-type/{id}")
    @Operation(summary = "删除会员卡类型")
    public Result<Void> deleteCardType(@PathVariable Long id) {
        adminMemberService.deleteCardType(id);
        return Result.success();
    }

    /**
     * 更新会员卡类型状态
     */
    @PutMapping("/card-type/{id}/status")
    @Operation(summary = "更新会员卡类型状态")
    public Result<Void> updateCardTypeStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        adminMemberService.updateCardTypeStatus(id, status);
        return Result.success();
    }

    /**
     * 获取所有会员卡列表
     */
    @GetMapping("/cards")
    @Operation(summary = "获取所有会员卡列表")
    public Result<PageResult<MemberCardVO>> getAllCards(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return Result.success(adminMemberService.getAllCards(keyword, status, page, pageSize));
    }

    /**
     * 激活会员卡
     */
    @PutMapping("/card/{id}/activate")
    @Operation(summary = "激活会员卡")
    public Result<Void> activateCard(@PathVariable Long id) {
        adminMemberService.activateCard(id);
        return Result.success();
    }

    /**
     * 冻结会员卡
     */
    @PutMapping("/card/{id}/freeze")
    @Operation(summary = "冻结会员卡")
    public Result<Void> freezeCard(@PathVariable Long id) {
        adminMemberService.freezeCard(id);
        return Result.success();
    }
}
