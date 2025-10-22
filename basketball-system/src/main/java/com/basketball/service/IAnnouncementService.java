package com.basketball.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAnnouncementService extends IService<Announcement> {

    Page<Announcement> getAnnouncementPage(Page<Announcement> page, Integer type, Integer status);

    Announcement getAnnouncementById(Long id);

    boolean publishAnnouncement(Announcement announcement);

    boolean markAsRead(Long announcementId, Long userId);

    boolean hasReadAnnouncement(Long announcementId, Long userId);

    Long countUnreadAnnouncements(Long userId);

    java.util.Map<Long, Boolean> getReadStatusBatch(java.util.List<Long> announcementIds, Long userId);
}