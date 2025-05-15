<template>
  <div :class="classObj" class="app-wrapper">
    <div
      v-if="device === 'mobile' && sidebar.opened"
      class="drawer-bg"
      @click="handleClickOutside"
    ></div>
    <!-- <el-scrollbar ref="mainScrollbar" class="page-component__scrollbar"> -->
    <el-container ref="mainScrollbar">
      <el-main id="mainContent">
        <el-row>
          <el-col
            id="side-cnt"
            class="hidden-sm-only side-cnt"
            :class="{ 'side-cnt-scrolled': isLeftSideScrolled }"
            :md="6"
            :xs="0"
          >
            <el-scrollbar class="page-component__scrollbar">
              <profile />
            </el-scrollbar>
          </el-col>
          <el-col :md="12" :sm="24" :xs="24">
            <app-main :layer="layer" />
          </el-col>
          <el-col
            :md="4"
            :xs="0"
            class="hidden-sm-only right-side-cnt"
            :class="{ 'side-cnt-scrolled': isLeftSideScrolled }"
          >
            <el-scrollbar class="page-component__scrollbar">
              <archive
                :recommend="latestRecommend"
                @get-category-list="handleLazyLoadCategory"
                @handler-category="handlerCategory"
              />
            </el-scrollbar>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
    <!-- </el-scrollbar> -->
  </div>
</template>

<script setup lang="ts">
import { Profile, Archive, AppMain } from './components'
import { useBolgTreeStore } from '@/store/modules/blogmatrix'
import { reconstructorTitle } from '@/utils/pathTransferUtil'
import * as GithubApi from '@/api/github'
import { BolgTreeItem } from '@/api/github'
import { storeToRefs } from 'pinia'
const router = useRouter()
const bolgTreeStore = useBolgTreeStore()

const { latestRecommend } = storeToRefs(bolgTreeStore)

withDefaults(
  defineProps<{
    title: string
    arr: number[]
    layer: string
  }>(),
  {
    arr: () => [123],
    title: () => '',
    layer: () => 'java'
  }
)
const device = ref('web')
const isLeftSideScrolled = ref(false)
const sidebar = computed(() => {
  return {
    opened: true
  }
})
const classObj = ref({
  hideSidebar: false,
  openSidebar: true,
  withoutAnimation: false,
  mobile: false
})

const handleClickOutside = () => {
  // this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
  console.log(`handleClickOutside->`)
}

const handleLazyLoadCategory = (node, resolve) => {
  console.log('loadNode', node, node.level, node.data)
  if (node.level === 0) {
    return resolve(bolgTreeStore.getCategory)
  }
  if (node.level > 1) {
    return resolve([])
  }
  const { sha } = node.data
  GithubApi.getDestinationTrees(sha)
    .then((response) => {
      const result = response
      const blogItems: BolgTreeItem[] = reconstructorTitle(result.tree)
      console.log('blogItems', blogItems)
      if (blogItems && blogItems.length > 0) {
        const data = blogItems.map((item) => {
          return { label: item.name, leaf: true, sha: item.sha }
        })
        return resolve(data)
      } else {
        return resolve([])
      }
    })
    .catch((error) => {
      console.log('error occur', error)
    })
}

const handlerCategory = (data) => {
  console.log(`output->data`, data)
  if (data && data.leaf) {
    router.push({
      name: 'Blog',
      params: {
        shaCode: data.sha
      }
    })
    // console.log('handlerCategory', data, CURRENT_TITLE)
    // localStorage.setItem(CURRENT_TITLE, data.label)
    // this.$router.push({ name: 'spring-details', params: { sha: data.sha }})
  }
}

watch(
  () => bolgTreeStore.getLatestRecommend,
  (newVal) => {
    console.log('appStore.title.watch>>>', newVal)
  },
  { deep: true }
)
</script>

<style lang="scss" scoped></style>
