<script lang="ts" setup>
import { propTypes } from '@/utils/propTypes'
import Iconify from '@purge-icons/generated'
import { useDesign } from '@/hooks/web/useDesign'

defineOptions({ name: 'Icon' })
// 维护一个mapping列表管理icon： https://github.com/unplugin/unplugin-icons/issues/5
const items = [
  {
    name: 'home',
    icon: IconMdiHomeSoundOut
  },
  {
    name: 'peoples',
    icon: IconMdiPeopleGroup
  },
  {
    name: 'monitor',
    icon: IconMdiMonitorDashboard
  },
  {
    name: 'eye',
    icon: IconMdiEye
  },
  {
    name: 'user',
    icon: IconMdiUserHeart
  },
  {
    name: 'table',
    icon: IconMdiFileTable
  },
  {
    name: 'tree',
    icon: IconMdiPineTreeVariant
  },
  {
    name: 'post',
    icon: IconMdiPostageStamp
  },
  {
    name: 'dict',
    icon: IconMdiDictionary
  },
  {
    name: 'message',
    icon: IconMdiMessageBadge
  },
  {
    name: 'log',
    icon: IconMdiMathLog
  },
  {
    name: 'form',
    icon: IconMdiFormOutline
  },
  {
    name: 'logininfor',
    icon: IconMdiPsychology
  },
  {
    name: 'tool',
    icon: IconMdiTools
  },
  {
    name: 'ep:tools',
    icon: IconEpTools
  },
  {
    name: 'ep:menu',
    icon: IconEpMenu
  },
  {
    name: 'ep:switch-button',
    icon: IconEpSwitchButton
  },
  {
    name: 'online',
    icon: IconMdiAccountOnline
  },
  {
    name: 'validCode',
    icon: IconMdiCodeBlockBraces
  },
  {
    name: 'phone',
    icon: IconMdiCellphone
  },
  {
    name: 'email',
    icon: IconMdiEmail
  },
  {
    name: 'education',
    icon: IconMdiBookEducation
  },
  {
    name: 'edit',
    icon: IconMdiFileDocumentEdit
  },
  {
    name: 'code',
    icon: IconMdiFileCode
  },
  {
    name: 'row',
    icon: IconMdiTableRowHeight
  },
  {
    name: 'rate',
    icon: IconMdiStarRate
  },
  {
    name: 'build',
    icon: IconMdiBuild
  },
  {
    name: 'swagger',
    icon: IconMdiFileDocumentBoxCheck
  },
  {
    name: 'download',
    icon: IconMdiTrayDownload
  },
  {
    name: 'config',
    icon: IconMdiSettingsPlay
  },
  {
    name: 'upload',
    icon: IconMdiCloudUpload
  },
  {
    name: 'redis',
    icon: IconMdiMemory
  },
  {
    name: 'server',
    icon: IconMdiServer
  },
  {
    name: 'job',
    icon: IconMdiFanSchedule
  },
  {
    name: 'eye-open',
    icon: IconMdiEyeCircle
  },
  {
    name: 'money',
    icon: IconMdiMoney100
  },
  {
    name: 'notebook',
    icon: IconMdiNotebookHeart
  },
  {
    name: 'angular',
    icon: IconMdiAngular
  },
  {
    name: 'java',
    icon: IconMdiLanguageJava
  },
  {
    name: 'spring',
    icon: IconMdiLeafCircle
  },
  {
    name: 'cloud',
    icon: IconMdiCloudTags
  },
  {
    name: 'vuejs',
    icon: IconMdiVuejs
  },
  {
    name: 'receiptSend',
    icon: IconMdiReceiptSend
  },
  {
    name: 'github',
    icon: IconMdiGithub
  },
  {
    name: 'vite',
    icon: IconMdiVuetify
  },
  {
    name: 'react',
    icon: IconMdiReact
  },
  {
    name: 'webpack',
    icon: IconMdiWebpack
  },
  {
    name: 'ion:language-sharp',
    icon: IconMdiLanguageBox
  },
  {
    name: 'ep:bell',
    icon: IconMdiBell
  },
  {
    name: 'zmdi:fullscreen',
    icon: IconMdiFullscreen
  },
  {
    name: 'zmdi:fullscreen-exit',
    icon: IconMdiFullscreenExit
  },
  {
    name: 'mdi:format-size',
    icon: IconMdiFormatSize
  },
  {
    name: 'ep:refresh',
    icon: IconEpRefresh
  },
  {
    name: 'ep:refresh-right',
    icon: IconEpRefreshRight
  },
  {
    name: 'ep:refresh',
    icon: IconEpRefresh
  },
  {
    name: 'ep:close',
    icon: IconEpClose
  },
  {
    name: 'ep:d-arrow-left',
    icon: IconEpDArrowLeft
  },
  {
    name: 'ep:d-arrow-right',
    icon: IconEpDArrowRight
  },
  {
    name: 'ep:discount',
    icon: IconEpDiscount
  },
  {
    name: 'ep:minus',
    icon: IconEpMinus
  },
  {
    name: 'default',
    icon: IconMdiBlinky
  }
]

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
  // console.log(`symbolId:`, res) //debug 查看Miss的symbolId
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
  // console.log(`output->icon`, icon)
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
const getIcon = (icon: string) => {
  return items.find((i) => i.name === icon) || items.slice(-1).pop()
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
      <component :is="getIcon(symbolId)?.icon" />
    </span>
  </ElIcon>
</template>
