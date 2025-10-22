package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.common.exception.BusinessException;
import com.basketball.dto.request.PointsDeductDTO;
import com.basketball.dto.response.PointsDeductResultVO;
import com.basketball.entity.PointsRecord;
import com.basketball.entity.User;
import com.basketball.mapper.PointsRecordMapper;
import com.basketball.mapper.UserMapper;
import com.basketball.service.IPointsService;
import com.basketball.vo.PointsRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 积分服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
@Slf4j
@Service
public class PointsServiceImpl implements IPointsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PointsRecordMapper pointsRecordMapper;

    // 积分使用规则常量
    private static final int POINTS_TO_YUAN = 100; // 100积分=1元
    private static final int MIN_POINTS_USE = 100; // 最少使用100积分
    private static final BigDecimal MAX_DEDUCT_RATIO = new BigDecimal("0.5"); // 最多抵扣50%

    /**
     * 计算积分抵扣
     */
    @Override
    public PointsDeductResultVO calculatePointsDeduct(Long userId, Integer pointsToUse, BigDecimal orderAmount) {
        // 1. 查询用户积分
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Integer userPoints = user.getPoints() != null ? user.getPoints() : 0;

        // 2. 校验积分是否足够
        if (userPoints < pointsToUse) {
            return PointsDeductResultVO.builder()
                    .available(false)
                    .message("积分不足，当前积分：" + userPoints)
                    .remainingPoints(userPoints)
                    .build();
        }

        // 3. 校验最少使用积分
        if (pointsToUse < MIN_POINTS_USE) {
            return PointsDeductResultVO.builder()
                    .available(false)
                    .message("最少使用" + MIN_POINTS_USE + "积分")
                    .remainingPoints(userPoints)
                    .build();
        }

        // 4. 计算抵扣金额：100积分=1元
        BigDecimal deductAmount = new BigDecimal(pointsToUse)
                .divide(new BigDecimal(POINTS_TO_YUAN), 2, RoundingMode.DOWN);

        // 5. 计算最大可抵扣金额（订单金额的50%）
        BigDecimal maxDeductAmount = orderAmount.multiply(MAX_DEDUCT_RATIO);

        // 6. 如果抵扣金额超过最大限制，调整为最大值
        if (deductAmount.compareTo(maxDeductAmount) > 0) {
            deductAmount = maxDeductAmount;
            // 重新计算实际使用的积分
            pointsToUse = deductAmount.multiply(new BigDecimal(POINTS_TO_YUAN)).intValue();
        }

        // 7. 计算实付金额
        BigDecimal finalAmount = orderAmount.subtract(deductAmount);

        // 8. 计算剩余积分
        Integer remainingPoints = userPoints - pointsToUse;

        return PointsDeductResultVO.builder()
                .originalAmount(orderAmount)
                .pointsUsed(pointsToUse)
                .deductAmount(deductAmount)
                .finalAmount(finalAmount)
                .remainingPoints(remainingPoints)
                .available(true)
                .message("成功使用" + pointsToUse + "积分抵扣" + deductAmount + "元")
                .build();
    }

    /**
     * 使用积分抵扣
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointsDeductResultVO usePointsDeduct(Long userId, PointsDeductDTO deductDTO) {
        // 1. 计算抵扣结果
        PointsDeductResultVO result = calculatePointsDeduct(
                userId,
                deductDTO.getPointsToUse(),
                deductDTO.getOrderAmount()
        );

        if (!result.getAvailable()) {
            throw new BusinessException(result.getMessage());
        }

        // 2. 扣除用户积分
        User user = userMapper.selectById(userId);
        Integer pointsBefore = user.getPoints() != null ? user.getPoints() : 0;
        Integer pointsAfter = pointsBefore - result.getPointsUsed();

        user.setPoints(pointsAfter);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 3. 创建积分使用记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(-result.getPointsUsed()); // 负数表示消费
        record.setType(2); // 2-兑换商品（这里用于抵扣支付）
        record.setRelatedOrder(deductDTO.getOrderNo());
        record.setPointsBefore(pointsBefore);
        record.setPointsAfter(pointsAfter);
        record.setRemark("积分抵扣支付：订单" + deductDTO.getOrderNo() +
                "，使用" + result.getPointsUsed() + "积分抵扣" + result.getDeductAmount() + "元");
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("积分抵扣成功: userId={}, points={}, orderNo={}, deductAmount={}",
                userId, result.getPointsUsed(), deductDTO.getOrderNo(), result.getDeductAmount());

        return result;
    }

    /**
     * 获取用户积分信息
     */
    @Override
    public Map<String, Object> getUserPointsInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Map<String, Object> info = new HashMap<>();
        Integer points = user.getPoints() != null ? user.getPoints() : 0;

        info.put("totalPoints", points);
        info.put("availablePoints", points);
        info.put("pointsValue", new BigDecimal(points).divide(new BigDecimal(POINTS_TO_YUAN), 2, RoundingMode.DOWN));
        info.put("memberLevel", user.getMemberLevel());

        // 查询即将过期的积分
        LambdaQueryWrapper<PointsRecord> expireQuery = new LambdaQueryWrapper<>();
        expireQuery.eq(PointsRecord::getUserId, userId)
                .gt(PointsRecord::getPoints, 0)
                .isNotNull(PointsRecord::getExpireDate)
                .le(PointsRecord::getExpireDate, java.time.LocalDate.now().plusDays(30))
                .ge(PointsRecord::getExpireDate, java.time.LocalDate.now());

        List<PointsRecord> expiringSoon = pointsRecordMapper.selectList(expireQuery);
        int expiringPoints = expiringSoon.stream().mapToInt(PointsRecord::getPoints).sum();
        info.put("expiringPoints", expiringPoints);

        return info;
    }

    /**
     * 获取用户积分记录
     */
    @Override
    public List<PointsRecordVO> getUserPointsRecords(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<PointsRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreateTime);

        int offset = (page - 1) * pageSize;
        queryWrapper.last("LIMIT " + offset + ", " + pageSize);

        List<PointsRecord> records = pointsRecordMapper.selectList(queryWrapper);
        List<PointsRecordVO> voList = new ArrayList<>();

        for (PointsRecord record : records) {
            PointsRecordVO vo = new PointsRecordVO();
            BeanUtils.copyProperties(record, vo);
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 获取积分使用规则
     */
    @Override
    public Map<String, Object> getPointsRules() {
        Map<String, Object> rules = new HashMap<>();

        rules.put("pointsToYuan", POINTS_TO_YUAN);
        rules.put("minPointsUse", MIN_POINTS_USE);
        rules.put("maxDeductRatio", MAX_DEDUCT_RATIO);

        List<String> ruleDescriptions = Arrays.asList(
                "100积分 = 1元",
                "单笔订单最多使用50%积分抵扣",
                "最低使用门槛：100积分起",
                "积分有效期：1年",
                "消费1元获得1积分"
        );
        rules.put("descriptions", ruleDescriptions);

        return rules;
    }
}
