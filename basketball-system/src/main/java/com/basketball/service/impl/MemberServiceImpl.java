package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.CardConsumeDTO;
import com.basketball.dto.request.CardPurchaseDTO;
import com.basketball.dto.request.CardRechargeDTO;
import com.basketball.entity.*;
import com.basketball.mapper.*;
import com.basketball.service.IMemberService;
import com.basketball.vo.MemberCardRecordVO;
import com.basketball.vo.MemberCardTypeVO;
import com.basketball.vo.MemberCardVO;
import com.basketball.vo.PointsRecordVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会员服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Service
public class MemberServiceImpl implements IMemberService {

    @Resource
    private MemberCardTypeMapper cardTypeMapper;

    @Resource
    private MemberCardMapper memberCardMapper;

    @Resource
    private MemberCardRecordMapper cardRecordMapper;

    @Resource
    private PointsRecordMapper pointsRecordMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public PageResult<MemberCardTypeVO> listCardTypes() {
        // 查询上架的会员卡类型
        LambdaQueryWrapper<MemberCardType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCardType::getStatus, 1)
                .orderByAsc(MemberCardType::getSortOrder);

        List<MemberCardType> types = cardTypeMapper.selectList(wrapper);

        // 转换为VO
        List<MemberCardTypeVO> voList = types.stream()
                .map(this::convertToCardTypeVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, (long) voList.size(), 1L, (long) voList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long purchaseCard(Long userId, CardPurchaseDTO dto) {
        // 1. 查询卡类型
        MemberCardType cardType = cardTypeMapper.selectById(dto.getCardTypeId());
        if (cardType == null || cardType.getStatus() != 1) {
            throw new BusinessException("会员卡类型不存在或已下架");
        }

        // 2. 检查用户余额(如果使用余额支付)
        if (dto.getPayMethod() == 2) {
            User user = userMapper.selectById(userId);
            if (user.getBalance().compareTo(cardType.getPrice()) < 0) {
                throw new BusinessException("余额不足");
            }
            // 扣除余额
            user.setBalance(user.getBalance().subtract(cardType.getPrice()));
            userMapper.updateById(user);
        }

        // 3. 创建会员卡
        MemberCard card = new MemberCard();
        card.setUserId(userId);
        card.setCardTypeId(cardType.getId());
        card.setCardNo(generateCardNo());
        card.setStatus(0); // 未激活

        // 根据卡类型设置相应字段
        if (cardType.getCardType() == 1) {
            // 时间卡
            card.setStartDate(LocalDate.now());
            card.setExpireDate(LocalDate.now().plusDays(cardType.getDuration()));
        } else if (cardType.getCardType() == 2) {
            // 次卡
            card.setRemainingTimes(cardType.getTimes());
        } else if (cardType.getCardType() == 3) {
            // 储值卡
            card.setBalance(cardType.getPrice());
        }

        memberCardMapper.insert(card);

        // 4. 记录购买记录
        MemberCardRecord record = new MemberCardRecord();
        record.setCardId(card.getId());
        record.setUserId(userId);
        record.setRecordType(1); // 充值
        record.setChangeAmount(cardType.getPrice());
        record.setDescription("购买会员卡");
        cardRecordMapper.insert(record);

        // 5. 更新用户会员等级
        User user = userMapper.selectById(userId);
        if (user.getMemberLevel() < cardType.getMemberLevel()) {
            user.setMemberLevel(cardType.getMemberLevel());
            userMapper.updateById(user);
        }

        return card.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rechargeCard(Long userId, CardRechargeDTO dto) {
        // 1. 查询会员卡
        MemberCard card = memberCardMapper.selectById(dto.getCardId());
        if (card == null || !card.getUserId().equals(userId)) {
            throw new BusinessException("会员卡不存在");
        }

        MemberCardType cardType = cardTypeMapper.selectById(card.getCardTypeId());
        if (cardType.getCardType() != 3) {
            throw new BusinessException("只有储值卡可以充值");
        }

        // 2. 检查余额(如果使用余额支付)
        if (dto.getPayMethod() == 2) {
            User user = userMapper.selectById(userId);
            if (user.getBalance().compareTo(dto.getAmount()) < 0) {
                throw new BusinessException("余额不足");
            }
            user.setBalance(user.getBalance().subtract(dto.getAmount()));
            userMapper.updateById(user);
        }

        // 3. 充值
        BigDecimal balanceBefore = card.getBalance();
        card.setBalance(card.getBalance().add(dto.getAmount()));
        memberCardMapper.updateById(card);

        // 4. 记录
        MemberCardRecord record = new MemberCardRecord();
        record.setCardId(card.getId());
        record.setUserId(userId);
        record.setRecordType(1); // 充值
        record.setChangeAmount(dto.getAmount());
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(card.getBalance());
        record.setDescription("会员卡充值");
        cardRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consumeCard(Long userId, CardConsumeDTO dto) {
        // 1. 查询会员卡
        MemberCard card = memberCardMapper.selectById(dto.getCardId());
        if (card == null || !card.getUserId().equals(userId)) {
            throw new BusinessException("会员卡不存在");
        }

        if (card.getStatus() != 1) {
            throw new BusinessException("会员卡状态异常");
        }

        MemberCardType cardType = cardTypeMapper.selectById(card.getCardTypeId());

        MemberCardRecord record = new MemberCardRecord();
        record.setCardId(card.getId());
        record.setUserId(userId);
        record.setRecordType(2); // 消费
        record.setRelatedOrderNo(dto.getOrderNo());
        record.setDescription(dto.getDescription());

        // 2. 根据卡类型扣除
        if (cardType.getCardType() == 2) {
            // 次卡
            if (card.getRemainingTimes() < dto.getTimes()) {
                throw new BusinessException("剩余次数不足");
            }
            record.setTimesBefore(card.getRemainingTimes());
            record.setChangeTimes(-dto.getTimes());
            card.setRemainingTimes(card.getRemainingTimes() - dto.getTimes());
            record.setTimesAfter(card.getRemainingTimes());
        } else if (cardType.getCardType() == 3) {
            // 储值卡
            if (card.getBalance().compareTo(dto.getAmount()) < 0) {
                throw new BusinessException("余额不足");
            }
            record.setBalanceBefore(card.getBalance());
            record.setChangeAmount(dto.getAmount().negate());
            card.setBalance(card.getBalance().subtract(dto.getAmount()));
            record.setBalanceAfter(card.getBalance());
        }

        memberCardMapper.updateById(card);
        cardRecordMapper.insert(record);
    }

    @Override
    public PageResult<MemberCardVO> getMyCards(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getUserId, userId)
                .orderByDesc(MemberCard::getCreateTime);

        Page<MemberCard> pageParam = new Page<>(page, pageSize);
        Page<MemberCard> result = memberCardMapper.selectPage(pageParam, wrapper);

        // 转换为VO
        List<MemberCardVO> voList = result.getRecords().stream()
                .map(this::convertToCardVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<MemberCardRecordVO> getCardRecords(Long userId, Long cardId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<MemberCardRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCardRecord::getUserId, userId);
        if (cardId != null) {
            wrapper.eq(MemberCardRecord::getCardId, cardId);
        }
        wrapper.orderByDesc(MemberCardRecord::getCreateTime);

        Page<MemberCardRecord> pageParam = new Page<>(page, pageSize);
        Page<MemberCardRecord> result = cardRecordMapper.selectPage(pageParam, wrapper);

        // 转换为VO
        List<MemberCardRecordVO> voList = result.getRecords().stream()
                .map(this::convertToCardRecordVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Integer getMyPoints(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getPoints() : 0;
    }

    @Override
    public PageResult<PointsRecordVO> getPointsRecords(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreateTime);

        Page<PointsRecord> pageParam = new Page<>(page, pageSize);
        Page<PointsRecord> result = pointsRecordMapper.selectPage(pageParam, wrapper);

        // 转换为VO
        List<PointsRecordVO> voList = result.getRecords().stream()
                .map(this::convertToPointsRecordVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, BigDecimal amount, String orderNo) {
        // 消费送积分规则: 每消费1元送1积分
        int points = amount.intValue();

        if (points <= 0) {
            return;
        }

        // 更新用户积分
        User user = userMapper.selectById(userId);
        int pointsBefore = user.getPoints();
        user.setPoints(pointsBefore + points);
        userMapper.updateById(user);

        // 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(1); // 消费赠送
        record.setRelatedOrder(orderNo);
        record.setPointsBefore(pointsBefore);
        record.setPointsAfter(user.getPoints());
        record.setExpireDate(LocalDate.now().plusYears(1)); // 1年后过期
        record.setRemark("消费赠送积分");
        pointsRecordMapper.insert(record);
    }

    /**
     * 生成卡号
     */
    private String generateCardNo() {
        return "CARD" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    /**
     * 转换为会员卡类型VO
     */
    private MemberCardTypeVO convertToCardTypeVO(MemberCardType cardType) {
        MemberCardTypeVO vo = new MemberCardTypeVO();
        BeanUtils.copyProperties(cardType, vo);

        // 处理benefits JSON
        if (cardType.getBenefits() != null) {
            try {
                List<String> benefits = objectMapper.readValue(
                        cardType.getBenefits(),
                        new TypeReference<List<String>>() {}
                );
                vo.setBenefitsList(benefits);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        return vo;
    }

    /**
     * 转换为会员卡VO
     */
    private MemberCardVO convertToCardVO(MemberCard card) {
        MemberCardVO vo = new MemberCardVO();
        BeanUtils.copyProperties(card, vo);

        // 填充卡类型信息
        MemberCardType cardType = cardTypeMapper.selectById(card.getCardTypeId());
        if (cardType != null) {
            vo.setCardName(cardType.getCardName());
            vo.setCardType(cardType.getCardType());
        }

        return vo;
    }

    /**
     * 转换为会员卡记录VO
     */
    private MemberCardRecordVO convertToCardRecordVO(MemberCardRecord record) {
        MemberCardRecordVO vo = new MemberCardRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 填充卡信息
        MemberCard card = memberCardMapper.selectById(record.getCardId());
        if (card != null) {
            vo.setCardNo(card.getCardNo());
            MemberCardType cardType = cardTypeMapper.selectById(card.getCardTypeId());
            if (cardType != null) {
                vo.setCardName(cardType.getCardName());
            }
        }

        return vo;
    }

    /**
     * 转换为积分记录VO
     */
    private PointsRecordVO convertToPointsRecordVO(PointsRecord record) {
        PointsRecordVO vo = new PointsRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}
