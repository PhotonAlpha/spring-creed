import CryptoJS from 'crypto-js'

const key = CryptoJS.enc.Utf8.parse('1234123412ABCDEF')
const iv = CryptoJS.enc.Utf8.parse('ABCDEF1234123412')

// 加密
export function encryptAES(rawDate) {
  const srcs = CryptoJS.enc.Utf8.parse(rawDate)
  const encrypted = CryptoJS.AES.encrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  })
  return encrypted.ciphertext.toString().toUpperCase()
}

// 解密
export function decryptAES(cookedData) {
  console.log(`cookedData->`, cookedData)
  if (!cookedData) return ''
  const encryptedHexStr = CryptoJS.enc.Hex.parse(cookedData)
  const srcs = CryptoJS.enc.Base64.stringify(encryptedHexStr)
  const decrypt = CryptoJS.AES.decrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  })
  return decrypt.toString(CryptoJS.enc.Utf8)
}
