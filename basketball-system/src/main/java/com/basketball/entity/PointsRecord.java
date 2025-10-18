package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 积分记录实体类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Data
@TableName("points_record")
@Schema(description = "积分记录实体")
public class PointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "积分记录ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "积分变动(正数为获得,负数为消费)")
    private Integer points;

    @Schema(description = "类型: 1-消费赠送, 2-兑换商品, 3-签到, 4-活动, 5-过期扣除, 6-管理员调整")
    private Integer type;

    @Schema(description = "关联订单号")
    private String relatedOrder;

    @Schema(description = "变动前积分")
    private Integer pointsBefore;

    @Schema(description = "变动后积分")
    private Integer pointsAfter;

    @Schema(description = "积分过期日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
