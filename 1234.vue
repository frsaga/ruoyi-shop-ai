<template>

    <h2>商品列表</h2>
  
    <table>
  
      <tr><th>商品名称</th><th>价格</th><th>操作</th></tr>
  
      <tr v-for="product in products" :key="product.id">
  
        <td>{{product.name}}</td>
  
        <td>{{product.price}}</td>
  
        <td><button @click="addCart(product)">加入购物车</button></td>
  
      </tr>
  
    </table>
  
    <h3 v-if="carts.length==0">购物车为空</h3>
  
    <div v-else>
  
      <h3>购物车（共{{totalCount}}件）</h3>
  
      <table>
  
        <tr><th>商品名称</th><th>单价</th><th>数量</th><th>总价</th><th>操作</th></tr>
  
        <tr v-for="product in carts" :key="product.id">
  
          <td>{{product.name}}</td>
  
          <td>{{product.price}}</td>
  
          <td><button @click="reduceNum(product.id)">-</button> {{product.num}} <button @click="addNum(product.id)">+</button></td>
  
          <td>{{product.num*product.price}}</td>
  
          <td><button @click="removeCart(product.id)">删除</button></td>
  
        </tr>
  
      </table>
  
      <p>总计：{{totalPrice}}元</p>
  
      <button @click="clearCarts">清空购物车</button>
  
    </div>
  
  </template>
  
  <script setup lang="ts">
  
  import {ref,computed} from "vue";
  
  
  
  const products = [
  
    { id: 1, name: '苹果', price: 6 },
  
    { id: 2, name: '香蕉', price: 3 },
  
    { id: 5, name: '牛奶', price: 12 },
  
    { id: 7, name: '面包', price: 8 }
  
  ];
  
  let carts=ref([])
  
  
  
  const addCart=(prod)=>{
  
    let index=carts.value.findIndex(item=>item.id==prod.id);
  
    if(index!=-1){
  
      carts.value[index].num++;
  
      return;
  
    }
  
    carts.value.push({...prod,num:1});
  
  }
  
  
  
  const addNum=(id)=>{
  
    let index=carts.value.findIndex(item=>item.id==id);
  
    carts.value[index].num++;
  
  }
  
  
  
  const reduceNum=(id)=>{
  
    let index=carts.value.findIndex(item=>item.id==id);
  
    if(carts.value[index].num>1){
  
      carts.value[index].num--;
  
    }else{
  
      removeCart(id);
  
    }
  
  }
  
  
  
  const removeCart=(id)=>{
  
    let index=carts.value.findIndex(item=>item.id==id);
  
    carts.value.splice(index,1);
  
  }
  
  
  
  const clearCarts=()=>{
  
    carts.value=[]
  
  }
  
  
  
  const totalCount=computed(()=>{
  
    let count=0;
  
    for(let i=0;i<carts.value.length;i++){
  
      count+=carts.value[i].num;
  
    }
  
    return count;
  
  })
  
  
  
  const totalPrice=computed(()=>{
  
    let price=0;
  
    for(let i=0;i<carts.value.length;i++){
  
      price+=carts.value[i].num*carts.value[i].price;
  
    }
  
    return price;
  
  })
  
  
  
  </script>
  
  