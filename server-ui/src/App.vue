<template>
  <ElConfigProvider size="default" :locale="locales[language]" :z-index="3000">
    <RouterView></RouterView>
  </ElConfigProvider>
</template>

<script setup lang="ts">
  import { useUserStore } from './store/modules/user'
  import zh from 'element-plus/es/locale/lang/zh-cn'
  import en from 'element-plus/es/locale/lang/en'
  import { systemUpgrade } from './utils/upgrade'
  import { initState, saveUserData } from './utils/storage'
  import { UserService } from './api/usersApi'
  import { ApiStatus } from './utils/http/status'
  import { setThemeTransitionClass } from './utils/theme/animation'

  const userStore = useUserStore()
  const { language } = storeToRefs(userStore)

  const locales = {
    zh: zh,
    en: en
  }

  onBeforeMount(() => {
    setThemeTransitionClass(true)
  })

  onMounted(() => {
    initState()
    saveUserData()
    setThemeTransitionClass(false)
    systemUpgrade()
    getUserInfo()
  })

  // 获取用户信息
  const getUserInfo = async () => {
    if (userStore.isLogin) {
      const userRes = await UserService.getCurrentUser()
      if (!userRes.data.icon || userRes.data.icon === '' || !userRes.data.icon.startsWith('http')) {
        userRes.data.icon = `https://api.dicebear.com/9.x/adventurer/svg?seed=${userRes.data.id}`
      }
      if (userRes.code === ApiStatus.success) {
        userStore.setUserInfo({
          id: userRes.data.id,
          name: userRes.data.nickName,
          username: userRes.data.loginName,
          avatar: userRes.data.icon,
          email: userRes.data.email,
          tel: userRes.data.tel,
          token: userStore.info.accessToken || '',
          accessToken: userStore.info.accessToken || '',
          refreshToken: userStore.info.refreshToken || ''
        })
      }
    }
  }
</script>
