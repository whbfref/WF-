<template>
  <div class="landlord-properties">
    <div class="properties-container">
      <div class="page-header">
        <h1 class="page-title">房源管理</h1>
        <p class="page-desc">管理您的房产信息和出租状态</p>
      </div>

      <!-- 房源统计 -->
      <div class="property-stats">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ propertyStats.total || 0 }}</div>
              <div class="stat-label">总房源</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card available" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ propertyStats.available || 0 }}</div>
              <div class="stat-label">可出租</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card rented" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ propertyStats.rented || 0 }}</div>
              <div class="stat-label">已出租</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card maintenance" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ propertyStats.maintenance || 0 }}</div>
              <div class="stat-label">维修中</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 筛选和操作 -->
      <el-card class="filter-card" shadow="hover">
        <div class="filter-container">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索房源名称或地址"
            class="search-input"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-select v-model="filterStatus" placeholder="房源状态" @change="handleFilter">
            <el-option label="全部状态" value="" />
            <el-option label="可出租" value="VACANT" />
            <el-option label="已出租" value="RENTED" />
            <el-option label="维修中" value="MAINTENANCE" />
          </el-select>
          
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          
          <el-button type="success" @click="handleAddProperty">
            <el-icon><Plus /></el-icon>
            添加房源
          </el-button>
        </div>
      </el-card>

      <!-- 房源列表 -->
      <div class="properties-grid" v-loading="loading">
        <div v-if="propertiesList.length > 0" class="property-items">
          <el-card 
            v-for="property in propertiesList" 
            :key="property.id" 
            class="property-card"
            shadow="hover"
          >
            <div class="property-image">
              <el-image
                v-if="property.imageUrls && property.imageUrls.length > 0"
                :src="property.imageUrls[0]"
                :preview-src-list="property.imageUrls"
                fit="cover"
                :lazy="true"
                @error="handleImageError"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="image-slot" v-else>
                <el-icon><Picture /></el-icon>
              </div>
              <div class="property-status" :class="getStatusClass(property.status)">
                {{ getStatusText(property.status) }}
              </div>
            </div>
            
            <div class="property-content">
              <div class="property-header">
                <h3 class="property-name">{{ property.roomNumber || property.name }}</h3>
                <div class="property-price">¥{{ formatMoney(property.monthlyRent || property.price) }}/月</div>
              </div>
              
              <div class="property-info">
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ property.property?.address || property.address || '地址信息暂无' }}</span>
                </div>
                <div class="info-item">
                  <el-icon><House /></el-icon>
                  <span>{{ property.area }}㎡ · {{ property.layout || '布局信息暂无' }}</span>
                </div>
                <div v-if="property.tenant" class="info-item">
                  <el-icon><User /></el-icon>
                  <span>租户：{{ property.tenant.name }}</span>
                </div>
              </div>
              
              <div class="property-description">
                {{ property.description || '暂无描述' }}
              </div>
              
              <div class="property-meta">
                <div class="create-time">
                  发布时间：{{ formatTime(property.createTime) }}
                </div>
                <div v-if="property.rentStartTime" class="rent-time">
                  租期：{{ formatTime(property.rentStartTime) }} - {{ formatTime(property.rentEndTime) }}
                </div>
              </div>
            </div>
            
            <div class="property-actions">
              <el-button type="primary" size="small" @click="handleEdit(property)">
                编辑
              </el-button>
              
              <el-button 
                v-if="property.status === 'VACANT'"
                type="warning" 
                size="small" 
                @click="handleSetMaintenance(property)"
              >
                设为维修
              </el-button>
              
              <el-button 
                v-if="property.status === 'MAINTENANCE'"
                type="success" 
                size="small" 
                @click="handleSetAvailable(property)"
              >
                恢复出租
              </el-button>
              
              <el-button 
                v-if="property.status === 'RENTED'"
                type="info" 
                size="small" 
                @click="handleViewTenant(property)"
              >
                查看租户
              </el-button>
              
              <el-button type="danger" size="small" @click="handleDelete(property)">
                删除
              </el-button>
            </div>
          </el-card>
        </div>
        
        <el-empty v-else description="暂无房源信息" />
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :page-sizes="[12, 24, 48]"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 添加/编辑房源对话框 -->
    <el-dialog
      v-model="propertyDialogVisible"
      :title="isEdit ? '编辑房源' : '添加房源'"
      width="800px"
      :before-close="handleCloseDialog"
    >
      <el-form
        ref="propertyFormRef"
        :model="propertyForm"
        :rules="propertyRules"
        label-width="100px"
        class="property-form"
      >
        <el-form-item label="房源名称" prop="name">
          <el-input v-model="propertyForm.name" placeholder="请输入房源名称" />
        </el-form-item>
        
        <el-form-item label="房源地址" prop="address">
          <el-input v-model="propertyForm.address" placeholder="请输入详细地址" />
        </el-form-item>
        
        <div class="form-row">
          <el-form-item label="面积" prop="area">
            <el-input v-model="propertyForm.area" placeholder="平方米">
              <template #append>㎡</template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="房间数" prop="roomCount">
            <el-input-number v-model="propertyForm.roomCount" :min="1" :max="10" />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="月租金" prop="price">
            <el-input v-model="propertyForm.price" placeholder="元/月">
              <template #prepend>¥</template>
            </el-input>
          </el-form-item>
          
          <el-form-item label="房源状态" prop="status">
            <el-select v-model="propertyForm.status" placeholder="请选择状态">
              <el-option label="可出租" value="AVAILABLE" />
              <el-option label="维修中" value="MAINTENANCE" />
            </el-select>
          </el-form-item>
        </div>
        
        <el-form-item label="房源描述" prop="description">
          <el-input
            v-model="propertyForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入房源描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="房源图片">
          <el-upload
            class="property-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            accept="image/*"
          >
            <img v-if="propertyForm.imageUrl" :src="propertyForm.imageUrl" class="property-image-preview" />
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <div class="upload-text">上传房源图片</div>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseDialog">取消</el-button>
          <el-button 
            type="primary" 
            :loading="submitting"
            @click="handleSubmitProperty"
          >
            {{ isEdit ? '保存修改' : '添加房源' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  House, CircleCheck, User, Warning, Search, Refresh, Plus, Location, Picture 
} from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { getLandlordRooms, getLandlordRoomStats, updateRoomStatus, getRoomTenant } from '@/api/landlords';

const userStore = useUserStore();

// 响应式数据
const loading = ref(false);
const propertiesList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);

// 筛选条件
const searchKeyword = ref('');
const filterStatus = ref('');

// 房源统计
const propertyStats = ref({
  total: 0,
  available: 0,
  rented: 0,
  maintenance: 0
});

// 对话框相关
const propertyDialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const propertyFormRef = ref();

// 表单数据
const propertyForm = ref({
  id: null,
  name: '',
  address: '',
  area: '',
  roomCount: 1,
  price: '',
  status: 'AVAILABLE',
  description: '',
  imageUrl: ''
});

// 上传配置
const uploadAction = '/api/v1/upload/image';
const uploadHeaders = {
  'Authorization': `Bearer ${userStore.token}`
};

// 表单验证规则
const propertyRules = {
  name: [
    { required: true, message: '请输入房源名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在2到50个字符', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入房源地址', trigger: 'blur' },
    { min: 5, max: 200, message: '地址长度在5到200个字符', trigger: 'blur' }
  ],
  area: [
    { required: true, message: '请输入房源面积', trigger: 'blur' },
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的面积数值', trigger: 'blur' }
  ],
  roomCount: [
    { required: true, message: '请选择房间数', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入月租金', trigger: 'blur' },
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的金额', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择房源状态', trigger: 'change' }
  ]
};

// 格式化金额
const formatMoney = (amount) => {
  if (!amount) return '0';
  return Number(amount).toLocaleString();
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleDateString();
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'VACANT':
      return '可出租';
    case 'RENTED':
      return '已出租';
    case 'MAINTENANCE':
      return '维修中';
    default:
      return '未知';
  }
};

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 'VACANT':
      return 'available';
    case 'RENTED':
      return 'rented';
    case 'MAINTENANCE':
      return 'maintenance';
    default:
      return 'default';
  }
};

// 处理图片错误
const handleImageError = (error) => {
  console.warn('图片加载失败:', error);
};

// 获取房源列表
const fetchProperties = async () => {
  loading.value = true;
  
  try {
    console.log('开始获取房间列表...');
    const response = await getLandlordRooms({
      keyword: searchKeyword.value,
      status: filterStatus.value,
      page: currentPage.value,
      size: pageSize.value
    });
    
    console.log('API响应:', response);
    
    if (response && response.code === 200) {
      propertiesList.value = response.data.content || [];
      total.value = response.data.pageable?.total || 0;
      
      console.log('房间列表:', propertiesList.value);
      console.log('总数:', total.value);
      
      // 计算统计数据
      await calculateStats();
      
      ElMessage.success('房源列表加载成功');
    } else {
      console.error('API响应错误:', response);
      throw new Error(response?.message || '获取数据失败');
    }
  } catch (error) {
    console.error('获取房源列表失败:', error);
    ElMessage.error('获取房源列表失败，请稍后重试');
    
    // 设置空数据
    propertiesList.value = [];
    total.value = 0;
    propertyStats.value = {
      total: 0,
      available: 0,
      rented: 0,
      maintenance: 0
    };
  } finally {
    loading.value = false;
  }
};

// 计算统计数据
const calculateStats = async () => {
  try {
    console.log('开始获取统计数据...');
    const response = await getLandlordRoomStats();
    console.log('统计数据响应:', response);
    
    if (response && response.code === 200) {
      const stats = response.data;
      propertyStats.value = {
        total: stats.totalProperties || 0,
        available: stats.availableProperties || 0,
        rented: stats.rentedProperties || 0,
        maintenance: stats.maintenanceProperties || 0
      };
      console.log('统计数据:', propertyStats.value);
    } else {
      throw new Error('统计数据API响应错误');
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
    // 如果API失败，则基于当前列表计算
    const stats = {
      total: propertiesList.value.length,
      available: 0,
      rented: 0,
      maintenance: 0
    };
    
    propertiesList.value.forEach(property => {
      switch (property.status) {
        case 'VACANT':
          stats.available++;
          break;
        case 'RENTED':
          stats.rented++;
          break;
        case 'MAINTENANCE':
          stats.maintenance++;
          break;
      }
    });
    
    propertyStats.value = stats;
    console.log('基于列表计算的统计数据:', propertyStats.value);
  }
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchProperties();
};

// 筛选处理
const handleFilter = () => {
  currentPage.value = 1;
  fetchProperties();
};

// 重置筛选
const handleReset = () => {
  searchKeyword.value = '';
  filterStatus.value = '';
  currentPage.value = 1;
  fetchProperties();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchProperties();
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchProperties();
};

// 添加房源
const handleAddProperty = () => {
  isEdit.value = false;
  propertyForm.value = {
    id: null,
    name: '',
    address: '',
    area: '',
    roomCount: 1,
    price: '',
    status: 'AVAILABLE',
    description: '',
    imageUrl: ''
  };
  propertyDialogVisible.value = true;
};

// 编辑房源
const handleEdit = (property) => {
  isEdit.value = true;
  propertyForm.value = { ...property };
  propertyDialogVisible.value = true;
};

// 设为维修
const handleSetMaintenance = async (property) => {
  try {
    await ElMessageBox.confirm(
      `确定要将房间"${property.roomNumber}"设为维修状态吗？`,
    '确认操作',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
    );
    
    const response = await updateRoomStatus(property.id, 'MAINTENANCE');
    if (response && response.code === 200) {
    property.status = 'MAINTENANCE';
      await calculateStats();
      ElMessage.success('房间状态已更新为维修中');
    } else {
      throw new Error(response?.message || '更新失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('设置维修状态失败:', error);
      ElMessage.error('设置维修状态失败，请稍后重试');
    }
  }
};

// 恢复出租
const handleSetAvailable = async (property) => {
  try {
    await ElMessageBox.confirm(
      `确定要将房间"${property.roomNumber}"恢复为可出租状态吗？`,
    '确认操作',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
    );
    
    const response = await updateRoomStatus(property.id, 'VACANT');
    if (response && response.code === 200) {
      property.status = 'VACANT';
      await calculateStats();
      ElMessage.success('房间状态已更新为可出租');
    } else {
      throw new Error(response?.message || '更新失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复出租状态失败:', error);
      ElMessage.error('恢复出租状态失败，请稍后重试');
    }
  }
};

// 查看租户
const handleViewTenant = async (property) => {
  try {
    const response = await getRoomTenant(property.id);
    if (response && response.code === 200) {
      const tenantInfo = response.data;
      
      if (tenantInfo.hasTenant) {
        ElMessageBox.alert(
          `
          <div style="text-align: left;">
            <p><strong>租户姓名：</strong>${tenantInfo.tenantName}</p>
            <p><strong>联系电话：</strong>${tenantInfo.tenantPhone}</p>
            <p><strong>租期：</strong>${tenantInfo.startDate} 至 ${tenantInfo.endDate}</p>
            <p><strong>月租金：</strong>¥${tenantInfo.monthlyRent}</p>
            <p><strong>押金：</strong>¥${tenantInfo.deposit}</p>
            <p><strong>合同状态：</strong>${tenantInfo.contractStatus}</p>
          </div>
          `,
          '租户信息',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定'
          }
        );
      } else {
        ElMessage.info(tenantInfo.message || '该房间暂无租户');
      }
    } else {
      throw new Error(response?.message || '获取租户信息失败');
    }
  } catch (error) {
    console.error('获取租户信息失败:', error);
    ElMessage.error('获取租户信息失败，请稍后重试');
  }
};

// 删除房源
const handleDelete = (property) => {
  ElMessageBox.confirm(
    `确定要删除房源"${property.name}"吗？此操作不可恢复！`,
    '确认删除',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(() => {
    // 模拟API调用
    const index = propertiesList.value.findIndex(p => p.id === property.id);
    if (index !== -1) {
      propertiesList.value.splice(index, 1);
      total.value--;
      calculateStats();
      ElMessage.success('房源删除成功');
    }
  });
};

// 图片上传成功
const handleImageSuccess = (response) => {
  if (response.code === 200) {
    propertyForm.value.imageUrl = response.data;
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error(response.message || '图片上传失败');
  }
};

// 图片上传前验证
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!');
    return false;
  }
  return true;
};

// 提交房源
const handleSubmitProperty = async () => {
  try {
    await propertyFormRef.value.validate();
    
    submitting.value = true;
    
    // 模拟API调用
    if (isEdit.value) {
      const index = propertiesList.value.findIndex(p => p.id === propertyForm.value.id);
      if (index !== -1) {
        propertiesList.value[index] = { ...propertyForm.value };
        ElMessage.success('房源更新成功');
      }
    } else {
      const newProperty = {
        ...propertyForm.value,
        id: Date.now(),
        createTime: new Date().toISOString()
      };
      propertiesList.value.unshift(newProperty);
      total.value++;
      ElMessage.success('房源添加成功');
    }
    
    calculateStats();
    handleCloseDialog();
  } catch (error) {
    console.log('表单验证失败:', error);
  } finally {
    submitting.value = false;
  }
};

// 关闭对话框
const handleCloseDialog = () => {
  propertyDialogVisible.value = false;
  propertyForm.value = {
    id: null,
    name: '',
    address: '',
    area: '',
    roomCount: 1,
    price: '',
    status: 'AVAILABLE',
    description: '',
    imageUrl: ''
  };
};

// 组件挂载时执行
onMounted(() => {
  fetchProperties();
});
</script>

<style lang="scss" scoped>
.landlord-properties {
  .properties-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
  }

  .page-header {
    text-align: center;
    margin-bottom: 30px;

    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: #2c3e50;
      margin-bottom: 10px;
    }

    .page-desc {
      font-size: 16px;
      color: #7f8c8d;
      margin: 0;
    }
  }

  .property-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;

    @media (max-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }

    .stat-card {
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
      }

      .stat-content {
        display: flex;
        align-items: center;
        padding: 20px;

        .stat-icon {
          width: 50px;
          height: 50px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          margin-right: 15px;
        }

        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1;
          }

          .stat-label {
            font-size: 12px;
            color: #7f8c8d;
            margin-top: 5px;
          }
        }
      }

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
      }

      &.available .stat-icon {
        background: linear-gradient(135deg, #11998e, #38ef7d);
        color: white;
      }

      &.rented .stat-icon {
        background: linear-gradient(135deg, #ff6b6b, #ee5a24);
        color: white;
      }

      &.maintenance .stat-icon {
        background: linear-gradient(135deg, #feca57, #ff9ff3);
        color: white;
      }
    }
  }

  .filter-card {
    margin-bottom: 30px;

    .filter-container {
      display: flex;
      gap: 15px;
      align-items: center;
      flex-wrap: wrap;

      .search-input {
        width: 300px;
      }
    }
  }

  .properties-grid {
    .property-items {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 20px;

      .property-card {
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }

        .property-image {
          position: relative;
          height: 200px;
          overflow: hidden;
          border-radius: 8px 8px 0 0;

          .el-image {
            width: 100%;
            height: 100%;
          }

          .image-slot {
            width: 100%;
            height: 100%;
            background-color: #f5f7fa;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #c0c4cc;
            
            .el-icon {
              font-size: 40px;
            }
          }

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .property-status {
            position: absolute;
            top: 10px;
            right: 10px;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
            color: white;

            &.available {
              background: #27ae60;
            }

            &.rented {
              background: #e74c3c;
            }

            &.maintenance {
              background: #f39c12;
            }
          }
        }

        .property-content {
          padding: 20px;

          .property-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 15px;

            .property-name {
              font-size: 18px;
              font-weight: 600;
              color: #2c3e50;
              margin: 0;
              flex: 1;
              margin-right: 10px;
            }

            .property-price {
              font-size: 20px;
              font-weight: 700;
              color: #e74c3c;
            }
          }

          .property-info {
            margin-bottom: 15px;

            .info-item {
              display: flex;
              align-items: center;
              margin-bottom: 8px;
              font-size: 14px;
              color: #7f8c8d;

              &:last-child {
                margin-bottom: 0;
              }

              .el-icon {
                margin-right: 8px;
                color: #409eff;
              }
            }
          }

          .property-description {
            font-size: 14px;
            color: #606266;
            line-height: 1.5;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .property-meta {
            font-size: 12px;
            color: #999;

            .create-time,
            .rent-time {
              margin-bottom: 5px;
            }
          }
        }

        .property-actions {
          padding: 0 20px 20px;
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
        }
      }
    }
  }

  .pagination-container {
    margin-top: 30px;
    text-align: center;
  }
}

.property-form {
  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .property-uploader {
    .property-image-preview {
      width: 200px;
      height: 120px;
      object-fit: cover;
      border-radius: 8px;
      border: 2px solid #ddd;
    }

    .upload-placeholder {
      width: 200px;
      height: 120px;
      border: 2px dashed #ddd;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #409eff;
        color: #409eff;
      }

      .upload-icon {
        font-size: 24px;
        margin-bottom: 10px;
      }

      .upload-text {
        font-size: 14px;
      }
    }
  }
}
</style> 