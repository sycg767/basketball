<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="600px"
    @close="handleClose"
  >
    <div class="rules-content">
      <!-- 卡片信息 -->
      <div v-if="cardInfo" class="card-info">
        <div class="info-item">
          <span class="label">卡片名称:</span>
          <span class="value">{{ cardInfo.cardName }}</span>
        </div>
        <div class="info-item" v-if="cardInfo.cardNo">
          <span class="label">卡号:</span>
          <span class="value">{{ cardInfo.cardNo }}</span>
        </div>
        <div class="info-item" v-if="cardInfo.cardType === 2 && cardInfo.remainingTimes !== undefined">
          <span class="label">剩余次数:</span>
          <span class="value highlight">{{ cardInfo.remainingTimes }} 次</span>
        </div>
        <div class="info-item" v-if="cardInfo.cardType === 3 && cardInfo.balance !== undefined">
          <span class="label">卡内余额:</span>
          <span class="value highlight">¥{{ cardInfo.balance }}</span>
        </div>
        <div class="info-item" v-if="cardInfo.cardType === 1 && cardInfo.expireDate">
          <span class="label">有效期至:</span>
          <span class="value">{{ cardInfo.expireDate }}</span>
        </div>
      </div>

      <el-divider />

      <!-- 使用规则说明 -->
      <div class="rules-section">
        <h3 class="section-title">
          <el-icon><InfoFilled /></el-icon>
          会员卡使用规则
        </h3>

        <!-- 次卡规则 -->
        <div v-if="!cardType || cardType === 2" class="rule-item">
          <div class="rule-header">
            <el-icon class="rule-icon"><Ticket /></el-icon>
            <h4>次卡使用规则</h4>
          </div>
          <ul class="rule-list">
            <li>每次预订扣减 <strong>1次</strong>，不论预订时长</li>
            <li>例如：预订2小时 = 扣1次，预订5小时 = 扣1次</li>
            <li>适合经常使用但时长不固定的用户</li>
            <li>次数用完后会员卡自动失效</li>
          </ul>
        </div>

        <!-- 储值卡规则 -->
        <div v-if="!cardType || cardType === 3" class="rule-item">
          <div class="rule-header">
            <el-icon class="rule-icon"><Wallet /></el-icon>
            <h4>储值卡使用规则</h4>
          </div>
          <ul class="rule-list">
            <li>按 <strong>实际订单金额</strong> 扣减余额</li>
            <li>可随时充值，余额永久有效</li>
            <li>适合长期使用的用户</li>
            <li>余额不足时可继续充值使用</li>
          </ul>
        </div>

        <!-- 时间卡规则 -->
        <div v-if="!cardType || cardType === 1" class="rule-item">
          <div class="rule-header">
            <el-icon class="rule-icon"><Clock /></el-icon>
            <h4>时间卡使用规则</h4>
          </div>
          <ul class="rule-list">
            <li>有效期内预订享受 <strong>会员折扣</strong></li>
            <li>不限次数，不限时长</li>
            <li>激活后开始计算有效期</li>
            <li>适合高频使用的用户</li>
            <li>过期后会员卡自动失效</li>
          </ul>
        </div>
      </div>

      <!-- 特别提示 -->
      <el-alert
        v-if="showActivateWarning"
        title="激活后立即生效，时间卡将开始计算有效期"
        type="warning"
        :closable="false"
        show-icon
        class="activate-warning"
      />
    </div>

    <template #footer>
      <el-button @click="handleClose">{{ cancelText }}</el-button>
      <el-button v-if="showConfirm" type="primary" @click="handleConfirm" :loading="loading">
        {{ confirmText }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { InfoFilled, Ticket, Wallet, Clock } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '会员卡使用规则'
  },
  cardInfo: {
    type: Object,
    default: null
  },
  cardType: {
    type: Number,
    default: null // null表示显示所有规则，1/2/3表示只显示对应类型
  },
  showConfirm: {
    type: Boolean,
    default: false
  },
  confirmText: {
    type: String,
    default: '确认'
  },
  cancelText: {
    type: String,
    default: '关闭'
  },
  showActivateWarning: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const handleClose = () => {
  visible.value = false
}

const handleConfirm = () => {
  emit('confirm')
}
</script>

<style lang="scss" scoped>
.rules-content {
  .card-info {
    background: #f5f7fa;
    padding: 16px;
    border-radius: 8px;
    margin-bottom: 16px;

    .info-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
      font-size: 14px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: #606266;
      }

      .value {
        font-weight: 500;
        color: #303133;

        &.highlight {
          color: #409eff;
          font-size: 16px;
          font-weight: 600;
        }
      }
    }
  }

  .rules-section {
    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 20px 0;

      .el-icon {
        color: #409eff;
      }
    }

    .rule-item {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      .rule-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;

        .rule-icon {
          font-size: 20px;
          color: #409eff;
        }

        h4 {
          font-size: 15px;
          font-weight: 600;
          color: #303133;
          margin: 0;
        }
      }

      .rule-list {
        margin: 0;
        padding-left: 28px;
        list-style: none;

        li {
          position: relative;
          font-size: 14px;
          line-height: 1.8;
          color: #606266;
          margin-bottom: 6px;

          &:before {
            content: '•';
            position: absolute;
            left: -16px;
            color: #409eff;
            font-weight: bold;
          }

          strong {
            color: #409eff;
            font-weight: 600;
          }
        }
      }
    }
  }

  .activate-warning {
    margin-top: 16px;
  }
}
</style>
