// src/i18n/zh.ts
export default {
  topBar: {
    search: {
      title: '搜索'
    },
    user: ['个人中心', '使用文档', 'Github', '退出登录']
  },
  common: {
    tips: '提示',
    cancel: '取消',
    confirm: '确定',
    logOutTips: '您是否要退出登录?'
  },
  search: {
    placeholder: '搜索页面',
    historyTitle: '搜索历史',
    switchKeydown: '切换',
    selectKeydown: '选择'
  },
  setting: {
    menuType: {
      title: '菜单布局',
      list: ['垂直', '水平', '混合', '双列']
    },
    theme: {
      title: '主题风格',
      list: ['浅色', '深色', '系统']
    },
    menu: {
      title: '菜单风格'
    },
    color: {
      title: '系统主题色'
    },
    box: {
      title: '盒子样式',
      list: ['边框', '阴影']
    },
    basics: {
      title: '基础配置',
      list: [
        '侧边栏开启手风琴模式',
        '显示折叠侧边栏按钮',
        '显示重载页面按钮',
        '显示全局面包屑导航',
        '开启多标签页',
        '显示多语言选择',
        '显示顶部进度条',
        '色弱模式',
        '自动关闭设置中心',
        '全局水印',
        '菜单宽度',
        '页面切换动画',
        '自定义圆角'
      ]
    }
  },
  notice: {
    title: '通知',
    btnRead: '标为已读',
    bar: ['通知', '消息', '代办'],
    text: ['暂无']
  },
  worktab: {
    btn: ['关闭左侧', '关闭右侧', '关闭其它', '关闭全部']
  },
  console: {
    card: ['总访问次数', '在线访客数', '点击量', '文章数', '留言数', '待办任务'],
    histogram: {
      title: '访问量'
    },
    plan: {
      title: '计划'
    },
    lingChart: {
      title: '流量趋势'
    },
    todo: {
      title: '代办事项'
    },
    pieChart: {
      title: '分类统计'
    }
  },
  login: {
    leftView: {
      title: '专注于用户体验的后台管理系统模版',
      subTitle: '美观实用的界面，经过视觉优化，确保卓越的用户体验'
    },
    title: '欢迎回来',
    subTitle: '输入您的账号和密码登录',
    placeholder: ['请输入账号', '请输入密码', '请拖动滑块完成验证', '请输入验证码'],
    rule: [
      '请再次输入密码',
      '两次输入密码不一致!',
      '长度在 3 到 20 个字符',
      '密码长度不能小于6位',
      '请同意隐私协议',
      '请输入验证码'
    ],
    sliderText: '按住滑块拖动',
    sliderSuccessText: '验证成功',
    rememberPwd: '记住密码',
    forgetPwd: '忘记密码',
    btnText: '登录',
    noAccount: '还没有账号？',
    register: '注册'
  },
  forgetPassword: {
    title: '忘记密码？',
    subTitle: '输入您的电子邮件来重置您的密码',
    placeholder: '请输入您的电子邮件',
    submitBtnText: '提交',
    backBtnText: '返回',
    checkEmail: '验证邮箱',
    resetPassword: '重置密码'
  },
  register: {
    title: '创建账号',
    subTitle: '欢迎加入我们，请填写以下信息完成注册',
    placeholder: ['请输入账号', '请输入密码', '请再次输入密码'],
    agreeText: '我同意',
    privacyPolicy: '《隐私政策》',
    submitBtnText: '注册',
    hasAccount: '已有账号？',
    toLogin: '去登录'
  },
  lockScreen: {
    pwdError: '密码错误',
    lock: {
      inputPlaceholder: '请输入锁屏密码',
      btnText: '锁定'
    },
    unlock: {
      inputPlaceholder: '请输入解锁密码',
      btnText: '解锁',
      backBtnText: '返回登录'
    }
  },
  // 分析页多语言示例
  analysis: {
    todaySales: {
      title: '今日销售',
      subtitle: '销售总结',
      export: '导出',
      cards: {
        totalSales: {
          label: '总销售额',
          change: '+15%'
        },
        totalOrder: {
          label: '总订单',
          change: '+5%'
        },
        productSold: {
          label: '已售产品',
          change: '+2%'
        },
        newCustomers: {
          label: '新客户',
          change: '+8%'
        }
      },
      fromYesterday: '较昨天'
    },
    visitorInsights: {
      title: '访客洞察',
      legend: {
        loyalCustomers: '忠实客户',
        newCustomers: '新客户',
        uniqueCustomers: '独立客户'
      }
    },
    totalRevenue: {
      title: '总收入',
      legend: {
        onlineSales: '线上销售',
        offlineSales: '线下销售'
      }
    },
    customerSatisfaction: {
      title: '客户满意度',
      legend: {
        lastMonth: '上月',
        thisMonth: '本月'
      }
    },
    targetVsReality: {
      title: '目标与实际',
      realitySales: {
        label: '实际销售',
        sublabel: '全球'
      },
      targetSales: {
        label: '目标销售',
        sublabel: '商业'
      }
    },
    topProducts: {
      title: '热门产品',
      columns: {
        name: '名称',
        popularity: '热度',
        sales: '销量'
      },
      products: {
        homeDecor: {
          name: '家居装饰系列',
          sales: '10%'
        },
        disneyBag: {
          name: '迪士尼公主粉色包 18"',
          sales: '29%'
        },
        bathroom: {
          name: '浴室用品',
          sales: '65%'
        },
        smartwatch: {
          name: '苹果智能手表',
          sales: '32%'
        },
        fitness: {
          name: '健身追踪器',
          sales: '78%'
        },
        earbuds: {
          name: '无线耳机',
          sales: '41%'
        }
      }
    },
    salesMappingCountry: {
      title: '各国销售分布'
    },
    volumeServiceLevel: {
      title: '业务量与服务水平',
      legend: {
        volume: '业务量',
        services: '服务'
      }
    }
  }
}
