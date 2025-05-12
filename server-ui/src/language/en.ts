// src/i18n/en.ts
export default {
  topBar: {
    search: {
      title: 'Search'
    },
    user: ['User center', 'Document', 'Github', 'Log out']
  },
  common: {
    tips: 'Prompt',
    cancel: 'Cancel',
    confirm: 'Confirm',
    logOutTips: 'Do you want to log out?'
  },
  search: {
    placeholder: 'Search page',
    historyTitle: 'Search history',
    switchKeydown: 'Navigate',
    selectKeydown: 'Select'
  },
  setting: {
    menuType: {
      title: 'Menu Layout',
      list: ['Vertical', 'Horizontal', 'Mixed', 'Dual']
    },
    theme: {
      title: 'Theme Style',
      list: ['Light', 'Dark', 'System']
    },
    menu: {
      title: 'Menu Style'
    },
    color: {
      title: 'Theme Color'
    },
    box: {
      title: 'Box Style',
      list: ['Border', 'Shadow']
    },
    basics: {
      title: 'Basic Config',
      list: [
        'Sidebar opens accordion',
        'Show sidebar button',
        'Show reload page button',
        'Show crumb navigation',
        'Show work tab',
        'Show multilingual selection',
        'Show top progress bar',
        'Color Weakness Mode',
        'Auto close settings center',
        'Global watermark',
        'Menu width',
        'Page animation',
        'Custom radius'
      ]
    }
  },
  notice: {
    title: 'Notice',
    btnRead: 'Mark as read',
    bar: ['Notice', 'Message', 'Todo'],
    text: ['No']
  },
  worktab: {
    btn: ['Close left', 'Close right', 'Close outher', 'Close All']
  },
  console: {
    card: [
      'Total visits',
      'Number of online visitors',
      'Visits',
      'Number of articles',
      'Number of messages',
      'To do task'
    ],
    histogram: {
      title: 'Visits'
    },
    plan: {
      title: 'Plan'
    },
    lingChart: {
      title: 'Traffic trend'
    },
    todo: {
      title: 'To do task'
    },
    pieChart: {
      title: 'Classified statistics'
    }
  },
  login: {
    leftView: {
      title: 'An Admin template focused on user experience',
      subTitle: 'A sleek and practical interface for a great user experience'
    },
    title: 'Welcome back',
    subTitle: 'Please enter your account and password to login',
    placeholder: [
      'Please enter your account',
      'Please enter your password',
      'Please slide to verify',
      'Please enter the captcha'
    ],
    sliderText: 'Please slide to verify',
    sliderSuccessText: 'Verification successful',
    rememberPwd: 'Remember password',
    forgetPwd: 'Forget password',
    btnText: 'Login',
    noAccount: 'No account yet?',
    register: 'Register'
  },
  forgetPassword: {
    title: 'Forget password?',
    subTitle: 'Enter your email to reset your password',
    placeholder: 'Please enter your email',
    submitBtnText: 'Submit',
    backBtnText: 'Back',
    checkEmail: 'Verify Email',
    resetPassword: 'Reset Password'
  },
  register: {
    title: 'Create account',
    subTitle:
      'Welcome to join us, please fill in the following information to complete the registration',
    placeholder: [
      'Please enter your account',
      'Please enter your password',
      'Please enter your password again'
    ],
    rule: [
      'Please enter your password again',
      'The two passwords are inconsistent!',
      'The length is 3 to 20 characters',
      'The password length cannot be less than 6 digits',
      'Please agree to the privacy policy',
      'Please enter the captcha'
    ],
    agreeText: 'I agree',
    privacyPolicy: 'Privacy policy',
    submitBtnText: 'Register',
    hasAccount: 'Already have an account?',
    toLogin: 'To login'
  },
  sendEmail: {
    title: 'Send email verification code',
    subTitle: 'Please enter your email address, we will send the verification code to your email',
    placeholder: 'Please enter your email',
    submitBtnText: 'Send verification code',
    hasAccount: 'Already have an account?',
    toLogin: 'To login',
    emailNullCheck: 'Please enter your email',
    emailRuleCheck: 'Please enter a valid email address',
    emailHasCheck: 'The email has been verified, please register directly',
    emailHasSend: 'The verification code has been sent, please check your email',
    emailSendFail: 'Failed to send email, please try again later'
  },
  lockScreen: {
    pwdError: 'Password error',
    lock: {
      inputPlaceholder: 'Please input lock screen password',
      btnText: 'Lock'
    },
    unlock: {
      inputPlaceholder: 'Please input unlock password',
      btnText: 'Unlock',
      backBtnText: 'Back to login'
    }
  },
  // Analysis page language example
  analysis: {
    todaySales: {
      title: "Today's Sales",
      subtitle: 'Sales Summary',
      export: 'Export',
      cards: {
        totalSales: {
          label: 'Total Sales',
          change: '+15%'
        },
        totalOrder: {
          label: 'Total Order',
          change: '+5%'
        },
        productSold: {
          label: 'Product Sold',
          change: '+2%'
        },
        newCustomers: {
          label: 'New Customers',
          change: '+8%'
        }
      },
      fromYesterday: 'from yesterday'
    },
    visitorInsights: {
      title: 'Visitor Insights',
      legend: {
        loyalCustomers: 'Loyal Customers',
        newCustomers: 'New Customers',
        uniqueCustomers: 'Unique Customers'
      }
    },
    totalRevenue: {
      title: 'Total Revenue',
      legend: {
        onlineSales: 'Online Sales',
        offlineSales: 'Offline Sales'
      }
    },
    customerSatisfaction: {
      title: 'Customer Satisfaction',
      legend: {
        lastMonth: 'Last Month',
        thisMonth: 'This Month'
      }
    },
    targetVsReality: {
      title: 'Target vs Reality',
      realitySales: {
        label: 'Reality Sales',
        sublabel: 'Global'
      },
      targetSales: {
        label: 'Target Sales',
        sublabel: 'Commercial'
      }
    },
    topProducts: {
      title: 'Top Products',
      columns: {
        name: 'Name',
        popularity: 'Popularity',
        sales: 'Sales'
      },
      products: {
        homeDecor: {
          name: 'Home Decor Range',
          sales: '10%'
        },
        disneyBag: {
          name: 'Disney Princess Pink Bag 18"',
          sales: '29%'
        },
        bathroom: {
          name: 'Bathroom Essentials',
          sales: '65%'
        },
        smartwatch: {
          name: 'Apple Smartwatches',
          sales: '32%'
        },
        fitness: {
          name: 'Fitness Tracker',
          sales: '78%'
        },
        earbuds: {
          name: 'Wireless Earbuds',
          sales: '41%'
        }
      }
    },
    salesMappingCountry: {
      title: 'Sales Mapping by Country'
    },
    volumeServiceLevel: {
      title: 'Volume vs Service Level',
      legend: {
        volume: 'Volume',
        services: 'Services'
      }
    }
  }
}
