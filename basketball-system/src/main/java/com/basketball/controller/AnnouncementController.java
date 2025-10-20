package com.basketball.controller;

import com.basketball.entity.Announcement;
import com.basketball.service.IAnnouncementService;
import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/announcement")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AnnouncementController {

    @Autowired
    private IAnnouncementService announcementService;

    @GetMapping("/page")
    public Result<PageResult<Announcement>> getAnnouncementPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {

        var page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Announcement>(current, size);
        var result = announcementService.getAnnouncementPage(page, type, status);

        PageResult<Announcement> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent(),
                result.getPages()
        );

        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        return Result.success(announcement);
    }

    @PostMapping("/read/{announcementId}")
    public Result<Void> markAsRead(@PathVariable Long announcementId) {
        // 从上下文获取用户ID，这里简化处理
        Long userId = 1L; // 实际中应该从JWT Token中获取
        boolean success = announcementService.markAsRead(announcementId, userId);
        return success ? Result.success() : Result.error("标记已读失败");
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Object>> getUnreadCount() {
        // 从上下文获取用户ID，这里简化处理
        Long userId = 1L; // 实际中应该从JWT Token中获取
        Long unreadCount = announcementService.countUnreadAnnouncements(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", unreadCount);
        return Result.success(result);
    }
}