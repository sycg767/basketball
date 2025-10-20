package com.basketball.controller;

import com.basketball.entity.Announcement;
import com.basketball.service.IAnnouncementService;
import com.basketball.common.result.PageResult;
import com.basketball.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/announcement")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminAnnouncementController {

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

    @PostMapping("/create")
    public Result<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        announcement.setId(null);
        boolean success = announcementService.publishAnnouncement(announcement);
        if (success) {
            return Result.success(announcement);
        } else {
            return Result.error("创建公告失败");
        }
    }

    @PostMapping("/update")
    public Result<Announcement> updateAnnouncement(@RequestBody Announcement announcement) {
        if (announcement.getId() == null) {
            return Result.error("公告ID不能为空");
        }
        boolean success = announcementService.updateById(announcement);
        if (success) {
            return Result.success(announcement);
        } else {
            return Result.error("更新公告失败");
        }
    }

    @PostMapping("/publish/{id}")
    public Result<Void> publishAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        announcement.setStatus(1); // 已发布
        boolean success = announcementService.updateById(announcement);
        return success ? Result.success() : Result.error("发布公告失败");
    }

    @PostMapping("/offline/{id}")
    public Result<Void> offlineAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        announcement.setStatus(2); // 已下架
        boolean success = announcementService.updateById(announcement);
        return success ? Result.success() : Result.error("下架公告失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.removeById(id);
        return success ? Result.success() : Result.error("删除公告失败");
    }

    @PostMapping("/upload-cover")
    public Result<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file) {
        // 这里简化处理，实际需要文件上传服务
        String fileName = "announcement-cover-" + System.currentTimeMillis() + ".jpg";
        String filePath = "/uploads/announcements/" + fileName;

        Map<String, String> result = new HashMap<>();
        result.put("url", filePath);
        result.put("fileName", fileName);
        return Result.success(result);
    }
}