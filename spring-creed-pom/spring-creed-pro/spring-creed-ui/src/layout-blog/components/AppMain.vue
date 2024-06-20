<template>
  <div v-loading="loading">
    <section class="app-main">
      <transition name="fade-transform" mode="out-in">
        <timeline
          @handle-pagenum="handleSizeChange"
          @show-details="handleShowDetails"
          :tree="blogItemsPagination"
        />
      </transition>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Timeline } from '@/components/Timeline'
import { CURRENT_TITLE } from '@/utils/constant'
import * as GithubApi from '@/api/github'
import { useBolgTreeStore } from '@/store/modules/blogmatrix'

import { BolgCategory, BolgTreeItem, GitCategoryItem, GitTree, GitTreeItem } from '@/api/github'
import { reconstructorTitle } from '@/utils/pathTransferUtil'
// import * as DictTypeApi from '@/api/system/dict/dict.type'

const bolgTreeStore = useBolgTreeStore()
const router = useRouter()
const loading = ref(true)
const props = withDefaults(
  defineProps<{
    layer: string
  }>(),
  {
    layer: () => 'java'
  }
)
const blogItemsPagination = ref<BolgTreeItem[]>([])
const blogItems = ref<BolgTreeItem[]>([])
const activeCategary = ref<GitTreeItem>()
console.log(`output`, props.layer)

const loadMasterTrees = async () => {
  const data = await GithubApi.getMasterTrees()
  // console.log('data', data)
  if (data.tree && Array.isArray(data.tree)) {
    const array = ['navigation']
    const targetItems = data.tree.filter((item: any) => array.includes(item.path))
    // console.log('targetItems', targetItems)
    loadDestinationTrees(targetItems)
  }
}
const loadDestinationTrees = async (firstCate: GitCategoryItem[]) => {
  // 设置目录
  if (firstCate && Array.isArray(firstCate)) {
    const cates: Promise<BolgCategory[]>[] = firstCate.map(async (item) => {
      const { tree }: GitTree = await GithubApi.getDestinationTrees(item.sha)
      console.log('tree', item.sha, tree)
      let cates: BolgCategory[] = []
      if (tree && Array.isArray(tree)) {
        const activeCategaryVal = tree.find(
          (cat) => cat.path.toLowerCase() === props.layer.toLowerCase() && cat.type === 'tree'
        )
        if (activeCategaryVal) {
          activeCategary.value = activeCategaryVal
        }

        cates = tree.map((cat) => {
          return { label: cat.path, url: cat.url, sha: cat.sha }
        })
      }
      return cates
    })
    Promise.all(cates).then((result) => {
      loading.value = false
      bolgTreeStore.setCategory(result.flatMap((i) => i))
    })
  }
}

const handleSizeChange = (pageNo: number) => {
  const start = (pageNo - 1) * 10
  const end = pageNo * 10
  blogItemsPagination.value = blogItems.value.slice(start, end)
  console.log(`page no`, pageNo)
}
const handleShowDetails = (sha: string, title: string) => {
  console.log('showDetails', sha, title, CURRENT_TITLE)

  router.push({
    name: 'Blog',
    params: {
      shaCode: sha
    }
  })
}

/** 初始化 **/
onMounted(() => {
  loadMasterTrees()
})
watch(
  () => activeCategary.value,
  async (activeCategaryVal) => {
    console.log(`watch val`, activeCategaryVal)
    // 设置前三个日志为推荐选项
    if (activeCategaryVal && activeCategaryVal.sha) {
      const { tree }: GitTree = await GithubApi.getDestinationTrees(activeCategaryVal.sha)
      blogItems.value = reconstructorTitle(tree)
      console.log('blogItems', blogItems.value)
      blogItemsPagination.value = blogItems.value.slice(0, 10)

      // 前三个日志为推荐选项
      const latestRecommend = blogItems.value.slice(0, 3).map((cat) => {
        return {
          label: cat.name,
          url: cat.git_url,
          sha: cat.sha,
          title: props.layer,
          name: cat.name,
          date: cat.date
        }
      })
      console.log('latestRecommend', latestRecommend)
      bolgTreeStore.setLatestRecommend(latestRecommend)

      bolgTreeStore.setTrees(blogItems.value)
    }
  }
)
</script>

<style scoped lang="scss">
.app-main {
  /*50 = navbar  */
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
  background: transparent;
  margin-bottom: 50px;
}
.fixed-header + .app-main {
  padding-top: 50px;
}
</style>
<style lang="scss">
/* fix css style bug in open el-dialog */
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
</style>
