import request from '@/config/github-axios'
import axios from 'axios'
import requestcommenter from './request-commenter'
import { Octokit } from 'octokit'
import { decryptAES } from '@/utils/aes'

export interface ProfileInfo {
  login?: string
  avatar_url?: string
  url?: string
  type?: string
  name?: string
  blog?: string
  location: string
  email: string
  bio: string
}

const token =
  '1DA56ABADACAF0739D0AF1B18133B0EE56A88EEA5939B0E5DD2702131A7B848843868E4554E6DEC845D67FC57328ACAA75168E50FD01DDD44C89381133B46A31810526272B2DFB23EF26BA85CA5EA8279D1FE36FAB4E1769FD4EAD3690A80AB4'

const octokit = new Octokit({
  auth: `${decryptAES(token)}`
})

// 查询readme
export async function getProfileInfo() {
  const { data } = await octokit.rest.users.getAuthenticated()
  return data
  // return request.get({ url: '/mock/blog/user-info.json' })
}

// 查询readme
export function getReadme() {
  return request.get({ url: '/mock/blog/readme.json' })
  // return request.get({
  //   url: '/repos/PhotonAlpha/blogs/readme'
  // })
}

// 查询父master的结构
export const getMasterTrees = async () => {
  const { data } = await octokit.request('GET /repos/{owner}/{repo}/git/trees/{tree_sha}', {
    owner: 'PhotonAlpha',
    repo: 'blogs',
    tree_sha: 'master'
  })
  return data
  // return request.get({ url: '/mock/blog/trees-master.json' })
  // return request.get({
  //   url: '/repos/PhotonAlpha/blogs/git/trees/master',
  //   params
  // })
}

// 查询子node的结构
export const getDestinationTrees = async (tree_sha) => {
  const { data } = await octokit.request('GET /repos/{owner}/{repo}/git/trees/{tree_sha}', {
    owner: 'PhotonAlpha',
    repo: 'blogs',
    tree_sha
  })
  return data
  // if (tree_sha === 'b719c0ec0ea316b2d00face5f2e4b638ecfb9485')
  //   return request.get({ url: '/mock/blog/trees-nav.json' })
  // return request.get({ url: '/mock/blog/trees-springboot.json' })
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
export const getBlog = async (sha) => {
  const { data } = await octokit.request('GET /repos/{owner}/{repo}/git/blobs/{file_sha}', {
    owner: 'PhotonAlpha',
    repo: 'blogs',
    file_sha: sha
  })
  return data
  // return request.get({ url: '/mock/blog/blog-multidatasource.json' })
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

export interface BolgTreeItem {
  name: string
  sha: string
  date: number
  git_url: string
  type?: string
  path?: string
}

export interface BolgCategory {
  label: string
  url: string
  sha: string
  date?: number
  title?: string
  leaf?: boolean
  name?: string
}

export interface GitTree {
  sha: string
  tree: GitTreeItem[]
  truncated: boolean
  url: string
}

export interface GitTreeItem {
  mode?: string
  path: string
  sha: string
  size?: number
  type: string
  url: string
}

export interface GitCategory {
  sha: string
  url: string
  truncated: boolean
  tree: GitCategoryItem[]
}

export interface GitCategoryItem {
  sha: string
  url: string
  type: string
  path: string
}

export interface PostDirectory {
  label: string
  element?: any
}
