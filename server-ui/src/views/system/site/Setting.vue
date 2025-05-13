<template>
  <div class="main-content">
    <PageWrapper :title="$t('site.title')">
      <div class="system-form-wrapper">
        <div class="action-buttons">
          <el-button
            v-if="!isEdit"
            type="primary"
            v-auth="'site_edit'"
            @click="handleAction('edit')"
            >{{ $t('site.button.edit') }}</el-button
          >
          <template v-else>
            <el-button type="success" v-auth="'site_save'" @click="handleAction('save')">{{
              $t('site.button.save')
            }}</el-button>
            <el-button v-auth="'site_cancle'" @click="handleAction('cancel')">{{
              $t('site.button.cancel')
            }}</el-button>
          </template>
        </div>

        <el-form :model="systemData" :disabled="!isEdit" label-width="120px" label-position="left">
          <!-- 使用固定高度的容器包裹tabs -->
          <div class="tabs-container">
            <el-tabs>
              <!-- 基础设置 -->
              <el-tab-pane :label="$t('site.tabs.basic')">
                <el-form-item :label="$t('site.form.websiteName')">
                  <el-input v-model="systemData.name" />
                </el-form-item>
                <el-form-item :label="$t('site.form.websiteUrl')">
                  <el-input v-model="systemData.url" />
                </el-form-item>
                <el-form-item :label="$t('site.form.version')">
                  <el-input v-model="systemData.version" />
                </el-form-item>
                <el-form-item :label="$t('site.form.author')">
                  <el-input v-model="systemData.author" />
                </el-form-item>
                <el-form-item :label="$t('site.form.webServiceKey')">
                  <el-input v-model="systemData.webServicekey" />
                </el-form-item>
                <el-form-item :label="$t('site.form.uploadType')">
                  <el-radio-group v-model="systemData.fileUploadType" size="large">
                    <el-tooltip
                      placement="top"
                      v-for="item in uploadTypes"
                      :key="item.typeCode"
                      :content="item.remarks"
                    >
                      <el-radio-button :value="item.typeCode">
                        {{ item.typeCode }}
                      </el-radio-button>
                    </el-tooltip>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('site.form.uploadLimit')">
                  <el-input-number v-model="systemData.maxUpload" :min="1" :max="100" />
                  <span class="unit-text">{{ $t('site.form.unit.mb') }}</span>
                </el-form-item>
                <el-form-item :label="$t('site.form.logo')">
                  <upload-img
                    v-model="systemData.logo"
                    :disabled="!isEdit"
                    :image-url="getImgUrl(systemData.logo)"
                    :max-size="systemData.maxUpload"
                    width="120px"
                    min-width="120px"
                  />
                </el-form-item>
                <el-form-item :label="$t('site.form.authorAvatar')">
                  <upload-img
                    v-model="systemData.authorIcon"
                    :disabled="!isEdit"
                    :image-url="getImgUrl(systemData.authorIcon)"
                    :max-size="systemData.maxUpload"
                    width="120px"
                    min-width="120px"
                  />
                </el-form-item>
              </el-tab-pane>

              <!-- 留言设置 -->
              <el-tab-pane :label="$t('site.tabs.comment')">
                <el-form-item :label="$t('site.form.enableComment')">
                  <el-switch v-model="systemData.openMessage" />
                </el-form-item>
                <el-form-item :label="$t('site.form.anonymousComment')">
                  <el-switch v-model="systemData.isNoName" />
                </el-form-item>
              </el-tab-pane>

              <!-- 联系方式 -->
              <el-tab-pane :label="$t('site.tabs.contact')">
                <el-form-item :label="$t('site.form.email')">
                  <el-input v-model="systemData.email" />
                </el-form-item>
                <el-form-item :label="$t('site.form.qq')">
                  <el-input v-model="systemData.qq" />
                </el-form-item>
                <el-form-item :label="$t('site.form.github')">
                  <el-input v-model="systemData.github" />
                </el-form-item>
                <el-form-item :label="$t('site.form.gitee')">
                  <el-input v-model="systemData.git" />
                </el-form-item>
                <el-form-item :label="$t('site.form.weibo')">
                  <el-input v-model="systemData.weibo" />
                </el-form-item>
                <el-form-item :label="$t('site.form.phone')">
                  <el-input v-model="systemData.phone" />
                </el-form-item>
                <el-form-item :label="$t('site.form.address')">
                  <el-input v-model="systemData.address" />
                </el-form-item>
                <el-form-item :label="$t('site.form.recordNumber')">
                  <el-input v-model="systemData.record" />
                </el-form-item>
              </el-tab-pane>

              <!-- SEO设置 -->
              <el-tab-pane :label="$t('site.tabs.seo')">
                <el-form-item :label="$t('site.form.defaultKeywords')">
                  <el-input v-model="systemData.keywords" type="textarea" :rows="4" />
                </el-form-item>
                <el-form-item :label="$t('site.form.description')">
                  <el-input v-model="systemData.description" type="textarea" :rows="4" />
                </el-form-item>
                <el-form-item :label="$t('site.form.copyright')">
                  <el-input v-model="systemData.powerby" />
                </el-form-item>
              </el-tab-pane>

              <!-- 服务器信息 -->
              <el-tab-pane :label="$t('site.tabs.server')">
                <el-form-item :label="$t('site.form.serverEnvironment')">
                  <el-radio-group v-model="systemData.server" size="large">
                    <el-radio-button :value="'Windows'">{{
                      $t('site.form.serverTypes.windows')
                    }}</el-radio-button>
                    <el-radio-button :value="'Linux'">{{
                      $t('site.form.serverTypes.linux')
                    }}</el-radio-button>
                    <el-radio-button :value="'macOS'">{{
                      $t('site.form.serverTypes.macos')
                    }}</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('site.form.databaseVersion')">
                  <el-input v-model="systemData.myDatabase" />
                </el-form-item>
              </el-tab-pane>
            </el-tabs>
          </div>

          <!-- 分隔线 -->
          <div class="divider">
            <span>{{ $t('site.tabs.profile') }}</span>
          </div>

          <!-- 将个人简介移出el-tabs，避免警告 -->
          <ArtWangEditor v-model="systemData.remarks" :disabled="!isEdit" />
        </el-form>
      </div>
    </PageWrapper>
  </div>
</template>

<script setup lang="ts">
  import { reactive, ref, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { SystemService } from '@/api/systemApi'
  import { SystemRecord, SiteUploadTypeResponse } from '@/api/model/systemModel'
  import PageWrapper from '@/components/Page/PageWrapper.vue'
  import UploadImg from '@/components/Upload/UploadImg.vue'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  // 表单编辑状态
  const isEdit = ref(false)

  // 文件上传类型列表
  const uploadTypes = ref<SiteUploadTypeResponse[]>([])

  // 系统数据
  const systemData = reactive<SystemRecord>({
    id: 0,
    name: '',
    url: '',
    openMessage: true,
    isNoName: false,
    version: '',
    author: '',
    authorIcon: '',
    fileUploadType: '',
    weibo: '',
    qq: '',
    git: '',
    github: '',
    phone: '',
    email: '',
    address: '',
    logo: '',
    webServicekey: '',
    server: 'Linux',
    myDatabase: '',
    maxUpload: 10,
    keywords: '',
    description: '',
    powerby: '',
    record: '',
    remarks: ''
  })

  // 原始数据（用于取消编辑时恢复）
  let originalData = {} as SystemRecord

  // 获取文件上传类型列表
  const getUploadTypes = async () => {
    try {
      const { data } = await SystemService.uploadTypeList()
      if (data) {
        uploadTypes.value = Array.isArray(data) ? data : [data]
      }
    } catch (error) {
      console.error(t('site.message.getUploadTypesFailed'), error)
      ElMessage.error(t('site.message.getUploadTypesFailed'))
    }
  }

  // 获取系统数据
  const getSystemData = async () => {
    try {
      const { data } = await SystemService.systemRecord()
      if (data) {
        Object.assign(systemData, data)
        // 备份原始数据
        originalData = JSON.parse(JSON.stringify(data))
      }
    } catch (error) {
      console.error(t('site.message.getSystemDataFailed'), error)
      ElMessage.error(t('site.message.getSystemDataFailed'))
    }
  }

  // 获取图片url
  const getImgUrl = (url: string | undefined) => {
    if (url && url.startsWith('upload')) {
      return `${import.meta.env.VITE_API_URL}/` + url
    }
    return url
  }

  // 处理操作按钮
  const handleAction = async (action: string) => {
    switch (action) {
      case 'edit':
        isEdit.value = true
        break
      case 'save':
        try {
          const result = await SystemService.editSystem(systemData)
          if (result.success) {
            ElMessage.success(t('site.message.saveSuccess'))
            isEdit.value = false
            getSystemData() // 重新获取最新数据
          } else {
            ElMessage.error(result.message || t('site.message.saveFailed'))
          }
        } catch (error) {
          console.error(t('site.message.saveFailed'), error)
          ElMessage.error(t('site.message.saveFailed'))
        }
        break
      case 'cancel':
        // 恢复原始数据
        Object.assign(systemData, originalData)
        isEdit.value = false
        break
    }
  }

  onMounted(() => {
    getSystemData()
    getUploadTypes() // 获取上传类型列表
  })
</script>

<style lang="scss" scoped>
  .system-form-wrapper {
    padding: 20px;
    background-color: #fff;
    border-radius: 4px;
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
  }

  .unit-text {
    margin-left: 10px;
    color: var(--art-text-gray-600);
  }

  .tabs-container {
    min-height: 580px; /* 设置最小高度，防止切换标签时内容高度变化影响布局 */
  }

  .divider {
    position: relative;
    height: 20px;
    margin: 30px 0;
    text-align: center;

    &::before {
      position: absolute;
      top: 50%;
      left: 0;
      z-index: 1;
      width: 100%;
      height: 1px;
      content: '';
      background-color: #dcdfe6;
    }

    span {
      position: relative;
      z-index: 2;
      display: inline-block;
      padding: 0 20px;
      font-size: 14px;
      color: #606266;
      background-color: #fff;
    }
  }

  .remarks-form-item {
    margin-top: 20px;
  }
</style>
