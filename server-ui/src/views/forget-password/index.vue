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
          <h3 class="title">{{ $t('forgetPassword.title') }}</h3>
          <p class="sub-title">{{ $t('forgetPassword.subTitle') }}</p>

          <el-form ref="formRef" :model="formData" :rules="rules" label-position="top">
            <el-form-item prop="email">
              <el-input
                v-model.trim="formData.email"
                :placeholder="$t('forgetPassword.placeholder')"
                size="large"
              />
            </el-form-item>

            <div style="margin-top: 15px">
              <el-button
                class="login-btn"
                size="large"
                type="primary"
                @click="sendResetEmail"
                :loading="loading"
                v-ripple
              >
                {{ $t('forgetPassword.submitBtnText') }}
              </el-button>
            </div>

            <div style="margin-top: 15px">
              <el-button style="width: 100%; height: 46px" size="large" plain @click="toLogin">
                {{ $t('forgetPassword.backBtnText') }}
              </el-button>
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
      callback(new Error(t('forgetPassword.rule[0]')))
    } else if (!emailRegex.test(value)) {
      callback(new Error(t('forgetPassword.rule[1]')))
    } else {
      callback()
    }
  }

  // 表单验证规则
  const rules = reactive<FormRules>({
    email: [{ required: true, validator: validateEmail, trigger: 'blur' }]
  })

  // 发送重置密码邮件
  const sendResetEmail = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      loading.value = true

      try {
        // 调用忘记密码API
        const response = await RegisterService.forgetPassword({
          email: formData.email
        })

        if (response.success) {
          ElMessage.success(t('forgetPassword.rule[2]'))
          // 跳转到验证码页面
          router.push({
            path: '/forget-password/check-email',
            query: { email: formData.email }
          })
        } else {
          ElMessage.error(response.message || t('forgetPassword.rule[3]'))
        }
      } catch (error) {
        console.error('发送重置密码邮件失败:', error)
        ElMessage.error(t('forgetPassword.rule[3]'))
      } finally {
        loading.value = false
      }
    } catch (error) {
      console.log('表单验证失败', error)
    }
  }

  const toLogin = () => {
    router.push('/login')
  }
</script>

<style lang="scss" scoped>
  @use '../login/index';

  .el-form {
    width: 100%;
  }

  .login-btn {
    width: 100%;
    height: 46px;
  }

  // 增加样式优化空间
  .login-wrap {
    max-width: 500px;
    padding: 30px 40px;
    margin: 0 auto;
  }

  .form {
    .title {
      margin-bottom: 15px;
      font-size: 24px;
    }

    .sub-title {
      margin-bottom: 25px;
      color: #606266;
    }
  }

  .el-form-item {
    margin-bottom: 25px;
  }

  .right-wrap {
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 40px;
  }

  .header {
    margin-bottom: 30px;

    h1 {
      margin-left: 10px;
    }
  }
</style>
