import Cookies from 'js-cookie'
import { decrypt } from './aes'

const tokenKey = 'admin_token'
const commenterTokenKey = 'commenter_token'
const accessToken =
  '00B2D5204EB85A1E9381CF162ACE18599353D35C5D7F3B1E5F58B3F9B3818755F13DEEE0B7E2A0403E6EAB810C57A7C0'

const redirect_uri = `${window.location.protocol}//${window.location.hostname}${
  window.location.port ? ':' + window.location.port : ''
}/auth`
const clientId = process.env.VUE_APP_CLIENT_ID
export const AUTHURL = ` https://github.com/login/oauth/authorize?client_id=${decrypt(
  clientId
)}&scope=public_repo&redirect_uri=${redirect_uri}`

export function getAdminToken() {
  let token = Cookies.get(tokenKey)
  if (!token) {
    const decryptedStr = decrypt(accessToken)
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
