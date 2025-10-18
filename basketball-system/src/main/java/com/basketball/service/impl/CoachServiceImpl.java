package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basketball.common.result.PageResult;
import com.basketball.entity.CoachInfo;
import com.basketball.entity.User;
import com.basketball.mapper.CoachInfoMapper;
import com.basketball.mapper.UserMapper;
import com.basketball.service.ICoachService;
import com.basketball.vo.CoachVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 教练信息服务实现类
 *
 * @author Basketball Team
 * @date 2025-10-02
 */
@Service
public class CoachServiceImpl implements ICoachService {

    @Resource
    private CoachInfoMapper coachInfoMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public PageResult<CoachVO> getCoachList(Integer page, Integer pageSize, String keyword, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<CoachInfo> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(CoachInfo::getStatus, status);
        }

        wrapper.orderByDesc(CoachInfo::getCreateTime);

        // 分页查询
        Page<CoachInfo> pageParam = new Page<>(page, pageSize);
        Page<CoachInfo> result = coachInfoMapper.selectPage(pageParam, wrapper);

        // 转换为VO并填充用户信息
        List<CoachVO> coachVOList = convertToVOList(result.getRecords(), keyword);

        return PageResult.of(coachVOList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<CoachVO> getAllActiveCoaches() {
        // 查询所有在职教练
        LambdaQueryWrapper<CoachInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachInfo::getStatus, 1)
                .orderByDesc(CoachInfo::getRating);

        List<CoachInfo> coachInfoList = coachInfoMapper.selectList(wrapper);

        // 转换为VO并填充用户信息
        List<CoachVO> coachVOList = convertToVOList(coachInfoList, null);

        return PageResult.of(coachVOList, (long) coachVOList.size(), 1L, (long) coachVOList.size());
    }

    /**
     * 转换为VO列表并填充用户信息
     */
    private List<CoachVO> convertToVOList(List<CoachInfo> coachInfoList, String keyword) {
        if (coachInfoList == null || coachInfoList.isEmpty()) {
            return List.of();
        }

        // 获取所有用户ID
        List<Long> userIds = coachInfoList.stream()
                .map(CoachInfo::getUserId)
                .collect(Collectors.toList());

        // 查询用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, userIds);

        // 如果有关键词，在用户信息中搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            userWrapper.and(w -> w.like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getUsername, keyword));
        }

        List<User> users = userMapper.selectList(userWrapper);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 转换为VO
        return coachInfoList.stream()
                .filter(coach -> userMap.containsKey(coach.getUserId())) // 只返回匹配关键词的教练
                .map(coach -> {
                    CoachVO vo = new CoachVO();
                    BeanUtils.copyProperties(coach, vo);

                    // 填充用户信息
                    User user = userMap.get(coach.getUserId());
                    if (user != null) {
                        vo.setUsername(user.getUsername());
                        vo.setRealName(user.getRealName());
                        vo.setPhone(user.getPhone());
                        vo.setAvatar(user.getAvatar());
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }
}
