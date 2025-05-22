<template>
  <div class="layout-chat">
    <el-drawer v-model="isDrawerVisible" :size="isMobile ? '100%' : '850px'" :with-header="false">
      <div class="header">
        <div class="header-left">
          <span class="name">AI Bot</span>
          <div class="status">
            <div class="dot" :class="{ online: isOnline, offline: !isOnline }"></div>
            <span class="status-text">{{ isOnline ? '在线' : '离线' }}</span>
          </div>
        </div>
        <div class="header-right">
          <el-tooltip content="新建会话" placement="bottom">
            <i class="iconfont-sys icon-button" @click="createNewChat">&#xe604;</i>
          </el-tooltip>
          <el-tooltip content="会话历史" placement="bottom">
            <i class="iconfont-sys icon-button" @click="showChatHistory">&#xe83d;</i>
          </el-tooltip>
          <el-icon class="icon-close" :size="20" @click="closeChat">
            <Close />
          </el-icon>
        </div>
      </div>
      <div class="chat-container">
        <!-- 聊天消息区域 -->
        <div class="chat-messages" ref="messageContainer">
          <!-- 历史会话列表 -->
          <div v-if="isShowingHistory" class="history-list">
            <h3>历史会话</h3>
            <div v-if="chatHistoryList.length === 0" class="empty-history">
              <el-empty description="暂无历史会话" />
            </div>
            <div
              v-for="(history, index) in chatHistoryList"
              :key="history.chatId"
              class="history-item"
              @click="loadHistoryChat(history)"
            >
              <div class="history-content">
                <span class="history-index">{{ index + 1 }}</span>
                <span class="history-text">{{ history.chatList[0]?.text || '空会话' }}</span>
              </div>
              <div class="history-time">
                {{ formatDate(history.chatList[0]?.time, 'yyyy-MM-dd HH:mm') }}
              </div>
            </div>
          </div>

          <!-- 正常聊天消息 -->
          <template v-else v-for="(message, index) in messages" :key="index">
            <div :class="['message-item', message.isMe ? 'message-right' : 'message-left']">
              <el-avatar :size="32" :src="message.avatar" class="message-avatar" />
              <div class="message-content">
                <div class="message-info">
                  <span class="sender-name">{{ message.sender }}</span>
                  <span class="message-time">{{ message.time }}</span>
                </div>
                <!-- 用户消息保持纯文本 -->
                <div v-if="message.isMe" class="message-text">{{ message.content }}</div>
                <!-- AI回复使用Markdown渲染 -->
                <div
                  v-else
                  class="message-text markdown-body"
                  v-html="renderMarkdown(message.content)"
                ></div>
              </div>
            </div>
          </template>
        </div>

        <!-- 聊天输入区域 -->
        <div class="chat-input">
          <el-input
            v-model="messageText"
            type="textarea"
            :rows="3"
            placeholder="输入消息"
            resize="none"
            @keyup.enter.prevent="sendMessage"
          >
            <template #append>
              <div class="input-actions">
                <el-button :icon="Paperclip" circle plain />
                <el-button :icon="Picture" circle plain />
                <el-button type="primary" @click="sendMessage" v-ripple>发送</el-button>
              </div>
            </template>
          </el-input>
          <div class="chat-input-actions">
            <div class="left">
              <i class="iconfont-sys">&#xe634;</i>
              <i class="iconfont-sys">&#xe809;</i>
            </div>
            <el-button type="primary" @click="sendMessage" v-ripple>发送</el-button>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
  import { md } from '@/utils/markdown'
  import 'highlight.js/styles/github.css' // 代码高亮样式
  import '@/assets/styles/mymarkdown.scss'
  import { ref, onMounted } from 'vue'
  import { Picture, Paperclip } from '@element-plus/icons-vue'
  import mittBus from '@/utils/mittBus'
  import aiAvatar from '@/assets/img/avatar/avatar10.jpg'
  import request from '@/utils/http'
  import { useUserStore } from '@/store/modules/user'
  import { UserService } from '@/api/usersApi'
  import { formatDate } from '@/utils/date'
  import { UserHistoryChatResponse } from '@/api/model/userModel'

  const { width } = useWindowSize()
  const isMobile = computed(() => width.value < 500)

  // 抽屉显示状态
  const isDrawerVisible = ref(false)
  // 是否在线
  const isOnline = ref(true)

  // 消息相关数据
  const messageText = ref('')
  const messages = ref<any[]>([])

  // 会话历史相关
  const isShowingHistory = ref(false)
  const chatHistoryList = ref<UserHistoryChatResponse[]>([])

  // 渲染Markdown函数
  const renderMarkdown = (text: string) => {
    if (!text) return ''
    try {
      return md.render(text)
    } catch (e) {
      console.error('Markdown渲染错误:', e)
      return text // 出错时显示原文本
    }
  }

  const messageId = ref(0) // 用于生成唯一的消息ID

  const chatId = ref('') // 用于生成当前会话唯一的消息ID

  const userAvatar = ref('') // 使用导入的头像

  const userStore = useUserStore()

  // 获取用户历史聊天记录
  const getUserHistoryChat = async () => {
    const result = await UserService.getUserHistoryChat({ limit: 1 })
    if (result.success) {
      if (result.data) {
        if (result.data instanceof Array && result.data.length > 0) {
          const historyChatList = result.data[0]
          historyChatList.chatList.forEach((item) => {
            messages.value.push({
              id: messageId.value++,
              sender: item.type === 'user' ? userStore.info.name || 'Ricky' : 'AI',
              content: item.text,
              time: formatDate(item.time, 'mm:ss'),
              isMe: item.type === 'user' ? true : false,
              avatar: item.type === 'user' ? userAvatar.value : aiAvatar
            })
          })
          chatId.value = historyChatList.chatId
        }
      }
    }
  }

  // 新建会话
  const createNewChat = () => {
    messages.value = []
    chatId.value = ''
    isShowingHistory.value = false
  }

  // 显示会话历史
  const showChatHistory = async () => {
    // 清空当前会话
    messages.value = []
    chatId.value = ''
    isShowingHistory.value = true

    try {
      const result = await UserService.getUserHistoryChat({ limit: 0 })
      if (result.success && result.data) {
        chatHistoryList.value = result.data
      } else {
        ElMessage.warning('获取历史会话失败')
      }
    } catch (error) {
      console.error('获取历史会话出错:', error)
      ElMessage.error('获取历史会话出错')
    }
  }

  // 加载指定的历史会话
  const loadHistoryChat = (historyChat: UserHistoryChatResponse) => {
    // 清空消息列表
    messages.value = []

    // 设置聊天ID
    chatId.value = historyChat.chatId

    // 加载会话消息
    historyChat.chatList.forEach((item) => {
      messages.value.push({
        id: messageId.value++,
        sender: item.type === 'user' ? userStore.info.name || 'Ricky' : 'AI Bot',
        content: item.text,
        time: formatDate(item.time, 'mm:ss'),
        isMe: item.type === 'user',
        avatar: item.type === 'user' ? userAvatar.value : aiAvatar
      })
    })

    // 关闭历史列表显示
    isShowingHistory.value = false

    // 滚动到底部
    scrollToBottom()
  }

  // 处理头像URL
  const formatAvatar = (icon: string, userId: number) => {
    if (!icon) {
      return `https://api.dicebear.com/9.x/adventurer/svg?seed=${userId}`
    }

    if (icon.startsWith('upload')) {
      return `${import.meta.env.VITE_API_URL}/` + icon
    }

    return icon
  }

  const abortController = new AbortController()

  // 发送消息
  const sendMessage = async () => {
    const text = messageText.value.trim()
    messageText.value = ''
    if (!text) return

    messages.value.push({
      id: messageId.value++,
      sender: userStore.info.name || 'Ricky',
      content: text,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isMe: true,
      avatar: userAvatar.value
    })
    scrollToBottom()

    // 发送消息
    if (!chatId.value) {
      chatId.value = crypto.randomUUID()
    }
    try {
      const result = {
        id: messageId.value++,
        sender: 'Art Bot',
        content: '',
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        isMe: false,
        avatar: aiAvatar
      }
      messages.value.push(result)
      await request.getChatContent({
        chatId: chatId.value,
        content: text,
        onChunk: (chunk) => {
          messages.value[messages.value.length - 1].content += chunk
          scrollToBottom()
        },
        abortSignal: abortController.signal
      })
      scrollToBottom()
    } catch (error) {
      console.error('消息发送失败:', error)
      // 可以添加重试按钮或错误提示
      ElMessage.error('消息发送失败')
    }
  }

  // 滚动到底部
  const messageContainer = ref<HTMLElement | null>(null)
  const scrollToBottom = () => {
    setTimeout(() => {
      if (messageContainer.value) {
        messageContainer.value.scrollTop = messageContainer.value.scrollHeight
      }
    }, 100)
  }

  const openChat = () => {
    isDrawerVisible.value = true
    getUserHistoryChat()
    scrollToBottom()
  }

  const closeChat = () => {
    isDrawerVisible.value = false
  }

  onMounted(() => {
    mittBus.on('openChat', openChat)
    userAvatar.value = formatAvatar(userStore.info?.avatar || '', userStore.info?.id || 0)
  })
</script>

<style lang="scss">
  .layout-chat {
    .el-overlay {
      background-color: rgb(0 0 0 / 20%) !important;
    }
  }
</style>

<style lang="scss" scoped>
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    .header-left {
      .name {
        font-size: 16px;
        font-weight: 500;
      }

      .status {
        display: flex;
        gap: 4px;
        align-items: center;
        margin-top: 6px;

        .dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;

          &.online {
            background-color: var(--el-color-success);
          }

          &.offline {
            background-color: var(--el-color-danger);
          }
        }

        .status-text {
          font-size: 12px;
          color: var(--art-gray-500);
        }
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 16px;

      .icon-button {
        font-size: 20px;
        cursor: pointer;
        color: var(--el-text-color-regular);
        transition: color 0.3s;

        &:hover {
          color: var(--el-color-primary);
        }
      }

      .icon-close {
        cursor: pointer;
      }
    }
  }

  .chat-container {
    display: flex;
    flex-direction: column;
    height: calc(100% - 70px);

    .chat-messages {
      flex: 1;
      padding: 30px 16px;
      overflow-y: auto;
      border-top: 1px solid var(--el-border-color-lighter);

      &::-webkit-scrollbar {
        width: 5px !important;
      }

      .message-item {
        display: flex;
        flex-direction: row;
        gap: 8px;
        align-items: flex-start;
        width: 100%;
        margin-bottom: 30px;

        .message-text {
          font-size: 14px;
          color: var(--art-gray-900);
          border-radius: 6px;
        }

        &.message-left {
          justify-content: flex-start;

          .message-content {
            align-items: flex-start;

            .message-info {
              flex-direction: row;
            }

            .message-text {
              background-color: #f8f5ff;
            }
          }
        }

        &.message-right {
          flex-direction: row-reverse;

          .message-content {
            align-items: flex-end;

            .message-info {
              flex-direction: row-reverse;
            }

            .message-text {
              background-color: #e9f3ff;
            }
          }
        }

        .message-avatar {
          flex-shrink: 0;
        }

        .message-content {
          display: flex;
          flex-direction: column;
          max-width: 70%;

          .message-info {
            display: flex;
            gap: 8px;
            margin-bottom: 4px;
            font-size: 12px;

            .message-time {
              color: var(--el-text-color-secondary);
            }

            .sender-name {
              font-weight: 500;
            }
          }

          .message-text {
            padding: 10px 14px;
            line-height: 1.4;
          }
        }
      }

      .history-list {
        padding: 10px 0;

        h3 {
          margin-bottom: 16px;
          font-size: 16px;
          font-weight: 500;
          color: var(--el-text-color-primary);
        }

        .history-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 16px;
          border-radius: 6px;
          margin-bottom: 8px;
          background-color: var(--el-fill-color-light);
          cursor: pointer;
          transition: background-color 0.3s;

          &:hover {
            background-color: var(--el-fill-color);
          }

          .history-content {
            display: flex;
            align-items: center;
            gap: 8px;
            width: 70%;

            .history-index {
              flex-shrink: 0;
              width: 24px;
              height: 24px;
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: 50%;
              background-color: var(--el-color-primary-light-8);
              color: var(--el-color-primary);
              font-size: 12px;
            }

            .history-text {
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          .history-time {
            font-size: 12px;
            color: var(--el-text-color-secondary);
          }
        }

        .empty-history {
          display: flex;
          justify-content: center;
          padding: 40px 0;
        }
      }
    }

    .chat-input {
      padding: 16px 16px 0;

      .input-actions {
        display: flex;
        gap: 8px;
        padding: 8px 0;
      }

      .chat-input-actions {
        display: flex;
        align-items: center; // 修正为单数
        justify-content: space-between;
        margin-top: 12px;

        .left {
          display: flex;
          align-items: center;

          i {
            margin-right: 20px;
            font-size: 16px;
            color: var(--art-gray-500);
            cursor: pointer;
          }
        }

        // 确保发送按钮与输入框对齐
        el-button {
          min-width: 80px;
        }
      }
    }
  }

  .dark {
    .chat-container {
      .chat-messages {
        .message-item {
          &.message-left {
            .message-text {
              background-color: #232323 !important;
            }
          }

          &.message-right {
            .message-text {
              background-color: #182331 !important;
            }
          }
        }
      }
    }
  }
</style>
