<template>
  <div style="margin-top: 20px">
    <el-checkbox-group v-model="checkboxGroup1">
      <el-checkbox-button
        v-for="item in reactions"
        :key="item.id"
        :label="item.content"
        @change="handleCheckedChange($event, item)"
      >
        <span class="box-image-font">{{ getemoji(item.content) }}</span>
        <!-- <el-image :fit="'contain'" class="box-image-icon" :src="getemoji(item.content)" /> -->
        <span class="box-image-font">{{ item.amount }}</span>
      </el-checkbox-button>
    </el-checkbox-group>
  </div>
</template>

<script setup lang="ts">
// import { getCommentReactions, addCommentReaction, deleteCommentReaction } from '@/api/githubApi'
const cityOptions = ['上海', '北京', '广州', '深圳']

const props = withDefaults(
  defineProps<{
    id: number
  }>(),
  {
    id: () => 1
  }
)

const checkboxGroup1 = ref('')
const cities = ref(cityOptions)
const reactions = ref([])
const loading = ref(false)
</script>

<style></style>
