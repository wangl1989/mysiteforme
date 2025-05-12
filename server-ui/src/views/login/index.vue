<template>
  <div class="login">
    <div class="left-wrap">
      <LoginLeftView></LoginLeftView>
    </div>
    <div class="right-wrap">
      <div class="top-right-wrap">
        <div class="btn theme-btn" @click="toggleTheme">
          <i class="iconfont-sys">
            {{ isDark ? '&#xe6b5;' : '&#xe725;' }}
          </i>
        </div>
        <el-dropdown @command="changeLanguage" popper-class="langDropDownStyle">
          <div class="btn language-btn">
            <i class="iconfont-sys icon-language">&#xe611;</i>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <div v-for="lang in languageOptions" :key="lang.value" class="lang-btn-item">
                <el-dropdown-item
                  :command="lang.value"
                  :class="{ 'is-selected': locale === lang.value }"
                >
                  <span class="menu-txt">{{ lang.label }}</span>
                  <i v-if="locale === lang.value" class="iconfont-sys icon-check">&#xe621;</i>
                </el-dropdown-item>
              </div>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="header">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#iconsys-zhaopian-copy"></use>
        </svg>
        <h1>{{ systemName }}</h1>
      </div>
      <div class="login-wrap">
        <div class="form">
          <h3 class="title">{{ $t('login.title') }}</h3>
          <p class="sub-title">{{ $t('login.subTitle') }}</p>
          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            @keyup.enter="handleSubmit"
            style="margin-top: 25px"
          >
            <el-form-item prop="username">
              <el-input
                :placeholder="$t('login.placeholder[0]')"
                v-model.trim="formData.username"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                :placeholder="$t('login.placeholder[1]')"
                v-model.trim="formData.password"
                type="password"
                radius="8px"
                autocomplete="off"
              />
            </el-form-item>
            <el-form-item prop="captcha" class="input-wrap captcha-wrap">
              <el-input
                :placeholder="$t('login.placeholder[2]')"
                size="large"
                v-model.trim="formData.captcha"
                @keyup.enter="handleSubmit"
              />
              <img :src="captchaSrc" @click="refreshCaptcha" class="captcha-img" alt="captcha" />
            </el-form-item>
            <div class="drag-verify">
              <div class="drag-verify-content" :class="{ error: !isPassing && isClickPass }">
                <ArtDragVerify
                  ref="dragVerify"
                  v-model:value="isPassing"
                  :width="width < 500 ? 328 : 438"
                  :text="$t('login.sliderText')"
                  textColor="var(--art-gray-800)"
                  :successText="$t('login.sliderSuccessText')"
                  :progressBarBg="getCssVariable('--el-color-primary')"
                  background="var(--art-gray-200)"
                  handlerBg="var(--art-main-bg-color)"
                  @pass="onPass"
                />
              </div>
              <p class="error-text" :class="{ 'show-error-text': !isPassing && isClickPass }">{{
                $t('login.placeholder[3]')
              }}</p>
            </div>

            <div class="forget-password">
              <el-checkbox v-model="formData.rememberPassword">{{
                $t('login.rememberPwd')
              }}</el-checkbox>
              <router-link to="/forget-password">{{ $t('login.forgetPwd') }}</router-link>
            </div>

            <div style="margin-top: 30px">
              <el-button
                class="login-btn"
                type="primary"
                @click="handleSubmit"
                :loading="loading"
                v-ripple
              >
                {{ $t('login.btnText') }}
              </el-button>
            </div>

            <div class="footer">
              <p>
                {{ $t('login.noAccount') }}
                <router-link to="/register/sendEmail">{{ $t('login.register') }}</router-link>
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
  import { ElMessage, ElNotification } from 'element-plus'
  import { useUserStore } from '@/store/modules/user'
  import { HOME_PAGE } from '@/router'
  import { ApiStatus } from '@/utils/http/status'
  import { getCssVariable } from '@/utils/colors'
  import { languageOptions } from '@/language'
  import { LanguageEnum, SystemThemeEnum } from '@/enums/appEnum'
  import { UserService } from '@/api/usersApi'
  // 切换主题
  import { useTheme } from '@/composables/useTheme'
  import { useI18n } from 'vue-i18n'
  import { useWindowSize } from '@vueuse/core'

  const { t } = useI18n()
  import { useSettingStore } from '@/store/modules/setting'
  import type { FormInstance, FormRules } from 'element-plus'
  import { onMounted } from 'vue'
  import { getDeviceId } from '@/utils/deviceId'

  const settingStore = useSettingStore()
  const { isDark, systemThemeType } = storeToRefs(settingStore)

  const dragVerify = ref()

  const userStore = useUserStore()
  const router = useRouter()
  const isPassing = ref(false)
  const isClickPass = ref(false)

  const systemName = AppConfig.systemInfo.name
  const formRef = ref<FormInstance>()
  const formData = reactive({
    username: AppConfig.systemInfo.login.username,
    password: AppConfig.systemInfo.login.password,
    rememberPassword: true,
    captcha: ''
  })

  const rules = computed<FormRules>(() => ({
    username: [{ required: true, message: t('login.placeholder[0]'), trigger: 'blur' }],
    password: [{ required: true, message: t('login.placeholder[1]'), trigger: 'blur' }],
    captcha: [{ required: true, message: t('login.placeholder[2]'), trigger: 'blur' }]
  }))

  const captchaSrc = ref('')
  const loading = ref(false)
  const { width } = useWindowSize()

  const onPass = () => {}

  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        if (!formData.captcha) {
          ElMessage.error(t('login.rule[5]'))
          return
        }

        if (!isPassing.value) {
          isClickPass.value = true
          return
        }
        loading.value = true
        // Prepare body and headers separately
        const bodyParams = {
          username: formData.username,
          password: formData.password,
          captcha: formData.captcha
        }
        // 获取设备ID
        const deviceId = await getDeviceId()

        try {
          const res = await UserService.login(bodyParams, deviceId)
          if (res.code === ApiStatus.success) {
            const user = res.data // Assuming data includes user details and token
            if (user) {
              // 保存登录状态和token信息
              userStore.setAccessToken(user.accessToken, user.refreshToken)

              // 获取用户详细信息
              await fetchUserDetailAndComplete()
            } else {
              ElMessage.error(res.message || '登录成功但未收到用户信息')
              refreshCaptcha()
            }
          } else {
            ElMessage.error(res.message || '登录失败')
            refreshCaptcha()
            resetDragVerify()
          }
        } catch (error) {
          console.error('Login error:', error)
          ElMessage.error('登录请求失败')
          refreshCaptcha()
        } finally {
          loading.value = false
        }
      }
    })
  }

  // 重置拖拽验证
  const resetDragVerify = () => {
    dragVerify.value.reset()
  }

  // 获取用户详情并完成登录流程
  async function fetchUserDetailAndComplete() {
    loading.value = true // 继续显示加载状态

    try {
      // 使用 UserService.getCurrentUser 方法获取当前用户详情
      const userDetailRes = await UserService.getCurrentUser()

      if (userDetailRes.code === ApiStatus.success && userDetailRes.data) {
        // 保存用户详细信息
        const userData = userDetailRes.data
        if (!userData.icon || userData.icon === '' || !userData.icon?.startsWith('http')) {
          if (userData.icon?.startsWith('upload')) {
            userData.icon = `${import.meta.env.VITE_API_URL}/` + userData.icon
          } else {
            userData.icon = `https://api.dicebear.com/9.x/adventurer/svg?seed=${userData.id}`
          }
        }
        // 确保数据符合UserInfo类型或进行必要的适配
        userStore.setUserInfo({
          id: userData.id,
          name: userData.nickName,
          username: userData.loginName,
          avatar: userData.icon,
          email: userData.email,
          tel: userData.tel,
          location: userData.location || '',
          // 如果缺少token字段，可以从store中获取当前保存的token
          token: userStore.info.accessToken || '',
          accessToken: userStore.info.accessToken || '',
          refreshToken: userStore.info.refreshToken || ''
        })
        userStore.setLoginStatus(true)
        userStore.saveUserData()
        showLoginSuccessNotice(userData.nickName ? userData.nickName : userData.loginName)
        router.push(HOME_PAGE)
      } else {
        // 获取用户信息失败视为登录流程失败
        ElMessage.error(userDetailRes.message || '获取用户信息失败')
        userStore.logOut() // 登出
        refreshCaptcha()
      }
    } catch (error) {
      console.error('获取用户详情错误:', error)
      ElMessage.error('处理登录信息时发生错误，请重试')
      userStore.logOut() // 发生任何严重错误都应登出
      refreshCaptcha()
    } finally {
      loading.value = false // 结束加载
    }
  }

  // 登录成功提示
  const showLoginSuccessNotice = (name: string) => {
    setTimeout(() => {
      ElNotification({
        title: t('login.success.title'),
        type: 'success',
        showClose: false,
        duration: 2500,
        zIndex: 10000,
        message: `${t('login.success.message')}, ${name}!`
      })
    }, 300)
  }

  // 切换语言
  const { locale } = useI18n()

  const changeLanguage = (lang: LanguageEnum) => {
    if (locale.value === lang) return
    locale.value = lang
    userStore.setLanguage(lang)
  }

  const toggleTheme = () => {
    let { LIGHT, DARK } = SystemThemeEnum
    useTheme().switchThemeStyles(systemThemeType.value === LIGHT ? DARK : LIGHT)
  }

  // 获取验证码
  const refreshCaptcha = async () => {
    try {
      loading.value = true
      // 使用 UserService 获取验证码
      const response = await UserService.getCaptcha()

      // 假设 BaseResult 结构是 { code, data, message }
      // 并且 data 符合 CaptchaData 接口
      const { code, data, message } = response

      if (code === ApiStatus.success && data) {
        // 将 Base64 字符串构造成 Data URI
        captchaSrc.value = `data:image/png;base64,${data.image}`
        formData.captcha = '' // 重置验证码输入框
      } else {
        ElMessage.error(message || '获取验证码失败')
        captchaSrc.value = '' // 清空图片
      }
    } catch (error) {
      console.error('获取验证码错误:', error)
      ElMessage.error('获取验证码时发生错误')
      captchaSrc.value = '' // 清空图片
    } finally {
      loading.value = false // 结束加载状态
    }
  }

  // --- 结束修改 ---

  // 组件挂载时获取初始验证码并确保 deviceId 存在 (修改为异步)
  onMounted(async () => {
    refreshCaptcha() // 然后再获取验证码
  })
</script>

<style lang="scss" scoped>
  @use './index';

  // 添加验证码相关样式
  .captcha-wrap {
    display: flex;
    align-items: center;

    .el-input {
      flex: 1;
      margin-right: 10px;
    }

    .captcha-img {
      width: 120px; // 可根据实际图片调整宽度
      height: 46px; // 与输入框高度一致
      cursor: pointer;
      border-radius: var(--el-border-radius-base); // 使用Element Plus变量
    }
  }
</style>
