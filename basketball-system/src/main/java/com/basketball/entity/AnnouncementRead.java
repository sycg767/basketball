package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("announcement_read")
public class AnnouncementRead {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long announcementId;

    private Long userId;

    private LocalDateTime readTime;
}