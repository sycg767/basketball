package com.basketball.controller;

import com.basketball.annotation.OperationLog;
import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.entity.NotificationTemplate;
import com.basketball.service.INotificationTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 管理端通知模板控制器
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/notification-template")
public class AdminNotificationTemplateController {

    @Resource
    private INotificationTemplateService notificationTemplateService;

    /**
     * 分页查询通知模板
     */
    @GetMapping("/page")
    public Result<PageResult<NotificationTemplate>> getTemplatePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String templateType,
            @RequestParam(required = false) String scene,
            @RequestParam(required = false) String keyword
    ) {
        log.info("分页查询通知模板: page={}, size={}, templateType={}, scene={}, keyword={}",
                page, size, templateType, scene, keyword);

        PageResult<NotificationTemplate> result = notificationTemplateService.getTemplatePage(
                page, size, templateType, scene, keyword
        );

        return Result.success(result);
    }

    /**
     * 根据ID查询模板详情
     */
    @GetMapping("/{id}")
    public Result<NotificationTemplate> getTemplateById(@PathVariable Long id) {
        log.info("查询模板详情: id={}", id);

        NotificationTemplate template = notificationTemplateService.getById(id);

        if (template == null) {
            return Result.error("模板不存在");
        }

        return Result.success(template);
    }

    /**
     * 新增通知模板
     */
    @PostMapping
    @OperationLog("新增通知模板")
    public Result<Void> addTemplate(@RequestBody NotificationTemplate template) {
        log.info("新增通知模板: templateCode={}, templateName={}",
                template.getTemplateCode(), template.getTemplateName());

        // 检查模板编码是否已存在
        NotificationTemplate existing = notificationTemplateService.getByTemplateCode(template.getTemplateCode());
        if (existing != null) {
            return Result.error("模板编码已存在");
        }

        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());

        notificationTemplateService.save(template);

        return Result.success();
    }

    /**
     * 更新通知模板
     */
    @PutMapping("/{id}")
    @OperationLog("更新通知模板")
    public Result<Void> updateTemplate(@PathVariable Long id, @RequestBody NotificationTemplate template) {
        log.info("更新通知模板: id={}, templateCode={}", id, template.getTemplateCode());

        NotificationTemplate existing = notificationTemplateService.getById(id);
        if (existing == null) {
            return Result.error("模板不存在");
        }

        // 如果修改了模板编码,检查新编码是否已被使用
        if (!existing.getTemplateCode().equals(template.getTemplateCode())) {
            NotificationTemplate codeCheck = notificationTemplateService.getByTemplateCode(template.getTemplateCode());
            if (codeCheck != null && !codeCheck.getId().equals(id)) {
                return Result.error("模板编码已被使用");
            }
        }

        template.setId(id);
        template.setUpdateTime(LocalDateTime.now());

        notificationTemplateService.updateById(template);

        return Result.success();
    }

    /**
     * 删除通知模板
     */
    @DeleteMapping("/{id}")
    @OperationLog("删除通知模板")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        log.info("删除通知模板: id={}", id);

        NotificationTemplate template = notificationTemplateService.getById(id);
        if (template == null) {
            return Result.error("模板不存在");
        }

        notificationTemplateService.removeById(id);

        return Result.success();
    }

    /**
     * 启用/禁用模板
     */
    @PutMapping("/{id}/status")
    @OperationLog("切换模板状态")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("切换模板状态: id={}, status={}", id, status);

        notificationTemplateService.toggleStatus(id, status);

        return Result.success();
    }
}
