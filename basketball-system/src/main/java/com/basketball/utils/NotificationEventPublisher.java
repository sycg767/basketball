package com.basketball.utils;

import com.basketball.dto.request.NotificationSendDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 通知事件发布工具
 * 用于在事务提交后异步发送通知
 */
@Slf4j
@Component
public class NotificationEventPublisher {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发布通知事件（事务提交后异步执行）
     */
    public void publishNotification(NotificationSendDTO sendDTO) {
        try {
            eventPublisher.publishEvent(sendDTO);
            log.debug("发布通知事件: userId={}, templateCode={}", sendDTO.getUserId(), sendDTO.getTemplateCode());
        } catch (Exception e) {
            log.error("发布通知事件失败", e);
        }
    }
}
