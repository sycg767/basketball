# 篮球场馆管理系统 - 后端开发规范

## 一、项目结构

```
basketball-system/
├── src/main/java/com/basketball/
│   ├── common/              # 公共模块
│   │   ├── exception/      # 异常处理
│   │   └── result/         # 统一响应
│   ├── config/             # 配置类
│   ├── security/           # 安全模块(JWT)
│   ├── entity/             # 实体类(对应数据库表)
│   ├── dto/request/        # 请求DTO
│   ├── vo/                 # 视图对象
│   ├── mapper/             # MyBatis Mapper
│   ├── service/            # 服务接口
│   │   └── impl/          # 服务实现
│   └── controller/         # 控制器
│       ├── admin/         # 管理端
│       └── 用户端
```

## 二、命名规范

### 1. 类命名(大驼峰)

| 类型 | 规则 | 示例 |
|------|------|------|
| Entity | 表名 | `Venue`, `Booking`, `VenuePrice` |
| VO | 实体+VO | `VenueVO`, `BookingVO` |
| DTO请求 | 动作+实体+DTO | `VenueCreateDTO`, `BookingPayDTO` |
| DTO查询 | 实体+QueryDTO | `VenueQueryDTO` |
| Mapper | 实体+Mapper | `VenueMapper` |
| Service | I+实体+Service | `IVenueService` |
| ServiceImpl | 实体+ServiceImpl | `VenueServiceImpl` |
| Controller | 实体+Controller | `VenueController` |
| 管理端Controller | Admin+实体+Controller | `AdminVenueController` |

### 2. 方法命名(小驼峰)

| 操作 | 规则 | 示例 |
|------|------|------|
| 创建 | create实体 | `createVenue()` |
| 更新 | update实体 | `updateVenue()` |
| 删除 | delete实体 | `deleteVenue()` |
| 查询单个 | get实体ById | `getVenueById()` |
| 查询列表 | list实体s | `listVenues()` |
| 分页查询 | get实体List | `getBookingList()` |
| 业务方法 | 动词+名词 | `payBooking()`, `cancelBooking()` |

## 三、数据库字段映射

### 1. 命名转换
- **数据库**: snake_case (venue_name)
- **Java**: camelCase (venueName)
- **自动映射**: MyBatis-Plus自动转换

### 2. 类型映射

| 数据库 | Java | 示例 |
|--------|------|------|
| BIGINT | Long | `private Long id;` |
| INT/TINYINT | Integer | `private Integer status;` |
| VARCHAR/TEXT | String | `private String venueName;` |
| DECIMAL(10,2) | BigDecimal | `private BigDecimal price;` |
| DATETIME | LocalDateTime | `private LocalDateTime createTime;` |
| DATE | LocalDate | `private LocalDate bookingDate;` |
| TIME | LocalTime | `private LocalTime startTime;` |

### 3. JSON字段处理
```java
// 数据库: images TEXT (JSON数组)
// Entity: private String images;
// VO: private List<String> imageList;

// 转换
List<String> list = objectMapper.readValue(
    images, new TypeReference<List<String>>() {}
);
```

## 四、API路径规范

### 1. RESTful规范
```
POST   /api/资源        # 创建
GET    /api/资源/{id}   # 查询
GET    /api/资源        # 列表
PUT    /api/资源/{id}   # 更新
DELETE /api/资源/{id}   # 删除
```

### 2. 用户端接口

```java
// 认证
POST   /api/auth/register          // 注册
POST   /api/auth/login             // 登录
GET    /api/auth/info              // 用户信息

// 场地
GET    /api/venue                  // 场地列表(分页)
GET    /api/venue/{id}             // 场地详情
GET    /api/venue/prices/{id}      // 价格列表

// 预订
POST   /api/booking                // 创建预订
GET    /api/booking/my             // 我的预订
GET    /api/booking/{id}           // 预订详情
PUT    /api/booking/{id}/pay       // 支付
PUT    /api/booking/{id}/cancel    // 取消
GET    /api/booking/check-available // 检查可用
```

### 3. 管理端接口

```java
// 场地管理
POST   /api/admin/venue            // 创建
PUT    /api/admin/venue/{id}       // 更新
DELETE /api/admin/venue/{id}       // 删除
GET    /api/admin/venue            // 列表
POST   /api/admin/venue/price      // 设置价格

// 预订管理
GET    /api/admin/booking          // 列表
PUT    /api/admin/booking/{id}/cancel // 取消

// 用户管理
GET    /api/admin/user             // 列表
PUT    /api/admin/user/{id}/status // 更新状态
```

## 五、Entity规范

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("venue")  // 表名
@Schema(description = "场地实体")
public class Venue extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "场地名称")
    private String venueName;  // 对应 venue_name

    @Schema(description = "场地类型: 1-室内全场, 2-室内半场, 3-室外全场, 4-室外半场")
    private Integer venueType;

    @Schema(description = "状态: 0-不可用, 1-可用, 2-维护中")
    private Integer status;

    // ... 其他字段必须与数据库一致
}
```

## 六、DTO规范

### 1. 请求DTO
```java
@Data
@Schema(description = "创建预订请求")
public class BookingCreateDTO {

    @NotNull(message = "场地ID不能为空")
    private Long venueId;

    @NotNull(message = "预订日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String contactPhone;
}
```

### 2. 查询DTO
```java
@Data
public class VenueQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private Integer venueType;
    private Integer status;
}
```

## 七、VO规范

```java
@Data
@Schema(description = "场地视图对象")
public class VenueVO {
    private Long id;
    private String venueName;

    // 图片列表 - 从JSON转换
    private List<String> imageList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

### Entity转VO
```java
private VenueVO convertToVO(Venue venue) {
    VenueVO vo = new VenueVO();
    BeanUtils.copyProperties(venue, vo);

    // 处理JSON字段
    if (StringUtils.isNotBlank(venue.getImages())) {
        vo.setImageList(objectMapper.readValue(
            venue.getImages(),
            new TypeReference<List<String>>() {}
        ));
    }
    return vo;
}
```

## 八、统一响应

### 成功
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {...}
}
```

### 分页
```json
{
    "code": 200,
    "data": {
        "records": [...],
        "total": 100,
        "current": 1,
        "size": 10,
        "pages": 10
    }
}
```

## 九、数据库状态码

### 通用状态
```
0 - 禁用/删除/无效
1 - 启用/正常/有效
2 - 特殊状态(维护中等)
```

### 预订状态(booking.status)
```
0 - 待支付
1 - 已支付
2 - 已取消
3 - 已完成
4 - 已退款
5 - 超时取消
```

### 场地类型(venue.venue_type)
```
1 - 室内全场
2 - 室内半场
3 - 室外全场
4 - 室外半场
```

### 时段类型(venue_price.time_type)
```
1 - 工作日
2 - 周末
3 - 节假日
```

### 支付方式(payment_method)
```
1 - 在线支付
2 - 余额支付
3 - 会员卡
4 - 现场支付
```

## 十、Service规范

### 接口
```java
public interface IBookingService {
    Long createBooking(Long userId, BookingCreateDTO dto);
    PageResult<BookingVO> getUserBookingList(...);
    BookingVO getBookingDetail(Long id);
    void payBooking(Long id, BookingPayDTO dto);
    void cancelBooking(Long id, BookingCancelDTO dto);
    boolean checkVenueAvailable(...);
}
```

### 实现
```java
@Service
public class BookingServiceImpl
    extends ServiceImpl<BookingMapper, Booking>
    implements IBookingService {

    @Resource
    private BookingMapper bookingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBooking(Long userId, BookingCreateDTO dto) {
        // 1. 校验
        // 2. 业务逻辑
        // 3. 数据库操作
        // 4. 返回结果
    }
}
```

## 十一、Controller规范

```java
@RestController
@RequestMapping("/api/booking")
@Tag(name = "预订管理")
public class BookingController {

    @Resource
    private IBookingService bookingService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @Operation(summary = "创建预订")
    public Result<Long> createBooking(
        @Valid @RequestBody BookingCreateDTO dto,
        HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(bookingService.createBooking(userId, dto));
    }

    @GetMapping("/my")
    public Result<PageResult<BookingVO>> getMyList(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) Integer status,
        HttpServletRequest request
    ) {
        Long userId = jwtTokenProvider.getUserIdFromRequest(request);
        return Result.success(
            bookingService.getUserBookingList(userId, page, pageSize, status)
        );
    }
}
```

## 十二、异常处理

```java
// 抛出业务异常
throw new BusinessException("场地不存在");
throw new BusinessException(ErrorCode.USER_NOT_FOUND);

// 全局处理器自动返回统一格式
```

## 十三、开发注意事项

### 1. 字段匹配
- Entity字段必须与数据库表字段一致
- 使用驼峰自动转换
- JSON字段在VO中转换

### 2. 事务管理
```java
@Transactional(rollbackFor = Exception.class)
```

### 3. 日期时间
```java
LocalDateTime.now()
LocalDate.now()
LocalTime.now()

@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
```

### 4. 分页查询
```java
Page<Venue> page = new Page<>(pageNum, pageSize);
IPage<Venue> result = mapper.selectPage(page, wrapper);
```

### 5. 密码加密
```java
String encoded = passwordEncoder.encode(raw);
boolean matches = passwordEncoder.matches(raw, encoded);
```
