import request from '@/utils/request'

/**
 * 注册API - 独立注册逻辑
 * 不依赖登录模块
 */

// 注册方法
export function register(data) {
  return request({
    url: '/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 获取验证码（注册页面使用）
export function getRegisterCodeImg() {
  return request({
    url: '/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  })
}

// 检查用户名是否可用
export function checkUsername(username) {
  return request({
    url: '/system/user/checkUserNameUnique/' + username,
    headers: {
      isToken: false
    },
    method: 'get'
  })
}

