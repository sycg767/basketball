package com.basketball.controller;

import com.basketball.entity.FinancialRecord;
import com.basketball.service.IFinancialRecordService;
import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/financial")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FinancialRecordController {

    @Autowired
    private IFinancialRecordService financialRecordService;

    @GetMapping("/page")
    public Result<PageResult<FinancialRecord>> getFinancialRecordPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer recordType,
            @RequestParam(required = false) Integer businessType,
            @RequestParam(required = false) String orderNo) {

        var page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<FinancialRecord>(current, size);
        var result = financialRecordService.getFinancialRecordPage(page, recordType, businessType, orderNo);

        PageResult<FinancialRecord> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent(),
                result.getPages()
        );

        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getFinancialStatistics() {
        BigDecimal totalIncome = financialRecordService.getTotalIncome();
        BigDecimal totalExpense = financialRecordService.getTotalExpense();
        BigDecimal balance = financialRecordService.getBalance();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        statistics.put("totalExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);
        statistics.put("balance", balance != null ? balance : BigDecimal.ZERO);

        return Result.success(statistics);
    }

    @GetMapping("/balance")
    public Result<Map<String, BigDecimal>> getBalance() {
        BigDecimal balance = financialRecordService.getBalance();
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("balance", balance != null ? balance : BigDecimal.ZERO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<FinancialRecord> getFinancialRecordById(@PathVariable Long id) {
        FinancialRecord record = financialRecordService.getById(id);
        if (record == null) {
            return Result.error("财务记录不存在");
        }
        return Result.success(record);
    }

    @PostMapping("/create")
    public Result<FinancialRecord> createFinancialRecord(@RequestBody FinancialRecord record) {
        record.setId(null);
        boolean success = financialRecordService.save(record);
        if (success) {
            return Result.success(record);
        } else {
            return Result.error("创建财务记录失败");
        }
    }

    @PostMapping("/update")
    public Result<FinancialRecord> updateFinancialRecord(@RequestBody FinancialRecord record) {
        if (record.getId() == null) {
            return Result.error("财务记录ID不能为空");
        }
        boolean success = financialRecordService.updateById(record);
        if (success) {
            return Result.success(record);
        } else {
            return Result.error("更新财务记录失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteFinancialRecord(@PathVariable Long id) {
        boolean success = financialRecordService.removeById(id);
        return success ? Result.success() : Result.error("删除财务记录失败");
    }
}