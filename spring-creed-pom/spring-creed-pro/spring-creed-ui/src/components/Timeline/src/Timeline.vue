<template>
  <div class="block">
    <el-timeline v-if="tree && tree.length > 0">
      <el-timeline-item
        v-for="(item, index) in tree"
        :key="index"
        :timestamp="handlerDateFormatSlash(item.date)"
        :size="handlerColor(index).size"
        :type="handlerColor(index).type"
        :icon="handlerColor(index).icon"
        :color="handlerColor(index).color"
        placement="top"
      >
        <el-card>
          <el-link :underline="false" @click="showDetails(item.sha, item.name)">
            <h4>{{ item.name }}</h4>
          </el-link>
          <p><i class="el-icon-date"></i> {{ handlerDateFormat(item.date) }}</p>
        </el-card>
      </el-timeline-item>

      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalNum"
        @current-change="handleCurrentChange"
      />
    </el-timeline>
    <el-alert v-else title="未找到数据" type="info" :closable="false" />
  </div>
</template>

<script setup lang="ts">
import { handlerDateFormat, handlerDateFormatSlash } from '@/utils/pathTransferUtil'
import { BolgTreeItem, GitTreeItem } from '@/api/github'
import { MoreFilled, MagicStick } from '@element-plus/icons-vue'

withDefaults(
  defineProps<{
    tree: BolgTreeItem[]
    totalNum: number
  }>(),
  {
    tree: () => [
      {
        name: 'test',
        sha: 'abc',
        date: new Date().getMilliseconds(),
        git_url: 'NA'
      }
    ],
    totalNum: () => 20
  }
)

const emit = defineEmits<{
  (e: 'handle-pagenum', v: number)
  (e: 'show-details', v: string, v2: string)
}>()

const showDetails = (sha: string, title: string) => {
  emit('show-details', sha, title)
}

const handleCurrentChange = (val: number) => {
  emit('handle-pagenum', val)
}

const handlerColor = (index) => {
  if (index === 0) {
    return { icon: MoreFilled, size: 'large', type: 'primary' }
  } else if (index === 1) {
    return { icon: MagicStick, size: 'large', type: 'primary' }
  } else if (index === 2) {
    return { color: '#0bbd87', size: 'large' }
  } else {
    return { color: '#0bbd87', size: 'normal' }
  }
}
</script>

<style lang="scss" scoped>
.block {
  padding-top: 30px;
  background: #fff;
}
</style>
