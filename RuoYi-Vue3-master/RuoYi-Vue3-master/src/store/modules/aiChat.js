const useAiChatStore = defineStore(
  'aiChat',
  {
    state: () => ({
      isOpen: false,
      isMinimized: false,
      unreadCount: 0
    }),
    actions: {
      openChat() {
        this.isOpen = true
        this.isMinimized = false
        this.unreadCount = 0
      },
      closeChat() {
        this.isOpen = false
      },
      toggleMinimize() {
        this.isMinimized = !this.isMinimized
      },
      setUnreadCount(count) {
        this.unreadCount = count
      },
      incrementUnreadCount() {
        this.unreadCount++
      }
    }
  })

export default useAiChatStore

