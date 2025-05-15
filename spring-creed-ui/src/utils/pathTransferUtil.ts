import { BolgTreeItem, GitTreeItem } from '@/api/github'
import moment from 'moment'

/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string | null}
 */
export const parseTime = (time, cFormat) => {
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
      time = parseInt(time)
    }
    if (typeof time === 'number' && time.toString().length === 10) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    return value.toString().padStart(2, '0')
  })
  return time_str
}

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export const formatTime = (time, option) => {
  if (('' + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  const before = new Date(time)
  const now = Date.now()

  const diff = (now - before.getMilliseconds()) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return (
      before.getMonth() +
      1 +
      '月' +
      before.getDate() +
      '日' +
      before.getHours() +
      '时' +
      before.getMinutes() +
      '分'
    )
  }
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function param2Obj(url) {
  const search = url.split('?')[1]
  if (!search) {
    return {}
  }
  return JSON.parse(
    '{"' +
      decodeURIComponent(search)
        .replace(/"/g, '\\"')
        .replace(/&/g, '","')
        .replace(/=/g, '":"')
        .replace(/\+/g, ' ') +
      '"}'
  )
}

export const handlerDateFormat = (timestamp) => {
  return moment(timestamp).format('YYYY-MM-DD')
}
export const handlerDateFormatSlash = (timestamp) => {
  return moment(timestamp).format('YYYY/MM/DD')
}

export const reconstructorTitle = (tree: GitTreeItem[] = []) => {
  const pattern = /^[0-9]{4}[-]{1}[0-9]{1,2}[-]{1}[0-9]{1,2}/
  let blogdatas: BolgTreeItem[] = []
  if (tree && tree.length > 0) {
    blogdatas = tree
      .filter((item) => {
        const match = pattern.exec(item.path)
        return match && item.type === 'blob'
      })
      .map((item) => {
        const name = item.path
        const match = pattern.exec(name) || ['']

        const blogname = name.substring(match[0].length + 1, name.length).replace('.md', '')
        return {
          name: blogname,
          date: Date.parse(match[0].replace(/-/g, '/')),
          path: item.path,
          git_url: item.url,
          sha: item.sha,
          type: item.type
        }
      })
    blogdatas.sort((a, b) => {
      return b.date - a.date
    })
  }
  return blogdatas
}

export const CURRENT_TITLE = 'current_title'
