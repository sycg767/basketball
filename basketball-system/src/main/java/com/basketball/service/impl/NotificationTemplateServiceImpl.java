package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.result.PageResult;
import com.basketball.entity.NotificationTemplate;
import com.basketball.mapper.NotificationTemplateMapper;
import com.basketball.service.INotificationTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 通知模板服务实现
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
@Slf4j
@Service
public class NotificationTemplateServiceImpl extends ServiceImpl<NotificationTemplateMapper, NotificationTemplate> implements INotificationTemplateService {

    /**
     * 分页查询通知模板
     */
    @Override
    public PageResult<NotificationTemplate> getTemplatePage(Integer page, Integer size, String templateType, String scene, String keyword) {
        Page<NotificationTemplate> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<NotificationTemplate> query = new LambdaQueryWrapper<>();

        // 按类型筛选
        if (StringUtils.hasText(templateType)) {
            query.eq(NotificationTemplate::getTemplateType, templateType);
        }

        // 按场景筛选
        if (StringUtils.hasText(scene)) {
            query.eq(NotificationTemplate::getScene, scene);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            query.and(wrapper -> wrapper
                    .like(NotificationTemplate::getTemplateName, keyword)
                    .or()
                    .like(NotificationTemplate::getTemplateCode, keyword)
                    .or()
                    .like(NotificationTemplate::getTitle, keyword)
            );
        }

        query.orderByDesc(NotificationTemplate::getCreateTime);

        Page<NotificationTemplate> result = this.page(pageParam, query);

        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    /**
     * 根据模板编码查询模板
     */
    @Override
    public NotificationTemplate getByTemplateCode(String templateCode) {
        LambdaQueryWrapper<NotificationTemplate> query = new LambdaQueryWrapper<>();
        query.eq(NotificationTemplate::getTemplateCode, templateCode);

        return this.getOne(query);
    }

    /**
     * 启用/禁用模板
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleStatus(Long id, Integer status) {
        NotificationTemplate template = this.getById(id);

        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        template.setStatus(status);
        this.updateById(template);

        log.info("模板状态已更新: id={}, status={}", id, status);
    }
}
