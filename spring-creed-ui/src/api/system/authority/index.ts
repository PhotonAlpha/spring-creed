import request from '@/config/axios'

export interface AuthorityVO {
  id: number
  authority: string
  remark: string
  sort: number
  status: number
  type: number
  createTime: Date
}

export interface UpdateStatusReqVO {
  id: number
  status: number
}

// 查询权限列表
export const getAuthorityPage = async (params: PageParam) => {
  return await request.get({ url: '/system/authority/page', params })
}

// 查询权限（精简)列表
export const getSimpleAuthorityList = async (): Promise<AuthorityVO[]> => {
  return await request.get({ url: '/system/authority/list-all-simple' })
}

// 查询权限详情
export const getAuthority = async (id: number) => {
  return await request.get({ url: '/system/authority/get?id=' + id })
}

// 新增权限
export const createAuthority = async (data: AuthorityVO) => {
  return await request.post({ url: '/system/authority/create', data })
}

// 修改权限
export const updateAuthority = async (data: AuthorityVO) => {
  return await request.put({ url: '/system/authority/update', data })
}

// 修改权限状态
export const updateAuthorityStatus = async (data: UpdateStatusReqVO) => {
  return await request.put({ url: '/system/authority/update-status', data })
}

// 删除权限
export const deleteAuthority = async (id: number) => {
  return await request.delete({ url: '/system/authority/delete?id=' + id })
}

// 导出权限
export const exportAuthority = (params) => {
  return request.download({
    url: '/system/authority/export-excel',
    params
  })
}
