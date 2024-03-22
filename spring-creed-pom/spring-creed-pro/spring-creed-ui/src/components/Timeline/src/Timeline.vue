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
import moment from 'moment'

type TreeItem = {
  name: string
  sha: string
  date: Date
}

withDefaults(
  defineProps<{
    tree: TreeItem[]
    totalNum: number
  }>(),
  {
    tree: () => [
      {
        name: 'test',
        sha: 'abc',
        date: new Date()
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
const handlerDateFormat = (timestamp) => {
  return moment(timestamp).format('YYYY-MM-DD')
}
const handlerDateFormatSlash = (timestamp) => {
  return moment(timestamp).format('YYYY/MM/DD')
}
const handlerColor = (index) => {
  if (index === 0) {
    return { icon: 'el-icon-more', size: 'large', type: 'primary' }
  } else if (index === 1) {
    return { icon: 'el-icon-star-on', size: 'large', type: 'primary' }
  } else if (index === 2) {
    return { size: 'large', type: 'warning' }
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
