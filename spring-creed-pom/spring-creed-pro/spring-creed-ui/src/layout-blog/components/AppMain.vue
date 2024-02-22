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
</script>

<style scoped>
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
<style>
/* fix css style bug in open el-dialog */
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
</style>
