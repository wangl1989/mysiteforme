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
          <h3 class="title">{{ $t('sendEmail.title') }}</h3>
          <p class="sub-title">{{ $t('sendEmail.subTitle') }}</p>
          <el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
            <el-form-item prop="email">
              <el-input
                v-model.trim="formData.email"
                :placeholder="$t('sendEmail.placeholder')"
                size="large"
              />
            </el-form-item>

            <div style="margin-top: 15px">
              <el-button
                class="register-btn"
                size="large"
                type="primary"
                @click="sendEmail"
                :loading="loading"
                v-ripple
              >
                {{ $t('sendEmail.submitBtnText') }}
              </el-button>
            </div>

            <div class="footer">
              <p>
                {{ $t('sendEmail.hasAccount') }}
                <router-link to="/login">{{ $t('sendEmail.toLogin') }}</router-link>
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
  import { RegisterService } from '@/api/registerApi'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const router = useRouter()
  const formRef = ref<FormInstance>()

  const systemName = AppConfig.systemInfo.name
  const loading = ref(false)

  const formData = reactive({
    email: ''
  })

  // 邮箱验证规则
  const validateEmail = (rule: any, value: string, callback: any) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (value === '') {
      callback(new Error(t('sendEmail.rule[0]')))
    } else if (!emailRegex.test(value)) {
      callback(new Error(t('sendEmail.rule[1]')))
    } else {
      callback()
    }
  }

  const rules = reactive<FormRules>({
    email: [{ required: true, validator: validateEmail, trigger: 'blur' }]
  })

  const sendEmail = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      loading.value = true

      try {
        const response = await RegisterService.sendEmail({
          email: formData.email
        })

        if (response.success) {
          // 根据返回的code值决定跳转逻辑
          if (response.code === 203) {
            // 邮箱已验证过，直接跳转到注册页面
            ElMessage.success(t('sendEmail.rule[2]'))
            router.push({
              path: '/register',
              query: {
                email: formData.email,
                verified: 'true'
              }
            })
          } else {
            // 默认情况(code为200)，发送验证码并跳转到验证页面
            ElMessage.success(t('sendEmail.rule[3]'))
            router.push({
              path: '/register/checkEmail',
              query: { email: formData.email }
            })
          }
        } else {
          ElMessage.error(response.message || t('sendEmail.rule[4]'))
        }
      } catch (error) {
        console.error('发送邮件失败:', error)
        ElMessage.error(t('sendEmail.rule[4]'))
      } finally {
        loading.value = false
      }
    } catch (error) {
      console.log('验证失败', error)
    }
  }
</script>

<style lang="scss" scoped>
  @use '../login/index' as login;
  @use './index' as register;
</style>
