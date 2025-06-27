<template>
  <div class="properties-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>房屋管理</h2>
      <el-button type="primary" @click="handleAddProperty">
        <el-icon><Plus /></el-icon>
        添加房屋
      </el-button>
    </div>

    <!-- 搜索筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="房屋名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入房屋名称或地址"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="可租" value="AVAILABLE" />
            <el-option label="已租" value="RENTED" />
            <el-option label="维护中" value="MAINTENANCE" />
            <el-option label="下线" value="OFFLINE" />
          </el-select>
        </el-form-item>
        <el-form-item label="房东">
          <el-select v-model="searchForm.landlordId" placeholder="请选择房东" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option 
              v-for="landlord in landlords" 
              :key="landlord.id" 
              :label="landlord.username" 
              :value="landlord.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
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

    <!-- 房屋列表 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="propertyList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="房屋名称" min-width="150">
          <template #default="scope">
            <div class="property-name">
              <el-image
                v-if="scope.row.imageUrls && scope.row.imageUrls.length > 0"
                :src="scope.row.imageUrls[0]"
                :preview-src-list="scope.row.imageUrls"
                class="property-image"
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
              <div>
                <div class="name">{{ scope.row.title }}</div>
                <div class="address">{{ scope.row.address }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="landlordName" label="房东" width="120" />
        <el-table-column prop="propertyType" label="房屋类型" width="100" />
        <el-table-column prop="totalRooms" label="房间数" width="80" />
        <el-table-column prop="availableRooms" label="可租" width="70" />
        <el-table-column prop="rentedRooms" label="已租" width="70" />
        <el-table-column prop="monthlyRent" label="月租金" width="100">
          <template #default="scope">
            <span class="price">¥{{ scope.row.monthlyRent }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleViewRooms(scope.row)">
              <el-icon><House /></el-icon>
              房间
            </el-button>
            <el-button link type="primary" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="warning" @click="handleChangeStatus(scope.row)">
              <el-icon><Switch /></el-icon>
              状态
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑房屋对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      :title="editingProperty ? '编辑房屋' : '添加房屋'"
      width="600px"
      @close="resetAddForm"
    >
      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addFormRules"
        label-width="80px"
      >
        <el-form-item label="房屋名称" prop="title">
          <el-input v-model="addForm.title" placeholder="请输入房屋名称" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="addForm.address" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="房东" prop="landlordId">
          <el-select v-model="addForm.landlordId" placeholder="请选择房东" style="width: 100%">
            <el-option 
              v-for="landlord in landlords" 
              :key="landlord.id" 
              :label="landlord.username" 
              :value="landlord.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房屋类型" prop="propertyType">
          <el-select v-model="addForm.propertyType" placeholder="请选择房屋类型" style="width: 100%">
            <el-option label="普通住宅" value="RESIDENTIAL" />
            <el-option label="别墅" value="VILLA" />
            <el-option label="联排别墅" value="TOWNHOUSE" />
            <el-option label="单间" value="STUDIO" />
            <el-option label="合租房" value="SHARED" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总楼层" prop="totalFloors">
              <el-input-number v-model="addForm.totalFloors" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建筑面积" prop="area">
              <el-input-number v-model="addForm.area" :precision="2" :min="1" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="月租金" prop="monthlyRent">
              <el-input-number v-model="addForm.monthlyRent" :precision="2" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="押金" prop="deposit">
              <el-input-number v-model="addForm.deposit" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="addForm.description" type="textarea" :rows="3" placeholder="请输入房屋描述" />
        </el-form-item>
        <el-form-item label="设施" prop="facilities">
          <el-checkbox-group v-model="addForm.facilities">
            <el-checkbox label="wifi">WiFi</el-checkbox>
            <el-checkbox label="air_conditioning">空调</el-checkbox>
            <el-checkbox label="heating">暖气</el-checkbox>
            <el-checkbox label="washing_machine">洗衣机</el-checkbox>
            <el-checkbox label="refrigerator">冰箱</el-checkbox>
            <el-checkbox label="television">电视</el-checkbox>
            <el-checkbox label="parking">停车位</el-checkbox>
            <el-checkbox label="elevator">电梯</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="submitAddForm">
          {{ editingProperty ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 房间管理对话框 -->
    <el-dialog
      v-model="roomsDialogVisible"
      title="房间管理"
      width="900px"
    >
      <div class="rooms-header">
        <h3>{{ currentProperty?.title }} - 房间列表</h3>
        <el-button type="primary" @click="handleAddRoom">
          <el-icon><Plus /></el-icon>
          添加房间
        </el-button>
      </div>
      
      <el-table
        v-loading="roomsLoading"
        :data="roomList"
        style="width: 100%"
      >
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="type" label="房间类型" width="100" />
        <el-table-column prop="area" label="面积(㎡)" width="80" />
        <el-table-column prop="monthlyRent" label="月租金" width="100">
          <template #default="scope">
            <span class="price">¥{{ scope.row.monthlyRent }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getRoomStatusType(scope.row.status)">
              {{ getRoomStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="orientation" label="朝向" width="80" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleEditRoom(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="warning" @click="handleChangeRoomStatus(scope.row)">
              <el-icon><Switch /></el-icon>
              状态
            </el-button>
            <el-button link type="danger" @click="handleDeleteRoom(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search, Refresh, Edit, Delete, Switch, House, Picture } from '@element-plus/icons-vue';
import { getProperties, createProperty, updateProperty, deleteProperty, getPropertyRooms } from '@/api/properties';
import { getRooms, createRoom, updateRoom, deleteRoom } from '@/api/rooms';
import { getLandlords } from '@/api/landlords';

// 响应式数据
const loading = ref(false);
const roomsLoading = ref(false);
const addLoading = ref(false);
const propertyList = ref([]);
const roomList = ref([]);
const landlords = ref([]);
const selectedProperties = ref([]);
const currentProperty = ref(null);

// 对话框状态
const addDialogVisible = ref(false);
const roomsDialogVisible = ref(false);
const editingProperty = ref(null);

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  landlordId: ''
});

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
});

// 添加房屋表单
const addFormRef = ref();
const addForm = reactive({
  title: '',
  address: '',
  landlordId: '',
  propertyType: '',
  totalFloors: 1,
  area: 0,
  monthlyRent: 0,
  deposit: 0,
  description: '',
  facilities: []
});

// 表单验证规则
const addFormRules = {
  title: [
    { required: true, message: '请输入房屋名称', trigger: 'blur' },
    { min: 2, max: 100, message: '房屋名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 200, message: '地址长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  landlordId: [
    { required: true, message: '请选择房东', trigger: 'change' }
  ],
  propertyType: [
    { required: true, message: '请选择房屋类型', trigger: 'change' }
  ],
  monthlyRent: [
    { required: true, message: '请输入月租金', trigger: 'blur' },
    { type: 'number', min: 1, message: '月租金必须大于0', trigger: 'blur' }
  ]
};

// 初始化
onMounted(() => {
  fetchProperties();
  fetchLandlords();
});

// 获取房屋列表
const fetchProperties = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.current - 1,
      size: pagination.size,
      ...searchForm
    };
    const response = await getProperties(params);
    if (response.code === 200) {
      propertyList.value = response.data.content || [];
      pagination.total = response.data.totalElements || 0;
    }
  } catch (error) {
    ElMessage.error('获取房屋列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取房东列表
const fetchLandlords = async () => {
  try {
    const response = await getLandlords();
    if (response.code === 200) {
      landlords.value = response.data || [];
    }
  } catch (error) {
    console.error('获取房东列表失败:', error);
  }
};

// 获取房间列表
const fetchRooms = async (propertyId) => {
  roomsLoading.value = true;
  try {
    const response = await getRooms({ propertyId });
    if (response.code === 200) {
      roomList.value = response.data.rooms || [];
    }
  } catch (error) {
    ElMessage.error('获取房间列表失败');
  } finally {
    roomsLoading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  fetchProperties();
};

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    landlordId: ''
  });
  pagination.current = 1;
  fetchProperties();
};

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.current = 1;
  fetchProperties();
};

const handleCurrentChange = (current) => {
  pagination.current = current;
  fetchProperties();
};

// 选择变化
const handleSelectionChange = (selection) => {
  selectedProperties.value = selection;
};

// 添加房屋
const handleAddProperty = () => {
  editingProperty.value = null;
  resetAddForm();
  addDialogVisible.value = true;
};

// 编辑房屋
const handleEdit = (property) => {
  editingProperty.value = property;
  Object.assign(addForm, {
    ...property,
    facilities: property.facilities ? property.facilities.split(',') : []
  });
  addDialogVisible.value = true;
};

// 查看房间
const handleViewRooms = (property) => {
  currentProperty.value = property;
  roomsDialogVisible.value = true;
  fetchRooms(property.id);
};

// 更改状态
const handleChangeStatus = async (property) => {
  const statusOptions = [
    { value: 'AVAILABLE', label: '可租' },
    { value: 'RENTED', label: '已租' },
    { value: 'MAINTENANCE', label: '维护中' },
    { value: 'OFFLINE', label: '下线' }
  ];
  
  try {
    const { value: status } = await ElMessageBox.select(
      '请选择新状态',
      '更改房屋状态',
      {
        options: statusOptions,
        value: property.status
      }
    );
    
    const response = await updateProperty(property.id, { ...property, status });
    if (response.code === 200) {
      ElMessage.success('状态更新成功');
      fetchProperties();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('状态更新失败');
    }
  }
};

// 删除房屋
const handleDelete = (property) => {
  ElMessageBox.confirm(
    `确定要删除房屋"${property.title}"吗？此操作不可撤销！`,
    '确认删除',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await deleteProperty(property.id);
      if (response.code === 200) {
        ElMessage.success('删除成功');
        fetchProperties();
      }
    } catch (error) {
      ElMessage.error('删除失败');
    }
  });
};

// 提交表单
const submitAddForm = () => {
  addFormRef.value.validate(async (valid) => {
    if (valid) {
      addLoading.value = true;
      try {
        const formData = {
          ...addForm,
          facilities: addForm.facilities.join(',')
        };
        
        const response = editingProperty.value 
          ? await updateProperty(editingProperty.value.id, formData)
          : await createProperty(formData);
          
        if (response.code === 200) {
          ElMessage.success(editingProperty.value ? '更新成功' : '添加成功');
          addDialogVisible.value = false;
          fetchProperties();
        }
      } catch (error) {
        ElMessage.error(editingProperty.value ? '更新失败' : '添加失败');
      } finally {
        addLoading.value = false;
      }
    }
  });
};

// 重置表单
const resetAddForm = () => {
  Object.assign(addForm, {
    title: '',
    address: '',
    landlordId: '',
    propertyType: '',
    totalFloors: 1,
    area: 0,
    monthlyRent: 0,
    deposit: 0,
    description: '',
    facilities: []
  });
  addFormRef.value?.resetFields();
};

// 房间操作
const handleAddRoom = () => {
  // 实现添加房间逻辑
  ElMessage.info('添加房间功能开发中...');
};

const handleEditRoom = (room) => {
  // 实现编辑房间逻辑
  ElMessage.info('编辑房间功能开发中...');
};

const handleChangeRoomStatus = (room) => {
  // 实现房间状态更改逻辑
  ElMessage.info('房间状态更改功能开发中...');
};

const handleDeleteRoom = (room) => {
  // 实现删除房间逻辑
  ElMessage.info('删除房间功能开发中...');
};

// 工具函数
const getStatusType = (status) => {
  const statusMap = {
    'AVAILABLE': 'success',
    'RENTED': 'warning',
    'MAINTENANCE': 'info',
    'OFFLINE': 'danger'
  };
  return statusMap[status] || 'info';
};

const getStatusText = (status) => {
  const statusMap = {
    'AVAILABLE': '可租',
    'RENTED': '已租',
    'MAINTENANCE': '维护中',
    'OFFLINE': '下线'
  };
  return statusMap[status] || status;
};

const getRoomStatusType = (status) => {
  const statusMap = {
    'VACANT': 'success',
    'RENTED': 'warning',
    'MAINTENANCE': 'info'
  };
  return statusMap[status] || 'info';
};

const getRoomStatusText = (status) => {
  const statusMap = {
    'VACANT': '空闲',
    'RENTED': '已租',
    'MAINTENANCE': '维护中'
  };
  return statusMap[status] || status;
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  return new Date(dateTime).toLocaleString('zh-CN');
};

// 图片加载错误处理
const handleImageError = (error) => {
  console.warn('图片加载失败:', error);
};
</script>

<style lang="scss" scoped>
.properties-container {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      color: #303133;
    }
  }

  .search-card {
    margin-bottom: 20px;
    
    .search-form {
      margin-bottom: 0;
    }
  }

  .table-card {
    .property-name {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .property-image {
        width: 60px;
        height: 45px;
        border-radius: 4px;
        flex-shrink: 0;
      }
      
      .image-slot {
        width: 60px;
        height: 45px;
        border-radius: 4px;
        background-color: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #c0c4cc;
        flex-shrink: 0;
        
        .el-icon {
          font-size: 20px;
        }
      }
      
      .name {
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .address {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .price {
      font-weight: 500;
      color: #f56c6c;
    }
    
    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
  
  .rooms-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h3 {
      margin: 0;
      color: #303133;
    }
  }
}
</style> 