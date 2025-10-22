package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.exception.ErrorCode;
import com.basketball.dto.response.WechatAuthUrlVO;
import com.basketball.entity.User;
import com.basketball.entity.UserOauth;
import com.basketball.entity.UserRole;
import com.basketball.entity.UserSession;
import com.basketball.mapper.UserMapper;
import com.basketball.mapper.UserOauthMapper;
import com.basketball.mapper.UserRoleMapper;
import com.basketball.mapper.UserSessionMapper;
import com.basketball.security.JwtTokenProvider;
import com.basketball.service.IWechatService;
import com.basketball.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信服务实现类
 * 注意: 这是一个模拟实现,生产环境需要配置真实的微信SDK
 *
 * @author Basketball Team
 * @date 2025-10-17
 */
@Slf4j
@Service
public class WechatServiceImpl implements IWechatService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserOauthMapper userOauthMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private HttpServletRequest request;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 模拟的state存储 (生产环境应使用Redis)
     */
    private static final Map<String, Long> stateMap = new ConcurrentHashMap<>();

    @org.springframework.beans.factory.annotation.Value("${wechat.oauth.enabled:false}")
    private Boolean wechatEnabled;

    @org.springframework.beans.factory.annotation.Value("${wechat.oauth.app-id:}")
    private String wechatAppId;

    @org.springframework.beans.factory.annotation.Value("${wechat.oauth.app-secret:}")
    private String wechatAppSecret;

    @org.springframework.beans.factory.annotation.Value("${wechat.oauth.redirect-uri:http://localhost:8080/api/wechat/auth/callback}")
    private String wechatRedirectUri;

    /**
     * 获取微信授权URL
     */
    @Override
    public WechatAuthUrlVO getAuthorizationUrl() {
        // 检查是否配置了真实的微信AppID
        if (!wechatEnabled || StringUtils.isBlank(wechatAppId) || "YOUR_WECHAT_APP_ID".equals(wechatAppId)) {
            throw new BusinessException("微信登录功能未配置，请在application.yml中配置真实的微信开放平台AppID。\n" +
                    "申请地址: https://open.weixin.qq.com/");
        }

        // 生成state防CSRF
        String state = UUID.randomUUID().toString().replace("-", "");
        stateMap.put(state, System.currentTimeMillis());

        // 构建微信授权URL
        String authUrl = String.format(
                "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s#wechat_redirect",
                wechatAppId,
                wechatRedirectUri,
                state
        );

        WechatAuthUrlVO vo = new WechatAuthUrlVO();
        vo.setAuthUrl(authUrl);
        vo.setState(state);

        log.info("生成微信授权URL: state={}", state);
        return vo;
    }

    /**
     * 处理微信授权回调
     * 注意: 这是模拟实现,真实环境需要调用微信API获取用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleCallback(String code, String state) {
        // 1. 验证state (防CSRF攻击)
        if (!verifyState(state)) {
            throw new BusinessException("无效的状态码");
        }

        // 2. 模拟获取微信用户信息
        // 生产环境需要:
        // - 通过code换取access_token
        // - 通过access_token获取用户openId和unionId
        // - 获取用户基本信息
        String openId = "mock_openid_" + code;
        String unionId = "mock_unionid_" + code;
        String nickname = "微信用户" + System.currentTimeMillis();
        String avatar = "https://thirdwx.qlogo.cn/mmopen/vi_32/default/0";

        log.info("模拟获取微信用户信息: openId={}, unionId={}, nickname={}", openId, unionId, nickname);

        // 3. 查询是否已绑定用户
        LambdaQueryWrapper<UserOauth> oauthQuery = new LambdaQueryWrapper<>();
        oauthQuery.eq(UserOauth::getOauthType, "wechat")
                .eq(UserOauth::getOpenId, openId);
        UserOauth userOauth = userOauthMapper.selectOne(oauthQuery);

        User user;
        if (userOauth != null) {
            // 已绑定,直接登录
            user = userMapper.selectById(userOauth.getUserId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            // 更新OAuth信息
            userOauth.setNickname(nickname);
            userOauth.setAvatar(avatar);
            userOauth.setLastLoginTime(LocalDateTime.now());
            userOauthMapper.updateById(userOauth);

            log.info("微信用户已绑定,直接登录: userId={}", user.getId());
        } else {
            // 未绑定,自动注册新用户
            user = new User();
            user.setUsername("wx_" + System.currentTimeMillis());
            user.setRealName(nickname);
            user.setAvatar(avatar);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 随机密码
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);

            // 分配默认角色
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(3L); // 普通用户
            userRole.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(userRole);

            // 创建OAuth绑定
            UserOauth oauth = new UserOauth();
            oauth.setUserId(user.getId());
            oauth.setOauthType("wechat");
            oauth.setOpenId(openId);
            oauth.setUnionId(unionId);
            oauth.setNickname(nickname);
            oauth.setAvatar(avatar);
            oauth.setBindTime(LocalDateTime.now());
            oauth.setLastLoginTime(LocalDateTime.now());
            oauth.setStatus(1);
            oauth.setCreateTime(LocalDateTime.now());
            userOauthMapper.insert(oauth);

            log.info("微信用户自动注册: userId={}, openId={}", user.getId(), openId);
        }

        // 4. 校验账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 5. 生成JWT Token
        String token = jwtTokenProvider.generateToken(user.getId());

        // 6. 创建会话记录
        UserSession session = new UserSession();
        session.setUserId(user.getId());
        session.setToken(token);
        session.setLoginType(3); // 3-微信登录
        session.setDeviceType(getDeviceType());
        session.setIpAddress(getClientIp());
        session.setExpireTime(LocalDateTime.now().plusDays(7));
        session.setStatus(1);
        session.setCreateTime(LocalDateTime.now());
        userSessionMapper.insert(session);

        // 7. 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 8. 清除state
        stateMap.remove(state);

        // 9. 返回登录信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", convertToVO(user));

        return result;
    }

    /**
     * 绑定微信账号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindWechat(Long userId, String code, String state) {
        // 1. 验证state
        if (!verifyState(state)) {
            throw new BusinessException("无效的状态码");
        }

        // 2. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 3. 模拟获取微信信息
        String openId = "mock_openid_" + code;
        String unionId = "mock_unionid_" + code;
        String nickname = "微信用户" + System.currentTimeMillis();
        String avatar = "https://thirdwx.qlogo.cn/mmopen/vi_32/default/0";

        // 4. 检查是否已被其他用户绑定
        LambdaQueryWrapper<UserOauth> existQuery = new LambdaQueryWrapper<>();
        existQuery.eq(UserOauth::getOauthType, "wechat")
                .eq(UserOauth::getOpenId, openId);
        UserOauth existOauth = userOauthMapper.selectOne(existQuery);

        if (existOauth != null && !existOauth.getUserId().equals(userId)) {
            throw new BusinessException("该微信号已被其他用户绑定");
        }

        if (existOauth != null && existOauth.getUserId().equals(userId)) {
            throw new BusinessException("您已绑定该微信号");
        }

        // 5. 检查当前用户是否已绑定其他微信
        LambdaQueryWrapper<UserOauth> userOauthQuery = new LambdaQueryWrapper<>();
        userOauthQuery.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, "wechat");
        UserOauth userOauth = userOauthMapper.selectOne(userOauthQuery);

        if (userOauth != null) {
            throw new BusinessException("您已绑定其他微信号,请先解绑");
        }

        // 6. 创建绑定
        UserOauth oauth = new UserOauth();
        oauth.setUserId(userId);
        oauth.setOauthType("wechat");
        oauth.setOpenId(openId);
        oauth.setUnionId(unionId);
        oauth.setNickname(nickname);
        oauth.setAvatar(avatar);
        oauth.setBindTime(LocalDateTime.now());
        oauth.setStatus(1);
        oauth.setCreateTime(LocalDateTime.now());
        userOauthMapper.insert(oauth);

        // 7. 清除state
        stateMap.remove(state);

        log.info("绑定微信成功: userId={}, openId={}", userId, openId);
    }

    /**
     * 解绑微信账号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindWechat(Long userId) {
        // 1. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 查询微信绑定
        LambdaQueryWrapper<UserOauth> oauthQuery = new LambdaQueryWrapper<>();
        oauthQuery.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, "wechat");
        UserOauth userOauth = userOauthMapper.selectOne(oauthQuery);

        if (userOauth == null) {
            throw new BusinessException("您还未绑定微信账号");
        }

        // 3. 检查是否有其他登录方式 (至少保留一种)
        if (StringUtils.isBlank(user.getPassword()) && StringUtils.isBlank(user.getPhone())) {
            throw new BusinessException("请先设置密码或绑定手机号,确保至少有一种登录方式");
        }

        // 4. 删除绑定
        userOauthMapper.deleteById(userOauth.getId());

        log.info("解绑微信成功: userId={}", userId);
    }

    /**
     * 验证state
     */
    private boolean verifyState(String state) {
        if (StringUtils.isBlank(state)) {
            return false;
        }

        Long timestamp = stateMap.get(state);
        if (timestamp == null) {
            return false;
        }

        // state 10分钟有效
        return System.currentTimeMillis() - timestamp < 10 * 60 * 1000;
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取设备类型
     */
    private String getDeviceType() {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "unknown";
        }

        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile")) {
            return "mobile";
        } else if (userAgent.contains("tablet")) {
            return "tablet";
        } else {
            return "pc";
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
