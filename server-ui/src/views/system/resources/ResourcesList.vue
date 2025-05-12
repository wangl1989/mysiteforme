<template>
  <div class="page-content">
    <el-row :gutter="15" class="mb-4">
      <el-col :xs="19" :sm="12" :lg="6">
        <el-input
          title="搜索系统资源文件"
          v-model="queryParams.fileName"
          placeholder="请输入文件名搜索"
        ></el-input>
      </el-col>
      <el-col :xs="4" :sm="6" :lg="3">
        <el-select v-model="queryParams.source" placeholder="资源来源" clearable>
          <el-option label="本地存储" value="local"></el-option>
          <el-option label="阿里云OSS" value="oss"></el-option>
          <el-option label="七牛云" value="qiniu"></el-option>
        </el-select>
      </el-col>
      <el-col :xs="4" :sm="6" :lg="3">
        <el-select v-model="queryParams.fileType" placeholder="文件类型" clearable>
          <el-option label="图片" value="image"></el-option>
          <el-option label="文档" value="document"></el-option>
          <el-option label="视频" value="video"></el-option>
          <el-option label="音频" value="audio"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
      </el-col>
      <el-col :xs="4" :sm="12" :lg="4">
        <el-button type="primary" v-ripple @click="handleQuery">搜索</el-button>
        <el-button v-ripple @click="resetQuery">重置</el-button>
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
      <el-table-column label="文件名" prop="fileName" />
      <el-table-column label="存储来源" prop="source">
        <template #default="scope">
          <el-tag v-if="scope.row.source === 'local'">本地存储</el-tag>
          <el-tag v-else-if="scope.row.source === 'oss'" type="success">阿里云OSS</el-tag>
          <el-tag v-else-if="scope.row.source === 'qiniu'" type="warning">七牛云</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="文件类型" prop="fileType" />
      <el-table-column label="文件大小" prop="fileSize" />
      <el-table-column label="上传时间" prop="createDate" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <ArtButtonTable
            title="预览系统文件"
            text="预览"
            icon="&#xe700;"
            iconColor="#67C23A"
            @click="previewResource(scope.row)"
          />
          <ArtButtonTable
            title="复制文件链接"
            text="复制链接"
            icon="&#xe63a;"
            iconColor="#125232"
            @click="copyUrl(scope.row.webUrl)"
          />
          <!-- <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button> -->
        </template>
      </el-table-column>
    </art-table>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="资源预览" width="60%">
      <div class="preview-container">
        <img
          v-if="isImageFile"
          :src="getImgUrl(currentResource.webUrl)"
          alt="预览图片"
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
          <p>此文件类型不支持直接预览，请下载后查看</p>
          <el-button title="下载资源文件" type="primary" @click="downloadResource"
            >下载文件</el-button
          >
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
        ElMessage.error(res.message || '获取资源列表失败')
      }
    } catch (error) {
      ElMessage.error('获取资源列表失败：' + error)
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
        ElMessage.success('链接已复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动复制')
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
