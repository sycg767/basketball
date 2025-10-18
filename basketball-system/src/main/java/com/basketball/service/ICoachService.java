package com.basketball.service;

import com.basketball.common.result.PageResult;
import com.basketball.vo.CoachVO;

/**
 * 教练信息服务接口
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
public interface ICoachService {

    /**
     * 获取教练列表（分页）
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @param keyword  关键词（姓名/手机号）
     * @param status   状态
     * @return 教练列表
     */
    PageResult<CoachVO> getCoachList(Integer page, Integer pageSize, String keyword, Integer status);

    /**
     * 获取所有在职教练（不分页，用于下拉选择）
     *
     * @return 教练列表
     */
    PageResult<CoachVO> getAllActiveCoaches();
}
