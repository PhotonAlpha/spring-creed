import { useCache } from '@/hooks/web/useCache'
import { TokenType } from '@/api/login/types'
import { decrypt, encrypt } from '@/utils/jsencrypt'

import Cookies from 'js-cookie'
import { decryptAES } from './aes'

const { wsCache } = useCache()

const AccessTokenKey = 'ACCESS_TOKEN'
const RefreshTokenKey = 'REFRESH_TOKEN'

// 获取token
export const getAccessToken = () => {
  // 此处与TokenKey相同，此写法解决初始化时Cookies中不存在TokenKey报错
  return wsCache.get(AccessTokenKey) ? wsCache.get(AccessTokenKey) : wsCache.get('ACCESS_TOKEN')
}

// 刷新token
export const getRefreshToken = () => {
  return wsCache.get(RefreshTokenKey)
}

// 设置token
export const setToken = (token: TokenType) => {
  wsCache.set(RefreshTokenKey, token.refreshToken)
  wsCache.set(AccessTokenKey, token.accessToken)
}

// 删除token
export const removeToken = () => {
  wsCache.delete(AccessTokenKey)
  wsCache.delete(RefreshTokenKey)
}

/** 格式化token（jwt格式） */
export const formatToken = (token: string): string => {
  return 'Bearer ' + token
}
// ========== 账号相关 ==========

const LoginFormKey = 'LOGINFORM'

export type LoginFormType = {
  tenantName: string
  username: string
  password: string
  rememberMe: boolean
}

export const getLoginForm = () => {
  const loginForm: LoginFormType = wsCache.get(LoginFormKey)
  if (loginForm) {
    loginForm.password = decrypt(loginForm.password) as string
  }
  return loginForm
}

export const setLoginForm = (loginForm: LoginFormType) => {
  loginForm.password = encrypt(loginForm.password) as string
  wsCache.set(LoginFormKey, loginForm, { exp: 30 * 24 * 60 * 60 })
}

export const removeLoginForm = () => {
  wsCache.delete(LoginFormKey)
}

// ========== 租户相关 ==========

const TenantIdKey = 'TENANT_ID'
const TenantNameKey = 'TENANT_NAME'

export const getTenantName = () => {
  return wsCache.get(TenantNameKey)
}

export const setTenantName = (username: string) => {
  wsCache.set(TenantNameKey, username, { exp: 30 * 24 * 60 * 60 })
}

export const removeTenantName = () => {
  wsCache.delete(TenantNameKey)
}

export const getTenantId = () => {
  return wsCache.get(TenantIdKey)
}

export const setTenantId = (username: string) => {
  wsCache.set(TenantIdKey, username)
}

export const removeTenantId = () => {
  wsCache.delete(TenantIdKey)
}

const tokenKey = 'admin_token'
const commenterTokenKey = 'commenter_token'
const accessToken =
  '00B2D5204EB85A1E9381CF162ACE18599353D35C5D7F3B1E5F58B3F9B3818755F13DEEE0B7E2A0403E6EAB810C57A7C0'

const redirect_uri = `${window.location.protocol}//${window.location.hostname}${window.location.port ? ':' + window.location.port : ''}/callback`
const clientId = import.meta.env.VUE_APP_CLIENT_ID
export const AUTHURL = ` https://github.com/login/oauth/authorize?client_id=${decryptAES(clientId)}&scope=public_repo&redirect_uri=${redirect_uri}`

export function getAdminToken() {
  let token = Cookies.get(tokenKey)
  console.log(`getAdminToken`, getAdminToken)
  if (!token) {
    const decryptedStr = decryptAES(accessToken)
    Cookies.set(tokenKey, decryptedStr)
    token = decryptedStr
  }
  return token
}

export function getGuestCommenterToken() {
  return sessionStorage.getItem(commenterTokenKey)
}

export function setGuestToken(token) {
  return sessionStorage.setItem(commenterTokenKey, token)
}

export function removeGuestToken() {
  sessionStorage.removeItem(commenterTokenKey)
}
export const COMMENTER_TOKEN_KEY = commenterTokenKey
