package com.basketball.service;

import com.basketball.common.result.PageResult;
import com.basketball.dto.request.NotificationSendDTO;
import com.basketball.dto.response.NotificationVO;

import java.util.List;

/**
 * 通知服务接口
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public interface INotificationService {

    /**
     * 发送通知
     *
     * @param sendDTO 发送请求
     * @return 通知记录ID
     */
    Long sendNotification(NotificationSendDTO sendDTO);

    /**
     * 批量发送通知
     *
     * @param userIds      用户ID列表
     * @param templateCode 模板编码
     * @param bizId        业务ID
     * @param bizType      业务类型
     */
    void sendBatchNotification(List<Long> userIds, String templateCode, String bizId, String bizType);

    /**
     * 获取用户未读通知列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param pageSize 每页数量
     * @return 通知列表
     */
    PageResult<NotificationVO> getUnreadNotifications(Long userId, Integer page, Integer pageSize);

    /**
     * 获取用户所有通知列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param pageSize 每页数量
     * @return 通知列表
     */
    PageResult<NotificationVO> getAllNotifications(Long userId, Integer page, Integer pageSize);

    /**
     * 标记通知为已读
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);
}
