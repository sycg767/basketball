# JWT 认证自动解析 - 使用说明

## 📋 概述

已实现JWT token自动解析userId功能，Controller不再需要通过`@RequestHeader`手动获取userId，系统会自动从JWT token中解析并注入。

## ✅ 已完成的功能

### 1. **UserContext** - 用户上下文工具类
- 位置：`com.basketball.security.UserContext`
- 作用：在ThreadLocal中存储当前登录用户ID
- 方法：
  - `setUserId(Long userId)` - 设置用户ID
  - `getUserId()` - 获取用户ID
  - `clear()` - 清除用户ID

### 2. **JwtAuthenticationInterceptor** - JWT认证拦截器  
- 位置：`com.basketball.interceptor.JwtAuthenticationInterceptor`
- 作用：拦截所有请求，自动从JWT token解析userId并存入ThreadLocal
- 支持多种token传递方式：
  - `Authorization: Bearer {token}` （推荐）
  - `token: {token}`
  - URL参数：`?token={token}`

### 3. **@CurrentUserId** - 当前用户ID注解
- 位置：`com.basketball.annotation.CurrentUserId`
- 作用：标记Controller方法参数，自动注入当前登录用户ID
- 属性：
  - `required` - 是否必须（默认true，未登录会抛出异常）

### 4. **CurrentUserIdArgumentResolver** - 参数解析器
- 位置：`com.basketball.resolver.CurrentUserIdArgumentResolver`
- 作用：自动解析@CurrentUserId注解的参数

### 5. **WebMvcConfig** - Web配置
- 位置：`com.basketball.config.WebMvcConfig`
- 注册了拦截器和参数解析器
- 配置了CORS跨域支持

## 🚀 Controller 使用方法

### ❌ 旧方式（不推荐）
```java
@GetMapping("/profile")
public Result<UserVO> getProfile(@RequestHeader("userId") Long userId) {
    // userId需要前端手动传递，容易伪造
    return Result.success(userService.getUserProfile(userId));
}
```

### ✅ 新方式（推荐）
```java
@GetMapping("/profile")
public Result<UserVO> getProfile(@CurrentUserId Long userId) {
    // userId自动从JWT token解析，安全可靠
    return Result.success(userService.getUserProfile(userId));
}
```

### 📝 完整示例

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 获取当前用户信息
     * 必须登录
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile(@CurrentUserId Long userId) {
        UserVO user = userService.getUserById(userId);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * 必须登录
     */
    @PutMapping("/update")
    public Result<Void> updateProfile(
            @CurrentUserId Long userId,
            @RequestBody @Validated UserUpdateDTO dto) {
        userService.updateUser(userId, dto);
        return Result.success();
    }

    /**
     * 获取用户列表
     * 可选登录（用于判断是否显示某些功能）
     */
    @GetMapping("/list")
    public Result<List<UserVO>> getUserList(
            @CurrentUserId(required = false) Long userId) {
        // userId可能为null（未登录用户）
        List<UserVO> users = userService.getUserList(userId);
        return Result.success(users);
    }

    /**
     * 在Service中获取当前用户ID
     */
    public void someMethod() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null) {
            // 用户已登录
        } else {
            // 用户未登录
        }
    }
}
```

## 🔐 前端使用方法

### 登录流程
```javascript
// 1. 登录接口
const loginResponse = await axios.post('/api/auth/login', {
  username: 'admin',
  password: '123456'
});

// 2. 保存token（不再需要单独保存userId）
localStorage.setItem('token', loginResponse.data.token);

// 3. 配置axios拦截器，自动添加token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

// 4. 后续请求自动携带token，后端自动解析userId
await axios.get('/api/user/profile'); // ✅ 无需手动传userId
```

## 📌 注意事项

1. **登录/注册接口**无需认证，已在拦截器中排除：
   - `/api/auth/**`
   - `/api/wechat/auth/**`
   - Swagger相关路径

2. **token过期处理**：
   - 前端需要在响应拦截器中捕获401错误
   - 自动跳转到登录页面
   - 清除本地存储的token

3. **安全性提升**：
   - ✅ 用户ID从JWT token解析，防止伪造
   - ✅ 统一认证机制，减少代码重复
   - ✅ ThreadLocal自动清理，防止内存泄漏

## 🔄 迁移指南

### 需要修改的地方

1. **Controller层**：
   ```java
   // 旧代码
   @RequestHeader("userId") Long userId
   
   // 改为
   @CurrentUserId Long userId
   ```

2. **前端代码**：
   ```javascript
   // 旧代码（删除）
   headers: {
     'userId': localStorage.getItem('userId')
   }
   
   // 新代码（只需要token）
   headers: {
     'Authorization': 'Bearer ' + localStorage.getItem('token')
   }
   ```

## 🧪 测试步骤

1. 启动后端服务
2. 使用 Postman/Swagger 测试：
   - 先调用登录接口获取token
   - 在后续请求头中添加：`Authorization: Bearer {token}`
   - 调用需要认证的接口，验证userId自动注入成功

## 📞 管理员账号

- **用户名**：`admin`
- **密码**：`123456`
- **手机号**：`13800138000`

## 🐛 故障排查

### 问题：提示"用户未登录或登录已过期"
**解决方案**：
1. 检查请求头是否包含token
2. 检查token是否过期
3. 检查token格式是否正确：`Authorization: Bearer {token}`

### 问题：userId为null
**解决方案**：
1. 确认拦截器已注册生效
2. 确认JWT token能正常解析
3. 检查UserContext.setUserId()是否被调用

---

**最后更新时间**：2025-10-18

