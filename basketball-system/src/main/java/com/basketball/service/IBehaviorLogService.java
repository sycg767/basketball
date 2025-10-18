package com.basketball.service;

import com.basketball.entity.UserBehaviorLog;

import java.util.List;
import java.util.Map;

/**
 * 用户行为日志服务接口
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
public interface IBehaviorLogService {

    /**
     * 记录用户行为
     *
     * @param userId       用户ID
     * @param behaviorType 行为类型
     * @param module       业务模块
     * @param businessId   业务ID
     * @param description  行为描述
     */
    void recordBehavior(Long userId, String behaviorType, String module, String businessId, String description);

    /**
     * 批量记录用户行为
     *
     * @param logs 行为日志列表
     */
    void batchRecordBehavior(List<UserBehaviorLog> logs);

    /**
     * 获取用户行为统计
     *
     * @param userId    用户ID
     * @param startDate 开始日期 (yyyy-MM-dd)
     * @param endDate   结束日期 (yyyy-MM-dd)
     * @return 统计数据
     */
    Map<String, Object> getUserBehaviorStats(Long userId, String startDate, String endDate);
}
