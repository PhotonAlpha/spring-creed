import { useCssVar } from '@vueuse/core'
import type { NProgressOptions } from 'nprogress'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

const primaryColor = useCssVar('--el-color-primary', document.documentElement)

export const useNProgress = () => {
  NProgress.configure({ showSpinner: false } as NProgressOptions)

  const initColor = async () => {
    // nextTick 等待DOM加载完毕，再加载数据。因为VUE更新DOM是异步的
    await nextTick()
    const bar = document.getElementById('nprogress')?.getElementsByClassName('bar')[0] as ElRef
    if (bar) {
      bar.style.background = unref(primaryColor.value)
    }
  }

  initColor()

  const start = () => {
    NProgress.start()
  }

  const done = () => {
    NProgress.done()
  }

  return {
    start,
    done
  }
}
