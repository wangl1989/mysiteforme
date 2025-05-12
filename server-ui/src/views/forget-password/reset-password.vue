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
          <h3 class="title">重置密码</h3>
          <p class="sub-title">请设置新的密码</p>

          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-position="top"
            class="reset-form"
          >
            <el-form-item prop="password">
              <el-input
                v-model.trim="formData.password"
                placeholder="请输入新密码"
                type="password"
                size="large"
                :show-password="true"
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model.trim="formData.confirmPassword"
                placeholder="请确认新密码"
                type="password"
                size="large"
                :show-password="true"
                @keyup.enter="resetPassword"
              />
            </el-form-item>

            <div style="margin-top: 25px">
              <el-button
                class="reset-btn"
                size="large"
                type="primary"
                @click="resetPassword"
                :loading="loading"
                v-ripple
              >
                重置密码
              </el-button>
            </div>

            <div class="footer">
              <p>
                <router-link to="/login">返回登录</router-link>
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
  import { RegisterService } from '@/api/registerApi'
  import type { FormInstance, FormRules } from 'element-plus'
  import { onMounted } from 'vue'

  const router = useRouter()
  const route = useRoute()
  const formRef = ref<FormInstance>()

  const systemName = AppConfig.systemInfo.name
  const loading = ref(false)
  const email = ref('')
  const code = ref('')

  const formData = reactive({
    password: '',
    confirmPassword: ''
  })

  onMounted(() => {
    // 获取URL参数
    email.value = (route.query.email as string) || ''
    code.value = (route.query.code as string) || ''

    if (!email.value || !code.value) {
      ElMessage.error('参数缺失，请重新验证')
      router.push('/forget-password')
    }
  })

  // 密码验证规则
  const validatePass = (rule: any, value: string, callback: any) => {
    if (value === '') {
      callback(new Error('请输入新密码'))
    } else {
      // 验证密码复杂度
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d\W_]{8,20}$/
      if (!passwordRegex.test(value)) {
        callback(new Error('密码必须包含8-20个字符，至少包含一个大写字母、一个小写字母和一个数字'))
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
      callback(new Error('请确认新密码'))
    } else if (value !== formData.password) {
      callback(new Error('两次输入的密码不一致'))
    } else {
      callback()
    }
  }

  // 表单验证规则
  const rules = reactive<FormRules>({
    password: [{ required: true, validator: validatePass, trigger: 'blur' }],
    confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
  })

  // 重置密码
  const resetPassword = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      loading.value = true

      try {
        const response = await RegisterService.resetPassword({
          email: email.value,
          password: formData.password
        })

        if (response.success) {
          ElMessage.success('密码重置成功')
          setTimeout(() => {
            router.push('/login')
          }, 1500)
        } else {
          ElMessage.error(response.message || '密码重置失败')
        }
      } catch (error) {
        console.error('重置密码失败:', error)
        ElMessage.error('重置密码失败，请稍后重试')
      } finally {
        loading.value = false
      }
    } catch (error) {
      console.log('表单验证失败', error)
    }
  }
</script>

<style lang="scss" scoped>
  @use '../login/index';

  .login-wrap {
    .form {
      .title {
        margin-bottom: 10px;
        font-size: 24px;
        font-weight: 600;
      }

      .sub-title {
        margin-bottom: 30px;
        font-size: 14px;
        color: #606266;
      }

      .reset-form {
        width: 100%;
        margin-top: 20px;

        :deep(.el-form-item) {
          margin-bottom: 20px;
        }

        :deep(.el-input__wrapper) {
          border-radius: 4px;
        }

        :deep(.el-input__inner) {
          height: 46px;
        }
      }
    }
  }

  .reset-btn {
    width: 100%;
    height: 46px;
    font-size: 16px;
    border-radius: 4px;
  }

  .footer {
    margin-top: 25px;
    text-align: center;

    a {
      font-size: 14px;
      color: var(--el-color-primary);
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
</style>
