<template>
  <div class="page-content">
    <el-row :gutter="15" class="mb-4">
      <el-col :xs="19" :sm="12" :lg="6">
        <el-input
          :title="$t('resources.search.title')"
          v-model="queryParams.fileName"
          :placeholder="$t('resources.search.fileNamePlaceholder')"
        ></el-input>
      </el-col>
      <el-col :xs="4" :sm="6" :lg="3">
        <el-select
          v-model="queryParams.source"
          :placeholder="$t('resources.search.source')"
          clearable
        >
          <el-option :label="$t('resources.sourceType.local')" value="local"></el-option>
          <el-option :label="$t('resources.sourceType.oss')" value="oss"></el-option>
          <el-option :label="$t('resources.sourceType.qiniu')" value="qiniu"></el-option>
        </el-select>
      </el-col>
      <el-col :xs="4" :sm="6" :lg="3">
        <el-select
          v-model="queryParams.fileType"
          :placeholder="$t('resources.search.fileType')"
          clearable
        >
          <el-option :label="$t('resources.fileType.image')" value="image"></el-option>
          <el-option :label="$t('resources.fileType.document')" value="document"></el-option>
          <el-option :label="$t('resources.fileType.video')" value="video"></el-option>
          <el-option :label="$t('resources.fileType.audio')" value="audio"></el-option>
          <el-option :label="$t('resources.fileType.other')" value="other"></el-option>
        </el-select>
      </el-col>
      <el-col :xs="4" :sm="12" :lg="4">
        <el-button type="primary" v-ripple @click="handleQuery">
          {{ $t('resources.search.search') }}
        </el-button>
        <el-button v-ripple @click="resetQuery">{{ $t('resources.search.reset') }}</el-button>
      </el-col>
    </el-row>

    <art-table
      :data="ResourcesList"
      v-loading="loading"
      pagination
      :currentPage="pagination.current"
      :pageSize="pagination.size"
      :total="pagination.total"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    >
      <el-table-column :label="$t('resources.table.fileName')" prop="fileName" />
      <el-table-column :label="$t('resources.table.source')" prop="source">
        <template #default="scope">
          <el-tag v-if="scope.row.source === 'local'">
            {{ $t('resources.sourceType.local') }}
          </el-tag>
          <el-tag v-else-if="scope.row.source === 'oss'" type="success">
            {{ $t('resources.sourceType.oss') }}
          </el-tag>
          <el-tag v-else-if="scope.row.source === 'qiniu'" type="warning">
            {{ $t('resources.sourceType.qiniu') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('resources.table.fileType')" prop="fileType" />
      <el-table-column :label="$t('resources.table.fileSize')" prop="fileSize" />
      <el-table-column :label="$t('resources.table.createDate')" prop="createDate" />
      <el-table-column :label="$t('resources.table.operation')" width="220" fixed="right">
        <template #default="scope">
          <ArtButtonTable
            :title="$t('resources.button.previewTitle')"
            :text="$t('resources.button.preview')"
            icon="&#xe700;"
            iconColor="#67C23A"
            @click="previewResource(scope.row)"
          />
          <ArtButtonTable
            :title="$t('resources.button.copyUrlTitle')"
            :text="$t('resources.button.copyUrl')"
            icon="&#xe63a;"
            iconColor="#125232"
            @click="copyUrl(scope.row.webUrl)"
          />
          <!-- <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button> -->
        </template>
      </el-table-column>
    </art-table>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="$t('resources.preview.title')" width="60%">
      <div class="preview-container">
        <img
          v-if="isImageFile"
          :src="getImgUrl(currentResource.webUrl)"
          :alt="$t('resources.preview.imageAlt')"
          class="preview-image"
        />
        <video
          v-else-if="isVideoFile"
          :src="currentResource.webUrl"
          controls
          class="preview-video"
        ></video>
        <audio
          v-else-if="isAudioFile"
          :src="currentResource.webUrl"
          controls
          class="preview-audio"
        ></audio>
        <div v-else class="preview-other">
          <el-icon><Document /></el-icon>
          <p>{{ $t('resources.preview.unsupportedType') }}</p>
          <el-button
            :title="$t('resources.button.downloadTitle')"
            type="primary"
            @click="downloadResource"
          >
            {{ $t('resources.button.download') }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { ResourcesService } from '@/api/resourcesApi'
  import type { ResourcesRecord, ResourcesListParams } from '@/api/model/resourcesModel'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  // 查询参数
  const queryParams = reactive({
    page: 1,
    limit: 10,
    fileName: '',
    fileType: '',
    hash: '',
    source: '',
    sortByCreateDateAsc: false
  })

  // 分页信息
  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
  })

  const loading = ref(false)
  const ResourcesList = ref<ResourcesRecord[]>([])
  const total = ref(0)

  // 预览相关
  const previewVisible = ref(false)
  const currentResource = ref<ResourcesRecord>({} as ResourcesRecord)

  const isImageFile = computed(() => {
    return currentResource.value.fileType?.includes('image')
  })

  const isVideoFile = computed(() => {
    return currentResource.value.fileType?.includes('video')
  })

  const isAudioFile = computed(() => {
    return currentResource.value.fileType?.includes('audio')
  })

  const getImgUrl = (url: string) => {
    if (url.startsWith('upload')) {
      return `${import.meta.env.VITE_API_URL}/` + url
    }
    return url
  }

  // 加载资源列表数据
  const loadResourcesList = async () => {
    loading.value = true
    try {
      const res = await ResourcesService.getResourcesList(queryParams as ResourcesListParams)
      if (res.success) {
        ResourcesList.value = res.data.records
        total.value = res.data.total
        pagination.total = res.data.total
        pagination.current = res.data.current
        pagination.size = res.data.size
        pagination.pages = res.data.pages
      } else {
        ElMessage.error(res.message || t('resources.message.getListFailed'))
      }
    } catch (error) {
      ElMessage.error(t('resources.message.getListError') + error)
    } finally {
      loading.value = false
    }
  }

  // 搜索
  const handleQuery = () => {
    queryParams.page = 1
    loadResourcesList()
  }

  // 重置搜索条件
  const resetQuery = () => {
    queryParams.fileName = ''
    queryParams.fileType = ''
    queryParams.hash = ''
    queryParams.source = ''
    queryParams.sortByCreateDateAsc = false
    handleQuery()
  }

  // 分页变化
  const handleCurrentChange = (page: number) => {
    queryParams.page = page
    loadResourcesList()
  }

  // 处理每页显示数量变化
  const handleSizeChange = (size: number) => {
    queryParams.limit = size
    queryParams.page = 1 // 切换每页数量时重置为第一页
    loadResourcesList()
  }

  // 预览资源
  const previewResource = (row: ResourcesRecord) => {
    currentResource.value = row
    previewVisible.value = true
  }

  // 复制链接
  const copyUrl = (url: string) => {
    navigator.clipboard
      .writeText(url)
      .then(() => {
        ElMessage.success(t('resources.message.copySuccess'))
      })
      .catch(() => {
        ElMessage.error(t('resources.message.copyFailed'))
      })
  }

  // 下载资源
  const downloadResource = () => {
    window.open(currentResource.value.webUrl, '_blank')
  }

  onMounted(() => {
    loadResourcesList()
  })
</script>

<style lang="scss" scoped>
  .mb-4 {
    margin-bottom: 16px;
  }

  .preview-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 200px;
  }

  .preview-image {
    max-width: 100%;
    max-height: 500px;
    object-fit: contain;
  }

  .preview-video,
  .preview-audio {
    width: 100%;
  }

  .preview-other {
    text-align: center;

    .el-icon {
      margin-bottom: 16px;
      font-size: 48px;
      color: #909399;
    }
  }
</style>
