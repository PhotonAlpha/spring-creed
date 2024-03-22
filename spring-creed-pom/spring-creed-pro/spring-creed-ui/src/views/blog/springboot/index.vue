<template>
  <layout-blog :layer="pathVal" />
</template>
<script setup lang="ts">
import LayoutBlog from '@/layout-blog/index.vue'

const route = useRoute()
console.log(`route`, route)
const getRouteParams = () => {
  const val = route.path.replace('/', '')
  console.log(`route val`, val)
  return val
}
const pathVal = ref(getRouteParams())
</script>
<style scoped lang="scss"></style>
