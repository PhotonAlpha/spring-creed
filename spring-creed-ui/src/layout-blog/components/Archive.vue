<template>
  <el-card v-if="isLeaf && anchor && anchor.length > 0" class="box-directory">
    <template #header>
      <span>Anchor</span>
    </template>
    <div>
      <el-tree :data="anchor" :props="defaultTree" @node-click="handlePostClick" />
    </div>
  </el-card>
  <el-card v-if="!isLeaf && recommend && recommend.length > 0" class="box-article">
    <template #header>
      <!-- <span class="item-title">{{ $t('navbar.lArticle') }}</span> -->
      <span class="item-title">{{ t('navbar.lArticle') }}</span>
    </template>
    <el-collapse v-model="activeName" accordion>
      <template v-for="(item, index) in recommend" :key="index">
        <el-collapse-item :title="item.title" :name="'name' + index" class="item-header">
          <el-link :underline="false" @click="handleRecommendedClick(item)">
            <div>{{ item.name }}</div>
          </el-link>
          <div class="item-date">{{ handlerDateFormatSlash(item.date) }}</div>
        </el-collapse-item>
      </template>
    </el-collapse>
  </el-card>
  <el-card v-if="!isLeaf && category && category.length > 0" class="box-card">
    <template #header>
      <span>{{ t('navbar.category') }}</span>
    </template>
    <div>
      <el-tree
        :data="category"
        :props="defaultTree"
        :load="loadNode"
        lazy
        @node-click="handleNodeClick"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type Node from 'element-plus/es/components/tree/src/model/node'
import moment from 'moment'
const { t } = useI18n()
const activeName = ref('name0')
const scrollEl = ref()

interface Tree {
  label: string
  name?: string
  title?: string
  date?: number
  leaf?: boolean
  children?: Tree[]
}
withDefaults(
  defineProps<{
    isLeaf?: boolean
    anchor?: Tree[]
    category?: Tree[]
    recommend?: Tree[]
    defaultTree?: {}
  }>(),
  {
    isLeaf: () => false,
    anchor: () => {
      return [
        {
          label: 'anchor',
          children: [
            {
              label: 'Level two 1-1',
              children: [
                {
                  label: 'Level three 1-1-1'
                }
              ]
            }
          ]
        },
        {
          label: 'Level one 2',
          children: [
            {
              label: 'Level two 2-1',
              children: [
                {
                  label: 'Level three 2-1-1'
                }
              ]
            },
            {
              label: 'Level two 2-2 test',
              children: [
                {
                  label: 'Level three 2-2-1'
                }
              ]
            }
          ]
        }
      ]
    },
    category: () => {
      return [
        {
          label: 'category',
          children: [
            {
              label: 'Level three 2-2-1'
            }
          ]
        }
      ]
    },
    recommend: () => {
      return [
        {
          label: 'recommend',
          name: 'name 1',
          title: 'tile 1'
        }
      ]
    },
    defaultTree: () => {
      return [
        {
          label: 'NA',
          children: 'NA'
        }
      ]
    }
  }
)
const emit = defineEmits<{
  (e: 'getCategoryList', node: Node, resolve: Function): void
  (e: 'handlerCategory', data: Tree): void
}>()

const loadNode = (node: Node, resolve: Function) => {
  emit('getCategoryList', node, resolve)
}
const handlePostClick = (data) => {
  // innerRef.value.$el!.scrollIntoView({ block: 'end', behavior: 'smooth' }) //滚动到底部
  data.element.scrollIntoView({ behavior: 'smooth' })
}
const handleNodeClick = (data: Tree) => {
  emit('handlerCategory', data)
}
const handleRecommendedClick = (data: Tree) => {
  emit('handlerCategory', { ...data, leaf: true, label: data?.name ?? '' })
}
const handlerDateFormatSlash = (timestamp) => {
  return moment(timestamp).format('YYYY/MM/DD')
}
const loadCategoryTrees = () => {}

const loadRecommendTrees = () => {}

/** 初始化 **/
onMounted(() => {
  loadCategoryTrees()
  loadRecommendTrees()
})
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css?family=Montserrat:400,500,700');

.item-title {
  font-weight: bold;
  font-family: 'Montserrat', sans-serif;
}

.item-header .el-collapse-item__header {
  font-weight: bold;
  font-family: 'Montserrat', sans-serif;
}

.item-date {
  color: #aaa;
  font-size: 13px;
  text-transform: uppercase;
}
</style>
<style lang="scss">
.box-directory {
  .el-card__body {
    padding: 0px;
  }

  .el-tree-node__content {
    color: #337ab7;
    font-weight: bold;
  }

  .el-card__header {
    font-weight: bold;
    font-family: 'Montserrat', sans-serif;
    padding-top: 1px;
    padding-bottom: 1px;
  }
}

.box-article {
  .el-collapse-item__header {
    color: #38b7ea;
  }
}
</style>
