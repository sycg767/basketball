package com.basketball.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basketball.common.constant.RedisKeyConstant;
import com.basketball.common.exception.BusinessException;
import com.basketball.common.exception.ErrorCode;
import com.basketball.dto.request.*;
import com.basketball.entity.User;
import com.basketball.entity.UserRole;
import com.basketball.entity.UserSession;
import com.basketball.mapper.UserMapper;
import com.basketball.mapper.UserRoleMapper;
import com.basketball.mapper.UserSessionMapper;
import com.basketball.security.JwtTokenProvider;
import com.basketball.service.ISmsService;
import com.basketball.service.IUserService;
import com.basketball.service.ILoginLogService;
import com.basketball.utils.RedisUtil;
import com.basketball.utils.IpUtils;
import com.basketball.utils.UserAgentParser;
import com.basketball.vo.UserVO;
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
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private ISmsService smsService;

    @Resource
    private ILoginLogService loginLogService;

    @Resource
    private HttpServletRequest request;

//    @Resource
//    private RedisUtil redisUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO registerDTO) {
        // 1. 校验用户名是否已存在
        LambdaQueryWrapper<User> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(User::getUsername, registerDTO.getUsername());
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 2. 校验邮箱是否已存在
        if (StringUtils.isNotBlank(registerDTO.getEmail())) {
            LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(User::getEmail, registerDTO.getEmail());
            if (userMapper.selectCount(emailQuery) > 0) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        // 3. 校验手机号是否已存在
        if (StringUtils.isNotBlank(registerDTO.getPhone())) {
            LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<>();
            phoneQuery.eq(User::getPhone, registerDTO.getPhone());
            if (userMapper.selectCount(phoneQuery) > 0) {
                throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
        }

        // 4. 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1); // 默认启用
        user.setRole(0); // 默认普通用户
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 5. 分配默认角色（普通用户）
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(3L); // 3=普通用户
        userRole.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(userRole);
    }

    /**
     * 用户登录
     */
    @Override
    public Map<String, Object> login(UserLoginDTO loginDTO) {
        // 获取请求信息
        String ip = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String browser = UserAgentParser.getBrowser(userAgent);
        String os = UserAgentParser.getOperatingSystem(userAgent);

        try {
            // 1. 查询用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, loginDTO.getUsername());
            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                // 记录登录失败日志
                loginLogService.recordLoginLog(null, loginDTO.getUsername(), 1, ip, null, browser, os, 0, "用户不存在");
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            // 2. 校验密码
            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                // 记录登录失败日志
                loginLogService.recordLoginLog(user.getId(), user.getUsername(), 1, ip, null, browser, os, 0, "密码错误");
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }

            // 3. 校验账号状态
            if (user.getStatus() == 0) {
                // 记录登录失败日志
                loginLogService.recordLoginLog(user.getId(), user.getUsername(), 1, ip, null, browser, os, 0, "账号已禁用");
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 4. 生成JWT Token
            String token = jwtTokenProvider.generateToken(user.getId());

            // 5. 将Token存入Redis（暂时注释）
//            String redisKey = RedisKeyConstant.USER_TOKEN_PREFIX + user.getId();
//            redisUtil.set(redisKey, token, 604800L);

            // 6. 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);

            // 7. 记录登录成功日志
            loginLogService.recordLoginLog(user.getId(), user.getUsername(), 1, ip, null, browser, os, 1, "登录成功");

            // 8. 返回登录信息
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", convertToVO(user));

            return result;
        } catch (BusinessException e) {
            // 业务异常直接抛出（已记录日志）
            throw e;
        } catch (Exception e) {
            // 其他异常记录日志
            loginLogService.recordLoginLog(null, loginDTO.getUsername(), 1, ip, null, browser, os, 0, "系统异常: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 校验邮箱是否被其他用户使用
        if (StringUtils.isNotBlank(updateDTO.getEmail()) && !updateDTO.getEmail().equals(user.getEmail())) {
            LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(User::getEmail, updateDTO.getEmail());
            emailQuery.ne(User::getId, userId);
            if (userMapper.selectCount(emailQuery) > 0) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        // 校验手机号是否被其他用户使用
        if (StringUtils.isNotBlank(updateDTO.getPhone()) && !updateDTO.getPhone().equals(user.getPhone())) {
            LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<>();
            phoneQuery.eq(User::getPhone, updateDTO.getPhone());
            phoneQuery.ne(User::getId, userId);
            if (userMapper.selectCount(phoneQuery) > 0) {
                throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
        }

        // 更新用户信息
        user.setRealName(updateDTO.getNickname());
        user.setEmail(updateDTO.getEmail());
        user.setPhone(updateDTO.getPhone());
        user.setAvatar(updateDTO.getAvatar());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, PasswordUpdateDTO passwordDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 校验原密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 清除Redis中的Token，强制重新登录（暂时注释）
//        String redisKey = RedisKeyConstant.USER_TOKEN_PREFIX + userId;
//        redisUtil.delete(redisKey);
    }

    /**
     * 获取用户列表（分页）- 管理员功能
     */
    @Override
    public com.basketball.common.result.PageResult<UserVO> getUserList(Integer page, Integer pageSize, String username, Integer memberLevel, Integer status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (org.apache.commons.lang3.StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username)
                    .or().like(User::getRealName, username);
        }

        if (memberLevel != null) {
            queryWrapper.eq(User::getMemberLevel, memberLevel);
        }

        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }

        queryWrapper.orderByDesc(User::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> iPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        com.baomidou.mybatisplus.core.metadata.IPage<User> userPage = userMapper.selectPage(iPage, queryWrapper);

        java.util.List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        return com.basketball.common.result.PageResult.<UserVO>builder()
                .records(voList)
                .total(userPage.getTotal())
                .current(userPage.getCurrent())
                .size(userPage.getSize())
                .pages(userPage.getPages())
                .build();
    }

    /**
     * 更新用户状态 - 管理员功能
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 删除用户 - 管理员功能
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 软删除：设置状态为禁用
        user.setStatus(0);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 手机验证码登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loginByPhone(SmsLoginDTO loginDTO) {
        // 获取请求信息
        String ip = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String browser = UserAgentParser.getBrowser(userAgent);
        String os = UserAgentParser.getOperatingSystem(userAgent);

        try {
            // 1. 验证验证码
            boolean verified = smsService.verifyCode(loginDTO.getPhone(), loginDTO.getCode(), "login");
            if (!verified) {
                loginLogService.recordLoginLog(null, loginDTO.getPhone(), 2, ip, null, browser, os, 0, "验证码错误");
                throw new BusinessException(ErrorCode.VERIFICATION_CODE_ERROR);
            }

            // 2. 查询用户是否存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, loginDTO.getPhone());
            User user = userMapper.selectOne(queryWrapper);

            // 3. 如果用户不存在，自动注册
            if (user == null) {
                user = new User();
                user.setPhone(loginDTO.getPhone());
                user.setUsername("user_" + loginDTO.getPhone().substring(7)); // 使用手机号后4位
                user.setPassword(passwordEncoder.encode("123456")); // 默认密码
                user.setStatus(1); // 默认启用
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                userMapper.insert(user);

                // 分配默认角色（普通用户）
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(3L); // 3=普通用户
                userRole.setCreateTime(LocalDateTime.now());
                userRoleMapper.insert(userRole);
            }

            // 4. 校验账号状态
            if (user.getStatus() == 0) {
                loginLogService.recordLoginLog(user.getId(), user.getUsername(), 2, ip, null, browser, os, 0, "账号已禁用");
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 5. 生成JWT Token
            String token = jwtTokenProvider.generateToken(user.getId());

            // 6. 创建会话记录
            UserSession session = new UserSession();
            session.setUserId(user.getId());
            session.setToken(token);
            session.setLoginType(2); // 2-手机验证码登录
            session.setDeviceType(getDeviceType());
            session.setIpAddress(getClientIp());
            session.setExpireTime(LocalDateTime.now().plusDays(7));
            session.setStatus(1);
            session.setCreateTime(LocalDateTime.now());
            userSessionMapper.insert(session);

            // 7. 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);

            // 8. 记录登录成功日志
            loginLogService.recordLoginLog(user.getId(), user.getUsername(), 2, ip, null, browser, os, 1, "登录成功");

            // 9. 清除已使用的验证码
            smsService.clearCode(loginDTO.getPhone(), "login");

            // 10. 返回登录信息
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", convertToVO(user));

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            loginLogService.recordLoginLog(null, loginDTO.getPhone(), 2, ip, null, browser, os, 0, "系统异常: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 绑定手机号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPhone(Long userId, PhoneBindDTO bindDTO) {
        // 1. 验证验证码
        boolean verified = smsService.verifyCode(bindDTO.getPhone(), bindDTO.getCode(), "bind");
        if (!verified) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_ERROR);
        }

        // 2. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 3. 检查用户是否已绑定手机号
        if (StringUtils.isNotBlank(user.getPhone())) {
            throw new BusinessException("用户已绑定手机号，请先解绑");
        }

        // 4. 检查手机号是否被其他用户使用
        LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<>();
        phoneQuery.eq(User::getPhone, bindDTO.getPhone());
        if (userMapper.selectCount(phoneQuery) > 0) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 5. 绑定手机号
        user.setPhone(bindDTO.getPhone());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 6. 清除已使用的验证码
        smsService.clearCode(bindDTO.getPhone(), "bind");
    }

    /**
     * 解绑手机号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindPhone(Long userId) {
        // 1. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 检查用户是否已绑定手机号
        if (StringUtils.isBlank(user.getPhone())) {
            throw new BusinessException("用户未绑定手机号");
        }

        // 3. 检查用户是否设置了密码（至少保留一种登录方式）
        if (StringUtils.isBlank(user.getPassword())) {
            throw new BusinessException("请先设置密码，确保至少有一种登录方式");
        }

        // 4. 解绑手机号
        user.setPhone(null);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 转换为VO对象
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
     * 获取客户端IP地址
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