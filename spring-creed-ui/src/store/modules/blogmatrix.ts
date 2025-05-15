import { defineStore } from 'pinia'
import { store } from '../index'
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'
import { BolgTreeItem, ProfileInfo, BolgCategory, PostDirectory } from '@/api/github'
import * as GithubApi from '@/api/github'

const { wsCache } = useCache()

export interface BolgTreeState {
  trees: BolgTreeItem[]
  profile: ProfileInfo
  postDirectory: PostDirectory[]
  category: BolgCategory[]
  latestRecommend: BolgCategory[]
}

export const useBolgTreeStore = defineStore('blogtree', {
  state: (): BolgTreeState => ({
    trees: [],
    profile: {
      bio: 'NA',
      email: 'NA',
      location: 'NA'
    },
    postDirectory: [],
    category: [],
    latestRecommend: []
  }),
  getters: {
    getTrees(): BolgTreeItem[] {
      const result = wsCache.get(CACHE_KEY.TREES_CACHE)
      if (!result) {
        return this.trees
      }
      return result
    },
    getProfile(): ProfileInfo {
      return this.profile
    },
    getCategory(): BolgCategory[] {
      const result = wsCache.get(CACHE_KEY.CATEGORY_CACHE)
      if (!result || result.length < 1) {
        return this.category
      }
      return result
    },
    getLatestRecommend(): BolgCategory[] {
      const result = wsCache.get(CACHE_KEY.RECOMMEND_CACHE)
      if (!result || result.length < 1) {
        return this.latestRecommend
      }
      return result
    },
    getPostDirectory(): PostDirectory[] {
      return this.postDirectory
    }
  },
  actions: {
    setTrees(trees: BolgTreeItem[]): void {
      this.trees = trees
      wsCache.set(CACHE_KEY.TREES_CACHE, this.trees)
    },
    setCategory(data: BolgCategory[]): void {
      console.log('setCategory', data)
      this.category = data
      wsCache.set(CACHE_KEY.CATEGORY_CACHE, data)
    },
    setLatestRecommend(data: BolgCategory[]): void {
      this.latestRecommend = data
      wsCache.set(CACHE_KEY.RECOMMEND_CACHE, data)
    },
    setPostDirectory(data: PostDirectory[]): void {
      this.postDirectory = data
      wsCache.set('PostDirectory', data)
    },
    async initProfileInfo(): Promise<ProfileInfo> {
      if (this.profile.email === 'NA') {
        const res = await GithubApi.getProfileInfo()
        this.profile = res
      }
      return this.profile
    }
  }
})

export const useBolgTreeStoreWithOut = () => {
  return useBolgTreeStore(store)
}
