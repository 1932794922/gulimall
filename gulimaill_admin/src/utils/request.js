import axios from 'axios'
import Cookies from 'js-cookie'
import router from '@/router'
import qs from 'qs'
import { clearLoginInfo } from '@/utils'
import isPlainObject from 'lodash/isPlainObject'
import { Message } from 'element-ui'
const http = axios.create({
  baseURL: window.SITE_CONFIG['apiURL'],
  timeout: 1000 * 180,
  withCredentials: true
})

/**
 * 请求拦截
 */
http.interceptors.request.use(
  config => {
    config.headers['Accept-Language'] = Cookies.get('language') || 'zh-CN'
    config.headers['token'] = Cookies.get('token') || ''
    // 默认参数
    var defaults = {}
    // 防止缓存，GET请求默认带_t参数
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        ...{ _t: new Date().getTime() }
      }
    }
    if (isPlainObject(config.params)) {
      config.params = {
        ...defaults,
        ...config.params
      }
    }
    if (isPlainObject(config.data)) {
      config.data = {
        ...defaults,
        ...config.data
      }
      if (
        /^application\/x-www-form-urlencoded/.test(
          config.headers['content-type']
        )
      ) {
        config.data = qs.stringify(config.data)
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截
 */
http.interceptors.response.use(
  response => {
    if (response.data.code === 401 || response.data.code === 10001) {
      clearLoginInfo()
      router.replace({ name: 'login' })
      return Promise.reject(response.data.msg)
    }
    if(response.data.code !== 200){
      Message.error(response.data.msg)
    }
    return response
  },
  error => {
    console.error(error)
    Message.error(error.response.statusText)
    return Promise.reject(error)
  }
)

// 封装请求方法
const request = {
  get: (url, params) => {
    return http.get(url, { params })
  },
  post: (url, data) => {
    return http.post(url, data)
  },
  put: (url, data) => {
    return http.put(url, data)
  },
  delete: (url, data) => {
    return http.delete(url, data)
  }
}
export default http
