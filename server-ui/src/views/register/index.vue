<template>
  <div class="login register">
    <div class="left-wrap">
      <LoginLeftView></LoginLeftView>
    </div>
    <div class="right-wrap">
      <div class="header">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#iconsys-zhaopian-copy"></use>
        </svg>
        <h1>{{ systemName }}</h1>
      </div>
      <div class="login-wrap">
        <div class="form">
          <h3 class="title">{{ $t('register.title') }}</h3>
          <p class="sub-title">
            {{ $t('register.subTitle') }}
            <span v-if="isEmailVerified" class="email-verified">
              {{ $t('register.emailVerified') }}: {{ email }}
            </span>
          </p>
          <el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
            <el-form-item prop="email" v-if="!isEmailVerified">
              <el-input
                v-model.trim="formData.email"
                :placeholder="$t('register.placeholder[3]')"
                size="large"
                disabled
              />
            </el-form-item>

            <el-form-item prop="loginName">
              <el-input
                v-model.trim="formData.loginName"
                :placeholder="$t('register.placeholder[0]')"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model.trim="formData.password"
                :placeholder="$t('register.placeholder[1]')"
                type="password"
                autocomplete="off"
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model.trim="formData.confirmPassword"
                :placeholder="$t('register.placeholder[2]')"
                type="password"
                autocomplete="off"
                @keyup.enter="register"
              />
            </el-form-item>

            <el-form-item prop="agreement">
              <el-checkbox v-model="formData.agreement">
                {{ $t('register.agreeText') }}
                <router-link
                  style="color: var(--main-color); text-decoration: none"
                  to="/privacy-policy"
                  >{{ $t('register.privacyPolicy') }}</router-link
                >
              </el-checkbox>
            </el-form-item>

            <div style="margin-top: 15px">
              <el-button
                class="register-btn"
                type="primary"
                @click="register"
                :loading="loading"
                v-ripple
              >
                {{ $t('register.submitBtnText') }}
              </el-button>
            </div>

            <div class="footer">
              <p>
                {{ $t('register.hasAccount') }}
                <router-link to="/login">{{ $t('register.toLogin') }}</router-link>
              </p>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import { ElMessage } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import { useI18n } from 'vue-i18n'
  import { RegisterService } from '@/api/registerApi'
  import { onMounted } from 'vue'

  const { t } = useI18n()
  const router = useRouter()
  const route = useRoute()
  const formRef = ref<FormInstance>()

  const systemName = AppConfig.systemInfo.name
  const loading = ref(false)
  const isEmailVerified = ref(false)
  const email = ref('')

  // 表单数据
  const formData = reactive({
    email: '',
    loginName: '',
    password: '',
    confirmPassword: '',
    agreement: false
  })

  onMounted(() => {
    // 检查URL参数，判断邮箱是否已验证
    const urlEmail = route.query.email as string
    const verified = route.query.verified === 'true'

    if (urlEmail && verified) {
      isEmailVerified.value = true
      email.value = urlEmail
      formData.email = urlEmail
    } else if (urlEmail) {
      formData.email = urlEmail
    } else {
      // 如果没有邮箱参数，跳转到发送邮箱验证码页面
      router.push('/register/sendEmail')
    }
  })

  // 密码验证规则
  const validatePass = (rule: any, value: string, callback: any) => {
    if (value === '') {
      callback(new Error(t('register.placeholder[1]')))
    } else {
      // 验证密码复杂度
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d\W_]{8,20}$/
      if (!passwordRegex.test(value)) {
        callback(new Error(t('register.rule[6]')))
      } else {
        if (formData.confirmPassword !== '') {
          formRef.value?.validateField('confirmPassword')
        }
        callback()
      }
    }
  }

  // 确认密码验证
  const validatePass2 = (rule: any, value: string, callback: any) => {
    if (value === '') {
      callback(new Error(t('register.rule[0]')))
    } else if (value !== formData.password) {
      callback(new Error(t('register.rule[1]')))
    } else {
      callback()
    }
  }

  // 账号验证规则
  const validateLoginName = (rule: any, value: string, callback: any) => {
    if (value === '') {
      callback(new Error(t('register.placeholder[0]')))
    } else {
      // 验证账号格式
      const loginNameRegex = /^[a-zA-Z][a-zA-Z0-9_-]{2,9}$/
      if (!loginNameRegex.test(value)) {
        callback(new Error(t('register.rule[5]')))
      } else {
        callback()
      }
    }
  }

  // 表单验证规则
  const rules = reactive<FormRules>({
    email: [
      { required: true, message: t('register.placeholder[3]'), trigger: 'blur' },
      { type: 'email', message: t('register.rule[7]'), trigger: 'blur' }
    ],
    loginName: [{ required: true, validator: validateLoginName, trigger: 'blur' }],
    password: [{ required: true, validator: validatePass, trigger: 'blur' }],
    confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],
    agreement: [
      {
        validator: (rule: any, value: boolean, callback: any) => {
          if (!value) {
            callback(new Error(t('register.rule[4]')))
          } else {
            callback()
          }
        },
        trigger: 'change'
      }
    ]
  })

  // 注册方法
  const register = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      loading.value = true

      try {
        // 调用注册API
        const response = await RegisterService.registerUser({
          email: formData.email,
          loginName: formData.loginName,
          password: formData.password
        })

        if (response.success) {
          ElMessage.success(t('register.success'))
          toLogin()
        } else {
          ElMessage.error(response.message || t('register.registerfied'))
          loading.value = false
        }
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error(t('register.registerfied'))
        loading.value = false
      }
    } catch (error) {
      console.log('表单验证失败', error)
    }
  }

  // 跳转到登录页
  const toLogin = () => {
    setTimeout(() => {
      router.push('/login')
    }, 1000)
  }
</script>

<style lang="scss" scoped>
  @use '../login/index' as login;
  @use './index' as register;

  .email-verified {
    display: block;
    margin-top: 5px;
    font-size: 14px;
    color: var(--el-color-success);
  }
</style>
