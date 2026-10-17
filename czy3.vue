<template>
    <!--  <h2>姓名:{{info.name}}<button @click="changeName">改变姓名</button></h2>-->
    <!--  <h2>年龄:{{info.age}}<button @click="changeAge">改变姓名</button></h2>-->
      <div>
        <input v-model="courseName" type="text" placeholder="请输入课程名称" />
        <button @click="addCourse">添加课程</button>
      </div>
      <select>
        <option v-for="item in course" :key="item.id">{{item.name}}</option>
      </select>
    </template>
    <script setup lang="ts">
    import {reactive, ref} from 'vue'
    // 数据
    let info = reactive({name: '张三', age: 18})
    let course = reactive([
      {id: '1', name: '计算机基础'},
      {id: '2', name: '计算机网络'},
      {id: '3', name: '大数据分析'}
    ])
    // 课程名称输入框
    let courseName = ref('')
    // 添加课程的方法
    const addCourse = () => {
      if (courseName.value.trim()) {
        const newId = String(course.length + 1)
        course.push({
          id: newId,
          name: courseName.value
        })
        courseName.value = '' // 清空输入框
      }
    }
    </script>
    <style scoped>
    </style>