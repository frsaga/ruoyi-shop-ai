<template>
  <div class="book-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书管理系统</span>
          <el-button type="primary" @click="handleAdd">新增图书</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.title" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="searchForm.category" placeholder="请选择类别" clearable>
            <el-option
              v-for="item in bookData.categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="filteredBooks" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="title" label="书名" min-width="200" />
        <el-table-column prop="author" label="作者" width="150" />
        <el-table-column prop="publisher" label="出版社" width="180" />
        <el-table-column prop="publishDate" label="出版日期" width="120" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="formData.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="formData.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="formData.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="formData.publishDate"
            type="date"
            placeholder="请选择出版日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="formData.category" placeholder="请选择类别" style="width: 100%">
            <el-option
              v-for="item in bookData.categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number
            v-model="formData.stock"
            :min="0"
            placeholder="请输入库存"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

const bookData = ref({
  // 图书类别数据
  categories: [
    {label: '计算机科学', value: '计算机科学'},
    {label: '文学小说', value: '文学小说'},
    {label: '历史传记', value: '历史传记'},
    {label: '社会科学', value: '社会科学'},
    {label: '自然科学', value: '自然科学'},
    {label: '艺术设计', value: '艺术设计'}
  ],

  books: [
    {"id":1,"isbn":"9787115546071","title":"JavaScript高级程序设计","author":"尼古拉斯·泽卡斯","publisher":"人民邮电出版社","publishDate":"2020-12-01","category":"计算机科学","stock":25},
    {"id":2,"isbn":"9787115428028","title":"Python编程：从入门到实践","author":"埃里克·马瑟斯","publisher":"人民邮电出版社","publishDate":"2016-07-01","category":"计算机科学","stock":18},
    {"id":3,"isbn":"9787508095216","title":"追风筝的人","author":"卡勒德·胡赛尼","publisher":"中信出版社","publishDate":"2018-05-01","category":"文学小说","stock":32},
    {"id":4,"isbn":"9787530217867","title":"平凡的世界","author":"路遥","publisher":"北京十月文艺出版社","publishDate":"2017-03-01","category":"文学小说","stock":45},
    {"id":5,"isbn":"9787508089059","title":"人类简史：从动物到上帝","author":"尤瓦尔·赫拉利","publisher":"中信出版社","publishDate":"2017-02-01","category":"历史传记","stock":12},
    {"id":6,"isbn":"9787508093026","title":"未来简史","author":"尤瓦尔·赫拉利","publisher":"中信出版社","publishDate":"2017-02-01","category":"社会科学","stock":8},
    {"id":7,"isbn":"9787535794567","title":"时间简史","author":"史蒂芬·霍金","publisher":"湖南科学技术出版社","publishDate":"2018-06-01","category":"自然科学","stock":15},
    {"id":8,"isbn":"9787508092005","title":"解忧杂货店","author":"东野圭吾","publisher":"南海出版公司","publishDate":"2014-05-01","category":"文学小说","stock":29}
  ]
})

// 搜索表单
const searchForm = reactive({
  title: '',
  author: '',
  category: ''
})

// 过滤后的图书列表
const filteredBooks = computed(() => {
  let result = bookData.value.books

  if (searchForm.title) {
    result = result.filter(book => 
      book.title.toLowerCase().includes(searchForm.title.toLowerCase())
    )
  }

  if (searchForm.author) {
    result = result.filter(book => 
      book.author.toLowerCase().includes(searchForm.author.toLowerCase())
    )
  }

  if (searchForm.category) {
    result = result.filter(book => book.category === searchForm.category)
  }

  return result
})

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增图书')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive<{
  id: number | null
  isbn: string
  title: string
  author: string
  publisher: string
  publishDate: string
  category: string
  stock: number
}>({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  publishDate: '',
  category: '',
  stock: 0
})

// 表单验证规则
const formRules: FormRules = {
  isbn: [
    { required: true, message: '请输入ISBN', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社', trigger: 'blur' }
  ],
  publishDate: [
    { required: true, message: '请选择出版日期', trigger: 'change' }
  ],
  category: [
    { required: true, message: '请选择类别', trigger: 'change' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存不能小于0', trigger: 'blur' }
  ]
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.isbn = ''
  formData.title = ''
  formData.author = ''
  formData.publisher = ''
  formData.publishDate = ''
  formData.category = ''
  formData.stock = 0
  formRef.value?.clearValidate()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增图书'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑图书'
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除《${row.title}》吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = bookData.value.books.findIndex(book => book.id === row.id)
    if (index > -1) {
      bookData.value.books.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (isEdit.value) {
        // 编辑
        if (formData.id !== null) {
          const index = bookData.value.books.findIndex(book => book.id === formData.id)
          if (index > -1) {
            bookData.value.books[index] = { ...formData, id: formData.id }
            ElMessage.success('编辑成功')
          }
        }
      } else {
        // 新增
        const maxId = Math.max(...bookData.value.books.map(book => book.id), 0)
        const newBook = {
          ...formData,
          id: maxId + 1
        }
        bookData.value.books.push(newBook)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 搜索
const handleSearch = () => {
  // 搜索逻辑已在 computed 中实现
}

// 重置搜索
const handleReset = () => {
  searchForm.title = ''
  searchForm.author = ''
  searchForm.category = ''
}
</script>

<style scoped>
.book-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

:deep(.el-table) {
  margin-top: 20px;
}
</style>

