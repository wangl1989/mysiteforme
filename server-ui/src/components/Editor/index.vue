<template>
  <div class="editor-container" ref="editorContainerRef">
    <Toolbar
      style="border-bottom: 1px solid #ccc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
      :class="{ 'disabled-toolbar': props.disabled }"
    />
    <Editor
      class="editor-body"
      v-model="editorContent"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="handleCreated"
      style="height: 400px"
    />
  </div>
</template>

<script setup lang="ts">
  import { ref, shallowRef, onBeforeUnmount, watch, onMounted, nextTick } from 'vue'
  import '@wangeditor/editor/dist/css/style.css'
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

  interface Props {
    modelValue?: string
    disabled?: boolean
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: '',
    disabled: false
  })

  const emit = defineEmits(['update:modelValue'])

  // 编辑器容器引用
  const editorContainerRef = ref<HTMLElement | null>(null)

  // 编辑器实例，必须用 shallowRef
  const editorRef = shallowRef()

  // 内容 HTML，确保始终是字符串
  const editorContent = ref(props.modelValue || '')

  // 模式
  const mode = ref('default')

  // 工具栏配置
  const toolbarConfig = {}

  // 编辑器配置
  const editorConfig = {
    placeholder: '请输入内容...',
    readOnly: props.disabled,
    autoFocus: false // 避免自动聚焦可能引起的问题
  }

  // 更新编辑器禁用状态
  const updateEditorDisabled = (isDisabled: boolean) => {
    // 确保编辑器实例存在
    if (!editorRef.value) return

    // 使用编辑器的API设置只读状态
    try {
      if (isDisabled) {
        // 禁用编辑器
        editorRef.value.disable()
      } else {
        // 启用编辑器
        editorRef.value.enable()
      }
      console.log('编辑器禁用状态已更新:', isDisabled)
    } catch (error) {
      console.error('设置编辑器禁用状态失败:', error)
    }
  }

  // 组件销毁时，也及时销毁编辑器
  onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
  })

  // 编辑器创建完成时的事件
  const handleCreated = (editor: any) => {
    editorRef.value = editor

    // 初始化内容
    if (props.modelValue) {
      editor.setHtml(props.modelValue)
    }

    // 初始化时设置禁用状态
    nextTick(() => {
      updateEditorDisabled(props.disabled)
    })
  }

  // 监听内容变化
  watch(
    () => editorContent.value,
    (newVal) => {
      emit('update:modelValue', newVal)
    }
  )

  // 监听传入的值变化
  watch(
    () => props.modelValue,
    (newVal) => {
      // 确保newVal是字符串
      const newContent = newVal || ''
      if (newContent !== editorContent.value && editorRef.value) {
        editorContent.value = newContent
        editorRef.value.setHtml(newContent)
      }
    }
  )

  // 监听禁用状态 - 立即生效且深度监听
  watch(
    () => props.disabled,
    (newVal) => {
      updateEditorDisabled(newVal)
    },
    { immediate: true, deep: true }
  )

  // 组件挂载后，确保禁用状态正确
  onMounted(() => {
    nextTick(() => {
      // 再次确认禁用状态
      if (props.disabled && editorRef.value) {
        setTimeout(() => {
          updateEditorDisabled(props.disabled)
        }, 300)
      }
    })
  })
</script>

<style lang="scss" scoped>
  .editor-container {
    width: 100%;
    margin-bottom: 20px;
    overflow: hidden;
    border: 1px solid #e8e8e8;
    border-radius: 4px;

    :deep(.editor-body) {
      min-height: 400px; // 确保最小高度
    }
  }

  /* 添加禁用样式 */
  .disabled-toolbar {
    pointer-events: none; /* 禁止工具栏交互 */
    background-color: #f5f7fa;
    opacity: 0.8;
  }
</style>
