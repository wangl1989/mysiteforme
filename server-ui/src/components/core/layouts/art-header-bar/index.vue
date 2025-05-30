<template>
  <div class="layout-top-bar" :class="[tabStyle]" :style="{ width: topBarWidth() }">
    <div class="menu">
      <div class="left" style="display: flex">
        <!-- 系统信息  -->
        <div class="top-header" @click="toHome" v-if="isTopMenu">
          <svg class="svg-icon2" aria-hidden="true">
            <use xlink:href="#iconsys-zhaopian-copy"></use>
          </svg>
          <p v-if="width >= 1400">{{ AppConfig.systemInfo.name }}</p>
        </div>

        <svg class="svg-icon" aria-hidden="true" @click="toHome()">
          <use xlink:href="#iconsys-zhaopian-copy"></use>
        </svg>

        <!-- 菜单按钮 -->
        <div class="btn-box" v-if="isLeftMenu && showMenuButton">
          <div class="btn menu-btn">
            <i class="iconfont-sys" @click="visibleMenu">&#xe6ba;</i>
          </div>
        </div>
        <!-- 刷新按钮 -->
        <div class="btn-box" v-if="showRefreshButton">
          <div class="btn refresh-btn" :style="{ marginLeft: !isLeftMenu ? '10px' : '0' }">
            <i class="iconfont-sys" @click="reload()"> &#xe6b3; </i>
          </div>
        </div>

        <!-- 快速入口 -->
        <ArtFastEnter v-if="width >= 1200" />

        <!-- 面包屑 -->
        <ArtBreadcrumb
          v-if="(showCrumbs && isLeftMenu) || (showCrumbs && isDualMenu)"
          :style="{ paddingLeft: !showRefreshButton && !showMenuButton ? '10px' : '0' }"
        />

        <!-- 顶部菜单 -->
        <ArtHorizontalMenu v-if="isTopMenu" :list="menuList" :width="menuTopWidth" />

        <!-- 混合菜单-顶部 -->
        <ArtMixedMenu v-if="isTopLeftMenu" :list="menuList" :width="menuTopWidth" />
      </div>

      <div class="right">
        <!-- 搜索 -->
        <div class="search-wrap">
          <div class="search-input" @click="openSearchDialog">
            <div class="left">
              <i class="iconfont-sys">&#xe710;</i>
              <span>{{ $t('topBar.search.title') }}</span>
            </div>
            <div class="search-keydown">
              <i class="iconfont-sys" v-if="isWindows">&#xeeac;</i>
              <i class="iconfont-sys" v-else>&#xe9ab;</i>
              <span>k</span>
            </div>
          </div>
        </div>

        <!-- 全屏按钮 -->
        <div class="btn-box screen-box" @click="toggleFullScreen">
          <div
            class="btn"
            :class="{ 'full-screen-btn': !isFullscreen, 'exit-full-screen-btn': isFullscreen }"
          >
            <i class="iconfont-sys">{{ isFullscreen ? '&#xe62d;' : '&#xe8ce;' }}</i>
          </div>
        </div>
        <!-- 通知 -->
        <div class="btn-box notice-btn" @click="visibleNotice">
          <div class="btn notice-button">
            <i class="iconfont-sys notice-btn">&#xe6c2;</i>
            <span class="count notice-btn"></span>
          </div>
        </div>
        <!-- 聊天 -->
        <div class="btn-box chat-btn" @click="openChat">
          <div class="btn chat-button">
            <i class="iconfont-sys">&#xe89a;</i>
            <span class="dot"></span>
          </div>
        </div>
        <!-- 语言 -->
        <div class="btn-box" v-if="showLanguage">
          <el-dropdown @command="changeLanguage" popper-class="langDropDownStyle">
            <div class="btn language-btn">
              <i class="iconfont-sys">&#xe611;</i>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <div v-for="item in languageOptions" :key="item.value" class="lang-btn-item">
                  <el-dropdown-item
                    :command="item.value"
                    :class="{ 'is-selected': locale === item.value }"
                  >
                    <span class="menu-txt">{{ item.label }}</span>
                    <i v-if="locale === item.value" class="iconfont-sys">&#xe621;</i>
                  </el-dropdown-item>
                </div>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <!-- 设置 -->
        <div class="btn-box" @click="openSetting">
          <el-popover :visible="showSettingGuide" placement="bottom-start" :width="190" :offset="0">
            <template #reference>
              <div class="btn setting-btn">
                <i class="iconfont-sys">&#xe6d0;</i>
              </div>
            </template>
            <template #default>
              <p
                >点击这里查看<span :style="{ color: systemThemeColor }"> 主题风格 </span>、
                <span :style="{ color: systemThemeColor }"> 开启顶栏菜单 </span>等更多配置
              </p>
            </template>
          </el-popover>
        </div>
        <!-- 切换主题 -->
        <div class="btn-box" @click="themeAnimation">
          <div class="btn theme-btn">
            <i class="iconfont-sys">{{ isDark ? '&#xe6b5;' : '&#xe725;' }}</i>
          </div>
        </div>

        <!-- 用户头像、菜单 -->
        <div class="user">
          <el-popover
            ref="userMenuPopover"
            placement="bottom-end"
            :width="240"
            :hide-after="0"
            :offset="10"
            trigger="hover"
            :show-arrow="false"
            popper-class="user-menu-popover"
            popper-style="border: 1px solid var(--art-border-dashed-color); border-radius: calc(var(--custom-radius) / 2 + 4px); padding: 5px 16px; 5px 16px;"
          >
            <template #reference>
              <img class="cover" :src="getImgUrl(`${userInfo.avatar}`, `${userInfo.id}`)" />
            </template>
            <template #default>
              <div class="user-menu-box">
                <div class="user-head">
                  <img
                    class="cover"
                    :src="getImgUrl(`${userInfo.avatar}`, `${userInfo.id}`)"
                    style="float: left"
                  />
                  <div class="user-wrap">
                    <span class="name">{{ getUserName }}</span>
                    <span class="email" v-if="userInfo.email">{{ userInfo.email }}</span>
                  </div>
                </div>
                <ul class="user-menu">
                  <li @click="goPage('/user/userDetail')">
                    <i class="menu-icon iconfont-sys">&#xe734;</i>
                    <span class="menu-txt">{{ $t('topBar.user.userCenter') }}</span>
                  </li>
                  <li @click="toDocs()">
                    <i class="menu-icon iconfont-sys" style="font-size: 15px">&#xe828;</i>
                    <span class="menu-txt">{{ $t('topBar.user.docs') }}</span>
                  </li>
                  <li @click="toGithub()">
                    <i class="menu-icon iconfont-sys">&#xe8d6;</i>
                    <span class="menu-txt">{{ $t('topBar.user.github') }}</span>
                  </li>
                  <li @click="lockScreen()">
                    <i class="menu-icon iconfont-sys">&#xe817;</i>
                    <span class="menu-txt">{{ $t('topBar.user.lockScreen') }}</span>
                  </li>
                  <div class="line"></div>
                  <div class="logout-btn" @click="loginOut">
                    {{ $t('topBar.user.logout') }}
                  </div>
                </ul>
              </div>
            </template>
          </el-popover>
        </div>
      </div>
    </div>
    <ArtWorkTab />

    <art-notification v-model:value="showNotice" ref="notice" />
  </div>
</template>

<script setup lang="ts">
  import { LanguageEnum, MenuTypeEnum, MenuWidth } from '@/enums/appEnum'
  import { useSettingStore } from '@/store/modules/setting'
  import { useUserStore } from '@/store/modules/user'
  import { useFullscreen } from '@vueuse/core'
  import { ElMessageBox } from 'element-plus'
  import { HOME_PAGE } from '@/router'
  import { useI18n } from 'vue-i18n'
  import mittBus from '@/utils/mittBus'
  import { useMenuStore } from '@/store/modules/menu'
  import AppConfig from '@/config'
  import { languageOptions } from '@/language'
  const isWindows = navigator.userAgent.includes('Windows')
  const { locale } = useI18n()

  const settingStore = useSettingStore()
  const userStore = useUserStore()
  const router = useRouter()

  const {
    showMenuButton,
    showRefreshButton,
    showLanguage,
    menuOpen,
    showCrumbs,
    systemThemeColor,
    showSettingGuide,
    menuType,
    isDark,
    tabStyle
  } = storeToRefs(settingStore)

  const { language, getUserInfo: userInfo } = storeToRefs(userStore)

  const { menuList } = storeToRefs(useMenuStore())

  const showNotice = ref(false)
  const notice = ref(null)
  const userMenuPopover = ref()

  const isLeftMenu = computed(() => menuType.value === MenuTypeEnum.LEFT)
  const isDualMenu = computed(() => menuType.value === MenuTypeEnum.DUAL_MENU)
  const isTopMenu = computed(() => menuType.value === MenuTypeEnum.TOP)
  const isTopLeftMenu = computed(() => menuType.value === MenuTypeEnum.TOP_LEFT)

  import { useCommon } from '@/composables/useCommon'
  import { WEB_LINKS } from '@/utils/links'
  import { themeAnimation } from '@/utils/theme/animation'

  const { t } = useI18n()

  const { width } = useWindowSize()

  const menuTopWidth = computed(() => {
    return width.value * 0.5
  })

  onMounted(() => {
    initLanguage()
    document.addEventListener('click', bodyCloseNotice)
  })

  onUnmounted(() => {
    document.removeEventListener('click', bodyCloseNotice)
  })

  const { isFullscreen, toggle: toggleFullscreen } = useFullscreen()

  const toggleFullScreen = () => {
    toggleFullscreen()
  }

  const topBarWidth = (): string => {
    const { TOP, DUAL_MENU } = MenuTypeEnum
    const { getMenuOpenWidth } = settingStore

    switch (menuType.value) {
      case TOP:
        return '100%'
      case DUAL_MENU:
        return `calc(100% - 80px - ${getMenuOpenWidth})`
      default:
        return menuOpen.value
          ? `calc(100% - ${getMenuOpenWidth})`
          : `calc(100% - ${MenuWidth.CLOSE})`
    }
  }

  const visibleMenu = () => {
    settingStore.setMenuOpen(!menuOpen.value)
  }

  // 获取图片url
  const getImgUrl = (url: string | undefined, userId: string | number) => {
    if (!url) {
      return `https://api.dicebear.com/9.x/adventurer/svg?seed=${userId}`
    }

    if (url.startsWith('upload')) {
      return `${import.meta.env.VITE_API_URL}/` + url
    }

    return url
  }

  const getUserName = computed(() => {
    if (userInfo.value.name) {
      return userInfo.value.name
    }
    return userInfo.value.username
  })

  const goPage = (path: string) => {
    // 检查 Popover 实例是否存在并尝试关闭
    if (userMenuPopover.value) {
      userMenuPopover.value.hide()
    }
    // 等待 DOM 更新，并尝试移除焦点后再进行路由跳转
    nextTick(() => {
      // 尝试让当前活动元素失焦
      if (document.activeElement instanceof HTMLElement) {
        document.activeElement.blur()
      }
      router.push(path)
    })
  }

  const toDocs = () => {
    window.open(WEB_LINKS.DOCS)
  }

  const toGithub = () => {
    window.open(WEB_LINKS.GITHUB)
  }

  const toHome = () => {
    router.push(HOME_PAGE)
  }

  const loginOut = () => {
    closeUserMenu()
    setTimeout(() => {
      ElMessageBox.confirm(t('common.logOutTips'), t('common.tips'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        customClass: 'login-out-dialog'
      }).then(() => {
        userStore.logOut()
      })
    }, 200)
  }

  const reload = (time: number = 0) => {
    setTimeout(() => {
      useCommon().refresh()
    }, time)
  }

  const initLanguage = () => {
    locale.value = language.value
  }

  const changeLanguage = (lang: LanguageEnum) => {
    if (locale.value === lang) return
    locale.value = lang
    userStore.setLanguage(lang)
    reload(50)
  }

  const openSetting = () => {
    mittBus.emit('openSetting')

    // 隐藏设置引导
    if (showSettingGuide.value) {
      settingStore.hideSettingGuide()
    }
    // 打开设置引导
    // settingStore.openSettingGuide()
  }

  const openSearchDialog = () => {
    mittBus.emit('openSearchDialog')
  }

  const bodyCloseNotice = (e: any) => {
    let { className } = e.target

    if (showNotice.value) {
      if (typeof className === 'object') {
        showNotice.value = false
        return
      }
      if (className.indexOf('notice-btn') === -1) {
        showNotice.value = false
      }
    }
  }

  const visibleNotice = () => {
    showNotice.value = !showNotice.value
  }

  const openChat = () => {
    mittBus.emit('openChat')
  }

  const lockScreen = () => {
    mittBus.emit('openLockScreen')
  }

  const closeUserMenu = () => {
    setTimeout(() => {
      userMenuPopover.value.hide()
    }, 100)
  }
</script>

<style lang="scss" scoped>
  @use './style';
  @use './mobile';
</style>
