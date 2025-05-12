<!-- eslint-disable prettier/prettier -->
<template>
  <div class="upload-container">
    <div class="upload-header-card">
      <div class="upload-header">
        <div class="header-content">
          <h1 class="title">上传方式配置</h1>
          <h2 class="subtitle">可自由配置本地或云存储方式</h2>
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
            <el-icon class="el-icon--left"><Plus /></el-icon>新增上传方式
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
                  title="编辑上传方式"
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
                  已启用
                </el-tag>
              </h3>
              <p class="description">{{ item.remarks }}</p>
              <div class="upload-info">
                <div class="info-item">
                  <span class="label">存储地址：</span>
                  <span class="value">{{ item.basePath }}</span>
                </div>
                <div v-if="item.bucketName" class="info-item">
                  <span class="label">存储空间：</span>
                  <span class="value">{{ item.bucketName }}</span>
                </div>
              </div>
            </div>

            <div class="features">
              <div class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>状态：{{ item.enable ? '已启用' : '已禁用' }}</span>
              </div>
              <div class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>测试状态：{{ item.testAccess ? '连接正常' : '连接异常' }}</span>
              </div>
              <div v-if="item.enable && item.testAccess" class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>
                  测试图片：
                  <el-button
                    title="查看测试图片"
                    type="primary"
                    link
                    v-auth="'uploadtype_show_test'"
                    @click="showImg(item.id)"
                    >点我查看</el-button
                  >
                </span>
              </div>
              <div class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>存储目录：{{ item.dir || '-' }}</span>
              </div>
              <div v-if="item.endpoint" class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>地域节点：{{ item.endpoint }}</span>
              </div>
              <div v-if="item.region" class="feature-item">
                <el-icon class="available">
                  <Check />
                </el-icon>
                <span>区域：{{ item.region }}</span>
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
                {{ item.enable ? '暂时禁用' : '立即启用' }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 添加/编辑上传方式对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑上传方式' : '新增上传方式'"
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
        <el-form-item label="上传类型" prop="type">
          <el-radio-group v-model="formData.type" :disabled="isEdit">
            <el-radio :value="UploadTypeEnum.LOCAL">本地上传</el-radio>
            <el-radio :value="UploadTypeEnum.QINIU">七牛云</el-radio>
            <el-radio :value="UploadTypeEnum.OSS">阿里云OSS</el-radio>
            <el-radio :value="UploadTypeEnum.COS">腾讯云COS</el-radio>
            <el-radio :value="UploadTypeEnum.BITIFUL">缤纷云</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 基础字段 - 所有类型都有 -->
        <el-form-item label="启用状态" prop="enable" v-if="formData.type !== UploadTypeEnum.LOCAL">
          <el-switch
            v-model="formData.enable"
            :active-text="formData.enable ? '启用' : '禁用'"
            :inactive-text="formData.enable ? '启用' : '禁用'"
          />
        </el-form-item>

        <el-form-item label="HTTP前缀" prop="basePath">
          <el-input
            v-model="formData.basePath"
            placeholder="例如：http://example.com 或 https://example.com"
          />
          <div class="form-tip">公开访问的URL前缀，包括http://或https://</div>
        </el-form-item>

        <el-form-item label="存储目录" prop="dir">
          <el-input v-model="formData.dir" placeholder="例如：images/ 或 uploads/" />
          <div class="form-tip">存储目录，不可以为空</div>
        </el-form-item>

        <!-- 云存储共有字段 -->
        <template v-if="formData.type !== UploadTypeEnum.LOCAL">
          <el-form-item label="Bucket名称" prop="bucketName">
            <el-input v-model="formData.bucketName" placeholder="存储空间名称" />
          </el-form-item>

          <el-form-item label="AccessKey" prop="accessKey">
            <el-input
              v-model="formData.accessKey"
              placeholder="云存储访问密钥ID"
              :show-password="isEdit"
            />
          </el-form-item>

          <el-form-item label="SecretKey" prop="secretKey">
            <el-input v-model="formData.secretKey" placeholder="云存储访问密钥密码" show-password />
            <div v-if="isEdit" class="form-tip">如不修改，请留空</div>
          </el-form-item>
        </template>

        <!-- 阿里云OSS特有字段 -->
        <template v-if="formData.type === UploadTypeEnum.OSS">
          <el-form-item label="地域节点" prop="endpoint">
            <el-input v-model="formData.endpoint" placeholder="例如：oss-cn-beijing.aliyuncs.com" />
          </el-form-item>
        </template>

        <!-- 腾讯云COS特有字段 -->
        <template v-if="formData.type === UploadTypeEnum.COS">
          <el-form-item label="地域" prop="region">
            <el-input v-model="formData.region" placeholder="例如：ap-guangzhou" />
          </el-form-item>
        </template>

        <!-- 缤纷云特有字段 -->
        <template v-if="formData.type === UploadTypeEnum.BITIFUL">
          <el-form-item label="地域节点" prop="endpoint">
            <el-input v-model="formData.endpoint" placeholder="例如：endpoint.example.com" />
          </el-form-item>

          <el-form-item label="区域" prop="region">
            <el-input v-model="formData.region" placeholder="区域代码" />
          </el-form-item>
        </template>

        <el-form-item label="备注" prop="remarks">
          <el-input
            type="textarea"
            v-model="formData.remarks"
            placeholder="请输入备注信息"
            :rows="3"
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imgDialogVisible" title="图片预览" width="500px" destroy-on-close>
      <div class="img-preview">
        <img v-if="currentImgUrl" :src="currentImgUrl" alt="图片预览" class="preview-img" />
        <div v-else class="no-image">暂无图片</div>

        <div class="upload-container">
          <el-upload
            title="上传测试图片"
            class="upload-demo"
            :action="''"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <el-button type="primary" :loading="imgUploadLoading">选择图片</el-button>
          </el-upload>
          <el-button
            type="success"
            :disabled="!selectedFile"
            :loading="imgUploadLoading"
            @click="handleUploadFile"
            style="margin-left: 10px"
          >
            上传图片
          </el-button>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="imgDialogVisible = false">关闭</el-button>
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
    type: [{ required: true, message: '请选择上传类型', trigger: 'change' }],
    basePath: [
      { required: true, message: '请输入HTTP前缀', trigger: 'blur' },
      {
        pattern: /^https?:\/\//,
        message: '请输入正确的HTTP前缀，以http://或https://开头',
        trigger: 'blur'
      }
    ],
    dir: [
      { required: true, message: '请输入上传目录', trigger: 'blur' },
      {
        pattern: /^[a-zA-Z][a-zA-Z/]*\/$/,
        message: '目录名称只能包含字母、斜杠(/),且首字母不能是斜杠,必须是斜杠(/)结尾',
        trigger: 'blur'
      }
    ],
    bucketName: [
      {
        validator: (rule, value, callback) => {
          if (formData.type !== UploadTypeEnum.LOCAL && !value) {
            callback(new Error('请输入Bucket名称'))
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
            callback(new Error('请输入AccessKey'))
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
            callback(new Error('请输入SecretKey'))
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
            callback(new Error('请输入地域节点'))
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
            callback(new Error('请输入区域'))
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
      [UploadTypeEnum.LOCAL]: '本地上传',
      [UploadTypeEnum.QINIU]: '七牛云存储',
      [UploadTypeEnum.OSS]: '阿里云OSS',
      [UploadTypeEnum.COS]: '腾讯云COS',
      [UploadTypeEnum.BITIFUL]: '缤纷云存储'
    }
    return type ? titleMap[type] || '未知上传方式' : '未知上传方式'
  }

  // 获取上传方式描述
  // const getUploadDescription = (type: string | null) => {
  //   const descMap: Record<string, string> = {
  //     [UploadTypeEnum.LOCAL]: '使用服务器本地存储，无需额外配置，适合小型应用',
  //     [UploadTypeEnum.QINIU]: '七牛云对象存储服务，提供高效稳定的云存储服务',
  //     [UploadTypeEnum.OSS]: '阿里云对象存储服务，提供海量、安全、低成本的云存储服务',
  //     [UploadTypeEnum.COS]: '腾讯云对象存储服务，稳定、安全、高效',
  //     [UploadTypeEnum.BITIFUL]: '缤纷云提供的云存储服务'
  //   }
  //   return type ? descMap[type] || '未知上传方式' : '未知上传方式'
  // }

  // 加载上传方式列表数据
  const loadUploadList = async () => {
    loading.value = true
    try {
      const res = await UploadService.getUploadBaseInfoList(queryParams)
      if (res.success) {
        uploadList.value = res.data.records
        totalCount.value = res.data.total
      } else {
        ElMessage.error(res.message || '获取上传方式列表失败')
      }
    } catch (error) {
      console.error('获取上传方式列表失败:', error)
      ElMessage.error('获取上传方式列表时发生错误')
    } finally {
      loading.value = false
    }
  }

  const showImg = async (id: number) => {
    // 查找当前ID对应的上传配置
    const currentItem = uploadList.value.find((item) => item.id === id)
    if (!currentItem) {
      ElMessage.error('找不到对应的上传配置')
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
      ElMessage.error('只能上传图片文件!')
      return false
    }
    if (!isLt2M) {
      ElMessage.error('图片大小不能超过 2MB!')
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
      ElMessage.warning('请先选择图片')
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
        ElMessage.success('上传成功')

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
        ElMessage.error(res.message || '上传失败')
      }
    } catch (error) {
      console.error('上传图片失败:', error)
      ElMessage.error('上传图片时发生错误')
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
        ElMessage.success(`${item.enable ? '禁用' : '启用'}成功`)
        loadUploadList() // 重新加载数据
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      console.error('切换状态失败:', error)
      ElMessage.error('切换状态时发生错误')
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
            ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
            dialogVisible.value = false
            loadUploadList() // 重新加载数据
          } else {
            ElMessage.error(res.message || '操作失败')
          }
        } catch (error) {
          console.error(isEdit.value ? '编辑失败:' : '新增失败:', error)
          ElMessage.error(isEdit.value ? '编辑时发生错误' : '新增时发生错误')
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
