package com.basketball.controller;

import com.basketball.annotation.CurrentUserId;
import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import com.basketball.dto.request.NotificationSendDTO;
import com.basketball.dto.response.NotificationVO;
import com.basketball.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 通知控制器
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Tag(name = "通知管理", description = "站内通知、短信通知、邮件通知等")
@RestController
@RequestMapping("/api/notification")
@Validated
public class NotificationController {

    @Resource
    private INotificationService notificationService;

    /**
     * 发送通知
     */
    @Operation(summary = "发送通知", description = "发送通知给指定用户")
    @PostMapping("/send")
    public Result<Long> sendNotification(@Valid @RequestBody NotificationSendDTO sendDTO) {
        log.info("发送通知: userId={}, templateCode={}", sendDTO.getUserId(), sendDTO.getTemplateCode());
        Long notificationId = notificationService.sendNotification(sendDTO);
        return Result.success(notificationId);
    }

    /**
     * 获取未读通知列表
     */
    @Operation(summary = "获取未读通知", description = "获取当前用户的未读通知列表")
    @GetMapping("/unread")
    public Result<PageResult<NotificationVO>> getUnreadNotifications(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Parameter(description = "页码", required = true)
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", required = true)
            @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info("获取未读通知: userId={}, page={}, pageSize={}", userId, page, pageSize);
        PageResult<NotificationVO> result = notificationService.getUnreadNotifications(userId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 获取所有通知列表
     */
    @Operation(summary = "获取所有通知", description = "获取当前用户的所有通知列表")
    @GetMapping("/list")
    public Result<PageResult<NotificationVO>> getAllNotifications(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Parameter(description = "页码", required = true)
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", required = true)
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "模板编码", required = false)
            @RequestParam(required = false) String templateCode) {

        log.info("获取所有通知: userId={}, page={}, pageSize={}, templateCode={}", userId, page, pageSize, templateCode);
        PageResult<NotificationVO> result = notificationService.getAllNotifications(userId, page, pageSize, templateCode);
        return Result.success(result);
    }

    /**
     * 标记通知为已读
     */
    @Operation(summary = "标记为已读", description = "标记指定通知为已读")
    @PostMapping("/read/{notificationId}")
    public Result<Void> markAsRead(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Parameter(description = "通知ID", required = true)
            @PathVariable Long notificationId) {

        log.info("标记通知为已读: userId={}, notificationId={}", userId, notificationId);
        notificationService.markAsRead(notificationId, userId);
        return Result.success("标记成功");
    }

    /**
     * 标记所有通知为已读
     */
    @Operation(summary = "全部标记为已读", description = "标记当前用户的所有通知为已读")
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId) {

        log.info("标记所有通知为已读: userId={}", userId);
        notificationService.markAllAsRead(userId);
        return Result.success("标记成功");
    }

    /**
     * 删除通知
     */
    @Operation(summary = "删除通知", description = "删除指定通知")
    @DeleteMapping("/{notificationId}")
    public Result<Void> deleteNotification(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId,
            @Parameter(description = "通知ID", required = true)
            @PathVariable Long notificationId) {

        log.info("删除通知: userId={}, notificationId={}", userId, notificationId);
        notificationService.deleteNotification(notificationId, userId);
        return Result.success("删除成功");
    }

    /**
     * 获取未读数量
     */
    @Operation(summary = "获取未读数量", description = "获取当前用户的未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId) {

        log.info("获取未读数量: userId={}", userId);
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 获取通知统计信息
     */
    @Operation(summary = "获取通知统计", description = "获取当前用户各类型通知的统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Long>> getStatistics(
            @Parameter(description = "用户ID", required = true)
            @CurrentUserId Long userId) {

        log.info("获取通知统计: userId={}", userId);
        Map<String, Long> statistics = notificationService.getNotificationStatistics(userId);
        return Result.success(statistics);
    }
}
