package com.basketball.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知模板实体类
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification_template")
@Schema(description = "通知模板实体")
public class NotificationTemplate extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板编码(唯一)")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板类型: sms/wechat/system/email")
    private String templateType;

    @Schema(description = "使用场景: member_expire/course_remind/booking_success等")
    private String scene;

    @Schema(description = "标题模板")
    private String title;

    @Schema(description = "内容模板(支持变量占位符)")
    private String content;

    @Schema(description = "变量列表(JSON数组)")
    private String variables;

    @Schema(description = "第三方平台模板ID")
    private String thirdPartyTemplateId;

    @Schema(description = "优先级: 1-低, 2-中, 3-高")
    private Integer priority;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "示例数据(JSON)")
    private String exampleData;

    @Schema(description = "备注")
    private String remark;
}
