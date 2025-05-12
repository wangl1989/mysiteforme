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
          <h3 class="title">{{ $t('checkEmail.title') }}</h3>
          <p class="sub-title">
            {{ $t('checkEmail.subTitle1') }} {{ email }}<br />
            {{ $t('checkEmail.subTitle2') }}
          </p>

          <div class="verification-code-container">
            <div v-for="(digit, index) in codeDigits" :key="index" class="code-input-box">
              <el-input
                v-model="codeDigits[index]"
                maxlength="1"
                :ref="(el) => (codeInputs[index] = el)"
                @input="handleCodeInput(index)"
                @keydown.delete="handleDelete(index)"
                @keydown.left="focusPrevInput(index)"
                @keydown.right="focusNextInput(index)"
                @paste="handlePaste($event)"
                class="verification-input"
              />
            </div>
          </div>

          <div class="resend-container">
            <el-button text :disabled="countdown > 0" @click="resendCode">
              {{
                countdown > 0
                  ? `${$t('checkEmail.resendBtnText')} (${countdown}s)`
                  : $t('checkEmail.resendBtnText')
              }}
            </el-button>
          </div>

          <div style="margin-top: 25px">
            <el-button
              class="verify-btn"
              size="large"
              type="primary"
              @click="verifyCode"
              :loading="loading"
              :disabled="!isCodeComplete"
              v-ripple
            >
              {{ $t('checkEmail.checkBtnText') }}
            </el-button>
          </div>

          <div class="footer">
            <p>
              <router-link to="/login">{{ $t('checkEmail.backBtnText') }}</router-link>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import { ElMessage } from 'element-plus'
  import { RegisterService } from '@/api/registerApi'
  import { onMounted, onBeforeUnmount } from 'vue'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const router = useRouter()
  const route = useRoute()

  const systemName = AppConfig.systemInfo.name
  const loading = ref(false)
  const email = ref('')

  // 验证码相关
  const codeDigits = ref(['', '', '', '', '', ''])
  const codeInputs = ref<any[]>([])
  const isCodeComplete = computed(() => codeDigits.value.every((digit) => digit !== ''))

  // 倒计时
  const countdown = ref(0)
  let countdownTimer: number | null = null

  onMounted(() => {
    // 获取URL参数中的邮箱
    email.value = (route.query.email as string) || ''

    if (!email.value) {
      ElMessage.error(t('checkEmail.rule[0]'))
      router.push('/register/sendEmail')
      return
    }

    // 自动聚焦第一个输入框
    setTimeout(() => {
      if (codeInputs.value[0]) {
        codeInputs.value[0].focus()
      }
    }, 100)

    // 开始倒计时
    startCountdown()
  })

  onBeforeUnmount(() => {
    // 清除倒计时
    if (countdownTimer) {
      clearInterval(countdownTimer)
    }
  })

  // 开始倒计时
  const startCountdown = () => {
    countdown.value = 60
    if (countdownTimer) {
      clearInterval(countdownTimer)
    }
    countdownTimer = setInterval(() => {
      if (countdown.value > 0) {
        countdown.value--
      } else if (countdownTimer) {
        clearInterval(countdownTimer)
      }
    }, 1000) as unknown as number
  }

  // 处理验证码输入
  const handleCodeInput = (index: number) => {
    // 限制只能输入数字
    codeDigits.value[index] = codeDigits.value[index].replace(/[^0-9]/g, '')

    // 自动跳到下一个输入框
    if (codeDigits.value[index] && index < 5) {
      focusNextInput(index)
    }

    // 当用户填完所有字段时自动提交
    if (isCodeComplete.value) {
      verifyCode()
    }
  }

  // 处理删除键
  const handleDelete = (index: number) => {
    if (!codeDigits.value[index] && index > 0) {
      codeDigits.value[index - 1] = ''
      focusPrevInput(index)
    }
  }

  // 聚焦前一个输入框
  const focusPrevInput = (index: number) => {
    if (index > 0 && codeInputs.value[index - 1]) {
      codeInputs.value[index - 1].focus()
    }
  }

  // 聚焦后一个输入框
  const focusNextInput = (index: number) => {
    if (index < 5 && codeInputs.value[index + 1]) {
      codeInputs.value[index + 1].focus()
    }
  }

  // 重新发送验证码
  const resendCode = async () => {
    if (countdown.value > 0) return

    try {
      loading.value = true
      const response = await RegisterService.sendEmail({
        email: email.value
      })

      if (response.success) {
        ElMessage.success(t('checkEmail.rule[1]'))
        startCountdown()
      } else {
        ElMessage.error(response.message || t('checkEmail.rule[2]'))
      }
    } catch (error) {
      console.error('发送验证码失败:', error)
      ElMessage.error(t('checkEmail.rule[2]'))
    } finally {
      loading.value = false
    }
  }

  // 验证码验证
  const verifyCode = async () => {
    if (!isCodeComplete.value) return

    const code = codeDigits.value.join('')

    try {
      loading.value = true
      const response = await RegisterService.checkEmail({
        email: email.value,
        code
      })

      if (response.success) {
        ElMessage.success(t('checkEmail.rule[3]'))
        // 跳转到注册页面或其他页面
        router.push({
          path: '/register',
          query: {
            email: email.value,
            verified: 'true'
          }
        })
      } else {
        ElMessage.error(response.message || t('checkEmail.rule[4]'))
      }
    } catch (error) {
      console.error('验证失败:', error)
      ElMessage.error(t('checkEmail.rule[2]'))
    } finally {
      loading.value = false
    }
  }

  // 处理粘贴事件
  const handlePaste = (event: ClipboardEvent) => {
    event.preventDefault()
    const pastedData = event.clipboardData?.getData('text') || ''

    // 只保留数字
    const cleanedData = pastedData.replace(/[^0-9]/g, '')

    // 检查是否有足够的数字
    if (cleanedData.length >= 6) {
      // 取前6位数字填入输入框
      for (let i = 0; i < 6; i++) {
        codeDigits.value[i] = cleanedData[i] || ''
      }

      // 当填充完毕后自动验证
      if (isCodeComplete.value) {
        verifyCode()
      }
    }
  }
</script>

<style lang="scss" scoped>
  @use '../login/index' as login;
  @use './index' as register;

  .verification-code-container {
    display: flex;
    justify-content: space-between;
    margin: 20px 0;

    .code-input-box {
      width: 50px;
      height: 60px;

      :deep(.el-input__wrapper) {
        padding: 0;
        box-shadow: 0 0 0 1px var(--el-border-color) inset;

        &.is-focus {
          box-shadow: 0 0 0 1px var(--el-color-primary) inset;
        }
      }

      .verification-input {
        width: 100%;
        height: 100%;
        font-size: 24px;
        text-align: center;

        :deep(input) {
          padding: 0;
          font-weight: bold;
          color: var(--el-text-color-primary);
          text-align: center;
        }
      }
    }
  }

  .resend-container {
    margin: 10px 0;
    text-align: center;
  }

  .verify-btn {
    width: 100%;
    height: 46px;
  }
</style>
