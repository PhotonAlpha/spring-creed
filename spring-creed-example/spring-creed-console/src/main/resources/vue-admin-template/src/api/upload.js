import request from '@/utils/request'

export function uploadFiles(formData) {
  // new FormData()
  // request.post('', formData, { headers})
  return request({
    url: '/api/v1/upload',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: formData
  })
}

export function listFiles(params) {
  return request({
    url: '/api/v1/list-files',
    method: 'get',
    params
  })
}

export function submitComm(dto) {
  return request({
    method: 'POST',
    url: '/api/v1/exec',
    data: dto
  })
}

export function downloadRepo(dto) {
  return fetch('/api/v1/download-repo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    cache: 'no-cache',
    body: JSON.stringify(dto)
  })
}

export function removeFile(dto) {
  return request({
    method: 'POST',
    url: '/api/v1/file-remove',
    data: dto
  })
}
