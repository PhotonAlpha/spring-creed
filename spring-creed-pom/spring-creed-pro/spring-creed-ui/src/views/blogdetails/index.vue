<template>
  <el-container ref="mainScrollbar">
    <el-main id="mainContent">
      <el-row>
        <el-col :md="18" :sm="24" :xs="24">
          <blog
            :content="content"
            :current-issue="currentIssue"
            @commit-comment="handlerCommitComment"
            @sign="sign"
            @update-markdown-action="handlerPostDirectory"
          />
        </el-col>
        <el-col
          :md="4"
          :xs="0"
          class="hidden-sm-only right-side-cnt"
          :class="{ 'side-cnt-scrolled': isLeftSideScrolled }"
        >
          <el-scrollbar class="page-component__scrollbar">
            <archive :is-leaf="true" :anchor="postDirectory" />
          </el-scrollbar>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { Archive } from '../../layout-blog/components'
import { Blog } from '@/components/Blog'
import * as GithubApi from '@/api/github'
import { Base64 } from 'js-base64'
import { ElLoading } from 'element-plus'

const route = useRoute()

const { shaCode } = route.params
console.log(`params`, shaCode)
const content = ref('')
const currentIssue = ref({})
const sha = ref('')
const device = ref('web')
const loadingInstance = ElLoading.service({ fullscreen: true })
const isLeftSideScrolled = ref(false)
const postDirectory = ref<any[]>([])
// const markdownEl = ref<null | Element>(null)

const getBlogDetails = async (sha) => {
  const response = await GithubApi.getBlog(sha)
  // const result = Base64.decode(response.content)
  // console.log(`getBlogDetails->`, result)
  content.value = Base64.decode(response.content)
  // this.loading = this.$loading(options)
  // getBlog(sha).then(response => {
  //   // console.log('blog', response)
  //   this.content = response.content
  // })
  //   .catch(error => {
  //     console.log('getBlogDetails', error)
  //     this.loading.close()
  //   })
  //   .finally(() => {
  //     this.loading.close()
  //     this.$nextTick(() => this.initDomTree())
  //   })
}

const initIssues = () => {
  console.log(`initIssues->`)
  // const currentTitle = localStorage.getItem(CURRENT_TITLE)
  // getIssues().then(response => {
  //   // console.log('get issues', response, currentTitle)
  //   if (response && Array.isArray(response)) {
  //     this.currentIssue = response.find(item => item.title === currentTitle)
  //     // console.log('no', this.currentIssue)
  //   }
  // })
  //   .catch(err => console.log(err))
  //   .finally(() => {
  //     if (this.currentIssue) {
  //       this.initComments(this.currentIssue.number)
  //     }
  //   })
}
const initComments = (issueNo) => {
  console.log(`output->issueNo`, issueNo)
  // if (issueNo) {
  //   getComments(issueNo).then(response => {
  //     console.log('get Comments response', response)
  //     this.currentIssue = { ...this.currentIssue, comments: response }
  //     console.log('get Comments', this.currentIssue)
  //   })
  //     .catch(err => console.log(err))
  // }
}
const initDomTree = () => {
  console.log(`initDomTree->`)
  // const select = document.querySelector('#markdown-content')
  // if (select) {
  //   const anchors = Array.from(select.querySelectorAll('h1,h2,h3,h4,h5,h6'))
  //   // console.log('anchors', anchors)
  //   const anchorsVal = []
  //   for (const item of anchors) {
  //   // console.log('ancher', item)
  //     anchorsVal.push(this.treeDecoration(item))
  //   }
  //   this.$store.dispatch('app/setPostDirectory', anchorsVal)
  // }
}
const addCommentEvent = (issueId, html) => {
  console.log(`output->issueId, html`, issueId, html)
  // const data = { body: html }
  // this.loading = this.$loading(options)
  // addComment(issueId, data).then(response => {

  // })
  //   .catch(error => {
  //     console.log('addComment encounter error:', error)
  //     this.loading.close()
  //     this.$message({
  //       message: `${this.$t('messgae.unknownError')}`,
  //       type: 'error'
  //     })
  //   }).finally(() => {
  //     this.initIssues()
  //     this.loading.close()
  //   })
}
const handlerCommitComment = (val) => {
  // const token = sessionStorage.getItem(COMMENTER_TOKEN_KEY)
  // // 如果存在 添加issue
  // if (val.html && val.issueId) {
  //   console.log('issue 存在', val)
  //   this.addCommentEvent(val.issueId, val.html)
  // } else if (val.html) {
  //   console.log('issue 不存在', val)
  //   // 如果不存在主题，创建并添加注释
  //   const currentTitle = localStorage.getItem(CURRENT_TITLE)
  //   createIssue({ title: currentTitle, body: currentTitle }).then(response => {
  //     const { number } = response
  //     this.addCommentEvent(number, val.html)
  //   })
  //     .catch(error => {
  //       console.log('createIssue encounter error:', error)
  //       this.$message({
  //         message: `${this.$t('messgae.unknownError')}`,
  //         type: 'error'
  //       })
  //     })
  // } else {
  //   this.$message({
  //     message: `${this.$t('messgae.unknownError')}`,
  //     type: 'error'
  //   })
  // }
  // console.log('details commit', val, token)
  console.log('details commit', val)
}
const handlerPostDirectory = (event) => {
  const select = document.querySelector('#markdown-content')
  if (select) {
    const anchors = Array.from(select.querySelectorAll('h1,h2,h3,h4,h5,h6'))
    // console.log('anchors', anchors)
    const anchorsVal: any[] = []
    for (const item of anchors) {
      anchorsVal.push(treeDecoration(item))
    }
    // console.log('ananchorsValcher', anchorsVal)
    postDirectory.value = anchorsVal
  }
  loadingInstance.close()
}
const sign = (action) => {
  console.log('signUp start', action)
  // if (action === 'signin') {
  //   const sha = this.$route.params.sha
  //   console.log('sha', sha)
  //   const authPath = AUTHURL + '?hash=' + sha
  //   console.log(authPath)
  //   window.location.href = authPath
  //   // this.$store.dispatch('user/setCommenterToken', '86954e4ca08987864a0527f5412ec90455bfcb2d')
  // } else if (action === 'signout') {
  //   this.$store.dispatch('user/logout')
  // }
}
const treeDecoration = (item) => {
  const level = item.tagName.substring(1, 2)
  let content = item.innerHTML
  if (level > 1) {
    content = '-\u00a0' + content
    for (var i = 0; i < level - 1; i++) {
      content = '\u00a0\u00a0' + content
    }
  }
  // console.log('content:', content)
  return { label: content, element: item }
}

/** 初始化 **/
onMounted(() => {
  getBlogDetails(shaCode)
  initIssues()
})
</script>

<style></style>
