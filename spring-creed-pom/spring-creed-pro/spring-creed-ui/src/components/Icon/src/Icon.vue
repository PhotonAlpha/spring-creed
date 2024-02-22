<script lang="ts" setup>
import { propTypes } from '@/utils/propTypes'
import Iconify from '@purge-icons/generated'
import { useDesign } from '@/hooks/web/useDesign'

defineOptions({ name: 'Icon' })

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('icon')

const props = defineProps({
  // icon name
  icon: propTypes.string,
  // icon color
  color: propTypes.string,
  // icon size
  size: propTypes.number.def(16),
  // icon svg class
  svgClass: propTypes.string.def('')
})

const elRef = ref<ElRef>(null)

const isLocal = computed(() => props.icon.startsWith('svg-icon:'))
// const isLocal = computed(() => false)

const symbolId = computed(() => {
  const res = unref(isLocal) ? `#icon-${props.icon.split('svg-icon:')[1]}` : props.icon
  console.log(`symbolId:`, res)
  return res
})

const getIconifyStyle = computed(() => {
  const { color, size } = props
  return {
    fontSize: `${size}px`,
    color
  }
})

const getSvgClass = computed(() => {
  const { svgClass } = props
  // console.log(`svg props`, props)
  return `iconify ${svgClass}`
})

const updateIcon = async (icon: string) => {
  console.log(`updateIcon`, icon)
  if (unref(isLocal)) return

  const el = unref(elRef)
  if (!el) return

  await nextTick()

  if (!icon) return
  //这是使用API的方式，如果使用本地icon，需要整合插件
  // 参考文档：https://iconify.design/docs/icon-components/vue/
  // https://juejin.cn/post/7087827571861585956
  // npm install --save-dev @iconify/vue
  console.log(`output->icon`, icon)
  const svg = Iconify.renderSVG(icon, {})
  if (svg) {
    el.textContent = ''
    el.appendChild(svg)
  } else {
    const span = document.createElement('span')
    span.className = 'iconify'
    span.dataset.icon = icon
    el.textContent = ''
    el.appendChild(span)
  }
}

watch(
  () => props.icon,
  (icon: string) => {
    updateIcon(icon)
  }
)
</script>

<template>
  <ElIcon :class="prefixCls" :color="color" :size="size">
    <svg v-if="isLocal" :class="getSvgClass" aria-hidden="true">
      <use :xlink:href="symbolId" />
    </svg>

    <span v-else ref="elRef" :class="$attrs.class" :style="getIconifyStyle">
      <span :class="getSvgClass" :data-icon="symbolId"></span>
    </span>
  </ElIcon>
</template>
