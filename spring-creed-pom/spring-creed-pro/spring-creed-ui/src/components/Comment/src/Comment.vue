<template>
  <el-card class="comment-card">
    <template #header>
      <div>
        <span>评论</span>
        <el-dropdown
          v-if="commenterProfile.avatar_url"
          :span="1"
          style="margin: auto 0px; float: right"
        >
          <span class="el-dropdown-link">
            <el-avatar
              shape="square"
              :size="35"
              :src="commenterProfile.avatar_url + '?imageView2/1/w/80/h/80'"
            />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="signout">
                <span style="display: block">Log Out</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </template>
    <div>
      <comment-list :current-issue="currentIssue" />
      <!-- TODO <comment-input :current-issue="currentIssue" @commitComment="commitComment" @sign="signin" /> -->
    </div>
  </el-card>
</template>

<script setup lang="ts">
import CommentList from './CommentList.vue'
import CommentInput from './CommentInput.vue'

type CommenterProfile = {
  avatar_url: string
}
const commenterProfile = reactive<CommenterProfile>({
  avatar_url: 'link'
})

const props = withDefaults(
  defineProps<{
    currentIssue: object
  }>(),
  {
    currentIssue: () => {
      return {}
    }
  }
)
const emit = defineEmits<{
  (e: 'commitComment', v: string)
  (e: 'sign', v: string)
}>()

const commitComment = (v: string) => {
  emit('commitComment', v)
}
const signin = (v: string) => {
  emit('sign', v)
}
const signout = () => {
  emit('sign', 'signout')
}
</script>
<style lang="scss" scoped>
.comment-card {
  .el-card__body {
    padding: 0px;
  }
}
</style>
