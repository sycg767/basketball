package com.basketball.service;

import com.basketball.dto.response.WechatAuthUrlVO;

import java.util.Map;

/**
 * 微信服务接口
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
public interface IWechatService {

    /**
     * 获取微信授权URL
     */
    WechatAuthUrlVO getAuthorizationUrl();

    /**
     * 处理微信授权回调
     *
     * @param code  授权码
     * @param state 状态码
     * @return 登录结果 (token和用户信息)
     */
    Map<String, Object> handleCallback(String code, String state);

    /**
     * 绑定微信账号
     *
     * @param userId 用户ID
     * @param code   授权码
     * @param state  状态码
     */
    void bindWechat(Long userId, String code, String state);

    /**
     * 解绑微信账号
     *
     * @param userId 用户ID
     */
    void unbindWechat(Long userId);
}
