<template>
  <layout-blog :layer="pathVal" />
</template>
<script setup lang="ts">
import LayoutBlog from '@/layout-blog/index.vue'
const pathVal = ref('Angular')
</script>
<style scoped lang="scss"></style>
