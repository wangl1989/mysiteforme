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
          <h3 class="title">{{ $t('forgetCheckEmail.title') }}</h3>
          <p class="sub-title">
            {{ $t('forgetCheckEmail.subTitle1') }} {{ email }}<br />
            {{ $t('forgetCheckEmail.subTitle2') }}
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
                  ? `${t('forgetCheckEmail.resendBtnText')} (${countdown}s)`
                  : t('forgetCheckEmail.resendBtnText')
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
              {{ $t('forgetCheckEmail.checkBtnText') }}
            </el-button>
          </div>

          <div class="footer">
            <p>
              <router-link to="/login">{{ $t('forgetCheckEmail.backBtnText') }}</router-link>
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
      ElMessage.error(t('forgetCheckEmail.rule[0]'))
      router.push('/forget-password')
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
    // 验证输入是否为数字
    if (codeDigits.value[index] && !/^\d$/.test(codeDigits.value[index])) {
      codeDigits.value[index] = ''
      return
    }

    // 自动跳转到下一个输入框
    if (codeDigits.value[index] && index < 5) {
      setTimeout(() => {
        if (codeInputs.value[index + 1]) {
          codeInputs.value[index + 1].focus()
        }
      }, 10)
    }
  }

  // 处理删除键
  const handleDelete = (index: number) => {
    if (!codeDigits.value[index] && index > 0) {
      setTimeout(() => {
        codeDigits.value[index - 1] = ''
        if (codeInputs.value[index - 1]) {
          codeInputs.value[index - 1].focus()
        }
      }, 10)
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

  // 处理粘贴事件
  const handlePaste = (event: ClipboardEvent) => {
    event.preventDefault()
    if (!event.clipboardData) return

    const pastedText = event.clipboardData.getData('text')
    if (!pastedText || !/^\d+$/.test(pastedText)) return

    const digits = pastedText.slice(0, 6).split('')
    digits.forEach((digit, index) => {
      if (index < 6) {
        codeDigits.value[index] = digit
      }
    })

    // 聚焦到粘贴后最后一个非空输入框的下一个输入框
    const lastIndex = Math.min(digits.length, 5)
    if (codeInputs.value[lastIndex]) {
      codeInputs.value[lastIndex].focus()
    }
  }

  // 重新发送验证码
  const resendCode = async () => {
    if (countdown.value > 0) return

    loading.value = true
    try {
      const response = await RegisterService.forgetPassword({
        email: email.value
      })

      if (response.success) {
        ElMessage.success(t('forgetCheckEmail.rule[1]'))
        startCountdown()
        // 清空输入框
        codeDigits.value = ['', '', '', '', '', '']
        // 聚焦第一个输入框
        setTimeout(() => {
          if (codeInputs.value[0]) {
            codeInputs.value[0].focus()
          }
        }, 100)
      } else {
        ElMessage.error(response.message || t('forgetCheckEmail.rule[2]'))
      }
    } catch (error) {
      console.error('重新发送验证码失败:', error)
      ElMessage.error(t('forgetCheckEmail.rule[2]'))
    } finally {
      loading.value = false
    }
  }

  // 验证验证码
  const verifyCode = async () => {
    if (!isCodeComplete.value) return

    const code = codeDigits.value.join('')
    if (code.length !== 6 || !/^\d{6}$/.test(code)) {
      ElMessage.error(t('forgetCheckEmail.rule[3]'))
      return
    }

    loading.value = true
    try {
      const response = await RegisterService.checkRestPassowrdEmail({
        email: email.value,
        code: code
      })

      if (response.success) {
        ElMessage.success(t('forgetCheckEmail.rule[4]'))
        router.push({
          path: '/forget-password/reset-password',
          query: { email: email.value, code: code }
        })
      } else {
        ElMessage.error(response.message || t('forgetCheckEmail.rule[5]'))
      }
    } catch (error) {
      console.error('验证验证码失败:', error)
      ElMessage.error(t('forgetCheckEmail.rule[2]'))
    } finally {
      loading.value = false
    }
  }
</script>

<style lang="scss" scoped>
  @use '../login/index';

  .verification-code-container {
    display: flex;
    justify-content: space-between;
    margin: 20px 0 15px;
  }

  .code-input-box {
    width: 48px;
    margin: 0 4px;

    &:first-child {
      margin-left: 0;
    }

    &:last-child {
      margin-right: 0;
    }
  }

  .verification-input {
    font-size: 20px;
    font-weight: bold;
    text-align: center;

    :deep(.el-input__inner) {
      height: 50px;
      padding: 0;
      text-align: center;
    }
  }

  .resend-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }

  .verify-btn {
    width: 100%;
    height: 46px;
  }

  .footer {
    margin-top: 20px;
    text-align: center;

    a {
      color: var(--el-color-primary);
      text-decoration: none;
    }
  }
</style>
