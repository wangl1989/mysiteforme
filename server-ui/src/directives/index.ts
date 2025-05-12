import type { App } from 'vue'
import { setupPermissionDirective } from './permission'
import { setupHighlightDirective } from './highlight'
import { setupRippleDirective } from './ripple'
import { setupMenuPermissionDirective } from './menuPermission'

export function setupGlobDirectives(app: App) {
  setupPermissionDirective(app) // 权限指令
  setupHighlightDirective(app) // 高亮指令
  setupRippleDirective(app) // 水波纹指令
  setupMenuPermissionDirective(app) // 菜单权限指令
}
