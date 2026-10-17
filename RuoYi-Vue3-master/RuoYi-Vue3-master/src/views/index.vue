<template>
  <div class="project-showcase">
    <!-- 粒子背景 -->
    <canvas ref="canvasRef" class="particles-canvas"></canvas>
    
    <!-- 主要内容 -->
    <div class="main-content">
      <!-- Hero Section -->
      <section class="hero-section" ref="heroRef">
        <div class="hero-content">
          <h1 class="hero-title" :class="{ 'animate-in': heroVisible }">
            <span class="gradient-text">淘淘网 Project Contributor</span>
          </h1>
          <p class="hero-subtitle" :class="{ 'animate-in': heroVisible }" style="animation-delay: 0.2s">
            基于Spring Boot + Vue3 的企业级全栈电商解决方案
          </p>
          <div class="hero-buttons" :class="{ 'animate-in': heroVisible }" style="animation-delay: 0.4s">
            <el-button type="primary" size="large" @click="scrollToSection('features')" class="glow-button">
              探索功能
            </el-button>
            <el-button size="large" @click="scrollToSection('tech')" class="outline-button">
              技术栈
            </el-button>
          </div>
        </div>
        
        <!-- 3D卡片展示 -->
        <div class="floating-cards" :class="{ 'animate-in': heroVisible }" style="animation-delay: 0.6s">
          <div class="card-3d" v-for="(card, index) in featureCards" :key="index" 
               :style="{ '--index': index, '--delay': index * 0.1 + 's' }">
            <div class="card-inner">
              <div class="card-front" :style="{ '--card-color': card.color }">
                <el-icon :size="40" :style="{ color: card.color }">
                  <component :is="card.icon" />
                </el-icon>
                <h3>{{ card.title }}</h3>
            </div>
              <div class="card-back">
                <p>{{ card.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 特性介绍 -->
      <section class="features-section" id="features" ref="featuresRef">
        <div class="section-header">
          <h2 class="section-title" :class="{ 'animate-in': featuresVisible }">
            核心特性
          </h2>
          <p class="section-subtitle" :class="{ 'animate-in': featuresVisible }" style="animation-delay: 0.1s">
            强大的功能模块，满足电商业务需求
          </p>
        </div>
        
        <div class="features-grid">
          <div 
            v-for="(feature, index) in features" 
            :key="index"
            class="feature-card"
            :class="{ 'animate-in': featuresVisible }"
            :style="{ 'animation-delay': (index % 3) * 0.1 + 's' }"
            @mouseenter="onCardHover(index)"
            @mouseleave="onCardLeave(index)"
          >
            <div class="feature-icon" :style="{ '--hover-scale': cardHovered === index ? 1.2 : 1 }">
              <div class="icon-bg" :style="{ background: feature.gradient }"></div>
              <el-icon :size="48">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
            <div class="feature-badge">{{ feature.badge }}</div>
          </div>
        </div>
      </section>

      <!-- 技术栈 -->
      <section class="tech-section" id="tech" ref="techRef">
        <div class="section-header">
          <h2 class="section-title" :class="{ 'animate-in': techVisible }">
            技术栈
          </h2>
          <p class="section-subtitle" :class="{ 'animate-in': techVisible }" style="animation-delay: 0.1s">
            现代化技术栈，高性能、可扩展
          </p>
        </div>
        
        <div class="tech-container">
          <!-- 后端技术 -->
          <div class="tech-column" :class="{ 'animate-in': techVisible }" style="animation-delay: 0.2s">
            <h3 class="tech-column-title">
              <el-icon><Back /></el-icon>
              后端技术
            </h3>
            <div class="tech-tags">
              <div 
                v-for="(tech, index) in backendTech" 
                :key="index"
                class="tech-tag"
                :style="{ '--delay': index * 0.05 + 's' }"
                @click="showTechInfo(tech)"
              >
                <span class="tech-name">{{ tech.name }}</span>
                <span class="tech-version">{{ tech.version }}</span>
          </div>
            </div>
          </div>
          
          <!-- 前端技术 -->
          <div class="tech-column" :class="{ 'animate-in': techVisible }" style="animation-delay: 0.4s">
            <h3 class="tech-column-title">
              <el-icon><Monitor /></el-icon>
              前端技术
            </h3>
            <div class="tech-tags">
              <div 
                v-for="(tech, index) in frontendTech" 
                :key="index"
                class="tech-tag"
                :style="{ '--delay': index * 0.05 + 's' }"
                @click="showTechInfo(tech)"
              >
                <span class="tech-name">{{ tech.name }}</span>
                <span class="tech-version">{{ tech.version }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 数据统计 -->
      <section class="stats-section" ref="statsRef">
        <div class="stats-container">
          <div 
            v-for="(stat, index) in stats" 
            :key="index"
            class="stat-card"
            :class="{ 'animate-in': statsVisible }"
            :style="{ 'animation-delay': index * 0.1 + 's' }"
          >
            <div class="stat-icon" :style="{ background: stat.gradient }">
              <el-icon :size="32">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">
                <span class="counter" :data-target="stat.value">{{ animatedStats[index] }}</span>
                <span v-if="stat.suffix" class="stat-suffix">{{ stat.suffix }}</span>
              </div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- 项目亮点 -->
      <section class="highlights-section" ref="highlightsRef">
        <div class="section-header">
          <h2 class="section-title" :class="{ 'animate-in': highlightsVisible }">
            项目亮点
          </h2>
        </div>
        
        <div class="highlights-timeline">
          <div 
            v-for="(highlight, index) in highlights" 
            :key="index"
            class="timeline-item"
            :class="{ 'animate-in': highlightsVisible, 'left': index % 2 === 0, 'right': index % 2 === 1 }"
            :style="{ 'animation-delay': index * 0.15 + 's' }"
          >
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <div class="timeline-year">{{ highlight.year }}</div>
              <h3 class="timeline-title">{{ highlight.title }}</h3>
              <p class="timeline-description">{{ highlight.description }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 返回顶部按钮 -->
    <transition name="fade">
      <div 
        v-if="showBackTop" 
        class="back-top" 
        @click="scrollToTop"
      >
        <el-icon :size="24"><Top /></el-icon>
          </div>
    </transition>
  </div>
</template>

<script setup name="ProjectShowcase">
import { ref, onMounted, onUnmounted, nextTick, getCurrentInstance } from 'vue'
import { 
  ShoppingCart, 
  User, 
  Shop, 
  ChatDotRound, 
  Setting, 
  Back,
  Monitor,
  Top,
  Document,
  Link,
  DataAnalysis,
  Goods
} from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()

// Refs
const canvasRef = ref(null)
const heroRef = ref(null)
const featuresRef = ref(null)
const techRef = ref(null)
const statsRef = ref(null)
const highlightsRef = ref(null)

// 可见性状态
const heroVisible = ref(false)
const featuresVisible = ref(false)
const techVisible = ref(false)
const statsVisible = ref(false)
const highlightsVisible = ref(false)
const showBackTop = ref(false)
const cardHovered = ref(-1)

// 动画统计数据
const animatedStats = ref([0, 0, 0, 0])

// 特性卡片 - 使用组件引用
const featureCards = ref([
  { icon: ShoppingCart, title: '购物车', color: '#409EFF', description: '完整的购物车系统' },
  { icon: Shop, title: '店铺管理', color: '#67C23A', description: '多店铺支持' },
  { icon: ChatDotRound, title: 'AI客服', color: '#E6A23C', description: '智能客服系统' },
  { icon: User, title: '用户中心', color: '#F56C6C', description: '完善的用户体系' }
])

// 特性列表
const features = ref([
  {
    icon: ShoppingCart,
    title: '商品管理',
    description: '完整的商品管理功能，支持分类、库存、价格管理等',
    badge: '核心功能',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    icon: Shop,
    title: '店铺系统',
    description: '多店铺支持，卖家可以创建和管理自己的店铺',
    badge: '多租户',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    icon: ChatDotRound,
    title: 'AI智能客服',
    description: '基于Qwen API的智能客服系统，支持数据库查询',
    badge: 'AI驱动',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    icon: User,
    title: '用户系统',
    description: '完善的用户权限管理，支持买家、卖家、管理员角色',
    badge: '权限控制',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  },
  {
    icon: Goods,
    title: '订单管理',
    description: '完整的订单流程，从下单到收货，支持评价系统',
    badge: '全流程',
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  },
  {
    icon: Setting,
    title: '系统配置',
    description: '灵活的系统配置，支持参数管理、字典管理等',
    badge: '可配置',
    gradient: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
  }
])

// 后端技术栈
const backendTech = ref([
  { name: 'Spring Boot', version: '2.5.x' },
  { name: 'Spring Security', version: '5.x' },
  { name: 'MyBatis', version: '3.5.x' },
  { name: 'MySQL', version: '8.0+' },
  { name: 'Redis', version: '6.x' },
  { name: 'JWT', version: '0.9.x' },
  { name: 'Druid', version: '1.2.x' },
  { name: 'Fastjson', version: '2.0.x' }
])

// 前端技术栈
const frontendTech = ref([
  { name: 'Vue 3', version: '3.x' },
  { name: 'Vue Router', version: '4.x' },
  { name: 'Pinia', version: '2.x' },
  { name: 'Element Plus', version: '2.x' },
  { name: 'Axios', version: '1.x' },
  { name: 'ECharts', version: '5.x' },
  { name: 'Vite', version: '4.x' },
  { name: 'TypeScript', version: '5.x' }
])

// 统计数据
const stats = ref([
  { icon: Document, value: 50, suffix: '+', label: '功能模块', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { icon: Link, value: 100, suffix: '+', label: 'API接口', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { icon: DataAnalysis, value: 99, suffix: '%', label: '代码覆盖率', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { icon: User, value: 1000, suffix: '+', label: '用户支持', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

// 项目亮点
const highlights = ref([
  {
    year: '2024',
    title: 'AI智能客服集成',
    description: '集成Qwen API，实现智能客服功能，支持数据库查询和上下文理解'
  },
  {
    year: '2024',
    title: '多店铺管理系统',
    description: '支持卖家创建多个店铺，完善的店铺管理和商品管理功能'
  },
  {
    year: '2024',
    title: '完整的订单流程',
    description: '从购物车到订单结算，从支付到收货评价，完整的电商业务流程'
  },
  {
    year: '2024',
    title: '权限精细化控制',
    description: '基于Spring Security的细粒度权限控制，支持角色和权限管理'
  }
])

// 粒子系统
let particles = []
let animationId = null

function initParticles() {
  const canvas = canvasRef.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  
  particles = []
  const particleCount = 100
  
  for (let i = 0; i < particleCount; i++) {
    particles.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      radius: Math.random() * 2 + 1,
      speedX: (Math.random() - 0.5) * 0.5,
      speedY: (Math.random() - 0.5) * 0.5,
      opacity: Math.random() * 0.5 + 0.2
    })
  }
  
  animateParticles()
}

function animateParticles() {
  const canvas = canvasRef.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 更新和绘制粒子
  particles.forEach((particle, i) => {
    particle.x += particle.speedX
    particle.y += particle.speedY
    
    // 边界检测
    if (particle.x < 0 || particle.x > canvas.width) particle.speedX *= -1
    if (particle.y < 0 || particle.y > canvas.height) particle.speedY *= -1
    
    // 绘制粒子
    ctx.beginPath()
    ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(64, 158, 255, ${particle.opacity})`
    ctx.fill()
    
    // 连接线
    particles.slice(i + 1).forEach(otherParticle => {
      const dx = particle.x - otherParticle.x
      const dy = particle.y - otherParticle.y
      const distance = Math.sqrt(dx * dx + dy * dy)
      
      if (distance < 150) {
        ctx.beginPath()
        ctx.moveTo(particle.x, particle.y)
        ctx.lineTo(otherParticle.x, otherParticle.y)
        ctx.strokeStyle = `rgba(64, 158, 255, ${0.2 * (1 - distance / 150)})`
        ctx.lineWidth = 1
        ctx.stroke()
      }
    })
  })
  
  animationId = requestAnimationFrame(animateParticles)
}

// 滚动监听
function handleScroll() {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  showBackTop.value = scrollTop > 300
  
  // 检查各个section是否可见
  checkVisibility(heroRef, heroVisible)
  checkVisibility(featuresRef, featuresVisible)
  checkVisibility(techRef, techVisible)
  checkVisibility(statsRef, statsVisible, () => animateCounters())
  checkVisibility(highlightsRef, highlightsVisible)
}

function checkVisibility(ref, visibleRef, callback = null) {
  if (!ref.value) return
  
  const rect = ref.value.getBoundingClientRect()
  const isVisible = rect.top < window.innerHeight && rect.bottom > 0
  
  if (isVisible && !visibleRef.value) {
    visibleRef.value = true
    if (callback) {
      nextTick(callback)
    }
  }
}

// 数字动画
function animateCounters() {
  stats.value.forEach((stat, index) => {
    const target = stat.value
    const duration = 2000
    const steps = 60
    const increment = target / steps
    let current = 0
    const timer = setInterval(() => {
      current += increment
      if (current >= target) {
        animatedStats.value[index] = target
        clearInterval(timer)
      } else {
        animatedStats.value[index] = Math.floor(current)
      }
    }, duration / steps)
  })
}

// 卡片悬浮
function onCardHover(index) {
  cardHovered.value = index
}

function onCardLeave(index) {
  cardHovered.value = -1
}

// 滚动到指定section
function scrollToSection(id) {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

// 返回顶部
function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 显示技术信息
function showTechInfo(tech) {
  proxy.$modal.msgInfo(`${tech.name} ${tech.version}`)
}

// 窗口大小改变
function handleResize() {
  const canvas = canvasRef.value
  if (canvas) {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
}

onMounted(() => {
  initParticles()
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('resize', handleResize)
  handleScroll() // 初始检查
  
  // 初始动画
  setTimeout(() => {
    heroVisible.value = true
  }, 100)
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.project-showcase {
  position: relative;
  min-height: 100vh;
  width: 100%;
  overflow-x: hidden;
  
  // 设置容器背景，确保覆盖整个容器
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  // 使用固定定位的背景层，确保覆盖整个视口
  // 这样无论页面内容多长，背景都会一直显示在视口中
  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    z-index: -2;
    pointer-events: none;
  }
  
  // 使用绝对定位的背景层作为补充，确保覆盖整个容器高度
  // 当内容超过视口高度时，这个背景层会延伸到容器底部
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    width: 100%;
    height: 100%;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    z-index: -1;
    pointer-events: none;
  }
}

.particles-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.main-content {
  position: relative;
  z-index: 1;
}

// Hero Section
.hero-section {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
  text-align: center;
  color: white;
}

.hero-content {
  max-width: 1200px;
  margin-bottom: 60px;
}

.hero-title {
  font-size: 4.5rem;
  font-weight: 800;
    margin-bottom: 20px;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
  
  @media (max-width: 768px) {
    font-size: 2.5rem;
  }
}

.gradient-text {
  background: linear-gradient(135deg, #fff 0%, #e0e7ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-shift 3s ease infinite;
  background-size: 200% 200%;
}

@keyframes gradient-shift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 40px;
  opacity: 0.9;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 0.9;
    transform: translateY(0);
  }
  
  @media (max-width: 768px) {
    font-size: 1.2rem;
  }
}

.hero-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
  
  @media (max-width: 768px) {
    flex-direction: column;
    align-items: center;
  }
}

.glow-button {
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    transform: translate(-50%, -50%);
    transition: width 0.6s, height 0.6s;
  }
  
  &:hover::before {
    width: 300px;
    height: 300px;
  }
}

.outline-button {
  background: transparent;
  border: 2px solid white;
  color: white;
  
  &:hover {
    background: white;
    color: #667eea;
  }
}

// 3D Cards
.floating-cards {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
  justify-content: center;
  opacity: 0;
  transform: translateY(50px);
  transition: all 1s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
  
  @media (max-width: 768px) {
    gap: 20px;
  }
}

.card-3d {
  perspective: 1000px;
  width: 150px;
  height: 200px;
  animation: float 3s ease-in-out infinite;
  animation-delay: var(--delay, 0s);
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotateY(0deg); }
  50% { transform: translateY(-20px) rotateY(5deg); }
}

.card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  transition: transform 0.6s;
  
  .card-3d:hover & {
    transform: rotateY(180deg);
  }
}

.card-front,
.card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.card-front {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  
  h3 {
    margin-top: 15px;
    font-size: 1.1rem;
    font-weight: 600;
  }
}

.card-back {
  background: rgba(255, 255, 255, 0.95);
  color: #333;
  transform: rotateY(180deg);
  
  p {
    text-align: center;
    font-size: 0.9rem;
    line-height: 1.6;
  }
}

// Sections
section {
  padding: 100px 20px;
  position: relative;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 3rem;
      font-weight: 700;
  margin-bottom: 20px;
  color: white;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
  
  @media (max-width: 768px) {
    font-size: 2rem;
  }
}

.section-subtitle {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.8);
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 0.8;
    transform: translateY(0);
  }
}

// Features Section
.features-section {
  background: transparent; // 使用透明背景，让父容器背景显示
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  opacity: 0;
  transform: translateY(50px);
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
  
  &:hover {
    transform: translateY(-10px) scale(1.02);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  }
}

.feature-icon {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 30px;
  transition: transform 0.4s;
  transform: scale(var(--hover-scale, 1));
}

.icon-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  opacity: 0.2;
  transform: scale(1.2);
  transition: transform 0.4s;
  
  .feature-card:hover & {
    transform: scale(1.5);
  }
}

.feature-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}

.feature-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.feature-badge {
  display: inline-block;
  padding: 5px 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

// Tech Section
.tech-section {
  background: transparent; // 使用透明背景，让父容器背景显示
}

.tech-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.tech-column {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  opacity: 0;
  transform: translateX(-50px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 1;
    transform: translateX(0);
  }
  
  &:nth-child(2) {
    transform: translateX(50px);
    
    &.animate-in {
      transform: translateX(0);
    }
  }
}

.tech-column-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 30px;
  color: #333;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.tech-tag {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s;
  opacity: 0;
  transform: scale(0.8);
  animation: tech-tag-in 0.5s ease forwards;
  animation-delay: var(--delay, 0s);
  
  &:hover {
    transform: scale(1.1) translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
  }
}

@keyframes tech-tag-in {
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.tech-name {
  font-weight: 600;
  font-size: 1rem;
  margin-bottom: 5px;
}

.tech-version {
  font-size: 0.85rem;
  opacity: 0.9;
}

// Stats Section
.stats-section {
  background: transparent; // 使用透明背景，让父容器背景显示
  padding: 80px 20px;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  transition: all 0.4s;
  opacity: 0;
  transform: translateY(50px) scale(0.9);
  
  &.animate-in {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  
  &:hover {
    transform: translateY(-10px) scale(1.05);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  }
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
}

.stat-content {
  .stat-value {
    font-size: 2.5rem;
    font-weight: 700;
    color: #333;
    margin-bottom: 10px;
    
    .counter {
      display: inline-block;
    }
    
    .stat-suffix {
      font-size: 1.5rem;
      margin-left: 5px;
    }
  }
  
  .stat-label {
    font-size: 1.1rem;
    color: #666;
    font-weight: 500;
  }
}

// Highlights Section
.highlights-section {
  background: transparent; // 使用透明背景，让父容器背景显示
  padding: 100px 20px;
  padding-bottom: 150px; // 增加底部padding，确保有足够空间
}

.highlights-timeline {
  position: relative;
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 0;
  
  &::before {
    content: '';
    position: absolute;
    left: 50%;
    top: 0;
    bottom: 0;
    width: 2px;
    background: rgba(255, 255, 255, 0.3);
    transform: translateX(-50%);
    
    @media (max-width: 768px) {
      left: 20px;
    }
  }
}

.timeline-item {
  position: relative;
  margin-bottom: 60px;
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.animate-in {
    opacity: 1;
  }
  
  &.left {
    padding-right: calc(50% + 40px);
    text-align: right;
    
    @media (max-width: 768px) {
      padding-right: 60px;
      padding-left: 0;
      text-align: left;
    }
    
    &.animate-in {
      animation: slide-in-left 0.8s ease forwards;
    }
  }
  
  &.right {
    padding-left: calc(50% + 40px);
    text-align: left;
    
    @media (max-width: 768px) {
      padding-left: 60px;
      padding-right: 0;
    }
    
    &.animate-in {
      animation: slide-in-right 0.8s ease forwards;
    }
  }
}

@keyframes slide-in-left {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slide-in-right {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.timeline-dot {
  position: absolute;
  left: 50%;
  top: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: white;
  border: 4px solid #667eea;
  transform: translateX(-50%);
  z-index: 1;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.5);
  
  @media (max-width: 768px) {
    left: 20px;
  }
}

.timeline-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.timeline-year {
  display: inline-block;
  padding: 5px 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 15px;
}

.timeline-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
}

.timeline-description {
  color: #666;
  line-height: 1.6;
}

// Back to Top
.back-top {
  position: fixed;
  bottom: 40px;
  right: 40px;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
  transition: all 0.3s;
  z-index: 1000;
  
  &:hover {
    transform: translateY(-5px) scale(1.1);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
