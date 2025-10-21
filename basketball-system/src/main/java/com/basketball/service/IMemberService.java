package com.basketball.service;

import com.basketball.common.result.PageResult;
import com.basketball.dto.request.CardConsumeDTO;
import com.basketball.dto.request.CardPurchaseDTO;
import com.basketball.dto.request.CardRechargeDTO;
import com.basketball.vo.MemberCardRecordVO;
import com.basketball.vo.MemberCardTypeVO;
import com.basketball.vo.MemberCardVO;
import com.basketball.vo.PointsRecordVO;

/**
 * 会员服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface IMemberService {

    /**
     * 获取会员卡类型列表
     *
     * @return 会员卡类型列表
     */
    PageResult<MemberCardTypeVO> listCardTypes();

    /**
     * 购买会员卡
     *
     * @param userId 用户ID
     * @param dto    购买请求
     * @return 会员卡ID
     */
    Long purchaseCard(Long userId, CardPurchaseDTO dto);

    /**
     * 会员卡充值
     *
     * @param userId 用户ID
     * @param dto    充值请求
     */
    void rechargeCard(Long userId, CardRechargeDTO dto);

    /**
     * 会员卡消费
     *
     * @param userId 用户ID
     * @param dto    消费请求
     */
    void consumeCard(Long userId, CardConsumeDTO dto);

    /**
     * 获取我的会员卡列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param pageSize 每页大小
     * @return 会员卡列表
     */
    PageResult<MemberCardVO> getMyCards(Long userId, Integer page, Integer pageSize);

    /**
     * 获取会员卡使用记录
     *
     * @param userId 用户ID
     * @param cardId 会员卡ID
     * @param page   页码
     * @param pageSize 每页大小
     * @return 使用记录列表
     */
    PageResult<MemberCardRecordVO> getCardRecords(Long userId, Long cardId, Integer page, Integer pageSize);

    /**
     * 获取我的积分
     *
     * @param userId 用户ID
     * @return 积分
     */
    Integer getMyPoints(Long userId);

    /**
     * 获取用户账户余额
     *
     * @param userId 用户ID
     * @return 账户余额
     */
    java.math.BigDecimal getUserBalance(Long userId);

    /**
     * 获取积分明细
     *
     * @param userId 用户ID
     * @param page   页码
     * @param pageSize 每页大小
     * @return 积分记录列表
     */
    PageResult<PointsRecordVO> getPointsRecords(Long userId, Integer page, Integer pageSize);

    /**
     * 消费送积分
     *
     * @param userId 用户ID
     * @param amount 消费金额
     * @param orderNo 订单号
     */
    void addPoints(Long userId, java.math.BigDecimal amount, String orderNo);

    /**
     * 激活会员卡
     *
     * @param userId 用户ID
     * @param cardId 会员卡ID
     */
    void activateCard(Long userId, Long cardId);

    /**
     * 账户余额充值
     *
     * @param userId 用户ID
     * @param dto 充值请求
     * @return 支付结果
     */
    com.basketball.dto.response.PaymentResultVO rechargeBalance(Long userId, com.basketball.dto.request.BalanceRechargeDTO dto);

    /**
     * 获取余额充值记录
     *
     * @param userId 用户ID
     * @param page   页码
     * @param pageSize 每页大小
     * @return 充值记录列表
     */
    PageResult<com.basketball.vo.BalanceRechargeRecordVO> getBalanceRechargeRecords(Long userId, Integer page, Integer pageSize);
}
