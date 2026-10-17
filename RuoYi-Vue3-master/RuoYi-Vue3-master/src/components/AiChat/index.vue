<template>
  <div>
    <!-- 悬浮按钮 -->
    <div 
      v-if="!isOpen" 
      class="ai-chat-button"
      @click="openChat"
      :style="{ right: buttonRight + 'px', bottom: buttonBottom + 'px' }"
    >
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="chat-badge">
        <el-button type="primary" :icon="ChatDotRound" circle size="large" />
      </el-badge>
      <span class="button-text">AI客服</span>
    </div>

    <!-- 聊天窗口 -->
    <div
      v-if="isOpen"
      class="ai-chat-window"
      :style="windowStyle"
    >
      <!-- 窗口头部 -->
      <div class="chat-header" @mousedown="startDrag">
        <div class="header-left">
          <el-avatar :size="32" :icon="ChatDotRound" />
          <span class="header-title">AI客服助手</span>
        </div>
        <div class="header-actions" @mousedown.stop @click.stop>
          <el-button
            link
            type="info"
            :icon="Refresh"
            @click.stop="clearConversation"
            title="清空对话"
          />
          <el-button
            link
            type="info"
            :icon="isMinimized ? FullScreen : Minus"
            @click.stop="toggleMinimize"
            title="最小化"
          />
          <el-button
            link
            type="info"
            :icon="Close"
            @click.stop="closeChat"
            title="关闭"
          />
        </div>
      </div>

      <!-- 消息区域 -->
      <div 
        v-show="!isMinimized" 
        class="chat-messages" 
        ref="messagesContainer"
        @mousedown.stop
      >
        <div v-if="messages.length === 0" class="empty-message">
          <el-empty description="您好！我是AI客服助手，有什么可以帮助您的吗？" :image-size="80" />
        </div>
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-item', msg.role === 'user' ? 'message-user' : 'message-ai']"
        >
          <div class="message-content">
            <div class="message-avatar">
              <el-avatar :size="32" v-if="msg.role === 'user'">
                <el-icon><User /></el-icon>
              </el-avatar>
              <el-avatar :size="32" :icon="ChatDotRound" v-else />
            </div>
            <div class="message-bubble">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>
              <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
            </div>
          </div>
        </div>
        <div v-if="loading" class="message-item message-ai">
          <div class="message-content">
            <div class="message-avatar">
              <el-avatar :size="32" :icon="ChatDotRound" />
            </div>
            <div class="message-bubble">
              <div class="message-text">
                <el-icon class="is-loading"><Loading /></el-icon>
                <span style="margin-left: 8px;">AI正在思考中...</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div v-show="!isMinimized" class="chat-input" @mousedown.stop>
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入您的问题..."
          @keydown.ctrl.enter="sendMessage"
          @keydown.meta.enter="sendMessage"
          :disabled="loading"
        />
        <div class="input-actions">
          <el-button
            type="primary"
            :icon="Promotion"
            :loading="loading"
            @click="sendMessage"
            :disabled="!inputMessage.trim()"
          >
            发送 (Ctrl+Enter)
          </el-button>
        </div>
      </div>

      <!-- 缩放控制 -->
      <div
        class="resize-handle"
        @mousedown.stop="startResize"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { ChatDotRound, Close, Minus, FullScreen, Refresh, User, Loading, Promotion } from '@element-plus/icons-vue'
import { sendAiMessage, clearAiConversation } from '@/api/shop/aiChat'
import { ElMessage } from 'element-plus'
import useAiChatStore from '@/store/modules/aiChat'

const props = defineProps({
  // 窗口初始位置
  initialRight: {
    type: Number,
    default: 20
  },
  initialBottom: {
    type: Number,
    default: 20
  },
  // 窗口初始大小
  initialWidth: {
    type: Number,
    default: 400
  },
  initialHeight: {
    type: Number,
    default: 600
  }
})

const aiChatStore = useAiChatStore()

// 状态 - 从store同步
const isOpen = computed({
  get: () => aiChatStore.isOpen,
  set: (val) => {
    if (val) {
      aiChatStore.openChat()
    } else {
      aiChatStore.closeChat()
    }
  }
})
const isMinimized = computed({
  get: () => aiChatStore.isMinimized,
  set: (val) => {
    if (val !== aiChatStore.isMinimized) {
      aiChatStore.toggleMinimize()
    }
  }
})
const unreadCount = computed(() => aiChatStore.unreadCount)

const loading = ref(false)
const inputMessage = ref('')
const messages = ref([])
const conversationId = ref(null)

// 位置和大小
const buttonRight = ref(props.initialRight)
const buttonBottom = ref(props.initialBottom)
const windowRight = ref(props.initialRight)
const windowBottom = ref(props.initialBottom)
const windowWidth = ref(props.initialWidth)
const windowHeight = ref(props.initialHeight)

// 拖拽状态
const isDragging = ref(false)
const dragStartX = ref(0)
const dragStartY = ref(0)
const dragStartRight = ref(0)
const dragStartBottom = ref(0)

// 缩放状态
const isResizing = ref(false)
const resizeStartX = ref(0)
const resizeStartY = ref(0)
const resizeStartWidth = ref(0)
const resizeStartHeight = ref(0)
const resizeStartRight = ref(0)
const resizeStartBottom = ref(0)

const messagesContainer = ref(null)

// 计算窗口样式
const windowStyle = computed(() => {
  return {
    right: windowRight.value + 'px',
    bottom: windowBottom.value + 'px',
    width: windowWidth.value + 'px',
    height: isMinimized.value ? 'auto' : windowHeight.value + 'px'
  }
})

// 打开聊天窗口
const openChat = () => {
  aiChatStore.openChat()
  nextTick(() => {
    scrollToBottom()
  })
}

// 关闭聊天窗口
const closeChat = () => {
  aiChatStore.closeChat()
}

// 切换最小化
const toggleMinimize = () => {
  aiChatStore.toggleMinimize()
  if (!aiChatStore.isMinimized) {
    nextTick(() => {
      scrollToBottom()
    })
  }
}

// 监听store状态变化
watch(() => aiChatStore.isOpen, (newVal) => {
  if (newVal) {
    nextTick(() => {
      scrollToBottom()
    })
  }
})

// 开始拖拽
const startDrag = (e) => {
  // 只允许通过头部拖拽，且不是点击按钮时
  const target = e.target
  if (target.closest('.header-actions') || target.closest('button') || target.closest('.el-button')) {
    return
  }
  isDragging.value = true
  dragStartX.value = e.clientX
  dragStartY.value = e.clientY
  dragStartRight.value = windowRight.value
  dragStartBottom.value = windowBottom.value
}

// 拖拽中
const onDrag = (e) => {
  if (isDragging.value) {
    const deltaX = dragStartX.value - e.clientX
    const deltaY = dragStartY.value - e.clientY
    windowRight.value = Math.max(0, Math.min(window.innerWidth - windowWidth.value, dragStartRight.value + deltaX))
    windowBottom.value = Math.max(0, Math.min(window.innerHeight - windowHeight.value - 50, dragStartBottom.value - deltaY))
  }
}

// 停止拖拽
const stopDrag = () => {
  if (isDragging.value) {
    isDragging.value = false
  }
}

// 开始缩放
const startResize = (e) => {
  isResizing.value = true
  resizeStartX.value = e.clientX
  resizeStartY.value = e.clientY
  resizeStartWidth.value = windowWidth.value
  resizeStartHeight.value = windowHeight.value
  resizeStartRight.value = windowRight.value
  resizeStartBottom.value = windowBottom.value
  e.preventDefault()
}

// 缩放处理
const handleResize = (e) => {
  if (isResizing.value) {
    const deltaX = resizeStartX.value - e.clientX
    const deltaY = e.clientY - resizeStartY.value
    
    // 计算新宽度和高度
    let newWidth = resizeStartWidth.value + deltaX
    let newHeight = resizeStartHeight.value + deltaY
    
    // 限制最小和最大尺寸
    newWidth = Math.max(300, Math.min(800, newWidth))
    newHeight = Math.max(400, Math.min(900, newHeight))
    
    // 限制在窗口内
    if (resizeStartRight.value + newWidth > window.innerWidth) {
      newWidth = window.innerWidth - resizeStartRight.value
    }
    if (resizeStartBottom.value + newHeight > window.innerHeight - 50) {
      newHeight = window.innerHeight - resizeStartBottom.value - 50
    }
    
    windowWidth.value = newWidth
    windowHeight.value = newHeight
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) {
    return
  }

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: userMessage,
    timestamp: Date.now()
  })

  scrollToBottom()
  loading.value = true

  try {
    const response = await sendAiMessage({
      message: userMessage,
      conversationId: conversationId.value
    })

    if (response.code === 200) {
      // 添加AI回复
      messages.value.push({
        role: 'assistant',
        content: response.data.response,
        timestamp: Date.now()
      })

      // 保存会话ID
      if (response.data.conversationId) {
        conversationId.value = response.data.conversationId
      }

      scrollToBottom()
    } else {
      ElMessage.error(response.msg || '发送失败，请稍后重试')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 清空对话
const clearConversation = async () => {
  try {
    await clearAiConversation({ conversationId: conversationId.value })
    messages.value = []
    conversationId.value = null
    ElMessage.success('对话已清空')
  } catch (error) {
    console.error('清空对话失败:', error)
    ElMessage.error('清空失败，请稍后重试')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 格式化消息
const formatMessage = (content) => {
  // 简单的文本格式化，将换行转换为<br>
  return content.replace(/\n/g, '<br>')
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 全局鼠标事件处理
const handleMouseMove = (e) => {
  if (isDragging.value) {
    onDrag(e)
  }
  if (isResizing.value) {
    handleResize(e)
  }
}

const handleMouseUp = () => {
  if (isDragging.value) {
    stopDrag()
  }
  if (isResizing.value) {
    isResizing.value = false
  }
}

// 监听鼠标移动和释放事件
onMounted(() => {
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
})
</script>

<style lang="scss" scoped>
.ai-chat-button {
  position: fixed;
  z-index: 9998;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: transform 0.3s;

  &:hover {
    transform: scale(1.1);
  }

  .button-text {
    font-size: 12px;
    color: #409eff;
    font-weight: 500;
  }

  .chat-badge {
    :deep(.el-badge__content) {
      top: -8px;
      right: -8px;
    }
  }
}

.ai-chat-window {
  position: fixed;
  z-index: 9999;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 300px;
  min-height: 400px;
  max-width: 800px;
  max-height: 900px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  cursor: move;
  user-select: none;

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;

    .header-title {
      font-weight: 500;
      font-size: 16px;
    }
  }

  .header-actions {
    display: flex;
    gap: 8px;

    .el-button {
      color: white;

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f5f5;
  min-height: 200px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 3px;
  }

  .empty-message {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    min-height: 300px;
  }

  .message-item {
    margin-bottom: 16px;

    .message-content {
      display: flex;
      gap: 12px;
      align-items: flex-start;

      .message-avatar {
        flex-shrink: 0;
      }

      .message-bubble {
        flex: 1;
        min-width: 0;

        .message-text {
          padding: 10px 14px;
          border-radius: 12px;
          line-height: 1.5;
          word-wrap: break-word;
          white-space: pre-wrap;
        }

        .message-time {
          font-size: 11px;
          color: #999;
          margin-top: 4px;
          padding: 0 4px;
        }
      }
    }

    &.message-user {
      .message-content {
        flex-direction: row-reverse;

        .message-bubble {
          .message-text {
            background: #409eff;
            color: white;
            border-bottom-right-radius: 4px;
          }

          .message-time {
            text-align: right;
          }
        }
      }
    }

    &.message-ai {
      .message-bubble {
        .message-text {
          background: white;
          color: #333;
          border-bottom-left-radius: 4px;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
}

.chat-input {
  padding: 12px;
  background: white;
  border-top: 1px solid #e4e7ed;

  .input-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 8px;
  }

  :deep(.el-textarea__inner) {
    resize: none;
  }
}

.resize-handle {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 20px;
  height: 20px;
  cursor: nwse-resize;
  background: linear-gradient(-45deg, transparent 30%, #ccc 30%, #ccc 35%, transparent 35%, transparent 65%, #ccc 65%, #ccc 70%, transparent 70%);

  &:hover {
    background: linear-gradient(-45deg, transparent 30%, #409eff 30%, #409eff 35%, transparent 35%, transparent 65%, #409eff 65%, #409eff 70%, transparent 70%);
  }
}

.is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>

