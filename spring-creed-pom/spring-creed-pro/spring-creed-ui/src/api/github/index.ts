import request from '@/config/github-axios'
import axios from 'axios'
import requestcommenter from './request-commenter'
// 查询readme
export function getReadme() {
  return request.get({ url: '/mock/blog/readme.json' })
  // return request.get({
  //   url: '/repos/PhotonAlpha/blogs/readme'
  // })
}

// 查询父master的结构
export const getMasterTrees = (params) => {
  return request.get({ url: '/mock/blog/trees-master.json' })
  // return request.get({
  //   url: '/repos/PhotonAlpha/blogs/git/trees/master',
  //   params
  // })
}

// 查询子node的结构
export const getDestinationTrees = (tree_sha) => {
  return request.get({ url: '/mock/blog/trees-springboot.json' })
  // return request.get({
  //   url: `/repos/PhotonAlpha/blogs/git/trees/${tree_sha}`
  // })
}
// 获取issue列表
export const getIssues = () => {
  return request.get({ url: '/mock/blog/issues.json' })
  // return request.get({
  //   url: `/repos/PhotonAlpha/blogs/issues`
  // })
}
// 获取评论列表
export const getComments = (issue_id) => {
  return request.get({ url: '/mock/blog/issue-comments.json' })
  // return request.get({
  //   url: `/repos/PhotonAlpha/blogs/issues/${issue_id}/comments`
  // })
}

// 查询日志内容
export const getBlog = (sha) => {
  return request.get({ url: '/mock/blog/blog-multidatasource.json' })
  // return request.get({
  //   url: `/repos/PhotonAlpha/blogs/git/blobs/${sha}`
  // })
}

// 查询日志内容
export const getCommentReactions = (commentId) => {
  return request.get({ url: '/mock/blog/comment-reaction.json' })
  // return request.get({
  //   url: `/repos/PhotonAlpha/blogs/issues/comments/${commentId}/reactions`,
  //   headers: { Accept: 'application/vnd.github.squirrel-girl-preview' }
  // })
}

// 查询日志内容
export function authGithub(params) {
  // create an axios instance
  const service = axios.create({
    baseURL: process.env.VITE_APP_BASE_API, // url = base url + request url
    // withCredentials: true, // send cookies when cross-domain requests
    timeout: 5000 // request timeout
  })
  return service({
    url: `https://cors-anywhere.herokuapp.com/https://github.com/login/oauth/access_token`,
    method: 'post',
    params
  })
}
// 查询IP
export function getIp() {
  // create an axios instance
  const service = axios.create({
    // withCredentials: true, // send cookies when cross-domain requests
    timeout: 5000 // request timeout
  })
  return service({
    url: `https://api.ipify.org/?format=json`,
    method: 'get'
  })
}

// 创建issue,用作评论栏
export function createIssue(data) {
  return request.post({
    url: `/repos/PhotonAlpha/blogs/issues`,
    data
  })
}

// 添加评论
export function addAdminComment(issueId, data) {
  return request.post({
    url: `/repos/PhotonAlpha/blogs/issues/${issueId}/comments`,
    data
  })
}

// 添加评论
export function addComment(issueId, data) {
  return requestcommenter({
    url: `/repos/PhotonAlpha/blogs/issues/${issueId}/comments`,
    method: 'post',
    data
  })
}
// 添加reaction, add header Accept application/vnd.github.squirrel-girl-preview+json
export function addCommentReaction(commentId, data) {
  return requestcommenter({
    url: `/repos/PhotonAlpha/blogs/issues/comments/${commentId}/reactions`,
    method: 'post',
    headers: { Accept: 'application/vnd.github.squirrel-girl-preview+json' },
    data
  })
}

// 删除reaction
export function deleteCommentReaction(commentId, reactionId) {
  return requestcommenter({
    url: `/repos/PhotonAlpha/blogs/issues/comments/${commentId}/reactions/${reactionId}`,
    method: 'delete',
    headers: { Accept: 'application/vnd.github.squirrel-girl-preview+json' }
  })
}
