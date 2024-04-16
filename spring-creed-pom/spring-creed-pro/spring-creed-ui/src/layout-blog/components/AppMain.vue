<template>
  <div>
    AppMain {{ layer }}
    <section class="app-main">
      <transition name="fade-transform" mode="out-in">
        <timeline @handle-pagenum="handleSizeChange" @show-details="handleShowDetails" />
        <!-- <timeline
          :total-num="blogItems.length"
          :tree="blogItemsPagination"
          @handle-pagenum="handleSizeChange"
        /> -->
      </transition>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Timeline } from '@/components/Timeline'
import { CURRENT_TITLE } from '@/utils/constant'
import * as GithubApi from '@/api/github'
// import * as DictTypeApi from '@/api/system/dict/dict.type'

const router = useRouter()

withDefaults(
  defineProps<{
    layer: string
  }>(),
  {
    layer: () => 'java'
  }
)
const blogItemsPagination = ref([])
const blogItems = ref([])

const loadMasterTrees = async () => {
  const data = await GithubApi.getMasterTrees('about')
  // console.log('data', data)
  if (data.tree && Array.isArray(data.tree)) {
    const array = ['navigation']
    const targetItems = data.tree.filter((item: any) => array.includes(item.path))
    // console.log('targetItems', targetItems)
    targetItems.forEach((element) => {
      console.log(`element`, element)
      loadDestinationTrees(element.sha)
    })
  }
}
const loadDestinationTrees = async (sha: string) => {
  const data = await GithubApi.getDestinationTrees(sha)
  console.log('tree', data.tree)
  // const result = data.tree
  // console.log('result', result)
  if (data.tree) {
    blogItems.value = data.tree
    console.log('blogItems', blogItems.value)
  }
}

const handleSizeChange = (pageNo: number) => {
  console.log(`page no`, pageNo)
}
const handleShowDetails = (sha: string, title: string) => {
  console.log('showDetails', sha, title, CURRENT_TITLE)
  //可以放到state中
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
