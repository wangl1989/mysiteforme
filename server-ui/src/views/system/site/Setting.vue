<template>
  <div class="main-content">
    <PageWrapper title="系统设置">
      <div class="system-form-wrapper">
        <div class="action-buttons">
          <el-button
            v-if="!isEdit"
            type="primary"
            v-auth="'site_edit'"
            @click="handleAction('edit')"
            >编辑</el-button
          >
          <template v-else>
            <el-button type="success" v-auth="'site_save'" @click="handleAction('save')"
              >保存</el-button
            >
            <el-button v-auth="'site_cancle'" @click="handleAction('cancel')">取消</el-button>
          </template>
        </div>

        <el-form :model="systemData" :disabled="!isEdit" label-width="120px" label-position="left">
          <!-- 使用固定高度的容器包裹tabs -->
          <div class="tabs-container">
            <el-tabs>
              <!-- 基础设置 -->
              <el-tab-pane label="基础设置">
                <el-form-item label="网站名称">
                  <el-input v-model="systemData.name" />
                </el-form-item>
                <el-form-item label="网站网址">
                  <el-input v-model="systemData.url" />
                </el-form-item>
                <el-form-item label="系统版本">
                  <el-input v-model="systemData.version" />
                </el-form-item>
                <el-form-item label="系统作者">
                  <el-input v-model="systemData.author" />
                </el-form-item>
                <el-form-item label="WebServiceKey">
                  <el-input v-model="systemData.webServicekey" />
                </el-form-item>
                <el-form-item label="文件上传方式">
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
                <el-form-item label="文件上传限制">
                  <el-input-number v-model="systemData.maxUpload" :min="1" :max="100" />
                  <span class="unit-text">MB</span>
                </el-form-item>
                <el-form-item label="LOGO">
                  <upload-img
                    v-model="systemData.logo"
                    :disabled="!isEdit"
                    :image-url="getImgUrl(systemData.logo)"
                    :max-size="systemData.maxUpload"
                    width="120px"
                    min-width="120px"
                  />
                </el-form-item>
                <el-form-item label="作者头像">
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
              <el-tab-pane label="留言设置">
                <el-form-item label="开启留言">
                  <el-switch v-model="systemData.openMessage" />
                </el-form-item>
                <el-form-item label="匿名留言">
                  <el-switch v-model="systemData.isNoName" />
                </el-form-item>
              </el-tab-pane>

              <!-- 联系方式 -->
              <el-tab-pane label="联系方式">
                <el-form-item label="邮箱">
                  <el-input v-model="systemData.email" />
                </el-form-item>
                <el-form-item label="QQ">
                  <el-input v-model="systemData.qq" />
                </el-form-item>
                <el-form-item label="Github">
                  <el-input v-model="systemData.github" />
                </el-form-item>
                <el-form-item label="Gitee">
                  <el-input v-model="systemData.git" />
                </el-form-item>
                <el-form-item label="微博">
                  <el-input v-model="systemData.weibo" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="systemData.phone" />
                </el-form-item>
                <el-form-item label="地址">
                  <el-input v-model="systemData.address" />
                </el-form-item>
                <el-form-item label="网站备案号">
                  <el-input v-model="systemData.record" />
                </el-form-item>
              </el-tab-pane>

              <!-- SEO设置 -->
              <el-tab-pane label="SEO设置">
                <el-form-item label="默认关键字">
                  <el-input v-model="systemData.keywords" type="textarea" :rows="4" />
                </el-form-item>
                <el-form-item label="网站描述">
                  <el-input v-model="systemData.description" type="textarea" :rows="4" />
                </el-form-item>
                <el-form-item label="版权信息">
                  <el-input v-model="systemData.powerby" />
                </el-form-item>
              </el-tab-pane>

              <!-- 服务器信息 -->
              <el-tab-pane label="服务器信息">
                <el-form-item label="服务器环境">
                  <el-radio-group v-model="systemData.server" size="large">
                    <el-radio-button :value="'Windows'">Windows</el-radio-button>
                    <el-radio-button :value="'Linux'">Linux</el-radio-button>
                    <el-radio-button :value="'macOS'">macOS</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="数据库版本">
                  <el-input v-model="systemData.myDatabase" />
                </el-form-item>
              </el-tab-pane>
            </el-tabs>
          </div>

          <!-- 分隔线 -->
          <div class="divider">
            <span>个人简介设置</span>
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
      console.error('获取上传类型列表失败', error)
      ElMessage.error('获取上传类型列表失败')
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
      console.error('获取系统设置失败', error)
      ElMessage.error('获取系统设置失败')
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
            ElMessage.success('保存成功')
            isEdit.value = false
            getSystemData() // 重新获取最新数据
          } else {
            ElMessage.error(result.message)
          }
        } catch (error) {
          console.error('保存失败', error)
          ElMessage.error('保存失败')
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
