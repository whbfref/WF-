<template>
  <div class="test-api">
    <h1>API测试页面</h1>
    
    <div class="test-section">
      <h2>用户管理API测试</h2>
      <button @click="testUserManagement">测试获取用户列表</button>
      <pre v-if="userResult">{{ JSON.stringify(userResult, null, 2) }}</pre>
    </div>
    
    <div class="test-section">
      <h2>租金账单API测试</h2>
      <button @click="testRentBills">测试获取租金账单</button>
      <pre v-if="rentBillResult">{{ JSON.stringify(rentBillResult, null, 2) }}</pre>
    </div>
    
    <div class="test-section">
      <h2>物业费账单API测试</h2>
      <button @click="testUtilityBills">测试获取物业费账单</button>
      <pre v-if="utilityBillResult">{{ JSON.stringify(utilityBillResult, null, 2) }}</pre>
    </div>
    
    <div class="test-section">
      <h2>当前用户信息</h2>
      <button @click="testCurrentUser">获取当前用户信息</button>
      <pre v-if="currentUserResult">{{ JSON.stringify(currentUserResult, null, 2) }}</pre>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { getRegularUsers } from '@/api/users';
import { getUserRentBills } from '@/api/rentBills';
import { getUserUtilityBills } from '@/api/utilityBills';
import request from '@/api/request';

const userResult = ref(null);
const rentBillResult = ref(null);
const utilityBillResult = ref(null);
const currentUserResult = ref(null);

const testUserManagement = async () => {
  try {
    console.log('测试用户管理API...');
    const response = await getRegularUsers({
      page: 0,
      size: 10,
      role: 'USER'
    });
    userResult.value = response;
    console.log('用户管理API响应:', response);
  } catch (error) {
    console.error('用户管理API错误:', error);
    userResult.value = { error: error.message };
  }
};

const testRentBills = async () => {
  try {
    console.log('测试租金账单API...');
    const response = await getUserRentBills({
      page: 1,
      size: 10
    });
    rentBillResult.value = response;
    console.log('租金账单API响应:', response);
  } catch (error) {
    console.error('租金账单API错误:', error);
    rentBillResult.value = { error: error.message };
  }
};

const testUtilityBills = async () => {
  try {
    console.log('测试物业费账单API...');
    const response = await getUserUtilityBills({
      page: 1,
      size: 10
    });
    utilityBillResult.value = response;
    console.log('物业费账单API响应:', response);
  } catch (error) {
    console.error('物业费账单API错误:', error);
    utilityBillResult.value = { error: error.message };
  }
};

const testCurrentUser = async () => {
  try {
    console.log('测试当前用户API...');
    const response = await request({
      url: '/api/v1/users/me',
      method: 'get'
    });
    currentUserResult.value = response;
    console.log('当前用户API响应:', response);
  } catch (error) {
    console.error('当前用户API错误:', error);
    currentUserResult.value = { error: error.message };
  }
};
</script>

<style scoped>
.test-api {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.test-section h2 {
  margin-top: 0;
  color: #333;
}

button {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 10px;
}

button:hover {
  background: #0056b3;
}

pre {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
}
</style> 