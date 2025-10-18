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
    type: Object,
    required: true,
    /*
    格式示例:
    {
      xAxisData: ['场地A', '场地B', '场地C', '场地D', '场地E'],
      series: [
        { name: '使用率', data: [85, 72, 68, 91, 76] }
      ]
    }
    */
  },
  title: {
    type: String,
    default: ''
  },
  yAxisName: {
    type: String,
    default: ''
  },
  horizontal: {
    type: Boolean,
    default: false
  },
  stack: {
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
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      top: props.title ? 35 : 10,
      data: props.data.series?.map(s => s.name) || []
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: props.title ? 70 : 45,
      containLabel: true
    },
    xAxis: {
      type: props.horizontal ? 'value' : 'category',
      data: props.horizontal ? undefined : (props.data.xAxisData || []),
      name: props.horizontal ? props.yAxisName : ''
    },
    yAxis: {
      type: props.horizontal ? 'category' : 'value',
      data: props.horizontal ? (props.data.xAxisData || []) : undefined,
      name: props.horizontal ? '' : props.yAxisName
    },
    series: (props.data.series || []).map(item => ({
      name: item.name,
      type: 'bar',
      data: item.data,
      stack: props.stack ? 'total' : undefined,
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true
        }
      }
    }))
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
