package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 统计缓存实体类
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("statistics_cache")
@Schema(description = "统计缓存")
public class StatisticsCache extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "缓存ID")
    private Long id;

    @Schema(description = "缓存Key")
    private String cacheKey;

    @Schema(description = "统计类型: member_activity-会员活跃, course_popularity-课程热度, venue_usage-场地使用")
    private String statsType;

    @Schema(description = "缓存数据(JSON)")
    private String cacheData;

    @Schema(description = "有效期(秒)")
    private Integer ttl;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "命中次数")
    private Integer hitCount;

    @Schema(description = "最后访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime;

    @Schema(description = "状态: 0-失效, 1-有效")
    private Integer status;
}
