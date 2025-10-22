package com.basketball.service.impl;

import com.basketball.dto.request.NotificationSendDTO;
import com.basketball.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

/**
 * 异步通知服务
 * 解决事务锁问题：在主事务提交后异步发送通知
 */
@Slf4j
@Service
public class AsyncNotificationService {

    @Resource
    private INotificationService notificationService;

    /**
     * 事务提交后异步发送通知
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationSendDTO sendDTO) {
        try {
            log.info("异步发送通知: userId={}, templateCode={}", sendDTO.getUserId(), sendDTO.getTemplateCode());
            notificationService.sendNotification(sendDTO);
        } catch (Exception e) {
            log.error("异步发送通知失败", e);
        }
    }
}
