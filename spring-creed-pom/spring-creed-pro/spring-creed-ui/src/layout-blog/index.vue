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
        <!-- 面包屑
          <el-row type="flex" :class="{'fixed-header':fixedHeader}">
            <navbar style="width: 100%;" />
          </el-row> -->
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
                @getCategoryList="handleLazyLoadCategory"
                @handlerCategory="handlerCategory"
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
import { getDestinationTrees } from '@/api/github/githubApi'
import { reconstructorTitle, Title } from '@/utils/github.helper'

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

const category = computed(() => {
  // return this.$store.getters.category
  return false
})
const sidebar = computed(() => {
  return {
    opened: true
  }
})
// const device = computed(() => {
//   return 'web'
// })
const fixedHeader = computed(() => {
  return '10px'
})
const styleTrianglify = computed(() => {
  // backgroundImage: 'url()'
  backgroundColor: 'red'
  height: 'auto'
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
  console.log('loadNode', node)
  if (node.level === 0) {
    return resolve('this.category')
  }
  if (node.level > 1) return resolve([])

  const { sha } = node.data

  // getDestinationTrees(sha)
  //   .then((response) => {
  //     const result = response
  //     const blogItems: Title[] = reconstructorTitle(result.tree)
  //     console.log('blogItems', blogItems)
  //     if (blogItems && blogItems.length > 0) {
  //       const data = blogItems.map((item) => {
  //         return { label: item.name, subItem: true, sha: item.sha }
  //       })
  //       resolve(data)
  //     }
  //   })
  //   .catch((error) => {
  //     console.log('error occur', error)
  //   })
}

const handlerCategory = (data) => {
  console.log(`output->data`, data)
  if (data && data.subItem) {
    // console.log('handlerCategory', data, CURRENT_TITLE)
    // localStorage.setItem(CURRENT_TITLE, data.label)
    // this.$router.push({ name: 'spring-details', params: { sha: data.sha }})
  }
}
</script>

<style lang="scss" scoped></style>
