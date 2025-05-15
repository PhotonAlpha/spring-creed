<template>
  <layout-blog :layer="pathVal" />
</template>
<script setup lang="ts">
import LayoutBlog from '@/layout-blog/index.vue'
const pathVal = ref('Vue')
</script>
<style scoped lang="scss"></style>
