package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("financial_record")
public class FinancialRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String recordNo;

    private Integer recordType;

    private Integer businessType;

    private String orderNo;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private Integer paymentMethod;

    private String description;

    private Long operatorId;

    private LocalDateTime createTime;
}