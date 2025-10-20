package com.basketball.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.FinancialRecord;
import com.basketball.service.IFinancialRecordService;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface IFinancialRecordService extends IService<FinancialRecord> {

    Page<FinancialRecord> getFinancialRecordPage(Page<FinancialRecord> page, Integer recordType, Integer businessType, String orderNo);

    FinancialRecord createFinancialRecord(FinancialRecord record);

    FinancialRecord createIncomeRecord(Long userId, BigDecimal amount, Integer businessType, String orderNo, String description);

    FinancialRecord createExpenseRecord(Long userId, BigDecimal amount, Integer businessType, String orderNo, String description);

    FinancialRecord createRefundRecord(Long userId, BigDecimal amount, Integer businessType, String orderNo, String description);

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();

    BigDecimal getBalance();
}