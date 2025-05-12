<template>
  <div class="icon-selector">
    <div
      class="select"
      @click="handleClick"
      :style="{ width: props.width }"
      :class="[size, { 'is-disabled': disabled }, { 'has-icon': internalValue }]"
    >
      <div class="icon">
        <i
          :class="`iconfont-sys ${internalValue}`"
          v-show="props.iconType === IconTypeEnum.CLASS_NAME"
          :style="iconStyle"
        ></i>
        <i
          class="iconfont-sys"
          v-html="internalValue"
          v-show="props.iconType === IconTypeEnum.UNICODE"
          :style="iconStyle"
        ></i>
      </div>
      <div class="text"> {{ props.text }} </div>
      <div class="arrow">
        <i class="iconfont-sys arrow-icon">&#xe709;</i>
        <i class="iconfont-sys clear-icon" @click.stop="clearIcon">&#xe83a;</i>
      </div>
    </div>

    <el-dialog title="选择图标" width="40%" v-model="visible" align-center>
      <el-scrollbar height="400px">
        <ul class="icons-list" v-show="activeName === 'icons'">
          <li v-for="icon in iconsList" :key="icon.className" @click="selectorIcon(icon)">
            <i
              :class="`iconfont-sys ${icon.className}`"
              v-show="iconType === IconTypeEnum.CLASS_NAME"
            ></i>
            <i
              class="iconfont-sys"
              v-html="icon.unicode"
              v-show="iconType === IconTypeEnum.UNICODE"
            ></i>
          </li>
        </ul>
      </el-scrollbar>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="visible = false">取 消</el-button>
          <el-button type="primary" @click="visible = false">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { IconTypeEnum } from '@/enums/appEnum'
  import { extractIconClasses } from '@/utils/iconfont'

  const emit = defineEmits(['update:modelValue'])

  const iconsList = extractIconClasses()

  const props = defineProps({
    iconType: {
      type: String as PropType<IconTypeEnum>,
      default: IconTypeEnum.CLASS_NAME
    },
    defaultIcon: {
      type: String,
      default: ''
    },
    text: {
      type: String,
      default: '图标选择器'
    },
    width: {
      type: String,
      default: '200px'
    },
    size: {
      type: String as PropType<'large' | 'default' | 'small'>,
      default: 'default'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    modelValue: {
      type: String,
      default: ''
    },
    iconColor: {
      type: String,
      default: null
    }
  })

  const internalValue = ref(props.modelValue || props.defaultIcon)

  const iconStyle = computed(() => {
    return {
      color: props.iconColor
    }
  })

  watch(
    () => props.modelValue,
    (newVal) => {
      if (newVal !== internalValue.value) {
        internalValue.value = newVal
      }
    },
    { immediate: true }
  )

  watch(
    () => props.defaultIcon,
    (newDefault) => {
      if (!props.modelValue && newDefault !== internalValue.value) {
        internalValue.value = newDefault
      }
    },
    { immediate: true }
  )

  const activeName = ref('icons')
  const visible = ref(false)

  const selectorIcon = (selectedIcon: any) => {
    let emitValue = ''

    if (props.iconType === IconTypeEnum.CLASS_NAME) {
      emitValue = selectedIcon.className
    } else if (props.iconType === IconTypeEnum.UNICODE && selectedIcon.unicode) {
      emitValue = selectedIcon.unicode
    }
    internalValue.value = emitValue
    visible.value = false
    emit('update:modelValue', emitValue)
  }

  const handleClick = () => {
    if (!props.disabled) {
      visible.value = true
    }
  }

  const clearIcon = () => {
    internalValue.value = ''
    emit('update:modelValue', '')
  }
</script>

<style lang="scss" scoped>
  .icon-selector {
    width: 100%;

    .select {
      box-sizing: border-box;
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: var(--el-component-custom-height);
      padding: 0 15px;
      cursor: pointer;
      border: 1px solid var(--art-border-dashed-color);
      border-radius: calc(var(--custom-radius) / 3 + 2px) !important;
      transition: border 0.3s;

      @media (width <= 500px) {
        width: 100% !important;
      }

      &.large {
        height: 40px;
      }

      &.small {
        height: 24px;
      }

      &:hover:not(.is-disabled).has-icon {
        .arrow-icon {
          display: none;
        }

        .clear-icon {
          display: block !important;
        }
      }

      &:hover {
        border-color: var(--art-text-gray-400);
      }

      .icon {
        display: flex;
        align-items: center;
        width: 20px;
        color: var(--art-gray-700);

        i {
          display: block;
          margin: 0 auto;
          font-size: 16px;
        }
      }

      .text {
        display: flex;
        display: inline-block;
        align-items: center;
        width: 50%;
        font-size: 14px;
        color: var(--art-gray-600);

        @include ellipsis();

        @media (width <= 500px) {
          display: none;
        }
      }

      .arrow {
        display: flex;
        align-items: center;
        height: calc(100% - 2px);

        i {
          font-size: 13px;
          color: var(--art-gray-600);
        }

        .clear-icon {
          display: none;
        }
      }

      &.is-disabled {
        cursor: not-allowed;
        background-color: var(--el-disabled-bg-color);
        border-color: var(--el-border-color-lighter);

        .icon,
        .text,
        .arrow {
          color: var(--el-text-color-placeholder);
        }

        &:hover {
          border-color: var(--el-border-color-lighter);
        }
      }
    }

    .icons-list {
      display: grid;
      grid-template-columns: repeat(10, 1fr);
      border-top: 1px solid var(--art-border-color);
      border-left: 1px solid var(--art-border-color);

      li {
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        justify-content: center;
        aspect-ratio: 1 / 1;
        color: var(--art-gray-600);
        text-align: center;
        border-right: 1px solid var(--art-border-color);
        border-bottom: 1px solid var(--art-border-color);

        &:hover {
          cursor: pointer;
          background: var(--art-gray-100);
        }

        i {
          font-size: 22px;
          color: var(--art-gray-800);
        }
      }
    }
  }
</style>
