<template>
  <div class="shop-page-container chat-page">
    <div class="page-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">客服沟通</h1>
        <p class="page-subtitle">与卖家或买家进行实时沟通</p>
      </div>

      <div class="chat-layout">
        <!-- 左侧：会话列表 -->
        <div class="session-list-glass">
          <div class="session-list-header">
            <h2 class="session-list-title">
              <el-icon><Message /></el-icon>
              会话列表
            </h2>
            <el-badge :value="totalUnreadCount" :hidden="totalUnreadCount === 0" class="unread-badge">
              <el-icon><Bell /></el-icon>
            </el-badge>
          </div>
          <div class="session-list" v-loading="sessionsLoading">
            <div 
              v-for="session in sessions" 
              :key="getSessionKey(session)"
              class="session-item-glass"
              :class="{ active: isSessionActive(session) }"
              @click="selectSession(session)"
            >
              <div class="session-info">
                <div class="session-name">
                  {{ isBuyer ? (session.sellerName || '卖家') : (session.buyerName || '买家') }}
                </div>
                <div class="session-store" v-if="session.storeName">
                  <el-icon><Shop /></el-icon>
                  {{ session.storeName }}
                </div>
                <div class="session-time">
                  {{ parseTime(session.createTime, '{m}-{d} {h}:{i}') }}
                </div>
              </div>
              <el-badge 
                :value="session.unreadCount || 0" 
                :hidden="!session.unreadCount || session.unreadCount === 0"
                class="session-badge"
              />
            </div>
            <div v-if="sessions.length === 0" class="empty-sessions">
              <el-empty description="暂无会话" :image-size="100" />
            </div>
          </div>
        </div>

        <!-- 右侧：聊天窗口 -->
        <div class="chat-window-glass">
          <div v-if="currentSessionId" class="chat-window-content">
            <div class="chat-header-glass">
              <div class="chat-header-info">
                <h3 class="chat-header-name">{{ currentSessionName }}</h3>
                <span class="chat-store" v-if="currentStoreName">
                  <el-icon><Shop /></el-icon>
                  {{ currentStoreName }}
                </span>
              </div>
            </div>
            
            <!-- 消息列表 -->
            <div class="message-list" ref="messageListRef" v-loading="messagesLoading">
              <div 
                v-for="message in messages" 
                :key="message.chatId"
                class="message-item"
                :class="{ 'message-sent': isMessageSent(message), 'message-received': !isMessageSent(message) }"
              >
                <div class="message-content">
                  <div class="message-bubble">
                    {{ message.content }}
                  </div>
                  <div class="message-time">
                    {{ parseTime(message.createTime, '{h}:{i}:{s}') }}
                  </div>
                </div>
              </div>
              <div v-if="messages.length === 0" class="empty-messages">
                <el-empty description="暂无消息" :image-size="100" />
              </div>
            </div>

            <!-- 输入框 -->
            <div class="message-input-glass">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="输入消息..."
                class="message-textarea"
                @keyup.ctrl.enter="sendMessage"
                @keyup.shift.enter="sendMessage"
              />
              <div class="input-actions">
                <el-button 
                  type="primary" 
                  class="send-button glow-button-primary"
                  @click="sendMessage" 
                  :disabled="!inputMessage.trim()"
                >
                  <el-icon><Promotion /></el-icon>
                  发送
                </el-button>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-chat-window">
            <el-empty description="请选择一个会话开始聊天" :image-size="150" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="Chat">
import { getBuyerSessions, getSellerSessions, getChatHistory, sendMessage as sendChatMessage, markAsRead, getUnreadCount } from "@/api/shop/chat";
import { getStorePublic } from "@/api/shop/store";
import { Message, Bell, Shop, Promotion } from '@element-plus/icons-vue';
import useUserStore from '@/store/modules/user';

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const sessions = ref([]);
const messages = ref([]);
const sessionsLoading = ref(false);
const messagesLoading = ref(false);
const currentSessionId = ref(null);
const currentSessionName = ref('');
const currentStoreName = ref('');
const inputMessage = ref('');
const messageListRef = ref(null);
const totalUnreadCount = ref(0);

// 判断当前用户是否为买家（通过检查角色和权限来判断）
const isBuyer = ref(true);

// 当前会话信息
const currentBuyerId = ref(null);
const currentSellerId = ref(null);
const currentStoreId = ref(null);

/** 查询会话列表 */
function getSessions() {
  sessionsLoading.value = true;
  
  // 先检查用户类型，然后调用对应的API
  checkUserType();
  const promise = isBuyer.value ? getBuyerSessions() : getSellerSessions();
  
  return promise.then(response => {
    const serverSessions = response.data || [];
    
    // 如果当前有活跃的会话，检查是否在后端返回的列表中
    // 如果不在，需要保留临时会话（因为可能还没有发送过消息）
    if (currentSellerId.value && currentStoreId.value) {
      // 先保存当前列表中的临时会话（如果存在）
      const existingTempSession = sessions.value.find(s => {
        if (isBuyer.value) {
          return s.sellerId === currentSellerId.value && s.storeId === currentStoreId.value;
        } else {
          return s.buyerId === currentBuyerId.value && s.storeId === currentStoreId.value;
        }
      });
      
      // 检查后端返回的列表中是否包含当前活跃的会话
      const hasCurrentSession = serverSessions.some(s => {
        if (isBuyer.value) {
          return s.sellerId === currentSellerId.value && s.storeId === currentStoreId.value;
        } else {
          return s.buyerId === currentBuyerId.value && s.storeId === currentStoreId.value;
        }
      });
      
      // 如果后端没有返回当前活跃的会话，但存在临时会话，则保留它
      if (!hasCurrentSession && existingTempSession) {
        // 将临时会话添加到列表顶部
        sessions.value = [existingTempSession, ...serverSessions];
      } else {
        // 后端有返回或没有临时会话，直接使用后端数据
        sessions.value = serverSessions;
      }
    } else {
      // 没有当前活跃的会话，直接使用后端数据
      sessions.value = serverSessions;
    }
    
    // 计算总未读数量
    totalUnreadCount.value = sessions.value.reduce((sum, session) => {
      return sum + (session.unreadCount || 0);
    }, 0);
    
    sessionsLoading.value = false;
    return sessions.value;
  }).catch(() => {
    sessionsLoading.value = false;
    return [];
  });
}

/** 获取会话的唯一标识key */
function getSessionKey(session) {
  if (isBuyer.value) {
    // 买家视角：使用 sellerId + storeId 作为唯一标识
    return `buyer_${session.sellerId}_${session.storeId || 'no_store'}`;
  } else {
    // 卖家视角：使用 buyerId + storeId 作为唯一标识
    return `seller_${session.buyerId}_${session.storeId || 'no_store'}`;
  }
}

/** 判断会话是否激活 */
function isSessionActive(session) {
  if (isBuyer.value) {
    return currentSellerId.value === session.sellerId && 
           currentStoreId.value === session.storeId;
  } else {
    return currentBuyerId.value === session.buyerId && 
           currentStoreId.value === session.storeId;
  }
}

/** 选择会话 */
function selectSession(session) {
  const currentUserId = userStore.id;
  
  if (isBuyer.value) {
    currentBuyerId.value = currentUserId;
    currentSellerId.value = session.sellerId;
    currentSessionId.value = session.sellerId;
    currentSessionName.value = session.sellerName || '卖家';
  } else {
    currentBuyerId.value = session.buyerId;
    currentSellerId.value = currentUserId;
    currentSessionId.value = session.buyerId;
    currentSessionName.value = session.buyerName || '买家';
  }
  currentStoreName.value = session.storeName || '';
  currentStoreId.value = session.storeId || null;
  
  // 加载聊天历史
  loadChatHistory();
  
  // 标记为已读
  markMessagesAsRead();
}

/** 加载聊天历史 */
function loadChatHistory() {
  if (!currentBuyerId.value || !currentSellerId.value) return;
  
  messagesLoading.value = true;
  getChatHistory(currentBuyerId.value, currentSellerId.value, currentStoreId.value).then(response => {
    messages.value = response.data || [];
    messagesLoading.value = false;
    
    // 滚动到底部
    nextTick(() => {
      scrollToBottom();
    });
  }).catch(() => {
    messagesLoading.value = false;
  });
}

/** 发送消息 */
function sendMessage() {
  if (!inputMessage.value.trim() || !currentBuyerId.value || !currentSellerId.value) {
    if (!currentBuyerId.value || !currentSellerId.value) {
      proxy.$modal.msgWarning("会话信息不完整，无法发送消息");
    }
    return;
  }
  
  const messageData = {
    buyerId: currentBuyerId.value,
    sellerId: currentSellerId.value,
    content: inputMessage.value.trim(),
    senderType: isBuyer.value ? '0' : '1',
    messageType: '0' // 文本消息
  };
  
  // 使用当前会话的storeId，如果没有则从路由参数获取
  let storeId = currentStoreId.value;
  if (!storeId) {
    storeId = route.query.storeId;
  }
  if (storeId) {
    messageData.storeId = parseInt(storeId);
  }
  
  sendChatMessage(messageData).then(() => {
    inputMessage.value = '';
    // 重新加载聊天历史
    loadChatHistory();
    // 刷新会话列表
    getSessions();
  }).catch((error) => {
    console.error('发送消息失败:', error);
    proxy.$modal.msgError(error.response?.data?.msg || "发送消息失败，请重试");
  });
}

/** 标记消息为已读 */
function markMessagesAsRead() {
  if (!currentBuyerId.value || !currentSellerId.value) return;
  
  const userType = isBuyer.value ? '0' : '1';
  markAsRead(currentBuyerId.value, currentSellerId.value, userType, currentStoreId.value).then(() => {
    // 刷新会话列表以更新未读数量
    getSessions();
  }).catch((error) => {
    // 静默处理错误，不显示错误提示（因为可能是所有消息都已经是已读状态）
    console.log('标记已读:', error);
  });
}

/** 判断消息是否为自己发送 */
function isMessageSent(message) {
  const currentUserId = userStore.id;
  return message.senderId === currentUserId;
}

/** 滚动到底部 */
function scrollToBottom() {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
  }
}

/** 检查用户类型 */
function checkUserType() {
  // 通过检查用户角色来判断：如果用户有seller角色，则是卖家
  try {
    const roles = userStore.roles || [];
    const permissions = userStore.permissions || [];
    
    // 检查是否有seller角色
    const hasSellerRole = roles.some(role => role.toLowerCase().includes('seller'));
    // 检查是否有店铺管理权限
    const hasStorePermission = permissions.includes('shop:store:list');
    
    // 如果有seller角色或店铺管理权限，则是卖家
    isBuyer.value = !hasSellerRole && !hasStorePermission;
    
    // 如果无法判断，默认设为买家
    if (roles.length === 0 && permissions.length === 0) {
      isBuyer.value = true;
    }
  } catch (e) {
    // 默认设为买家
    isBuyer.value = true;
  }
}

// 初始化
onMounted(() => {
  checkUserType();
  getSessions().then(() => {
    // 检查路由参数，如果是从店铺主页跳转过来的，自动选择会话
    nextTick(() => {
      const sellerId = route.query.sellerId;
      const storeId = route.query.storeId;
      
      if (sellerId && isBuyer.value) {
        // 从店铺主页或商品详情页跳转，自动选择卖家的会话
        const sellerIdNum = parseInt(sellerId);
        const storeIdNum = storeId ? parseInt(storeId) : null;
        
        // ⭐ 必须同时匹配 sellerId 和 storeId（同一卖家可能有多个店铺）
        const session = sessions.value.find(s => {
          if (s.sellerId !== sellerIdNum) return false;
          // 如果提供了storeId，必须完全匹配
          if (storeIdNum) {
            return s.storeId === storeIdNum;
          }
          // 如果没有提供storeId，匹配第一个会话（向后兼容）
          return true;
        });
        
        if (session) {
          // 如果已有会话，直接选择
          selectSession(session);
        } else {
          // 如果没有会话，创建一个新的会话选择
          const currentUserId = userStore.id;
          if (currentUserId) {
            currentBuyerId.value = currentUserId;
            currentSellerId.value = sellerIdNum;
            currentSessionId.value = sellerIdNum;
            currentSessionName.value = '卖家';
            currentStoreId.value = storeIdNum;
            
            // 如果有店铺ID，尝试获取店铺信息来设置店铺名称
            let storeNamePromise = Promise.resolve();
            if (storeId) {
              storeNamePromise = getStorePublic(storeId).then(response => {
                if (response.data && response.data.storeName) {
                  currentStoreName.value = response.data.storeName;
                  return response.data.storeName;
                }
                return '';
              }).catch(() => {
                return '';
              });
            }
            
            // 创建一个临时会话对象，添加到左侧列表，这样即使没有历史消息也能显示
            storeNamePromise.then(storeName => {
              const tempSession = {
                buyerId: currentUserId,
                sellerId: sellerIdNum,
                storeId: storeIdNum,
                sellerName: '卖家',
                storeName: storeName,
                unreadCount: 0,
                createTime: new Date()
              };
              
              // 检查是否已存在，如果不存在则添加到列表
              // ⭐ 必须同时匹配 sellerId 和 storeId
              const existingIndex = sessions.value.findIndex(s => {
                if (isBuyer.value) {
                  return s.sellerId === sellerIdNum && s.storeId === storeIdNum;
                } else {
                  return s.buyerId === sellerIdNum && s.storeId === storeIdNum;
                }
              });
              
              if (existingIndex === -1) {
                sessions.value.unshift(tempSession);
              } else {
                // 如果已存在，更新店铺名称等信息
                sessions.value[existingIndex].storeName = storeName;
              }
            });
            
            // 加载聊天历史（可能为空，但不影响）
            loadChatHistory();
            
            // 延迟刷新会话列表，等待后端可能有新数据
            // 注意：getSessions() 现在会自动保留当前活跃的临时会话
            setTimeout(() => {
              getSessions().then(() => {
                // 刷新后，确保当前会话仍然被选中
                // ⭐ 必须同时匹配 sellerId 和 storeId
                const updatedSession = sessions.value.find(s => {
                  if (isBuyer.value) {
                    return s.sellerId === sellerIdNum && s.storeId === storeIdNum;
                  } else {
                    return s.buyerId === sellerIdNum && s.storeId === storeIdNum;
                  }
                });
                if (updatedSession && currentSellerId.value === sellerIdNum && currentStoreId.value === storeIdNum) {
                  // 如果找到了会话，更新会话信息
                  currentStoreName.value = updatedSession.storeName || currentStoreName.value;
                  currentSessionName.value = (isBuyer.value ? updatedSession.sellerName : updatedSession.buyerName) || currentSessionName.value;
                }
              });
            }, 1000);
          }
        }
      }
    });
  });
  
  // 定时刷新会话列表和未读数量（每30秒）
  setInterval(() => {
    getSessions();
    // 如果当前有会话，也刷新消息
    if (currentBuyerId.value && currentSellerId.value) {
      loadChatHistory();
    }
  }, 30000);
});

// 监听输入框回车（已移除，使用按钮发送）
</script>

<style lang="scss" scoped>
@import '@/styles/shop-theme.scss';

.chat-page {
  .page-header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
  }

  .chat-layout {
    display: grid;
    grid-template-columns: 300px 1fr;
    gap: 20px;
    height: calc(100vh - 200px);
    min-height: 600px;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      height: auto;
      min-height: auto;
    }
  }

  .session-list-glass {
    @extend .glass-card;
    padding: 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    animation: slideDown 0.6s ease-out;
  }

  .session-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  }

  .session-list-title {
    font-size: 1.2rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    
    .el-icon {
      color: #667eea;
    }
  }

  .unread-badge {
    .el-icon {
      color: #667eea;
      font-size: 1.2rem;
    }
  }

  .session-list {
    flex: 1;
    overflow-y: auto;
    padding: 15px;
  }

  .session-item-glass {
    @extend .glass-card;
    padding: 15px;
    margin-bottom: 12px;
    cursor: pointer;
    transition: all 0.3s;
    border: 2px solid transparent;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    &:hover {
      border-color: rgba(102, 126, 234, 0.3);
      transform: translateX(5px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
    }
    
    &.active {
      border-color: #667eea;
      background: rgba(102, 126, 234, 0.1);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
    }
  }

  .session-info {
    flex: 1;
    min-width: 0;
  }

  .session-name {
    font-weight: 600;
    font-size: 0.95rem;
    color: rgba(0, 0, 0, 0.8);
    margin-bottom: 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .session-store {
    font-size: 0.85rem;
    color: rgba(0, 0, 0, 0.5);
    margin-bottom: 6px;
    display: flex;
    align-items: center;
    gap: 5px;
    
    .el-icon {
      color: #667eea;
      font-size: 0.9rem;
    }
  }

  .session-time {
    font-size: 0.8rem;
    color: rgba(0, 0, 0, 0.4);
  }

  .session-badge {
    margin-left: 10px;
  }

  .empty-sessions {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 200px;
  }

  .chat-window-glass {
    @extend .glass-card;
    padding: 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    animation: fadeInUp 0.6s ease-out;
  }

  .chat-window-content {
    display: flex;
    flex-direction: column;
    height: 100%;
  }

  .chat-header-glass {
    padding: 20px;
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  }

  .chat-header-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .chat-header-name {
    font-size: 1.2rem;
    font-weight: 700;
    margin: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .chat-store {
    font-size: 0.9rem;
    color: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    gap: 5px;
    
    .el-icon {
      color: #667eea;
    }
  }

  .message-list {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
  }

  .message-item {
    margin-bottom: 20px;
    display: flex;
    animation: fadeInUp 0.3s ease-out;
    
    &.message-sent {
      justify-content: flex-end;
      
      .message-bubble {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
        border-radius: 18px 18px 4px 18px;
      }
      
      .message-time {
        text-align: right;
        color: rgba(0, 0, 0, 0.4);
      }
    }
    
    &.message-received {
      justify-content: flex-start;
      
      .message-bubble {
        background: rgba(255, 255, 255, 0.95);
        color: rgba(0, 0, 0, 0.8);
        border: 1px solid rgba(102, 126, 234, 0.2);
        border-radius: 18px 18px 18px 4px;
      }
      
      .message-time {
        text-align: left;
        color: rgba(0, 0, 0, 0.4);
      }
    }
  }

  .message-content {
    max-width: 70%;
    display: flex;
    flex-direction: column;
  }

  .message-bubble {
    padding: 12px 18px;
    word-wrap: break-word;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }

  .message-time {
    font-size: 0.75rem;
    margin-top: 6px;
    padding: 0 4px;
  }

  .empty-messages {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    min-height: 300px;
  }

  .message-input-glass {
    padding: 20px;
    border-top: 1px solid rgba(102, 126, 234, 0.1);
    background: rgba(255, 255, 255, 0.95);
  }

  .message-textarea {
    :deep(.el-textarea__inner) {
      background: rgba(255, 255, 255, 0.9);
      border-radius: 12px;
      border: 1px solid rgba(102, 126, 234, 0.2);
      resize: none;
      transition: all 0.3s;
      
      &:focus {
        border-color: #667eea;
        box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
      }
    }
  }

  .input-actions {
    margin-top: 12px;
    display: flex;
    justify-content: flex-end;
  }

  .send-button {
    padding: 10px 30px;
    font-weight: 600;
    border-radius: 12px;
    
    .el-icon {
      margin-right: 5px;
    }
  }

  .empty-chat-window {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    min-height: 500px;
  }

  @media (max-width: 768px) {
    .chat-layout {
      grid-template-columns: 1fr;
    }
    
    .session-list-glass {
      max-height: 300px;
      margin-bottom: 20px;
    }
    
    .message-content {
      max-width: 85%;
    }
  }
}
</style>

