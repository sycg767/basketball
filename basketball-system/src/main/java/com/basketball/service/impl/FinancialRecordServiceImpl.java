package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.FinancialRecord;
import com.basketball.mapper.FinancialRecordMapper;
import com.basketball.service.IFinancialRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class FinancialRecordServiceImpl extends ServiceImpl<FinancialRecordMapper, FinancialRecord> implements IFinancialRecordService {

    @Override
    public Page<FinancialRecord> getFinancialRecordPage(Page<FinancialRecord> page, Integer recordType, Integer businessType, String orderNo) {
        LambdaQueryWrapper<FinancialRecord> wrapper = new LambdaQueryWrapper<>();
        if (recordType != null) {
            wrapper.eq(FinancialRecord::getRecordType, recordType);
        }
        if (businessType != null) {
            wrapper.eq(FinancialRecord::getBusinessType, businessType);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(FinancialRecord::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(FinancialRecord::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public FinancialRecord createFinancialRecord(FinancialRecord record) {
        if (record.getRecordNo() == null || record.getRecordNo().isEmpty()) {
            record.setRecordNo(generateRecordNo());
        }
        record.setCreateTime(LocalDateTime.now());
        save(record);
        return record;
    }

    @Override
    @Transactional
    public FinancialRecord createIncomeRecord(Long userId, BigDecimal amount, Integer businessType, String orderNo, String description) {
        FinancialRecord record = new FinancialRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(1); // 收入
        record.setBusinessType(businessType);
        record.setOrderNo(orderNo);
        record.setUserId(userId);
        record.setAmount(amount);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());

        // 计算余额
        BigDecimal currentBalance = getBalance();
        record.setBalanceBefore(currentBalance);
        record.setBalanceAfter(currentBalance.add(amount));

        save(record);
        return record;
    }

    @Override
    @Transactional
    public FinancialRecord createExpenseRecord(Long userId, BigDecimal amount, Integer businessType, String orderNo, String description) {
        FinancialRecord record = new FinancialRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(2); // 支出
        record.setBusinessType(businessType);
        record.setOrderNo(orderNo);
        record.setUserId(userId);
        record.setAmount(amount);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());

        // 计算余额
        BigDecimal currentBalance = getBalance();
        record.setBalanceBefore(currentBalance);
        record.setBalanceAfter(currentBalance.subtract(amount));

        save(record);
        return record;
    }

    @Override
    @Transactional
    public FinancialRecord createRefundRecord(Long userId, BigDecimal amount, Integer businessType, String orderNo, String description) {
        FinancialRecord record = new FinancialRecord();
        record.setRecordNo(generateRecordNo());
        record.setRecordType(3); // 退款
        record.setBusinessType(businessType);
        record.setOrderNo(orderNo);
        record.setUserId(userId);
        record.setAmount(amount);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());

        // 计算余额
        BigDecimal currentBalance = getBalance();
        record.setBalanceBefore(currentBalance);
        record.setBalanceAfter(currentBalance.add(amount));

        save(record);
        return record;
    }

    @Override
    public BigDecimal getTotalIncome() {
        LambdaQueryWrapper<FinancialRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinancialRecord::getRecordType, 1); // 收入
        java.math.BigDecimal total = baseMapper.selectList(wrapper).stream()
                .map(FinancialRecord::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return total;
    }

    @Override
    public BigDecimal getTotalExpense() {
        LambdaQueryWrapper<FinancialRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinancialRecord::getRecordType, 2); // 支出
        java.math.BigDecimal total = baseMapper.selectList(wrapper).stream()
                .map(FinancialRecord::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return total;
    }

    @Override
    public BigDecimal getBalance() {
        BigDecimal totalIncome = getTotalIncome();
        BigDecimal totalExpense = getTotalExpense();

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        return totalIncome.subtract(totalExpense);
    }

    private String generateRecordNo() {
        String prefix = "FR";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(new Random().nextInt(1000));
        return prefix + timestamp + random;
    }
}