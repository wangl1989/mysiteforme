<template>
  <div class="upload-img-container" :style="{ width, minWidth }">
    <el-upload
      class="avatar-uploader"
      action="#"
      :show-file-list="false"
      :disabled="disabled"
      :before-upload="beforeUpload"
      :http-request="customUpload"
      :on-preview="handlePictureCardPreview"
    >
      <div class="image-container" v-if="previewUrl" :style="{ minWidth, minHeight }">
        <img :src="previewUrl" class="avatar" />
        <div class="image-mask">
          <div class="image-actions">
            <el-icon
              @click.stop="
                handlePictureCardPreview({
                  name: 'preview',
                  status: 'success',
                  uid: -1,
                  url: previewUrl
                })
              "
              ><ZoomIn
            /></el-icon>
          </div>
        </div>
      </div>
      <div v-else class="empty-upload-area">
        <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
        <span class="upload-text">点击上传</span>
      </div>
    </el-upload>
    <el-dialog v-model="dialogVisible">
      <img w-full :src="dialogImageUrl" alt="Preview Image" class="preview-image" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, watch } from 'vue'
  import { Plus, ZoomIn } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { UploadService } from '@/api/uploadApi'
  import type { UploadProps } from 'element-plus'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const props = defineProps({
    modelValue: {
      type: String,
      default: ''
    },
    imageUrl: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '100px'
    },
    minWidth: {
      type: String,
      default: '120px'
    },
    minHeight: {
      type: String,
      default: '120px'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    maxSize: {
      type: Number,
      default: 2
    }
  })

  const dialogImageUrl = ref('')
  const dialogVisible = ref(false)

  const emit = defineEmits(['update:modelValue'])

  // 图片预览地址
  const previewUrl = ref(props.imageUrl || props.modelValue)

  // 监听外部值变化
  watch(
    () => props.modelValue,
    (newVal) => {
      if (newVal && newVal !== previewUrl.value) {
        previewUrl.value = newVal
      }
    }
  )

  watch(
    () => props.imageUrl,
    (newVal) => {
      if (newVal && newVal !== previewUrl.value) {
        previewUrl.value = newVal
      }
    }
  )

  // 上传前验证
  const beforeUpload: UploadProps['beforeUpload'] = (file) => {
    // 检查文件类型
    const isImage = file.type.startsWith('image/')
    if (!isImage) {
      ElMessage.error(t('upload.baseInfo.message.onlyImage'))
      return false
    }

    // 检查文件大小
    const isLtMax = file.size / 1024 / 1024 < props.maxSize
    if (!isLtMax) {
      ElMessage.error(t('upload.baseInfo.message.imageSizeLimit'))
      return false
    }

    return true
  }

  // 自定义上传方法
  const customUpload = async (options: any) => {
    try {
      // 创建FormData对象
      const formData = new FormData()
      formData.append('test', options.file)

      // 调用上传API
      const result = await UploadService.upload(formData)

      if (result.success && result.data) {
        // 上传成功处理
        const url = result.data.url || URL.createObjectURL(options.file)
        previewUrl.value = url
        emit('update:modelValue', url)
        options.onSuccess(result.data)
        ElMessage.success(t('upload.baseInfo.message.uploadSuccess'))
      } else {
        // 上传失败处理
        options.onError(new Error(result.message || t('upload.baseInfo.message.uploadFailed')))
        ElMessage.error(result.message || t('upload.baseInfo.message.uploadFailed'))
      }
    } catch (error) {
      console.error(t('upload.baseInfo.message.uploadError'), error)
      options.onError(error)
      ElMessage.error(t('upload.baseInfo.message.uploadError'))
    }
  }

  const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
    dialogImageUrl.value = uploadFile.url!
    dialogVisible.value = true
  }
</script>

<style lang="scss" scoped>
  .upload-img-container {
    .avatar-uploader {
      .avatar {
        display: block;
        width: 100%;
        height: 100%;
      }

      .image-container {
        position: relative;
        width: 100%;
        height: 100%;
        overflow: hidden;

        .image-mask {
          position: absolute;
          top: 0;
          left: 0;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 100%;
          height: 100%;
          background-color: rgb(0 0 0 / 50%);
          opacity: 0;
          transition: opacity 0.3s;

          .image-actions {
            display: flex;
            gap: 10px;

            .el-icon {
              padding: 5px;
              font-size: 24px;
              color: #fff;
              cursor: pointer;
              background-color: rgb(255 255 255 / 20%);
              border-radius: 50%;

              &:hover {
                transform: scale(1.1);
              }
            }
          }
        }

        &:hover {
          .image-mask {
            opacity: 1;
          }
        }
      }

      .empty-upload-area {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
        min-width: 120px;
        min-height: 120px;
        padding: 20px;
        cursor: pointer;

        .upload-text {
          margin-top: 8px;
          font-size: 14px;
          color: #8c939d;
        }
      }
    }

    :deep(.el-dialog) {
      .el-dialog__body {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
      }

      .preview-image {
        max-width: 100%;
        max-height: 80vh;
        object-fit: contain;
      }
    }
  }

  :deep(.avatar-uploader .el-upload) {
    position: relative;
    overflow: hidden;
    cursor: pointer;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    transition: var(--el-transition-duration-fast);
  }

  :deep(.avatar-uploader .el-upload:hover) {
    border-color: var(--el-color-primary);
  }

  :deep(.el-icon.avatar-uploader-icon) {
    font-size: 28px;
    color: #8c939d;
    text-align: center;
  }
</style>
