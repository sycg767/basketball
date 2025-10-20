package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.entity.Announcement;
import com.basketball.entity.AnnouncementRead;
import com.basketball.mapper.AnnouncementMapper;
import com.basketball.mapper.AnnouncementReadMapper;
import com.basketball.service.IAnnouncementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {

    @Autowired
    private AnnouncementReadMapper announcementReadMapper;

    @Override
    public Page<Announcement> getAnnouncementPage(Page<Announcement> page, Integer type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Announcement getAnnouncementById(Long id) {
        Announcement announcement = getById(id);
        if (announcement != null) {
            announcement.setViewCount(announcement.getViewCount() + 1);
            updateById(announcement);
        }
        return announcement;
    }

    @Override
    @Transactional
    public boolean publishAnnouncement(Announcement announcement) {
        announcement.setPublishTime(LocalDateTime.now());
        announcement.setStatus(1); // 已发布
        announcement.setViewCount(0);
        return save(announcement);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long announcementId, Long userId) {
        AnnouncementRead announcementRead = new AnnouncementRead();
        announcementRead.setAnnouncementId(announcementId);
        announcementRead.setUserId(userId);
        announcementRead.setReadTime(LocalDateTime.now());

        // 检查是否已经阅读过
        LambdaQueryWrapper<AnnouncementRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementRead::getAnnouncementId, announcementId)
                .eq(AnnouncementRead::getUserId, userId);

        AnnouncementRead existing = announcementReadMapper.selectOne(wrapper);
        if (existing == null) {
            return announcementReadMapper.insert(announcementRead) > 0;
        }
        return true;
    }

    @Override
    public boolean hasReadAnnouncement(Long announcementId, Long userId) {
        LambdaQueryWrapper<AnnouncementRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnnouncementRead::getAnnouncementId, announcementId)
                .eq(AnnouncementRead::getUserId, userId);
        return announcementReadMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Long countUnreadAnnouncements(Long userId) {
        // 查询用户已阅读的公告ID
        LambdaQueryWrapper<AnnouncementRead> readWrapper = new LambdaQueryWrapper<>();
        readWrapper.select(AnnouncementRead::getAnnouncementId)
                .eq(AnnouncementRead::getUserId, userId);

        var readAnnouncementIds = announcementReadMapper.selectObjs(readWrapper);

        // 查询所有已发布的公告
        LambdaQueryWrapper<Announcement> announcementWrapper = new LambdaQueryWrapper<>();
        announcementWrapper.ne(Announcement::getStatus, 0); // 非草稿状态

        var allAnnouncements = list(announcementWrapper);

        // 计算未读数量
        long unreadCount = allAnnouncements.stream()
                .filter(announcement -> {
                    // 检查是否已阅读
                    boolean hasRead = false;
                    for (Object obj : readAnnouncementIds) {
                        if (obj != null && Long.valueOf(announcement.getId()).equals(obj)) {
                            hasRead = true;
                            break;
                        }
                    }
                    return !hasRead;
                })
                .count();

        return unreadCount;
    }
}