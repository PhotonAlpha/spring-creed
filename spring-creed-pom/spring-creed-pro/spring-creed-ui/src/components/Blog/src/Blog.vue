import { lang } from 'moment'; import { object } from 'vue-types';
<template>
  <div id="blog-main-content">
    <el-backtop :bottom="260" :right="0" />
    <template v-if="blogVal">
      <markdown
        id="markdown-content"
        @update-markdown-action="updateMarkdownAction"
        class="dashboard-container markdown-body"
        :content="content"
      />
      <!-- <comment
        :current-issue="currentIssue"
        @commit-comment="commitComment"
        @sign="sign"
        @commit-reaction="commitReaction"
      /> -->
    </template>
    <template v-else>
      <el-alert title="未找到数据" type="info" show-icon />
    </template>
  </div>
</template>

<script setup lang="ts">
import { Markdown } from '@/components/Markdown'
import { Base64 } from 'js-base64'

const props = withDefaults(
  defineProps<{
    content: string
    currentIssue: object
  }>(),
  {
    content: () => '# header',
    currentIssue: () => {
      return {}
    }
  }
)

// const a = ref(content)

const emit = defineEmits<{
  (e: 'updateMarkdownAction', v: string)
  (e: 'commitComment', v: string)
  (e: 'sign', v: string)
  (e: 'commitReaction', v: string)
}>()

const blogVal = () => {
  const { content } = props
  if (content) {
    // return Base64.decode(this.content)
    console.log(`content---->`, content)
    return content
  }
  return ''
}

const commitCommentAction = (html) => {
  emit('commitComment', html)
}
const signAction = (action) => {
  emit('sign', action)
}
const commitReactionAction = (val) => {
  emit('commitReaction', val)
}
const updateMarkdownAction = (val) => {
  emit('updateMarkdownAction', val)
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
    margin-bottom: 50px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
#blog-main-content {
  background-image: url('@/assets/imgs/site-background.png');
}
.dashboard-container {
  background: transparent;
}
</style>
