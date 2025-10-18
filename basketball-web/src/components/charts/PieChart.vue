<template>
  <div ref="chartRef" :style="{ width: width, height: height }"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: '400px'
  },
  data: {
    type: Array,
    required: true,
    /*
    格式示例:
    [
      { name: '普通会员', value: 335 },
      { name: '银卡会员', value: 310 },
      { name: '金卡会员', value: 234 },
      { name: '钻石会员', value: 135 }
    ]
    */
  },
  title: {
    type: String,
    default: ''
  },
  showLabel: {
    type: Boolean,
    default: true
  },
  doughnut: {
    type: Boolean,
    default: false
  }
});

const chartRef = ref(null);
let chartInstance = null;

onMounted(() => {
  initChart();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
  window.removeEventListener('resize', handleResize);
});

watch(() => props.data, () => {
  updateChart();
}, { deep: true });

const initChart = () => {
  if (!chartRef.value) return;

  chartInstance = echarts.init(chartRef.value);
  updateChart();
};

const updateChart = () => {
  if (!chartInstance || !props.data) return;

  const option = {
    title: {
      text: props.title,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 500
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: props.data.map(item => item.name)
    },
    series: [
      {
        name: props.title || '数据分布',
        type: 'pie',
        radius: props.doughnut ? ['40%', '70%'] : '70%',
        center: ['40%', '50%'],
        data: props.data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: props.showLabel,
          formatter: '{b}: {d}%'
        }
      }
    ]
  };

  chartInstance.setOption(option, true);
};

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

defineExpose({
  resize: handleResize
});
</script>
