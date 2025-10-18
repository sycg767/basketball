package com.basketball.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预订VO
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@Schema(description = "预订信息")
public class BookingVO {

    @Schema(description = "预订ID")
    private Long id;

    @Schema(description = "预订编号")
    private String bookingNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "场地ID")
    private Long venueId;

    @Schema(description = "场地名称")
    private String venueName;

    @Schema(description = "场地位置")
    private String venueLocation;

    @Schema(description = "预订日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "时间段")
    private String timeSlot;

    @Schema(description = "时长(小时)")
    private Integer duration;

    @Schema(description = "预订类型：1-按时段，2-包场")
    private Integer bookingType;

    @Schema(description = "总价")
    private BigDecimal totalPrice;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "实际支付金额")
    private BigDecimal actualPrice;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "支付方式：1-在线支付，2-余额，3-会员卡，4-现场支付")
    private Integer paymentMethod;

    @Schema(description = "支付方式名称")
    private String payMethodName;

    @Schema(description = "状态：0-待支付，1-已支付，2-已取消，3-已完成，4-已退款，5-超时取消")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "取消时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "人数")
    private Integer peopleCount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否已签到：0-否，1-是")
    private Integer isCheckedIn;

    @Schema(description = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
