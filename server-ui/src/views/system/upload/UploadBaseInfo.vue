<!-- eslint-disable prettier/prettier -->
<template>
  <div class="upload-container">
    <div class="upload-header-card">
      <div class="upload-header">
        <div class="header-content">
          <h1 class="title">{{ $t('upload.baseInfo.title') }}</h1>
          <h2 class="subtitle">{{ $t('upload.baseInfo.subtitle') }}</h2>
        </div>
        <div class="header-action">
          <el-button
            v-if="canAddMore"
            type="primary"
            v-auth="'uploadtype_add'"
            class="add-btn"
            @click="handleAddUpload"
            v-ripple
          >
            <el-icon class="el-icon--left"><Plus /></el-icon>{{ $t('upload.baseInfo.addButton') }}
          </el-button>
        </div>
      </div>
    </div>

    <div class="upload-cards">
      <el-row :gutter="20" justify="center">
        <el-col v-for="item in uploadList" :key="item.id" :xs="24" :sm="12" :md="6">
          <el-card class="upload-card" :class="{ enabled: item.enable }" shadow="never">
            <div class="card-header">
              <div class="header-actions">
                <span
                  :title="$t('upload.baseInfo.card.edit')"
                  class="edit-icon"
                  v-auth="'uploadtype_edit'"
                  @click="handleEditUpload(item)"
                >
                  <i class="iconfont-sys" v-html="'&#xe816;'"></i>
                </span>
              </div>
              <h3>
                {{ getUploadTitle(item.type) }}
                <el-tag v-if="item.enable" type="success" size="small" class="status-tag">
                  {{ $t('upload.baseInfo.card.enabled') }}
                </el-tag>
              </h3>
              <p class="description">{{ item.remarks }}</p>
              <div class="upload-info">
                <div class="info-item">
                  <span class="label">{{ $t('upload.baseInfo.card.storageAddress') }}：</span>
                  <span class="value">{{ item.basePath }}</span>
                </div>
                <div v-if="item.bucketName" class="info-item">
                  <span class="label">{{ $t('upload.baseInfo.card.storageSpace') }}：</span>
                  <span class="value">{{ item.bucketName }}</span>
                </div>
              </div>
            </div>

            <div class="features">
              <div class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>{{ $t('upload.baseInfo.card.status') }}：{{ item.enable ? $t('upload.baseInfo.card.enabled_status') : $t('upload.baseInfo.card.disabled_status') }}</span>
              </div>
              <div class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>{{ $t('upload.baseInfo.card.testStatus') }}：{{ item.testAccess ? $t('upload.baseInfo.card.connectionNormal') : $t('upload.baseInfo.card.connectionError') }}</span>
              </div>
              <div v-if="item.enable && item.testAccess" class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>
                  {{ $t('upload.baseInfo.card.testImage') }}：
                  <el-button
                    :title="$t('upload.baseInfo.card.viewTestImage')"
                    type="primary"
                    link
                    v-auth="'uploadtype_show_test'"
                    @click="showImg(item.id)"
                    >{{ $t('upload.baseInfo.card.viewTestImage') }}</el-button
                  >
                </span>
              </div>
              <div class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>{{ $t('upload.baseInfo.card.storageDir') }}：{{ item.dir || '-' }}</span>
              </div>
              <div v-if="item.endpoint" class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>{{ $t('upload.baseInfo.card.endpoint') }}：{{ item.endpoint }}</span>
              </div>
              <div v-if="item.region" class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>{{ $t('upload.baseInfo.card.region') }}：{{ item.region }}</span>
              </div>
            </div>

            <div class="card-footer" v-if="item.type !== UploadTypeEnum.LOCAL">
              <el-button
                :type="item.enable ? 'danger' : 'primary'"
                class="action-btn"
                v-auth="'uploadtype_enable'"
                @click="handleToggleStatus(item)"
                v-ripple
              >
                {{ item.enable ? $t('upload.baseInfo.card.tempDisable') : $t('upload.baseInfo.card.enable') }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 添加/编辑上传方式对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? $t('upload.baseInfo.dialog.editTitle') : $t('upload.baseInfo.dialog.addTitle')"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="uploadFormRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="upload-form"
      >
        <!-- 类型选择 - 仅新增时可选 -->
        <el-form-item :label="$t('upload.baseInfo.dialog.uploadType')" prop="type">
          <el-radio-group v-model="formData.type" :disabled="isEdit">
            <el-radio :value="UploadTypeEnum.LOCAL">{{ $t('upload.baseInfo.types.local') }}</el-radio>
            <el-radio :value="UploadTypeEnum.QINIU">{{ $t('upload.baseInfo.types.qiniu') }}</el-radio>
            <el-radio :value="UploadTypeEnum.OSS">{{ $t('upload.baseInfo.types.oss') }}</el-radio>
            <el-radio :value="UploadTypeEnum.COS">{{ $t('upload.baseInfo.types.cos') }}</el-radio>
            <el-radio :value="UploadTypeEnum.BITIFUL">{{ $t('upload.baseInfo.types.bitiful') }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 基础字段 - 所有类型都有 -->
        <el-form-item :label="$t('upload.baseInfo.dialog.statusLabel')" prop="enable" v-if="formData.type !== UploadTypeEnum.LOCAL">
          <el-switch
            v-model="formData.enable"
            :active-text="formData.enable ? $t('upload.baseInfo.dialog.enableText') : $t('upload.baseInfo.dialog.disableText')"
            :inactive-text="formData.enable ? $t('upload.baseInfo.dialog.enableText') : $t('upload.baseInfo.dialog.disableText')"
          />
        </el-form-item>

        <el-form-item :label="$t('upload.baseInfo.dialog.httpPrefix')" prop="basePath">
          <el-input
            v-model="formData.basePath"
            :placeholder="$t('upload.baseInfo.dialog.httpPrefixPlaceholder')"
          />
          <div class="form-tip">{{ $t('upload.baseInfo.dialog.httpPrefixTip') }}</div>
        </el-form-item>

        <el-form-item :label="$t('upload.baseInfo.dialog.storageDir')" prop="dir">
          <el-input v-model="formData.dir" :placeholder="$t('upload.baseInfo.dialog.storageDirPlaceholder')" />
          <div class="form-tip">{{ $t('upload.baseInfo.dialog.storageDirTip') }}</div>
        </el-form-item>

        <!-- 云存储共有字段 -->
        <template v-if="formData.type !== UploadTypeEnum.LOCAL">
          <el-form-item :label="$t('upload.baseInfo.dialog.bucketName')" prop="bucketName">
            <el-input v-model="formData.bucketName" :placeholder="$t('upload.baseInfo.dialog.bucketNamePlaceholder')" />
          </el-form-item>

          <el-form-item :label="$t('upload.baseInfo.dialog.accessKey')" prop="accessKey">
            <el-input
              v-model="formData.accessKey"
              :placeholder="$t('upload.baseInfo.dialog.accessKeyPlaceholder')"
              :show-password="isEdit"
            />
          </el-form-item>

          <el-form-item :label="$t('upload.baseInfo.dialog.secretKey')" prop="secretKey">
            <el-input v-model="formData.secretKey" :placeholder="$t('upload.baseInfo.dialog.secretKeyPlaceholder')" show-password />
            <div v-if="isEdit" class="form-tip">{{ $t('upload.baseInfo.dialog.secretKeyTip') }}</div>
          </el-form-item>
        </template>

        <!-- 阿里云OSS特有字段 -->
        <template v-if="formData.type === UploadTypeEnum.OSS">
          <el-form-item :label="$t('upload.baseInfo.dialog.endpoint')" prop="endpoint">
            <el-input v-model="formData.endpoint" :placeholder="$t('upload.baseInfo.dialog.endpointPlaceholder.oss')" />
          </el-form-item>
        </template>

        <!-- 腾讯云COS特有字段 -->
        <template v-if="formData.type === UploadTypeEnum.COS">
          <el-form-item :label="$t('upload.baseInfo.dialog.region')" prop="region">
            <el-input v-model="formData.region" :placeholder="$t('upload.baseInfo.dialog.regionPlaceholder.cos')" />
          </el-form-item>
        </template>

        <!-- 缤纷云特有字段 -->
        <template v-if="formData.type === UploadTypeEnum.BITIFUL">
          <el-form-item :label="$t('upload.baseInfo.dialog.endpoint')" prop="endpoint">
            <el-input v-model="formData.endpoint" :placeholder="$t('upload.baseInfo.dialog.endpointPlaceholder.bitiful')" />
          </el-form-item>

          <el-form-item :label="$t('upload.baseInfo.dialog.region')" prop="region">
            <el-input v-model="formData.region" :placeholder="$t('upload.baseInfo.dialog.regionPlaceholder.bitiful')" />
          </el-form-item>
        </template>

        <el-form-item :label="$t('upload.baseInfo.dialog.remarks')" prop="remarks">
          <el-input
            type="textarea"
            v-model="formData.remarks"
            :placeholder="$t('upload.baseInfo.dialog.remarksPlaceholder')"
            :rows="3"
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ $t('upload.baseInfo.dialog.cancel') }}</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">{{ $t('upload.baseInfo.dialog.confirm') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imgDialogVisible" :title="$t('upload.baseInfo.imagePreview.title')" width="500px" destroy-on-close>
      <div class="img-preview">
        <img v-if="currentImgUrl" :src="currentImgUrl" :alt="$t('upload.baseInfo.imagePreview.title')" class="preview-img" />
        <div v-else class="no-image">{{ $t('upload.baseInfo.imagePreview.noImage') }}</div>

        <div class="upload-container">
          <el-upload
            :title="$t('upload.baseInfo.imagePreview.uploadButton')"
            class="upload-demo"
            :action="''"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <el-button type="primary" :loading="imgUploadLoading">{{ $t('upload.baseInfo.imagePreview.selectImage') }}</el-button>
          </el-upload>
          <el-button
            type="success"
            :disabled="!selectedFile"
            :loading="imgUploadLoading"
            @click="handleUploadFile"
            style="margin-left: 10px"
          >
            {{ $t('upload.baseInfo.imagePreview.uploadImage') }}
          </el-button>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="imgDialogVisible = false">{{ $t('upload.baseInfo.imagePreview.close') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted } from 'vue'
  import { ElMessage, FormInstance, FormRules } from 'element-plus'
  import { Check, Plus } from '@element-plus/icons-vue'
  import { UploadService } from '@/api/uploadApi'
  import {
    UploadRecord,
    UploadListParams,
    UploadTypeEnum,
    AddUploadBaseInfoParam,
    EditUploadBaseInfoParam
  } from '@/api/model/uploadModel'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  // 上传方式列表数据
  const uploadList = ref<UploadRecord[]>([])
  const loading = ref(false)
  const totalCount = ref(0)

  // 分页参数
  const queryParams = reactive<UploadListParams>({
    page: 1,
    limit: 50 // 设置较大的值，一次性加载所有数据
  })

  // 对话框控制
  const dialogVisible = ref(false)
  const isEdit = ref(false)
  const submitLoading = ref(false)
  const uploadFormRef = ref<FormInstance>()

  // 图片预览对话框控制
  const imgDialogVisible = ref(false)
  const currentImgUrl = ref('')
  const currentUploadId = ref(0)
  const imgUploadLoading = ref(false)
  const selectedFile = ref<File | null>(null)

  // 表单数据
  const formData = reactive({
    id: 0,
    type: UploadTypeEnum.LOCAL,
    basePath: '',
    bucketName: '',
    dir: '',
    accessKey: '',
    secretKey: '',
    endpoint: '',
    testWebUrl: '',
    region: '',
    testAccess: false,
    enable: true,
    remarks: ''
  })

  // 表单验证规则
  const rules = reactive<FormRules>({
    type: [
      { required: true, message: t('upload.baseInfo.rules.type.required'), trigger: 'change' }
    ],
    basePath: [
      { required: true, message: t('upload.baseInfo.rules.basePath.required'), trigger: 'blur' },
      {
        pattern: /^https?:\/\//,
        message: t('upload.baseInfo.rules.basePath.pattern'),
        trigger: 'blur'
      }
    ],
    dir: [
      { required: true, message: t('upload.baseInfo.rules.dir.required'), trigger: 'blur' },
      {
        pattern: /^[a-zA-Z][a-zA-Z/]*\/$/,
        message: t('upload.baseInfo.rules.dir.pattern'),
        trigger: 'blur'
      }
    ],
    bucketName: [
      {
        validator: (rule, value, callback) => {
          if (formData.type !== UploadTypeEnum.LOCAL && !value) {
            callback(new Error(t('upload.baseInfo.rules.bucketName.required')))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    accessKey: [
      {
        validator: (rule, value, callback) => {
          if (formData.type !== UploadTypeEnum.LOCAL && !isEdit.value && !value) {
            callback(new Error(t('upload.baseInfo.rules.accessKey.required')))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    secretKey: [
      {
        validator: (rule, value, callback) => {
          if (formData.type !== UploadTypeEnum.LOCAL && !isEdit.value && !value) {
            callback(new Error(t('upload.baseInfo.rules.secretKey.required')))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    endpoint: [
      {
        validator: (rule, value, callback) => {
          if ([UploadTypeEnum.OSS, UploadTypeEnum.BITIFUL].includes(formData.type) && !value) {
            callback(new Error(t('upload.baseInfo.rules.endpoint.required')))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    region: [
      {
        validator: (rule, value, callback) => {
          if ([UploadTypeEnum.COS, UploadTypeEnum.BITIFUL].includes(formData.type) && !value) {
            callback(new Error(t('upload.baseInfo.rules.region.required')))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  })

  // 判断是否可以添加更多上传方式
  const canAddMore = computed(() => {
    // 获取已有的上传类型
    const existingTypes = new Set(uploadList.value.map((item) => item.type))
    // 检查是否所有的上传类型都已经存在
    const allTypesExist = Object.values(UploadTypeEnum).every((type) => existingTypes.has(type))
    return !allTypesExist
  })

  // 获取上传方式标题
  const getUploadTitle = (type: string | null) => {
    const titleMap: Record<string, string> = {
      [UploadTypeEnum.LOCAL]: t('upload.baseInfo.types.local'),
      [UploadTypeEnum.QINIU]: t('upload.baseInfo.types.qiniu'),
      [UploadTypeEnum.OSS]: t('upload.baseInfo.types.oss'),
      [UploadTypeEnum.COS]: t('upload.baseInfo.types.cos'),
      [UploadTypeEnum.BITIFUL]: t('upload.baseInfo.types.bitiful')
    }
    return type
      ? titleMap[type] || t('upload.baseInfo.types.unknown')
      : t('upload.baseInfo.types.unknown')
  }

  // 加载上传方式列表数据
  const loadUploadList = async () => {
    loading.value = true
    try {
      const res = await UploadService.getUploadBaseInfoList(queryParams)
      if (res.success) {
        uploadList.value = res.data.records
        totalCount.value = res.data.total
      } else {
        ElMessage.error(res.message || t('upload.baseInfo.message.getListFailed'))
      }
    } catch (error) {
      console.error('获取上传方式列表失败:', error)
      ElMessage.error(t('upload.baseInfo.message.getListError'))
    } finally {
      loading.value = false
    }
  }

  const showImg = async (id: number) => {
    // 查找当前ID对应的上传配置
    const currentItem = uploadList.value.find((item) => item.id === id)
    if (!currentItem) {
      ElMessage.error(t('upload.baseInfo.message.configNotFound'))
      return
    }

    // 设置当前图片URL和上传ID
    currentImgUrl.value =
      currentItem.basePath && currentItem.testWebUrl
        ? currentItem.basePath + currentItem.testWebUrl
        : ''
    currentUploadId.value = id
    selectedFile.value = null

    // 显示图片预览对话框
    imgDialogVisible.value = true
  }

  // 处理图片上传前的验证
  const beforeUpload = (file: File) => {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isImage) {
      ElMessage.error(t('upload.baseInfo.message.onlyImage'))
      return false
    }
    if (!isLt2M) {
      ElMessage.error(t('upload.baseInfo.message.imageSizeLimit'))
      return false
    }
    return true
  }

  // 处理文件选择变更
  const handleFileChange = (file: any) => {
    if (file.raw) {
      selectedFile.value = file.raw
      // 立即本地预览
      currentImgUrl.value = URL.createObjectURL(file.raw)
      URL.revokeObjectURL(file.raw)
    }
  }

  // 处理图片上传
  const handleUploadFile = async () => {
    if (!selectedFile.value) {
      ElMessage.warning(t('upload.baseInfo.message.selectImage'))
      return
    }

    imgUploadLoading.value = true

    try {
      const currentItem = uploadList.value.find((item) => item.id === currentUploadId.value)
      // 创建FormData对象
      const uploadFormData = new FormData()
      uploadFormData.append('test', selectedFile.value)
      uploadFormData.append('uploadType', currentItem?.type || '')

      // 调用上传API
      const res = await UploadService.upload(uploadFormData)

      if (res.success) {
        ElMessage.success(t('upload.baseInfo.message.uploadSuccess'))

        // 更新图片地址
        if (currentItem && res.data) {
          // 假设返回的数据中包含文件路径
          let url = res.data.url
          if (url.startsWith(currentItem.basePath as string)) {
            url = url.replaceAll(currentItem.basePath as string, '')
          }
          currentItem.testWebUrl = url
          currentImgUrl.value =
            currentItem.basePath && currentItem.testWebUrl
              ? currentItem.basePath + currentItem.testWebUrl
              : ''

          // 更新测试访问状态
          const params: EditUploadBaseInfoParam = {
            id: Number(currentItem.id),
            type: currentItem.type || '',
            basePath: currentItem.basePath || '',
            bucketName: currentItem.bucketName || '',
            dir: currentItem.dir || '',
            accessKey: '',
            secretKey: '',
            endpoint: currentItem.endpoint || '',
            region: currentItem.region || '',
            testWebUrl: currentItem.testWebUrl || '',
            testAccess: true,
            enable: !!currentItem.enable,
            remarks: currentItem.remarks
          }

          await UploadService.updateUploadBaseInfo(params)
          loadUploadList() // 重新加载数据
        }
      } else {
        ElMessage.error(res.message || t('upload.baseInfo.message.uploadFailed'))
      }
    } catch (error) {
      console.error('上传图片失败:', error)
      ElMessage.error(t('upload.baseInfo.message.uploadError'))
    } finally {
      imgUploadLoading.value = false
      selectedFile.value = null
    }
  }

  // 处理切换启用状态
  const handleToggleStatus = async (item: UploadRecord) => {
    try {
      // 构建更新参数
      const params: EditUploadBaseInfoParam = {
        id: Number(item.id),
        type: item.type,
        basePath: item.basePath || '',
        bucketName: item.bucketName,
        dir: item.dir,
        accessKey: '',
        secretKey: '', // 不传递密钥
        endpoint: item.endpoint,
        region: item.region,
        testAccess: item.testAccess,
        testWebUrl: item.testWebUrl,
        enable: !item.enable, // 切换状态
        remarks: item.remarks //备注
      }

      const res = await UploadService.updateUploadBaseInfo(params)
      if (res.success) {
        ElMessage.success(
          item.enable
            ? t('upload.baseInfo.message.toggleSuccess')
            : t('upload.baseInfo.message.toggleEnableSuccess')
        )
        loadUploadList() // 重新加载数据
      } else {
        ElMessage.error(res.message || t('upload.baseInfo.message.toggleFailed'))
      }
    } catch (error) {
      console.error('切换状态失败:', error)
      ElMessage.error(t('upload.baseInfo.message.toggleError'))
    }
  }

  // 处理新增上传方式
  const handleAddUpload = () => {
    isEdit.value = false
    dialogVisible.value = true

    // 重置表单数据
    Object.assign(formData, {
      id: 0,
      type: UploadTypeEnum.LOCAL,
      basePath: 'http://',
      bucketName: '',
      dir: '',
      localWindowUrl: '',
      localLinuxUrl: '',
      accessKey: '',
      secretKey: '',
      endpoint: '',
      region: '',
      testAccess: false,
      testWebUrl: '',
      enable: true,
      remarks: ''
    })
  }

  // 处理编辑上传方式
  const handleEditUpload = (item: UploadRecord) => {
    isEdit.value = true
    dialogVisible.value = true

    // 填充表单数据
    Object.assign(formData, {
      id: Number(item.id),
      type: item.type,
      basePath: item.basePath || '',
      bucketName: item.bucketName || '',
      dir: item.dir || '',
      accessKey: '', // 不回填accessKey
      secretKey: '', // 不回填secretKey
      endpoint: item.endpoint || '',
      region: item.endpoint || '',
      testAccess: !!item.testAccess,
      testWebUrl: item.testWebUrl,
      enable: !!item.enable,
      remarks: item.remarks || ''
    })
  }

  // 提交表单
  const submitForm = async () => {
    if (!uploadFormRef.value) return

    await uploadFormRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true

        try {
          let res

          if (isEdit.value) {
            // 编辑
            const params: EditUploadBaseInfoParam = {
              id: formData.id,
              type: formData.type,
              basePath: formData.basePath,
              bucketName: formData.bucketName,
              dir: formData.dir,
              accessKey: formData.accessKey,
              secretKey: formData.secretKey,
              endpoint: formData.endpoint,
              region: formData.region,
              testAccess: formData.testAccess,
              testWebUrl: formData.testWebUrl,
              enable: formData.enable,
              remarks: formData.remarks
            }

            res = await UploadService.updateUploadBaseInfo(params)
          } else {
            // 新增
            const params: AddUploadBaseInfoParam = {
              type: formData.type,
              basePath: formData.basePath,
              bucketName: formData.bucketName,
              dir: formData.dir,
              accessKey: formData.accessKey,
              secretKey: formData.secretKey,
              endpoint: formData.endpoint,
              region: formData.region,
              testAccess: formData.testAccess,
              testWebUrl: formData.testWebUrl,
              enable: formData.enable,
              remarks: formData.remarks
            }

            res = await UploadService.addUploadBaseInfo(params)
          }

          if (res.success) {
            ElMessage.success(
              isEdit.value
                ? t('upload.baseInfo.message.editSuccess')
                : t('upload.baseInfo.message.addSuccess')
            )
            dialogVisible.value = false
            loadUploadList() // 重新加载数据
          } else {
            ElMessage.error(res.message || t('upload.baseInfo.message.operationFailed'))
          }
        } catch (error) {
          console.error(isEdit.value ? '编辑失败:' : '新增失败:', error)
          ElMessage.error(
            isEdit.value
              ? t('upload.baseInfo.message.editError')
              : t('upload.baseInfo.message.addError')
          )
        } finally {
          submitLoading.value = false
        }
      }
    })
  }

  // 组件挂载时加载数据
  onMounted(() => {
    loadUploadList()
  })
</script>

<style lang="scss" scoped>
  .upload-container {
    padding: 2rem 1rem;

    .upload-header-card {
      padding: 1.5rem;
      margin-bottom: 2rem;
      background-color: var(--el-bg-color);
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgb(0 0 0 / 5%);
    }

    .upload-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .header-content {
        .title {
          margin: 0 0 0.5rem;
          font-size: 1.75rem;
          font-weight: 600;
          color: var(--el-color-primary);
        }

        .subtitle {
          margin: 0;
          font-size: 1rem;
          font-weight: 400;
          color: var(--el-text-color-secondary);
        }
      }

      .header-action {
        .add-btn {
          display: inline-flex;
          align-items: center;
          height: 40px;
          font-weight: 500;
        }
      }
    }

    .upload-cards {
      margin-top: 30px;

      .el-col {
        margin-bottom: 20px;
      }

      .upload-card {
        position: relative;
        display: flex;
        flex-direction: column;
        height: 100%;
        border-radius: 10px;

        &.enabled {
          border: 2px solid var(--el-color-primary);
        }

        .card-header {
          position: relative;
          margin-bottom: 20px;

          .header-actions {
            position: absolute;
            top: 0;
            right: 0;

            .edit-icon {
              font-size: 18px;
              color: var(--el-color-primary);
              cursor: pointer;

              &:hover {
                color: var(--el-color-primary-light-3);
              }
            }
          }

          h3 {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            font-size: 1.3rem;
            color: var(--art-text-gray-900) !important;

            .status-tag {
              margin-left: 8px;
              transform: scale(0.85);
              transform-origin: left;
            }
          }

          .description {
            display: -webkit-box;
            height: 40px;
            padding-bottom: 20px;
            margin-bottom: 20px;
            overflow: hidden;
            font-size: 14px;
            color: var(--art-text-gray-600);
            text-overflow: ellipsis;
            border-bottom: 1px solid var(--art-border-color);
            -webkit-box-orient: vertical;
          }

          .upload-info {
            margin-top: 10px;

            .info-item {
              margin-bottom: 5px;
              font-size: 14px;

              .label {
                color: var(--art-text-gray-600);
              }

              .value {
                color: var(--art-text-gray-900);
                word-break: break-all;
              }
            }
          }
        }

        .features {
          flex-grow: 1;
          margin-bottom: 20px;

          .feature-item {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            font-size: 14px;

            .el-icon {
              margin-right: 10px;

              &.available {
                color: var(--el-color-primary);
              }

              &.unavailable {
                color: var(--el-color-danger);
              }
            }
          }
        }

        .card-footer {
          margin-top: auto;
          text-align: center;

          .action-btn {
            width: 100%;
            height: 40px;
          }
        }
      }
    }
  }

  .form-tip {
    margin-top: 4px;
    font-size: 12px;
    line-height: 1.4;
    color: var(--art-text-gray-500);
  }

  :deep(.el-dialog__body) {
    padding-top: 10px;
  }

  .img-preview {
    display: flex;
    flex-direction: column;
    gap: 20px;
    align-items: center;

    .preview-img {
      max-width: 100%;
      max-height: 300px;
      object-fit: contain;
      border: 1px solid var(--art-border-color);
      border-radius: 4px;
    }

    .no-image {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 200px;
      color: var(--art-text-gray-400);
      border: 1px dashed var(--art-border-color);
      border-radius: 4px;
    }

    .upload-container {
      display: flex;
      justify-content: center;
      width: 100%;
      margin-top: 16px;
    }
  }

  @media only screen and (width <= 768px) {
    .upload-container {
      padding: 1rem 0;
    }

    .upload-header {
      flex-direction: column;
      gap: 1rem;
      align-items: flex-start;

      .header-content {
        width: 100%;
        text-align: center;

        .title {
          font-size: 1.5rem;
        }
      }

      .header-action {
        width: 100%;

        .add-btn {
          width: 100%;
        }
      }
    }
  }
</style>
