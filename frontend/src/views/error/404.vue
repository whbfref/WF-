<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <img src="@/assets/images/404.png" alt="404" class="error-image">
      <h1 class="error-title">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <div class="action-buttons">
        <el-button type="primary" @click="goBack">返回上一页</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
  router.go(-1);
};

const goHome = () => {
  router.push('/');
};
</script>

<style lang="scss" scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
  
  .not-found-content {
    text-align: center;
    padding: 30px;
    
    .error-image {
      width: 240px;
      margin-bottom: 20px;
    }
    
    .error-title {
      font-size: 72px;
      font-weight: bold;
      color: #409eff;
      margin: 0;
      line-height: 1;
    }
    
    .error-message {
      font-size: 20px;
      color: #606266;
      margin: 20px 0 30px;
    }
    
    .action-buttons {
      display: flex;
      justify-content: center;
      gap: 16px;
    }
  }
}
</style> 