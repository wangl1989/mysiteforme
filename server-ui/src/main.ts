import App from './App.vue'
import 'default-passive-events'
import { createApp } from 'vue'
import { initStore } from './store'                 // Store
import { initRouter } from './router'               // Router
import '@styles/reset.scss'                         // 重置HTML样式
import '@styles/app.scss'                           // 全局样式
import '@styles/pages.scss'                         // 公共页面样式
import '@styles/el-ui.scss'                         // 优化element样式
import '@styles/mobile.scss'                        // 移动端样式优化
import '@styles/change.scss'                        // 主题切换过渡优化
import '@styles/theme-animation.scss'               // 主题切换动画
import '@icons/system/iconfont.js'                  // 系统彩色图标
import '@icons/system/iconfont.css'                 // 系统图标
import '@styles/el-light.scss'                      // Element 自定义主题（亮色）
import '@styles/el-dark.scss'                       // Element 自定义主题（暗色）
import '@styles/dark.scss'                          // 系统主题
import '@utils/console.ts'                          // 控制台输出内容
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { setupGlobDirectives } from './directives'
import language from './language'
import { analytics } from '@/utils/analytics'

const app = createApp(App)
initStore(app)
initRouter(app)
setupGlobDirectives(app)

app.use(language)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.mount('#app')

// 全局事件委托处理点击
document.addEventListener('click', (event) => {
  const target = event.target as HTMLElement
  
  // 只跟踪特定元素
  const trackableElements = [
    'button', 'a', '[data-track]', '.el-button', 
    '.btn', '[role="button"]', 'input[type="submit"]',
    'input[type="button"]', '.clickable', '[aria-role="button"]',
    'li.el-menu-item','div.btn-text','span.edit-icon','div.upload-demo'
  ].join(',')
  
  // 查找最近的可跟踪元素
  const trackElement = target.closest(trackableElements)
  
  if (trackElement) {
    analytics.trackEvent(trackElement as HTMLElement)
  }
}, { passive: true })
