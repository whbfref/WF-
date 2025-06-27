<template>
  <div class="admin-landlords">
    <div class="page-header">
      <h1 class="page-title">房东信息管理</h1>
      <p class="page-desc">管理系统中的所有房东信息</p>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="hover">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="房东姓名">
          <el-input
            v-model="searchForm.realName"
            placeholder="请输入房东姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入联系电话"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="认证状态">
          <el-select
            v-model="searchForm.verified"
            placeholder="请选择认证状态"
            clearable
            style="width: 150px"
          >
            <el-option label="已认证" :value="true" />
            <el-option label="未认证" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 房东列表 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">房东列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加房东
            </el-button>
            <el-button @click="handleRefresh" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="user.username" label="用户名" width="120" />
        <el-table-column prop="user.phone" label="联系电话" width="130" />
        <el-table-column prop="user.email" label="邮箱" width="180" />
        <el-table-column label="银行信息" width="200">
          <template #default="scope">
            <div v-if="scope.row.bankCard">
              <div>{{ scope.row.bankName }}</div>
              <div class="bank-card">{{ maskBankCard(scope.row.bankCard) }}</div>
            </div>
            <span v-else class="text-muted">未填写</span>
          </template>
        </el-table-column>
        <el-table-column label="认证状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.verified ? 'success' : 'warning'">
              {{ scope.row.verified ? '已认证' : '未认证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="120">
          <template #default="scope">
            <el-rate
              v-if="scope.row.rating"
              :model-value="scope.row.rating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
            <span v-else class="text-muted">暂无评分</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="160">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="!scope.row.verified"
              type="success"
              size="small"
              @click="handleVerify(scope.row)"
            >
              认证
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 房东详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="房东详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentLandlord" class="landlord-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房东ID">
            {{ currentLandlord.id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户ID">
            {{ currentLandlord.userId }}
          </el-descriptions-item>
          <el-descriptions-item label="真实姓名">
            {{ currentLandlord.realName }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ currentLandlord.user?.username }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ currentLandlord.user?.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ currentLandlord.user?.email }}
          </el-descriptions-item>
          <el-descriptions-item label="身份证号">
            {{ maskIdCard(currentLandlord.idCard) }}
          </el-descriptions-item>
          <el-descriptions-item label="银行卡号">
            {{ maskBankCard(currentLandlord.bankCard) }}
          </el-descriptions-item>
          <el-descriptions-item label="银行名称">
            {{ currentLandlord.bankName }}
          </el-descriptions-item>
          <el-descriptions-item label="认证状态">
            <el-tag :type="currentLandlord.verified ? 'success' : 'warning'">
              {{ currentLandlord.verified ? '已认证' : '未认证' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评分">
            <el-rate
              v-if="currentLandlord.rating"
              :model-value="currentLandlord.rating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value} 分"
            />
            <span v-else>暂无评分</span>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatTime(currentLandlord.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 身份证照片 -->
        <div v-if="currentLandlord.verified" class="id-card-images">
          <h4>身份证照片</h4>
          <div class="images-grid">
            <div class="image-item">
              <div class="image-label">身份证正面</div>
              <img
                v-if="currentLandlord.idCardFrontUrl"
                :src="currentLandlord.idCardFrontUrl"
                class="id-card-image"
                @click="previewImage(currentLandlord.idCardFrontUrl)"
              />
              <div v-else class="no-image">暂无图片</div>
            </div>
            <div class="image-item">
              <div class="image-label">身份证背面</div>
              <img
                v-if="currentLandlord.idCardBackUrl"
                :src="currentLandlord.idCardBackUrl"
                class="id-card-image"
                @click="previewImage(currentLandlord.idCardBackUrl)"
              />
              <div v-else class="no-image">暂无图片</div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="50%" center>
      <div class="preview-container">
        <img :src="previewImageUrl" class="preview-image" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Search,
  Refresh,
  Plus
} from '@element-plus/icons-vue';
import { getLandlords, deleteLandlord, updateLandlordVerified } from '@/api/landlords';

// 响应式数据
const loading = ref(false);
const tableData = ref([]);
const selectedRows = ref([]);
const detailVisible = ref(false);
const previewVisible = ref(false);
const previewImageUrl = ref('');
const currentLandlord = ref(null);

// 搜索表单
const searchForm = ref({
  realName: '',
  phone: '',
  verified: null
});

// 分页数据
const pagination = ref({
  page: 1,
  size: 10,
  total: 0
});

// 获取房东列表
const fetchLandlords = async () => {
  try {
    loading.value = true;
    const params = {
      page: pagination.value.page,
      size: pagination.value.size,
      ...searchForm.value
    };
    
    const response = await getLandlords(params);
    if (response && response.code === 200) {
      tableData.value = response.data.content || [];
      pagination.value.total = response.data.total || 0;
    }
  } catch (error) {
    console.error('获取房东列表失败:', error);
    ElMessage.error('获取房东列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.value.page = 1;
  fetchLandlords();
};

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    realName: '',
    phone: '',
    verified: null
  };
  pagination.value.page = 1;
  fetchLandlords();
};

// 刷新
const handleRefresh = () => {
  fetchLandlords();
};

// 添加房东
const handleAdd = () => {
  ElMessage.info('添加房东功能开发中...');
};

// 查看房东详情
const handleView = (row) => {
  currentLandlord.value = row;
  detailVisible.value = true;
};

// 编辑房东
const handleEdit = (row) => {
  ElMessage.info('编辑房东功能开发中...');
};

// 认证房东
const handleVerify = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要认证房东 "${row.realName}" 吗？`,
      '确认认证',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const response = await updateLandlordVerified(row.id, true);
    if (response && response.code === 200) {
      ElMessage.success('房东认证成功');
      fetchLandlords();
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('认证房东失败:', error);
      ElMessage.error('认证房东失败');
    }
  }
};

// 删除房东
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除房东 "${row.realName}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const response = await deleteLandlord(row.id);
    if (response && response.code === 200) {
      ElMessage.success('删除成功');
      fetchLandlords();
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除房东失败:', error);
      ElMessage.error('删除房东失败');
    }
  }
};

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection;
};

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.value.size = size;
  pagination.value.page = 1;
  fetchLandlords();
};

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.value.page = page;
  fetchLandlords();
};

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url;
  previewVisible.value = true;
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};

// 脱敏身份证号
const maskIdCard = (idCard) => {
  if (!idCard) return '';
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2');
};

// 脱敏银行卡号
const maskBankCard = (bankCard) => {
  if (!bankCard) return '';
  return bankCard.replace(/(\d{4})\d+(\d{4})/, '$1****$2');
};

// 组件挂载时获取数据
onMounted(() => {
  fetchLandlords();
});
</script>

<style lang="scss" scoped>
.admin-landlords {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0 0 8px 0;
    }

    .page-desc {
      color: #7f8c8d;
      margin: 0;
    }
  }

  .search-card {
    margin-bottom: 20px;

    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-title {
        font-size: 16px;
        font-weight: 600;
      }

      .header-actions {
        display: flex;
        gap: 8px;
      }
    }

    .bank-card {
      font-size: 12px;
      color: #666;
    }

    .text-muted {
      color: #999;
    }

    .pagination-container {
      margin-top: 20px;
      text-align: right;
    }
  }

  .landlord-detail {
    .id-card-images {
      margin-top: 20px;

      h4 {
        margin-bottom: 16px;
        color: #2c3e50;
      }

      .images-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;

        .image-item {
          text-align: center;

          .image-label {
            margin-bottom: 8px;
            font-weight: 500;
            color: #666;
          }

          .id-card-image {
            width: 200px;
            height: 120px;
            object-fit: cover;
            border-radius: 6px;
            border: 1px solid #dcdfe6;
            cursor: pointer;
            transition: transform 0.3s;

            &:hover {
              transform: scale(1.05);
            }
          }

          .no-image {
            width: 200px;
            height: 120px;
            border: 2px dashed #dcdfe6;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #999;
            font-size: 14px;
          }
        }
      }
    }
  }

  .preview-container {
    text-align: center;

    .preview-image {
      max-width: 100%;
      max-height: 500px;
      border-radius: 8px;
    }
  }
}
</style> 