package com.basketball.controller;

import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.CardConsumeDTO;
import com.basketball.dto.request.CardPurchaseDTO;
import com.basketball.dto.request.CardRechargeDTO;
import com.basketball.security.JwtTokenProvider;
import com.basketball.service.IMemberService;
import com.basketball.vo.MemberCardRecordVO;
import com.basketball.vo.MemberCardTypeVO;
import com.basketball.vo.MemberCardVO;
import com.basketball.vo.PointsRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 会员管理控制器
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/api/member")
@Tag(name = "会员管理")
public class MemberController {

    @Resource
    private IMemberService memberService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/card-types")
    @Operation(summary = "获取会员卡类型列表")
    public Result<PageResult<MemberCardTypeVO>> getCardTypes() {
        return Result.success(memberService.listCardTypes());
    }

    @PostMapping("/card/purchase")
    @Operation(summary = "购买会员卡")
    public Result<Long> purchaseCard(
            @Valid @RequestBody CardPurchaseDTO dto,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.purchaseCard(userId, dto));
    }

    @PostMapping("/card/recharge")
    @Operation(summary = "会员卡充值")
    public Result<Void> rechargeCard(
            @Valid @RequestBody CardRechargeDTO dto,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        memberService.rechargeCard(userId, dto);
        return Result.success();
    }

    @GetMapping("/card/my")
    @Operation(summary = "我的会员卡列表")
    public Result<PageResult<MemberCardVO>> getMyCards(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.getMyCards(userId, page, pageSize));
    }

    @GetMapping("/card/records")
    @Operation(summary = "会员卡使用记录")
    public Result<PageResult<MemberCardRecordVO>> getCardRecords(
            @RequestParam(required = false) Long cardId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.getCardRecords(userId, cardId, page, pageSize));
    }

    @GetMapping("/points")
    @Operation(summary = "我的积分")
    public Result<Integer> getMyPoints(HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.getMyPoints(userId));
    }

    @GetMapping("/balance")
    @Operation(summary = "获取账户余额")
    public Result<java.math.BigDecimal> getBalance(HttpServletRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.getUserBalance(userId));
    }

    @GetMapping("/points/records")
    @Operation(summary = "积分明细")
    public Result<PageResult<PointsRecordVO>> getPointsRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.getPointsRecords(userId, page, pageSize));
    }

    @PostMapping("/card/activate")
    @Operation(summary = "激活会员卡")
    public Result<Void> activateCard(
            @RequestParam Long cardId,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        memberService.activateCard(userId, cardId);
        return Result.success();
    }

    @PostMapping("/balance/recharge")
    @Operation(summary = "账户余额充值")
    public Result<com.basketball.dto.response.PaymentResultVO> rechargeBalance(
            @Valid @RequestBody com.basketball.dto.request.BalanceRechargeDTO dto,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.rechargeBalance(userId, dto));
    }

    @GetMapping("/balance/records")
    @Operation(summary = "获取余额充值记录")
    public Result<PageResult<com.basketball.vo.BalanceRechargeRecordVO>> getBalanceRechargeRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(memberService.getBalanceRechargeRecords(userId, page, pageSize));
    }
}
