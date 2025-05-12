<template>
  <div class="page-content user">
    <div class="content">
      <div class="left-wrap">
        <div class="user-wrap box-style">
          <img class="bg" src="@imgs/user/bg.png" />
          <div class="avatar-container">
            <img
              class="avatar"
              :src="avatarPreviewUrl || formatAvatar(userDetail.icon, userDetail.id)"
            />
            <div class="avatar-upload-btn" v-if="isEdit">
              <el-upload
                title="上传用户头像"
                class="upload-demo"
                :action="''"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleAvatarChange"
                accept="image/*"
              >
                <el-button type="primary" circle size="small">
                  <el-icon><Upload /></el-icon>
                </el-button>
              </el-upload>
            </div>
          </div>
          <h2 class="name">{{ userDetail.nickName || userInfo.username }}</h2>
          <p class="des">{{ form.des }}</p>

          <div class="outer-info">
            <div>
              <i class="iconfont-sys">&#xe72e;</i>
              <span>{{ userDetail.email || 'email@example.com' }}</span>
            </div>
            <div>
              <i class="iconfont-sys">&#xe608;</i>
              <div class="role-tags">
                <template v-if="userDetail.roles && userDetail.roles.length > 0">
                  <el-tag
                    v-for="role in userDetail.roles"
                    :key="role.id"
                    size="small"
                    type="success"
                    class="role-tag"
                  >
                    {{ role.name }}
                  </el-tag>
                </template>
                <template v-else>
                  <span>未分配角色</span>
                </template>
              </div>
            </div>
            <div>
              <i class="iconfont-sys">&#xe736;</i>
              <span>{{ form.location }}</span>
            </div>
            <div>
              <i class="iconfont-sys">&#xe811;</i>
              <span>{{ userDetail.remarks }}</span>
            </div>
          </div>

          <!-- <div class="lables">
            <h3>标签</h3>
            <div>
              <div v-for="item in lableList" :key="item">
                {{ item }}
              </div>
            </div>
          </div> -->

          <div class="device-table">
            <h3>设备列表</h3>
            <art-table :data="deviceList" :pagination="false">
              <template #default>
                <el-table-column label="设备ID" prop="deviceId" width="150" />
                <el-table-column label="是否在线">
                  <template #default="scope">
                    <el-tag :type="scope.row.online ? 'success' : 'info'">
                      {{ scope.row.online ? '在线' : '离线' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="当前设备">
                  <template #default="scope">
                    <el-tag :type="scope.row.currentDevice ? 'primary' : 'info'">
                      {{ scope.row.currentDevice ? '是' : '否' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </template>
            </art-table>
          </div>
        </div>

        <!-- <el-carousel class="gallery" height="160px"
          :interval="5000"
          indicator-position="none"
        >
          <el-carousel-item class="item" v-for="item in galleryList" :key="item">
            <img :src="item"/>
          </el-carousel-item>
        </el-carousel> -->
      </div>
      <div class="right-wrap">
        <div class="info box-style">
          <h1 class="title">基本设置</h1>

          <el-form
            :model="form"
            class="form"
            ref="ruleFormRef"
            :rules="rules"
            label-width="86px"
            label-position="top"
          >
            <el-row>
              <el-form-item label="登录账号" prop="loginName">
                <el-input v-model="form.loginName" disabled />
              </el-form-item>
              <el-form-item label="角色" class="right-input">
                <div class="role-tags">
                  <el-tag
                    v-for="role in userDetail.roles"
                    :key="role.id"
                    type="success"
                    effect="light"
                  >
                    {{ role.name }}
                  </el-tag>
                </div>
              </el-form-item>
            </el-row>

            <el-row>
              <el-form-item label="昵称" prop="nickName">
                <el-input v-model="form.nickName" :disabled="!isEdit" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email" class="right-input">
                <el-input v-model="form.email" :disabled="!isEdit" />
              </el-form-item>
            </el-row>

            <el-row>
              <el-form-item label="手机" prop="tel">
                <el-input v-model="form.tel" :disabled="!isEdit" />
              </el-form-item>
              <el-form-item label="地址" prop="location" class="right-input">
                <el-input v-model="form.location" :disabled="!isEdit">
                  <template #suffix>
                    <i
                      class="iconfont-sys location-icon"
                      v-if="isEdit"
                      @click="getLocation"
                      title="获取当前位置"
                      >&#xe736;</i
                    >
                  </template>
                </el-input>
              </el-form-item>
            </el-row>

            <el-form-item label="个人介绍" prop="des" :style="{ height: '130px' }">
              <el-input type="textarea" :rows="4" v-model="form.des" :disabled="!isEdit" />
            </el-form-item>

            <div class="el-form-item-right">
              <el-button
                title="取消编辑用户详情"
                v-if="isEdit"
                type="default"
                style="width: 90px; margin-right: 10px"
                v-ripple
                @click="cancelEdit"
              >
                取消
              </el-button>
              <el-button
                :title="isEdit ? '保存用户字段' : '编辑用户字段'"
                type="primary"
                style="width: 90px"
                v-ripple
                @click="edit"
              >
                {{ isEdit ? '保存' : '编辑' }}
              </el-button>
            </div>
          </el-form>
        </div>

        <div class="info box-style" style="margin-top: 20px">
          <h1 class="title">更改密码</h1>

          <el-form
            :model="pwdForm"
            class="form"
            label-width="86px"
            label-position="top"
            ref="pwdFormRef"
            :rules="pwdRules"
          >
            <el-form-item label="当前密码" prop="password">
              <el-input
                v-model="pwdForm.password"
                :type="passwordVisible.current ? 'text' : 'password'"
                :disabled="!isEditPwd"
              >
                <template #suffix>
                  <el-icon
                    class="password-eye"
                    @click="passwordVisible.current = !passwordVisible.current"
                    v-if="isEditPwd"
                  >
                    <el-icon-view v-if="passwordVisible.current" />
                    <el-icon-hide v-else />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="pwdForm.newPassword"
                :type="passwordVisible.new ? 'text' : 'password'"
                :disabled="!isEditPwd"
              >
                <template #suffix>
                  <el-icon
                    class="password-eye"
                    @click="passwordVisible.new = !passwordVisible.new"
                    v-if="isEditPwd"
                  >
                    <el-icon-view v-if="passwordVisible.new" />
                    <el-icon-hide v-else />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="pwdForm.confirmPassword"
                :type="passwordVisible.confirm ? 'text' : 'password'"
                :disabled="!isEditPwd"
              >
                <template #suffix>
                  <el-icon
                    class="password-eye"
                    @click="passwordVisible.confirm = !passwordVisible.confirm"
                    v-if="isEditPwd"
                  >
                    <el-icon-view v-if="passwordVisible.confirm" />
                    <el-icon-hide v-else />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div class="el-form-item-right">
              <el-button
                title="取消重置密码"
                v-if="isEditPwd"
                type="default"
                style="width: 90px; margin-right: 10px"
                v-ripple
                @click="cancelEditPwd"
              >
                取消
              </el-button>
              <el-button
                :title="isEditPwd ? '保存重置密码' : '编辑重置密码'"
                type="primary"
                style="width: 90px"
                v-ripple
                @click="editPwd"
              >
                {{ isEditPwd ? '保存' : '编辑' }}
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useUserStore } from '@/store/modules/user'
  import { FormInstance, FormRules } from 'element-plus'
  import { UserService } from '@/api/usersApi'
  import { UserDetailResponse } from '@/api/model/userModel'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { View as ElIconView, Hide as ElIconHide, Upload } from '@element-plus/icons-vue'
  import { UploadService } from '@/api/uploadApi'

  const userStore = useUserStore()
  const userInfo = computed(() => userStore.getUserInfo)
  const userDetail = ref<UserDetailResponse & { originalIcon?: string }>({} as UserDetailResponse)
  const loading = ref(false)
  // 设备列表数据
  const deviceList = ref<any[]>([])

  const isEdit = ref(false)
  const isEditPwd = ref(false)
  const date = ref('')
  const form = reactive({
    loginName: '',
    nickName: '',
    email: '',
    tel: '',
    location: '',
    des: ''
  })

  const pwdForm = reactive({
    password: '',
    newPassword: '',
    confirmPassword: ''
  })

  // 控制密码显示/隐藏状态
  const passwordVisible = reactive({
    current: false,
    new: false,
    confirm: false
  })

  const ruleFormRef = ref<FormInstance>()
  const pwdFormRef = ref<FormInstance>()

  const rules = reactive<FormRules>({
    loginName: [],
    nickName: [
      { required: true, message: '请输入昵称', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    email: [],
    tel: [],
    location: [],
    des: []
  })

  // 密码表单校验规则
  const pwdRules = reactive<FormRules>({
    password: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (!value) {
            callback(new Error('请输入新密码'))
            return
          }

          // 使用正则表达式检查密码
          const isValid = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d\W_]{8,20}$/.test(value)
          console.log('密码验证结果:', value, isValid)

          if (!isValid) {
            callback(
              new Error('密码必须包含8-20个字符，至少包含一个大写字母、一个小写字母和一个数字')
            )
            return
          }

          callback()
        },
        trigger: 'blur'
      }
    ],
    confirmPassword: [
      { required: true, message: '请确认新密码', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (!value) {
            callback(new Error('请确认新密码'))
            return
          }

          if (value !== pwdForm.newPassword) {
            callback(new Error('两次输入的密码不一致'))
            return
          }

          callback()
        },
        trigger: 'blur'
      }
    ]
  })

  // 备份表单数据，用于取消编辑时恢复
  const formBackup = ref({
    loginName: '',
    nickName: '',
    email: '',
    tel: '',
    location: '',
    des: ''
  })

  // 选择的头像文件
  const selectedAvatar = ref<File | null>(null)

  // 添加头像预览URL变量
  const avatarPreviewUrl = ref<string>('')

  onMounted(async () => {
    getDate()
    await fetchUserDetail()
    await fetchUserDevices() // 获取用户设备列表
  })

  // 获取用户详情
  const fetchUserDetail = async () => {
    loading.value = true
    try {
      const res = await UserService.getCurrentUser()
      if (res.success) {
        userDetail.value = res.data

        // 初始化表单数据
        form.loginName = res.data.loginName || ''
        form.nickName = res.data.nickName || ''
        form.email = res.data.email || ''
        form.tel = res.data.tel || ''
        form.des = res.data.remarks || ''
        form.location = res.data.location || ''
      } else {
        ElMessage.error(res.message || '获取用户信息失败')
      }
    } catch (error) {
      console.error('获取用户详情失败:', error)
      ElMessage.error('获取用户详情失败')
    } finally {
      loading.value = false
    }
  }

  // 获取用户详细信息（包括敏感信息）
  const fetchDetailForEdit = async (userId: number) => {
    loading.value = true
    try {
      const res = await UserService.getUserDetail(userId)
      if (res.success) {
        const userData = res.data

        // 更新表单数据，使用完整的未加密信息
        form.loginName = userData.loginName || ''
        form.nickName = userData.nickName || ''
        form.email = userData.email || ''
        form.tel = userData.tel || ''
        form.des = userData.remarks || ''
        form.location = userData.location || ''

        isEdit.value = true
      } else {
        ElMessage.error(res.message || '获取用户详细信息失败')
      }
    } catch (error) {
      console.error('获取用户详细信息失败:', error)
      ElMessage.error('获取用户详细信息失败')
    } finally {
      loading.value = false
    }
  }

  const getDate = () => {
    const d = new Date()
    const h = d.getHours()
    let text = ''

    if (h >= 6 && h < 9) {
      text = '早上好'
    } else if (h >= 9 && h < 11) {
      text = '上午好'
    } else if (h >= 11 && h < 13) {
      text = '中午好'
    } else if (h >= 13 && h < 18) {
      text = '下午好'
    } else if (h >= 18 && h < 24) {
      text = '晚上好'
    } else if (h >= 0 && h < 6) {
      text = '很晚了，早点睡'
    }

    date.value = text
  }

  // 处理头像URL
  const formatAvatar = (icon: string, userId: number) => {
    if (!icon || icon === '' || !icon.startsWith('http')) {
      return `https://api.dicebear.com/9.x/adventurer/svg?seed=${userId}`
    }
    return icon
  }

  // 获取用户位置
  const getLocation = async () => {
    if (!isEdit.value) return

    try {
      ElMessage.info('正在获取位置...')
      const res = await UserService.getUserLocation()
      if (res.success) {
        const locationData = res.data
        // 拼接地址
        const fullAddress = [
          locationData.province || '',
          locationData.city || '',
          locationData.district || ''
        ]
          .filter(Boolean)
          .join('')

        // 更新location字段
        form.location = fullAddress

        ElMessage.success('位置获取成功')
      } else {
        ElMessage.error(res.message || '获取位置失败')
      }
    } catch (error) {
      console.error('获取位置失败:', error)
      ElMessage.error('获取位置失败')
    }
  }

  // 处理头像上传
  const handleAvatarChange = (file: any) => {
    if (file.raw) {
      // 验证文件大小和类型
      const isImage = file.raw.type.startsWith('image/')
      const isLt2M = file.raw.size / 1024 / 1024 < 2

      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB!')
        return
      }

      selectedAvatar.value = file.raw

      // 释放之前的URL
      if (avatarPreviewUrl.value) {
        URL.revokeObjectURL(avatarPreviewUrl.value)
      }

      // 生成新的预览URL
      avatarPreviewUrl.value = URL.createObjectURL(file.raw)
      console.log('生成预览URL:', avatarPreviewUrl.value)
    }
  }

  // 取消编辑基本信息
  const cancelEdit = () => {
    // 恢复表单数据
    Object.assign(form, formBackup.value)

    // 清除预览头像
    if (avatarPreviewUrl.value) {
      URL.revokeObjectURL(avatarPreviewUrl.value)
      avatarPreviewUrl.value = ''
    }

    // 重置头像文件
    selectedAvatar.value = null

    // 退出编辑模式
    isEdit.value = false
  }

  // 取消编辑密码
  const cancelEditPwd = () => {
    // 重置密码表单
    resetPwdForm()
    // 退出编辑模式
    isEditPwd.value = false
  }

  const edit = async () => {
    if (isEdit.value) {
      // 保存编辑内容
      if (ruleFormRef.value) {
        await ruleFormRef.value.validate(async (valid) => {
          if (valid) {
            try {
              // 检查邮箱和手机号是否包含*号，如果有则提交空值
              const email = form.email && form.email.includes('*') ? '' : form.email
              const tel = form.tel && form.tel.includes('*') ? '' : form.tel

              const params: any = {
                nickName: form.nickName,
                email: email,
                tel: tel,
                remarks: form.des,
                location: form.location
              }

              // 如果选择了新头像，上传头像
              if (selectedAvatar.value) {
                try {
                  // 构建FormData对象
                  const uploadFormData = new FormData()
                  uploadFormData.append('test', selectedAvatar.value)

                  // 调用上传API
                  const uploadRes = await UploadService.upload(uploadFormData)

                  if (uploadRes.success) {
                    // 上传成功，将返回的URL添加到用户更新参数中
                    params.icon = uploadRes.data.url
                    ElMessage.success('头像上传成功')
                  } else {
                    ElMessage.error(uploadRes.message || '头像上传失败')
                    return
                  }
                } catch (error) {
                  console.error('头像上传失败:', error)
                  ElMessage.error('头像上传失败')
                  return
                }
              }

              // 更新用户信息
              const res = await UserService.updateCurrentUserInfo(params)
              if (res.success) {
                ElMessage.success('更新成功')
                await fetchUserDetail() // 重新获取用户信息
                isEdit.value = false
                selectedAvatar.value = null
              } else {
                ElMessage.error(res.message || '更新失败')
              }
            } catch (error) {
              console.error('更新用户信息失败:', error)
              ElMessage.error('更新用户信息失败')
            }
          }
        })
      }
    } else {
      // 编辑模式：先获取用户详细信息（包括敏感信息）
      if (userDetail.value.id) {
        // 备份当前表单数据，用于取消时恢复
        formBackup.value = { ...form }
        // 备份原始头像
        userDetail.value.originalIcon = userDetail.value.icon

        await fetchDetailForEdit(userDetail.value.id)
      } else {
        ElMessage.error('无法获取用户ID，请刷新页面后重试')
      }
    }
  }

  // 清空密码表单
  const resetPwdForm = () => {
    pwdForm.password = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    // 如果表单实例存在，重置校验状态
    if (pwdFormRef.value) {
      pwdFormRef.value.resetFields()
    }
  }

  // 编辑/保存密码
  const editPwd = async () => {
    if (isEditPwd.value) {
      // 保存密码
      if (pwdFormRef.value) {
        await pwdFormRef.value.validate(async (valid) => {
          if (valid) {
            try {
              const params = {
                oldPwd: pwdForm.password,
                newPwd: pwdForm.newPassword
              }

              const res = await UserService.changePassword(params)
              if (res.success) {
                ElMessage.success('密码修改成功')
                isEditPwd.value = false
                resetPwdForm()

                // 密码修改成功后倒计时登出
                let countdown = 5
                const messageContent = `密码已成功修改！${countdown}秒后将退出系统，请使用新密码重新登录。`

                ElMessageBox.alert(messageContent, '密码已修改', {
                  confirmButtonText: '立即退出',
                  showClose: false,
                  beforeClose: (action, instance, done) => {
                    // 用户点击了立即退出按钮
                    clearInterval(timer)
                    userStore.logOut()
                    done()
                  }
                })

                const timer = setInterval(() => {
                  countdown--
                  if (countdown <= 0) {
                    clearInterval(timer)
                    userStore.logOut()
                  } else {
                    // 更新消息框内容
                    const messageEl = document.querySelector('.el-message-box__message p')
                    if (messageEl) {
                      messageEl.textContent = `密码已成功修改！${countdown}秒后将退出系统，请使用新密码重新登录。`
                    }
                  }
                }, 1000)
              } else {
                ElMessage.error(res.message || '密码修改失败')
              }
            } catch (error) {
              console.error('密码修改失败:', error)
              ElMessage.error('密码修改失败')
            }
          }
        })
      }
    } else {
      // 进入编辑模式，确保表单为空
      resetPwdForm()
      // 重置密码显示/隐藏状态
      passwordVisible.current = false
      passwordVisible.new = false
      passwordVisible.confirm = false
      isEditPwd.value = true
    }
  }

  // 当退出编辑模式时，确保密码表单清空
  watch(isEditPwd, (newVal) => {
    if (!newVal) {
      resetPwdForm()
    }
  })

  // 获取用户设备列表
  const fetchUserDevices = async () => {
    try {
      const res = await UserService.getUserDevice()
      if (res.success && res.data) {
        // 如果API返回单个设备，转换为数组
        if (!Array.isArray(res.data)) {
          deviceList.value = [res.data]
        } else {
          deviceList.value = res.data
        }
      } else {
        console.warn('获取用户设备列表失败:', res.message)
      }
    } catch (error) {
      console.error('获取用户设备列表失败:', error)
    }
  }
</script>

<style lang="scss">
  .user {
    .icon {
      width: 1.4em;
      height: 1.4em;
      overflow: hidden;
      vertical-align: -0.15em;
      fill: currentcolor;
    }
  }
</style>

<style lang="scss" scoped>
  .page-content {
    width: 100%;
    height: 100%;
    padding: 0 !important;
    background: transparent !important;
    border: none !important;
    box-shadow: none !important;

    $box-radius: calc(var(--custom-radius) + 4px);

    .box-style {
      border: 1px solid var(--art-border-color);
    }

    .content {
      position: relative;
      display: flex;
      justify-content: space-between;
      margin-top: 10px;

      .left-wrap {
        width: 450px;
        margin-right: 25px;

        .user-wrap {
          position: relative;
          padding: 35px 40px;
          overflow: hidden;
          text-align: center;
          background: var(--art-main-bg-color);
          border-radius: $box-radius;

          .bg {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 200px;
            object-fit: cover;
          }

          .avatar-container {
            position: relative;
            z-index: 10;
            display: inline-block;
            margin-top: 120px;

            .avatar {
              position: relative;
              z-index: 10;
              width: 80px;
              height: 80px;
              object-fit: cover;
              border: 2px solid #fff;
              border-radius: 50%;
            }

            .avatar-upload-btn {
              position: absolute;
              right: -5px;
              bottom: -5px;
              z-index: 11;

              .el-button {
                box-shadow: 0 2px 5px rgb(0 0 0 / 20%);
              }
            }
          }

          .name {
            margin-top: 20px;
            font-size: 22px;
            font-weight: 400;
          }

          .des {
            margin-top: 20px;
            font-size: 14px;
          }

          .outer-info {
            width: 300px;
            margin: auto;
            margin-top: 30px;
            text-align: left;

            > div {
              display: flex;
              align-items: flex-start;
              margin-top: 10px;

              i {
                margin-top: 2px; // 微调图标垂直对齐
              }

              span {
                margin-left: 8px;
                font-size: 14px;
              }

              .role-tags {
                display: flex;
                flex-wrap: wrap;
                gap: 4px;
                margin-left: 8px;

                .role-tag {
                  margin-bottom: 4px;
                  transform: scale(0.9);
                  transform-origin: left;
                }
              }
            }
          }

          .lables {
            margin-top: 40px;

            h3 {
              font-size: 15px;
              font-weight: 500;
            }

            > div {
              display: flex;
              flex-wrap: wrap;
              justify-content: center;
              margin-top: 15px;

              > div {
                padding: 3px 6px;
                margin: 0 10px 10px 0;
                font-size: 12px;
                background: var(--art-main-bg-color);
                border: 1px solid var(--art-border-color);
                border-radius: 2px;
              }
            }
          }

          .device-table {
            margin-top: 40px;
            text-align: left;

            h3 {
              margin-bottom: 15px;
              font-size: 15px;
              font-weight: 500;
            }

            :deep(.art-table) {
              background: transparent;
              border: none;
              box-shadow: none;

              .el-table {
                background: transparent;
                border: 1px solid var(--art-border-color);
                border-radius: 4px;

                th {
                  background: var(--art-bg-color);
                }

                th,
                td {
                  padding: 8px 12px;
                }
              }
            }
          }
        }

        .gallery {
          margin-top: 25px;
          border-radius: 10px;

          .item {
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
        }

        :deep(.el-tag--info) {
          color: #909399;
          background-color: #f4f4f5;
          border-color: #e9e9eb;
        }
      }

      .right-wrap {
        flex: 1;
        overflow: hidden;
        border-radius: $box-radius;

        .info {
          background: var(--art-main-bg-color);
          border-radius: $box-radius;

          .title {
            padding: 15px 25px;
            font-size: 20px;
            font-weight: 400;
            color: var(--art-text-gray-800);
            border-bottom: 1px solid var(--art-border-color);
          }

          .form {
            box-sizing: border-box;
            padding: 30px 25px;

            > .el-row {
              .el-form-item {
                width: calc(50% - 10px);
              }

              .el-input,
              .el-select {
                width: 100%;
              }
            }

            .right-input {
              margin-left: 20px;
            }

            .el-form-item-right {
              display: flex;
              align-items: center;
              justify-content: end;

              .el-button {
                width: 110px !important;
              }
            }

            .role-tags {
              display: flex;
              flex-wrap: wrap;
              gap: 5px;
              padding-top: 5px;
            }
          }
        }
      }
    }
  }

  // 位置图标样式
  .location-icon {
    font-size: 18px;
    color: var(--el-color-primary);
    cursor: pointer;
    transition: color 0.3s;

    &:hover {
      color: var(--el-color-primary-light-3);
    }
  }

  // 密码输入框的眼睛图标样式
  .password-eye {
    margin-right: 8px;
    color: var(--el-text-color-secondary);
    cursor: pointer;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  @media only screen and (max-width: $device-ipad-vertical) {
    .page-content {
      .content {
        display: block;
        margin-top: 5px;

        .left-wrap {
          width: 100%;
        }

        .right-wrap {
          width: 100%;
          margin-top: 15px;
        }
      }
    }
  }
</style>
