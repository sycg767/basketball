package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.NotificationSendDTO;
import com.basketball.dto.response.NotificationVO;
import com.basketball.entity.NotificationRecord;
import com.basketball.entity.NotificationSubscribe;
import com.basketball.entity.NotificationTemplate;
import com.basketball.entity.User;
import com.basketball.mapper.NotificationRecordMapper;
import com.basketball.mapper.NotificationSubscribeMapper;
import com.basketball.mapper.NotificationTemplateMapper;
import com.basketball.mapper.UserMapper;
import com.basketball.service.INotificationService;
import com.basketball.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通知服务实现
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Service
public class NotificationServiceImpl implements INotificationService {

    @Resource
    private NotificationRecordMapper notificationRecordMapper;

    @Resource
    private NotificationTemplateMapper notificationTemplateMapper;

    @Resource
    private NotificationSubscribeMapper notificationSubscribeMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private SmsUtil smsUtil;

    /**
     * 发送通知
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendNotification(NotificationSendDTO sendDTO) {
        log.info("发送通知: userId={}, templateCode={}, notificationType={}",
                sendDTO.getUserId(), sendDTO.getTemplateCode(), sendDTO.getNotificationType());

        // 1. 查询用户
        User user = userMapper.selectById(sendDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 查询通知模板
        LambdaQueryWrapper<NotificationTemplate> templateQuery = new LambdaQueryWrapper<>();
        templateQuery.eq(NotificationTemplate::getTemplateCode, sendDTO.getTemplateCode())
                .eq(NotificationTemplate::getStatus, 1);
        NotificationTemplate template = notificationTemplateMapper.selectOne(templateQuery);

        if (template == null) {
            throw new BusinessException("通知模板不存在或已禁用");
        }

        // 3. 检查用户是否订阅该类型通知
        if (!checkSubscription(sendDTO.getUserId(), template.getTemplateType(), sendDTO.getNotificationType())) {
            log.info("用户未订阅该类型通知: userId={}, type={}", sendDTO.getUserId(), template.getTemplateType());
            return null;
        }

        // 4. 渲染通知内容
        String title = renderTemplate(template.getTitle(), sendDTO.getParams());
        String content = renderTemplate(template.getContent(), sendDTO.getParams());

        // 如果有自定义内容,使用自定义内容
        if (sendDTO.getCustomTitle() != null) {
            title = sendDTO.getCustomTitle();
        }
        if (sendDTO.getCustomContent() != null) {
            content = sendDTO.getCustomContent();
        }

        // 5. 创建通知记录
        NotificationRecord record = new NotificationRecord();
        record.setUserId(sendDTO.getUserId());
        record.setTemplateId(template.getId());
        record.setTemplateCode(sendDTO.getTemplateCode());
        record.setNotificationType(sendDTO.getNotificationType() != null ? sendDTO.getNotificationType() : template.getTemplateType());
        record.setTitle(title);
        record.setContent(content);
        record.setBizId(sendDTO.getBizId());
        record.setBizType(sendDTO.getBizType());
        record.setSendStatus(0); // 待发送
        record.setIsRead(0); // 未读
        record.setRetryCount(0);
        record.setCreateTime(LocalDateTime.now());

        // 根据通知类型设置发送目标
        if ("sms".equals(record.getNotificationType())) {
            record.setTarget(user.getPhone());
        } else if ("email".equals(record.getNotificationType())) {
            record.setTarget(user.getEmail());
        } else if ("wechat".equals(record.getNotificationType())) {
            // 微信需要openId，这里暂时用userId
            record.setTarget(String.valueOf(sendDTO.getUserId()));
        } else {
            // system 站内信
            record.setTarget(String.valueOf(sendDTO.getUserId()));
        }

        notificationRecordMapper.insert(record);

        // 6. 异步发送通知
        sendNotificationAsync(record);

        return record.getId();
    }

    /**
     * 批量发送通知
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendBatchNotification(List<Long> userIds, String templateCode, String bizId, String bizType) {
        log.info("批量发送通知: userIds={}, templateCode={}", userIds.size(), templateCode);

        for (Long userId : userIds) {
            try {
                NotificationSendDTO sendDTO = new NotificationSendDTO();
                sendDTO.setUserId(userId);
                sendDTO.setTemplateCode(templateCode);
                sendDTO.setBizId(bizId);
                sendDTO.setBizType(bizType);

                sendNotification(sendDTO);
            } catch (Exception e) {
                log.error("批量发送通知失败: userId={}", userId, e);
            }
        }
    }

    /**
     * 获取未读通知列表
     */
    @Override
    public PageResult<NotificationVO> getUnreadNotifications(Long userId, Integer page, Integer pageSize) {
        Page<NotificationRecord> pageParam = new Page<>(page, pageSize);

        LambdaQueryWrapper<NotificationRecord> query = new LambdaQueryWrapper<>();
        query.eq(NotificationRecord::getUserId, userId)
                .eq(NotificationRecord::getIsRead, 0)
                .eq(NotificationRecord::getSendStatus, 2) // 发送成功的
                .orderByDesc(NotificationRecord::getCreateTime);

        Page<NotificationRecord> recordPage = notificationRecordMapper.selectPage(pageParam, query);

        List<NotificationVO> voList = new ArrayList<>();
        for (NotificationRecord record : recordPage.getRecords()) {
            voList.add(convertToVO(record));
        }

        return PageResult.of(voList, recordPage.getTotal(), recordPage.getCurrent(), recordPage.getSize());
    }

    /**
     * 获取所有通知列表
     */
    @Override
    public PageResult<NotificationVO> getAllNotifications(Long userId, Integer page, Integer pageSize) {
        Page<NotificationRecord> pageParam = new Page<>(page, pageSize);

        LambdaQueryWrapper<NotificationRecord> query = new LambdaQueryWrapper<>();
        query.eq(NotificationRecord::getUserId, userId)
                .eq(NotificationRecord::getSendStatus, 2)
                .orderByDesc(NotificationRecord::getCreateTime);

        Page<NotificationRecord> recordPage = notificationRecordMapper.selectPage(pageParam, query);

        List<NotificationVO> voList = new ArrayList<>();
        for (NotificationRecord record : recordPage.getRecords()) {
            voList.add(convertToVO(record));
        }

        return PageResult.of(voList, recordPage.getTotal(), recordPage.getCurrent(), recordPage.getSize());
    }

    /**
     * 标记为已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId, Long userId) {
        NotificationRecord record = notificationRecordMapper.selectById(notificationId);

        if (record == null) {
            throw new BusinessException("通知不存在");
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        if (record.getIsRead() == 1) {
            return; // 已读,无需重复操作
        }

        record.setIsRead(1);
        record.setReadTime(LocalDateTime.now());
        notificationRecordMapper.updateById(record);
    }

    /**
     * 标记所有为已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<NotificationRecord> query = new LambdaQueryWrapper<>();
        query.eq(NotificationRecord::getUserId, userId)
                .eq(NotificationRecord::getIsRead, 0);

        List<NotificationRecord> records = notificationRecordMapper.selectList(query);

        LocalDateTime now = LocalDateTime.now();
        for (NotificationRecord record : records) {
            record.setIsRead(1);
            record.setReadTime(now);
            notificationRecordMapper.updateById(record);
        }
    }

    /**
     * 删除通知
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long notificationId, Long userId) {
        NotificationRecord record = notificationRecordMapper.selectById(notificationId);

        if (record == null) {
            throw new BusinessException("通知不存在");
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        notificationRecordMapper.deleteById(notificationId);
    }

    /**
     * 获取未读数量
     */
    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<NotificationRecord> query = new LambdaQueryWrapper<>();
        query.eq(NotificationRecord::getUserId, userId)
                .eq(NotificationRecord::getIsRead, 0)
                .eq(NotificationRecord::getSendStatus, 2);

        return notificationRecordMapper.selectCount(query);
    }

    /**
     * 检查用户是否订阅
     */
    private boolean checkSubscription(Long userId, String scene, String notificationType) {
        LambdaQueryWrapper<NotificationSubscribe> query = new LambdaQueryWrapper<>();
        query.eq(NotificationSubscribe::getUserId, userId)
                .eq(NotificationSubscribe::getScene, scene);

        if (notificationType != null) {
            query.eq(NotificationSubscribe::getNotificationType, notificationType);
        }

        NotificationSubscribe subscribe = notificationSubscribeMapper.selectOne(query);

        // 如果没有订阅记录,默认订阅
        if (subscribe == null) {
            return true;
        }

        return subscribe.getIsEnabled() == 1;
    }

    /**
     * 渲染模板
     */
    private String renderTemplate(String template, Map<String, Object> params) {
        if (template == null || params == null) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = String.valueOf(entry.getValue());
            result = result.replace(placeholder, value);
        }

        return result;
    }

    /**
     * 异步发送通知
     */
    private void sendNotificationAsync(NotificationRecord record) {
        try {
            log.info("发送通知: notificationType={}, target={}", record.getNotificationType(), record.getTarget());

            String sendResult = null;
            // 模拟发送
            if ("sms".equals(record.getNotificationType())) {
                // 发送短信
                smsUtil.sendNotification(record.getTarget(), record.getContent());
                sendResult = "短信发送成功";
            } else if ("system".equals(record.getNotificationType())) {
                // 站内信,无需发送,直接标记为成功
                log.info("站内信通知: userId={}, content={}", record.getUserId(), record.getContent());
                sendResult = "站内信创建成功";
            } else if ("email".equals(record.getNotificationType())) {
                // 发送邮件 (模拟)
                log.info("邮件通知: email={}, content={}", record.getTarget(), record.getContent());
                sendResult = "邮件发送成功";
            } else if ("wechat".equals(record.getNotificationType())) {
                // 发送微信通知 (模拟)
                log.info("微信通知: userId={}, content={}", record.getUserId(), record.getContent());
                sendResult = "微信通知发送成功";
            }

            // 更新状态为发送成功
            record.setSendStatus(2);
            record.setSendTime(LocalDateTime.now());
            record.setSendResult(sendResult);
            notificationRecordMapper.updateById(record);

        } catch (Exception e) {
            log.error("发送通知失败: recordId={}", record.getId(), e);

            record.setSendStatus(3);
            record.setErrorMsg(e.getMessage());
            record.setRetryCount(record.getRetryCount() + 1);
            notificationRecordMapper.updateById(record);
        }
    }

    /**
     * 转换为VO
     */
    private NotificationVO convertToVO(NotificationRecord record) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}
