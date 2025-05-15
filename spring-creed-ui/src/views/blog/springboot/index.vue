<template>
  <layout-blog :layer="pathVal" />
</template>
<script setup lang="ts">
import LayoutBlog from '@/layout-blog/index.vue'

// const route = useRoute()

// const getRouteParams = () => {
//   const s = route.path.split('/').pop()
//   console.log(`route val`, s)
//   return (s && s[0].toUpperCase() + s.slice(1)) || ''
// }
const pathVal = ref('SpringBoot')
</script>
<style scoped lang="scss"></style>
