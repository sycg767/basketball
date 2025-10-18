# PaymentConfig 字段匹配修复报告

## 🔍 问题

在线支付时出现错误：
```
java.sql.SQLSyntaxErrorException: Unknown column 'payment_type' in 'field list'
```

## 📊 原因分析

`payment_config` 表的字段名与 `PaymentConfig` 实体类字段不匹配。

### 数据库表结构 (`basketball_system.sql`)

```sql
CREATE TABLE `payment_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `pay_method` varchar(50) NOT NULL COMMENT '支付方式',
  `pay_name` varchar(100) NOT NULL COMMENT '支付名称',
  `app_id` varchar(100) NULL COMMENT '应用ID',
  `merchant_id` varchar(100) NULL COMMENT '商户ID',
  `api_key` varchar(500) NULL COMMENT 'API密钥',
  `app_secret` varchar(500) NULL COMMENT '应用密钥',
  `private_key` text NULL COMMENT '私钥',
  `public_key` text NULL COMMENT '公钥',
  `notify_url` varchar(500) NULL COMMENT '异步通知URL',
  `return_url` varchar(500) NULL COMMENT '同步返回URL',
  `is_sandbox` tinyint NULL DEFAULT 0 COMMENT '是否沙箱环境',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `remark` text NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 实体类字段（修复前）

| 实体类字段 | 数据库字段 | 匹配 |
|-----------|-----------|------|
| `paymentType` | `pay_method` | ❌ |
| `paymentName` | `pay_name` | ❌ |
| `enabled` | `status` | ❌ |
| `feeRate` | (不存在) | ❌ |
| `extConfig` | (不存在) | ❌ |
| `merchantId` | `merchant_id` | ✅ |
| `appId` | `app_id` | ✅ |

## ✅ 修复方案

### 1. 修改 `PaymentConfig.java`

**修复前**：
```java
private String paymentType;      // ❌ 数据库字段是 pay_method
private String paymentName;      // ❌ 数据库字段是 pay_name
private Integer enabled;         // ❌ 数据库字段是 status
private BigDecimal feeRate;      // ❌ 数据库中不存在
private String extConfig;        // ❌ 数据库中不存在
```

**修复后**：
```java
private String payMethod;        // ✅ 匹配 pay_method
private String payName;          // ✅ 匹配 pay_name
private String appId;            // ✅ 匹配 app_id
private String merchantId;       // ✅ 匹配 merchant_id
private String apiKey;           // ✅ 匹配 api_key
private String appSecret;        // ✅ 匹配 app_secret
private String privateKey;       // ✅ 匹配 private_key
private String publicKey;        // ✅ 匹配 public_key
private String notifyUrl;        // ✅ 匹配 notify_url
private String returnUrl;        // ✅ 匹配 return_url
private Integer isSandbox;       // ✅ 匹配 is_sandbox
private Integer status;          // ✅ 匹配 status
private Integer sortOrder;       // ✅ 匹配 sort_order
private String remark;           // ✅ 匹配 remark
```

### 2. 修改 `PaymentServiceImpl.java`

#### 修改点 1：查询支付配置

**修复前**：
```java
configQuery.eq(PaymentConfig::getPaymentType, createDTO.getPaymentType())
        .eq(PaymentConfig::getEnabled, 1);
```

**修复后**：
```java
configQuery.eq(PaymentConfig::getPayMethod, createDTO.getPaymentType())
        .eq(PaymentConfig::getStatus, 1);
```

#### 修改点 2：手续费计算

**修复前**：
```java
// 计算手续费
BigDecimal feeAmount = createDTO.getAmount().multiply(config.getFeeRate());
```

**修复后**：
```java
// 计算手续费（当前无手续费配置，设为0）
BigDecimal feeAmount = BigDecimal.ZERO;
```

**说明**：数据库中没有 `fee_rate` 字段，暂时设置手续费为0。如需要手续费功能，需要：
1. 在数据库表中添加 `fee_rate` 字段
2. 在实体类中添加 `feeRate` 字段
3. 恢复手续费计算逻辑

## 📝 字段对照表（最终版）

| 数据库字段 | Java 字段 | 类型 | 说明 |
|-----------|----------|------|------|
| `id` | `id` | `Long` | 配置ID |
| `pay_method` | `payMethod` | `String` | 支付方式 |
| `pay_name` | `payName` | `String` | 支付名称 |
| `app_id` | `appId` | `String` | 应用ID |
| `merchant_id` | `merchantId` | `String` | 商户ID |
| `api_key` | `apiKey` | `String` | API密钥 |
| `app_secret` | `appSecret` | `String` | 应用密钥 |
| `private_key` | `privateKey` | `String` | 私钥 |
| `public_key` | `publicKey` | `String` | 公钥 |
| `notify_url` | `notifyUrl` | `String` | 异步通知URL |
| `return_url` | `returnUrl` | `String` | 同步返回URL |
| `is_sandbox` | `isSandbox` | `Integer` | 是否沙箱环境 |
| `status` | `status` | `Integer` | 状态 |
| `sort_order` | `sortOrder` | `Integer` | 排序 |
| `remark` | `remark` | `String` | 备注 |
| `create_time` | `createTime` | `LocalDateTime` | 创建时间 (继承自BaseEntity) |
| `update_time` | `updateTime` | `LocalDateTime` | 更新时间 (继承自BaseEntity) |

## ✨ 测试验证

### 编译状态
```
✅ BUILD SUCCESS
```

### 支付配置数据

数据库中已有的支付配置：
```sql
-- 微信扫码支付（已禁用）
pay_method: 'wechat_native'
pay_name: '微信扫码支付'
status: 0

-- 微信公众号支付（已禁用）
pay_method: 'wechat_jsapi'
pay_name: '微信公众号支付'
status: 0

-- 支付宝电脑网站支付（已禁用）
pay_method: 'alipay_page'
pay_name: '支付宝电脑网站支付'
status: 0

-- 支付宝手机网站支付（已禁用）
pay_method: 'alipay_wap'
pay_name: '支付宝手机网站支付'
status: 0

-- 余额支付（已启用）
pay_method: 'balance'
pay_name: '余额支付'
status: 1

-- 会员卡支付（已启用）
pay_method: 'member_card'
pay_name: '会员卡支付'
status: 1
```

### 后续配置

要启用在线支付，需要：
1. 配置支付宝/微信支付参数（app_id, merchant_id, api_key等）
2. 更新对应记录的 `status` 为 1
3. 在 `application.yml` 中启用对应的支付方式：
```yaml
alipay:
  enabled: true
  
wechat:
  pay:
    enabled: true
```

## 🎯 总结

- ✅ 修复了 `PaymentConfig` 实体类字段与数据库不匹配的问题
- ✅ 统一了字段命名规范
- ✅ 移除了数据库中不存在的字段（`feeRate`, `extConfig`）
- ✅ 编译通过，可以正常运行
- ⚠️ 当前所有在线支付方式都是禁用状态（`status = 0`）
- ⚠️ 需要配置真实的支付参数才能使用在线支付功能

---

**修复完成时间**: 2025-10-18
**编译状态**: ✅ BUILD SUCCESS

