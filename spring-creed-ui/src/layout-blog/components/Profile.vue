<template>
  <el-card shadow="hover" :body-style="{ padding: '0px', textAlign: 'center' }">
    <img src="@/assets/profile/killua.jpg" class="image" />
    <div style="padding: 14px">
      <div class="box-name">Ethan</div>
      <span>{{ profileDetails.bio }}</span>
      <el-divider />
      <div>
        <i class="fas fa-map-marker-alt icon-right"></i>
        <span>{{ profileDetails.location }}</span>
      </div>
      <div>
        <i class="far fa-envelope icon-right"></i>
        <span>{{ profileDetails.email }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { useBolgTreeStore } from '@/store/modules/blogmatrix'
import { ProfileInfo } from '@/api/github'

const bolgTreeStore = useBolgTreeStore()
const profileDetails = ref<ProfileInfo>(bolgTreeStore.getProfile)

const loadProfile = async () => {
  bolgTreeStore.initProfileInfo().then((profile) => {
    // console.log('profile', profile)
    profileDetails.value = profile
  })
}

/** 初始化 **/
onMounted(() => {
  loadProfile()
})
</script>

<style scoped lang="scss">
.box-name {
  font-size: 1.5em;
  margin-block-start: 0.83em;
  margin-block-end: 0.83em;
  margin-inline-start: 0px;
  margin-inline-end: 0px;
  font-weight: bold;
}

.icon-right {
  margin-right: 10px;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.image {
  height: 500px;
  width: auto;
  display: block;
  margin-left: auto;
  margin-right: auto;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: '';
}

.clearfix:after {
  clear: both;
}
</style>
