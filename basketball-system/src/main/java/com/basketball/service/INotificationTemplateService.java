package com.basketball.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basketball.common.result.PageResult;
import com.basketball.entity.NotificationTemplate;

/**
 * 通知模板服务接口
 *
 * @author Basketball Team
 * @date 2025-10-22
 */
public interface INotificationTemplateService extends IService<NotificationTemplate> {

    /**
     * 分页查询通知模板
     *
     * @param page 页码
     * @param size 每页大小
     * @param templateType 模板类型
     * @param scene 场景
     * @param keyword 关键词
     * @return 分页结果
     */
    PageResult<NotificationTemplate> getTemplatePage(Integer page, Integer size, String templateType, String scene, String keyword);

    /**
     * 根据模板编码查询模板
     *
     * @param templateCode 模板编码
     * @return 模板信息
     */
    NotificationTemplate getByTemplateCode(String templateCode);

    /**
     * 启用/禁用模板
     *
     * @param id 模板ID
     * @param status 状态
     */
    void toggleStatus(Long id, Integer status);
}
