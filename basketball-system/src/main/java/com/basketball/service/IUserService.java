package com.basketball.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basketball.common.result.PageResult;
import com.basketball.dto.request.*;
import com.basketball.entity.User;
import com.basketball.vo.UserVO;

import java.util.Map;

/**
 * 用户服务接口
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     */
    Map<String, Object> login(UserLoginDTO loginDTO);

    /**
     * 手机验证码登录
     */
    Map<String, Object> loginByPhone(SmsLoginDTO loginDTO);

    /**
     * 获取当前用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, UserUpdateDTO updateDTO);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, PasswordUpdateDTO passwordDTO);

    /**
     * 绑定手机号
     */
    void bindPhone(Long userId, PhoneBindDTO bindDTO);

    /**
     * 解绑手机号
     */
    void unbindPhone(Long userId);

    /**
     * 获取用户列表（分页）- 管理员功能
     */
    PageResult<UserVO> getUserList(Integer page, Integer pageSize, String username, Integer memberLevel, Integer status);

    /**
     * 更新用户状态 - 管理员功能
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 删除用户 - 管理员功能
     */
    void deleteUser(Long userId);
}