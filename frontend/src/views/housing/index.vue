<template>
  <div class="housing-management">
    <h2 class="page-title">房屋管理</h2>
    
    <div class="filter-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索房间"
        class="search-input"
        clearable
        @clear="handleSearch"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="selectedStatus" placeholder="房间状态" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="已出租" value="RENTED" />
        <el-option label="待出租" value="VACANT" />
        <el-option label="维修中" value="MAINTENANCE" />
      </el-select>
      <el-button type="primary" @click="handleAddHousing">添加房间</el-button>
      <el-dropdown v-if="selectedHousings.length > 0" @command="handleBatchAction">
        <el-button type="warning">
          批量操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="batchDelete">批量删除</el-dropdown-item>
            <el-dropdown-item command="batchAvailable">批量设为待出租</el-dropdown-item>
            <el-dropdown-item command="batchMaintenance">批量设为维修中</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    
    <!-- 选择提示 -->
    <div v-if="selectedHousings.length > 0" class="selection-info">
      <el-alert
        :title="`已选择 ${selectedHousings.length} 个房间`"
        type="info"
        show-icon
        :closable="false"
      />
    </div>
    
    <el-row :gutter="20" class="housing-list" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="house in housingList" :key="house.id" class="housing-item-col">
        <el-card class="housing-item" :body-style="{ padding: '0px' }" :class="{ 'selected': selectedHousings.includes(house.id) }">
          <div class="housing-checkbox">
            <el-checkbox v-model="selectedHousings" :label="house.id" />
          </div>
          <div class="housing-image">
            <el-image 
              :src="house.coverImage || (house.imageUrls && house.imageUrls.length > 0 ? house.imageUrls[0] : null) || defaultHouseImage" 
              fit="cover"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="housing-status" :class="house.status.toLowerCase()">
              {{ getStatusText(house.status) }}
            </div>
          </div>
          <div class="housing-content">
            <h3 class="housing-title">{{ house.roomNumber }}</h3>
            <div class="housing-info">
              <p><el-icon><Location /></el-icon> {{ house.propertyTitle }}</p>
              <p><el-icon><House /></el-icon> {{ house.area }}㎡ | {{ house.floor }}楼 | {{ house.type || '标准间' }}</p>
              <p class="housing-price"><el-icon><Money /></el-icon> {{ house.monthlyRent }}元/月</p>
            </div>
            <div class="housing-actions">
              <div class="action-row">
                <el-button type="primary" size="small" @click="handleEdit(house)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(house)">删除</el-button>
              </div>
              <el-button v-if="house.status === 'VACANT'" type="success" size="small" class="rent-button" @click="handleRent(house)">
                出租
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-empty v-if="housingList.length === 0 && !loading" description="暂无房间数据" />
    </el-row>
    
    <div class="pagination-container">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :page-sizes="[8, 16, 24, 32]"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
    
    <!-- 添加/编辑房间对话框 -->
    <el-dialog
      :title="dialogStatus === 'create' ? '添加房间' : '编辑房间'"
      v-model="dialogVisible"
      width="600px"
    >
      <el-form :model="housingForm" :rules="rules" ref="housingFormRef" label-width="100px">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="housingForm.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="所属楼栋" prop="propertyId">
          <el-select v-model="housingForm.propertyId" placeholder="请选择所属楼栋" style="width: 100%">
            <el-option 
              v-for="property in propertyList" 
              :key="property.id" 
              :label="property.title" 
              :value="property.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房间面积" prop="area">
          <el-input-number v-model="housingForm.area" :min="1" :precision="2" :step="0.5" placeholder="请输入房间面积" />
          <span class="unit">㎡</span>
        </el-form-item>
        <el-form-item label="房间类型" prop="type">
          <el-select v-model="housingForm.type" placeholder="请选择房间类型" style="width: 100%">
            <el-option label="标准间" value="STANDARD" />
            <el-option label="大床房" value="KING" />
            <el-option label="双床房" value="TWIN" />
            <el-option label="套房" value="SUITE" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="housingForm.floor" :min="1" :precision="0" :step="1" placeholder="请输入楼层" />
        </el-form-item>
        <el-form-item label="朝向" prop="orientation">
          <el-select v-model="housingForm.orientation" placeholder="请选择朝向" style="width: 100%">
            <el-option label="南" value="SOUTH" />
            <el-option label="北" value="NORTH" />
            <el-option label="东" value="EAST" />
            <el-option label="西" value="WEST" />
            <el-option label="东南" value="SOUTHEAST" />
            <el-option label="西南" value="SOUTHWEST" />
            <el-option label="东北" value="NORTHEAST" />
            <el-option label="西北" value="NORTHWEST" />
          </el-select>
        </el-form-item>
        <el-form-item label="月租价格" prop="monthlyRent">
          <el-input-number v-model="housingForm.monthlyRent" :min="1" :precision="0" :step="100" placeholder="请输入月租价格" />
          <span class="unit">元/月</span>
        </el-form-item>
        <el-form-item label="房间图片" prop="imageUrl">
          <el-upload
            class="housing-image-upload"
            :action="uploadAction"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :on-exceed="handleExceed"
            :on-remove="handleRemoveImage"
            :limit="5"
            :file-list="fileList"
            list-type="picture-card"
            multiple
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                最多上传5张图片，只能上传 jpg/png 文件，且不超过 2MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="房间状态" prop="status">
          <el-select v-model="housingForm.status" placeholder="请选择房间状态">
            <el-option label="已出租" value="RENTED" />
            <el-option label="待出租" value="VACANT" />
            <el-option label="维修中" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
        <el-form-item label="设施配置" prop="facilities">
          <el-input
            v-model="housingForm.facilities"
            type="textarea"
            :rows="2"
            placeholder="请输入设施配置信息"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="housingForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入房间描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Picture, Location, House, Money, Plus, ArrowDown } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { 
  getRooms, 
  getRoomById, 
  createRoom, 
  updateRoom, 
  deleteRoom, 
  updateRoomStatus, 
  uploadRoomImage,
  deleteRoomImage,
  setRoomCoverImage,
  getRoomImages,
  batchDeleteRooms,
  batchUpdateRoomStatus
} from '@/api/rooms';

// 默认房源图片
const defaultHouseImage = 'https://via.placeholder.com/300x200?text=房源图片';

// 用户 store
const userStore = useUserStore();

// 上传文件的请求头
const uploadHeaders = computed(() => {
  return {
    Authorization: `Bearer ${userStore.token}`
  };
});

// 上传地址
const uploadAction = computed(() => {
  // 如果是编辑模式且有房间ID，使用房间专用的上传接口
  if (dialogStatus.value === 'update' && housingForm.id) {
    return `/api/v1/rooms/${housingForm.id}/images`;
  }
  // 创建模式下使用通用上传接口，稍后再关联到房间
  return '/api/v1/upload/housing-image';
});

// 批量上传房间图片（用于创建房间后）
const uploadRoomImages = async (roomId) => {
  if (!fileList.value || fileList.value.length === 0) {
    return;
  }
  
  try {
    for (let i = 0; i < fileList.value.length; i++) {
      const file = fileList.value[i];
      
      // 跳过已经有imageId的图片（已经上传过的）
      if (file.imageId) {
        continue;
      }
      
      // 如果文件有原始文件对象，重新上传到房间
      if (file.raw) {
        const formData = new FormData();
        formData.append('file', file.raw);
        
        const response = await uploadRoomImage(roomId, formData);
        if (response && response.code === 200) {
          // 更新文件信息
          file.imageId = response.data.id;
          file.url = response.data.imageUrl;
          
          // 如果是第一张图片，设为封面
          if (i === 0) {
            await setRoomCoverImage(roomId, response.data.id);
          }
        }
      }
    }
  } catch (error) {
    console.error('批量上传房间图片失败:', error);
    ElMessage.warning('部分图片上传失败，请重新编辑房间添加图片');
  }
};

// 搜索和筛选条件
const searchQuery = ref('');
const selectedStatus = ref('');

// 分页相关
const currentPage = ref(1);
const pageSize = ref(8);
const total = ref(0);

// 房间列表数据
const housingList = ref([]);
const loading = ref(false);

// 楼栋列表数据
const propertyList = ref([]);

// 对话框相关
const dialogVisible = ref(false);
const dialogStatus = ref('create');
const housingFormRef = ref(null);
const housingForm = reactive({
  id: null,
  roomNumber: '',
  propertyId: null,
  area: 0,
  type: 'STANDARD',
  floor: 1,
  orientation: 'SOUTH',
  monthlyRent: 0,
  status: 'VACANT',
  imageUrl: '',
  facilities: '',
  description: ''
});

// 上传图片列表
const fileList = ref([]);

// 表单验证规则
const rules = {
  roomNumber: [
    { required: true, message: '请输入房间号', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  propertyId: [
    { required: true, message: '请选择所属楼栋', trigger: 'change' }
  ],
  area: [
    { required: true, message: '请输入房间面积', trigger: 'blur' },
    { type: 'number', min: 1, message: '面积必须大于0', trigger: 'blur' }
  ],
  monthlyRent: [
    { required: true, message: '请输入租金价格', trigger: 'blur' },
    { type: 'number', min: 1, message: '价格必须大于0', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择房间状态', trigger: 'change' }
  ]
};

// 提交状态
const submitLoading = ref(false);

// 组件挂载时执行
onMounted(() => {
  // 获取房间列表数据
  fetchHousingList();
  // 获取楼栋列表数据
  fetchPropertyList();
});

// 获取房间状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'RENTED':
      return '已出租';
    case 'VACANT':
      return '待出租';
    case 'MAINTENANCE':
      return '维修中';
    default:
      return '未知状态';
  }
};

// 获取房间列表数据
const fetchHousingList = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1, // 后端分页从0开始
      size: pageSize.value,
      keyword: searchQuery.value,
      status: selectedStatus.value || null
    };
    
    const response = await getRooms(params);
    if (response && response.code === 200) {
      // 转换从后端API返回的数据格式
      const data = response.data;
      const rooms = data.content || [];
      
      // 转换房间数据格式
      housingList.value = rooms.map(room => ({
        id: room.id,
        roomNumber: room.roomNumber || '未知房间号',
        propertyTitle: room.propertyTitle || '未知楼栋',
        propertyAddress: room.propertyAddress || '',
        area: room.area || 0,
        floor: room.floor || 1,
        type: room.type || 'STANDARD',
        orientation: room.orientation || '',
        monthlyRent: room.monthlyRent || 0,
        status: room.status || 'VACANT',
        imageUrl: room.imageUrls && room.imageUrls.length > 0 ? room.imageUrls[0] : null,
        imageUrls: room.imageUrls || [],
        coverImage: room.coverImage || null,
        facilities: room.facilities || '',
        description: room.description || '',
        propertyId: room.propertyId,
        tenantName: room.tenantName || null,
        tenantId: room.tenantId || null
      }));
      
      // 设置总数
      total.value = data.totalElements || rooms.length;
    } else {
      ElMessage.error(response?.message || '获取房间列表失败');
    }
  } catch (error) {
    console.error('获取房间列表出错:', error);
    ElMessage.error('获取房间列表失败，请稍后重试');
    housingList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 获取楼栋列表数据
const fetchPropertyList = async () => {
  try {
    // 这里需要导入properties API
    const { getProperties } = await import('@/api/properties');
    const response = await getProperties({ page: 0, size: 100 });
    if (response && response.code === 200) {
      const data = response.data;
      const properties = data.content || [];
      propertyList.value = properties.map(property => ({
        id: property.id,
        title: property.title || '未命名楼栋'
      }));
    } else {
      console.warn('获取楼栋列表失败:', response);
      propertyList.value = [];
    }
  } catch (error) {
    console.error('获取楼栋列表出错:', error);
    propertyList.value = [];
  }
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchHousingList();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchHousingList();
};

// 添加房间
const handleAddHousing = () => {
  resetForm();
  dialogStatus.value = 'create';
  dialogVisible.value = true;
};

// 编辑房间
const handleEdit = async (house) => {
  resetForm();
  dialogStatus.value = 'update';
  
  // 填充表单
  housingForm.id = house.id;
  housingForm.roomNumber = house.roomNumber;
  housingForm.propertyId = house.propertyId;
  housingForm.area = house.area;
  housingForm.type = house.type;
  housingForm.floor = house.floor;
  housingForm.orientation = house.orientation;
  housingForm.monthlyRent = house.monthlyRent;
  housingForm.status = house.status;
  housingForm.imageUrl = house.imageUrl;
  housingForm.facilities = house.facilities;
  housingForm.description = house.description;
  
  // 从后端获取完整的图片信息
  try {
    const response = await getRoomImages(house.id);
    if (response && response.code === 200 && response.data) {
      const roomImages = response.data;
      fileList.value = roomImages.map((image, index) => ({
        name: `房间图片${index + 1}`,
        url: image.url,
        uid: Date.now() + index,
        status: 'success',
        imageId: image.id, // 添加图片ID，用于删除
        isCover: image.isCover
      }));
    } else {
      // 如果获取图片失败，使用房间数据中的图片URL
      if (house.imageUrls && house.imageUrls.length > 0) {
        fileList.value = house.imageUrls.map((url, index) => ({
          name: `房间图片${index + 1}`,
          url: url,
          uid: Date.now() + index,
          status: 'success'
        }));
      } else if (housingForm.imageUrl) {
        fileList.value = [{
          name: '房间图片',
          url: housingForm.imageUrl,
          uid: Date.now(),
          status: 'success'
        }];
      } else {
        fileList.value = [];
      }
    }
  } catch (error) {
    console.error('获取房间图片失败:', error);
    // 使用房间数据中的图片URL作为后备
    if (house.imageUrls && house.imageUrls.length > 0) {
      fileList.value = house.imageUrls.map((url, index) => ({
        name: `房间图片${index + 1}`,
        url: url,
        uid: Date.now() + index,
        status: 'success'
      }));
    } else {
      fileList.value = [];
    }
  }
  
  dialogVisible.value = true;
};

// 删除房间
const handleDelete = (house) => {
  ElMessageBox.confirm(
    `确定要删除房间 "${house.roomNumber}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        const response = await deleteRoom(house.id);
        if (response && response.code === 200) {
          ElMessage.success('删除成功');
          fetchHousingList();
        } else {
          ElMessage.error(response?.message || '删除失败');
        }
      } catch (error) {
        console.error('删除房间出错:', error);
        ElMessage.error('删除失败，请稍后重试');
      }
    })
    .catch(() => {
      // 取消删除
    });
};

// 出租处理
const handleRent = (house) => {
  ElMessageBox.confirm(
    `确定要将房间 "${house.roomNumber}" 标记为已出租吗？`,
    '出租确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  )
    .then(async () => {
      try {
        const response = await updateRoomStatus(house.id, 'RENTED');
        if (response && response.code === 200) {
          ElMessage.success('操作成功，房间已标记为已出租');
          fetchHousingList();
        } else {
          ElMessage.error(response?.message || '操作失败');
        }
      } catch (error) {
        console.error('更新房间状态出错:', error);
        ElMessage.error('操作失败，请稍后重试');
      }
    })
    .catch(() => {
      // 取消操作
    });
};

// 重置表单
const resetForm = () => {
  if (housingFormRef.value) {
    housingFormRef.value.resetFields();
  }
  
  housingForm.id = null;
  housingForm.roomNumber = '';
  housingForm.propertyId = null;
  housingForm.area = 0;
  housingForm.type = 'STANDARD';
  housingForm.floor = 1;
  housingForm.orientation = 'SOUTH';
  housingForm.monthlyRent = 0;
  housingForm.status = 'VACANT';
  housingForm.imageUrl = '';
  housingForm.facilities = '';
  housingForm.description = '';
  
  // 确保清空文件列表
  fileList.value = [];
};

// 上传图片前的校验
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!');
    return false;
  }
  
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!');
    return false;
  }
  
  return true;
};

// 上传成功回调
const handleUploadSuccess = (response, file, uploadFileList) => {
  if (response && response.code === 200) {
    ElMessage.success('图片上传成功');
    
    // 确保fileList已初始化
    if (!fileList.value) {
      fileList.value = [];
    }
    
    // 更新fileList - 同步上传组件的fileList
    fileList.value = [...uploadFileList];
    
    // 处理响应数据
    const lastFile = fileList.value[fileList.value.length - 1];
    if (lastFile && lastFile.response && lastFile.response.code === 200) {
      const responseData = lastFile.response.data;
      
      // 根据响应数据结构设置文件信息
      if (typeof responseData === 'string') {
        // 通用上传接口返回的是图片URL字符串
        lastFile.url = responseData;
      } else if (responseData && responseData.imageUrl) {
        // 房间图片上传接口返回的是对象
        lastFile.url = responseData.imageUrl;
        lastFile.imageId = responseData.id;
        lastFile.isCover = responseData.isCover;
      }
      
      delete lastFile.response; // 清理响应数据
    }
    
    // 如果是第一张图片，设为封面图
    if (fileList.value.length === 1 && fileList.value[0].url) {
      housingForm.imageUrl = fileList.value[0].url;
    }
    
  } else {
    ElMessage.error(response?.message || '图片上传失败');
  }
};

// 上传失败回调
const handleUploadError = (error, file, uploadFileList) => {
  console.error('上传失败:', error);
  ElMessage.error('图片上传失败');
};

// 移除图片回调
const handleRemoveImage = async (file, uploadFileList) => {
  try {
    // 确保fileList已初始化
    if (!fileList.value) {
      fileList.value = [];
    }
    
    // 如果是编辑模式且房间已存在，需要调用后端API删除图片
    if (dialogStatus.value === 'update' && housingForm.id && file.imageId) {
      try {
        const response = await deleteRoomImage(housingForm.id, file.imageId);
        if (response && response.code === 200) {
          ElMessage.success('图片删除成功');
        } else {
          ElMessage.error(response?.message || '删除图片失败');
          return; // 如果后端删除失败，不继续执行前端删除
        }
      } catch (error) {
        console.error('调用后端删除图片API失败:', error);
        ElMessage.error('删除图片失败');
        return;
      }
    }
    
    // 从fileList中移除图片
    const index = fileList.value.findIndex(item => item.uid === file.uid);
    if (index > -1) {
      fileList.value.splice(index, 1);
    }
    
    // 如果移除的是封面图，重新设置封面图
    if (housingForm.imageUrl === file.url) {
      if (fileList.value && fileList.value.length > 0) {
        housingForm.imageUrl = fileList.value[0].url;
      } else {
        housingForm.imageUrl = '';
      }
    }
    
    // 如果是新上传的图片（没有imageId），只显示成功消息
    if (!file.imageId) {
      ElMessage.success('图片删除成功');
    }
  } catch (error) {
    console.error('移除图片失败:', error);
    ElMessage.error('移除图片失败');
  }
};

// 上传超出限制回调
const handleExceed = () => {
  ElMessage.warning('最多只能上传5张图片');
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchHousingList();
};

// 提交表单
const submitForm = () => {
  housingFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        let response;
        
        // 准备提交的数据（不包含图片，图片单独处理）
        const submitData = {
          roomNumber: housingForm.roomNumber,
          propertyId: housingForm.propertyId,
          area: housingForm.area,
          type: housingForm.type,
          floor: housingForm.floor,
          orientation: housingForm.orientation,
          monthlyRent: housingForm.monthlyRent,
          status: housingForm.status,
          facilities: housingForm.facilities,
          description: housingForm.description
        };
        
        // 判断是添加还是编辑
        if (dialogStatus.value === 'create') {
          response = await createRoom(submitData);
          
          // 如果创建成功且有图片需要上传，则上传图片
          if (response && response.code === 200 && fileList.value && fileList.value.length > 0) {
            const roomId = response.data.id;
            await uploadRoomImages(roomId);
          }
        } else {
          response = await updateRoom(housingForm.id, submitData);
          
          // 编辑模式下，图片已经通过单独的上传/删除接口处理了
        }
        
        if (response && response.code === 200) {
          ElMessage.success(dialogStatus.value === 'create' ? '添加成功' : '更新成功');
          dialogVisible.value = false;
          // 重新加载房间列表以显示最新数据
          await fetchHousingList();
        } else {
          ElMessage.error(response?.message || (dialogStatus.value === 'create' ? '添加失败' : '更新失败'));
        }
      } catch (error) {
        console.error('提交表单出错:', error);
        ElMessage.error(dialogStatus.value === 'create' ? '添加失败，请稍后重试' : '更新失败，请稍后重试');
      } finally {
        submitLoading.value = false;
      }
    } else {
      ElMessage.error('请正确填写表单');
      return false;
    }
  });
};

// 批量操作
const selectedHousings = ref([]);
const handleBatchAction = (command) => {
  if (selectedHousings.value.length > 0) {
    if (command === 'batchDelete') {
      batchDelete();
    } else if (command === 'batchAvailable') {
      batchAvailable();
    } else if (command === 'batchMaintenance') {
      batchMaintenance();
    }
  }
};

const batchDelete = async () => {
  const ids = selectedHousings.value;
  try {
    const response = await batchDeleteRooms(ids);
    if (response && response.code === 200) {
      ElMessage.success('批量删除成功');
      selectedHousings.value = [];
      fetchHousingList();
    } else {
      ElMessage.error(response?.message || '批量删除失败');
    }
  } catch (error) {
    console.error('批量删除出错:', error);
    ElMessage.error('批量删除失败，请稍后重试');
  }
};

const batchAvailable = async () => {
  const ids = selectedHousings.value;
  try {
    const response = await batchUpdateRoomStatus(ids, 'VACANT');
    if (response && response.code === 200) {
      ElMessage.success('批量设置为待出租成功');
      selectedHousings.value = [];
      fetchHousingList();
    } else {
      ElMessage.error(response?.message || '批量设置为待出租失败');
    }
  } catch (error) {
    console.error('批量设置为待出租出错:', error);
    ElMessage.error('批量设置为待出租失败，请稍后重试');
  }
};

const batchMaintenance = async () => {
  const ids = selectedHousings.value;
  try {
    const response = await batchUpdateRoomStatus(ids, 'MAINTENANCE');
    if (response && response.code === 200) {
      ElMessage.success('批量设置为维修中成功');
      selectedHousings.value = [];
      fetchHousingList();
    } else {
      ElMessage.error(response?.message || '批量设置为维修中失败');
    }
  } catch (error) {
    console.error('批量设置为维修中出错:', error);
    ElMessage.error('批量设置为维修中失败，请稍后重试');
  }
};
</script>

<style lang="scss" scoped>
.housing-management {
  .page-title {
    margin-bottom: 20px;
    color: #303133;
    font-weight: 500;
  }
  
  .filter-container {
    display: flex;
    gap: 15px;
    margin-bottom: 20px;
    align-items: center;
    flex-wrap: wrap;
    
    .search-input {
      width: 250px;
    }
    
    @media (max-width: 768px) {
      .search-input {
        width: 100%;
      }
    }
  }
  
  .selection-info {
    margin-bottom: 20px;
  }
  
  .housing-list {
    .housing-item-col {
      margin-bottom: 20px;
    }
    
    .housing-item {
      position: relative;
      height: 100%;
      transition: all 0.3s ease;
      border-radius: 8px;
      overflow: hidden;
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
      }
      
      &.selected {
        border: 2px solid #409eff;
        box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
      }
      
      .housing-checkbox {
        position: absolute;
        top: 10px;
        left: 10px;
        z-index: 10;
        background: transparent;
        border-radius: 4px;
        padding: 2px;
      }
      
      .housing-image {
        position: relative;
        height: 200px;
        overflow: hidden;
        
        .el-image {
          width: 100%;
          height: 100%;
          transition: transform 0.3s ease;
        }
        
        &:hover .el-image {
          transform: scale(1.05);
        }
        
        .image-error {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 100%;
          background-color: #f5f7fa;
          color: #909399;
          font-size: 24px;
        }
        
        .housing-status {
          position: absolute;
          top: 10px;
          right: 10px;
          padding: 4px 8px;
          border-radius: 4px;
          color: white;
          font-size: 12px;
          font-weight: 500;
          border: 2px solid transparent;
          
          &.rented {
            background-color: #f56c6c;
            border-color: #f56c6c;
          }
          
          &.vacant {
            background-color: #67c23a;
            border-color: #67c23a;
          }
          
          &.maintenance {
            background-color: #e6a23c;
            border-color: #e6a23c;
          }
        }
      }
      
      .housing-content {
        padding: 16px;
        
        .housing-title {
          font-size: 16px;
          font-weight: 600;
          margin: 0 0 12px 0;
          color: #303133;
          line-height: 1.4;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .housing-info {
          margin-bottom: 16px;
          
          p {
            margin: 8px 0;
            font-size: 14px;
            color: #606266;
            display: flex;
            align-items: center;
            
            .el-icon {
              margin-right: 6px;
              color: #909399;
            }
            
            &.housing-price {
              color: #f56c6c;
              font-weight: 600;
              font-size: 16px;
            }
          }
        }
        
        .housing-actions {
          display: flex;
          flex-direction: column;
          gap: 8px;
          
          .action-row {
            display: flex;
            gap: 8px;
            
            .el-button {
              flex: 1;
              min-width: 60px;
            }
          }
          
          .rent-button {
            width: 100%;
            background-color: #67c23a;
            border-color: #67c23a;
            
            &:hover {
              background-color: #85ce61;
              border-color: #85ce61;
            }
          }
        }
      }
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
  }
  
  .housing-image-upload {
    .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;
      
      &:hover {
        border-color: #409eff;
      }
    }
    
    .el-upload__tip {
      color: #606266;
      font-size: 12px;
      margin-top: 8px;
    }
  }
  
  .unit {
    margin-left: 8px;
    color: #909399;
    font-size: 14px;
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}

@media (max-width: 1200px) {
  .housing-management .housing-list .housing-item-col {
    &:nth-child(4n) {
      display: block;
    }
  }
}

@media (max-width: 992px) {
  .housing-management .housing-list .housing-item-col {
    &:nth-child(3n) {
      display: block;
    }
  }
}

@media (max-width: 768px) {
  .housing-management {
    .filter-container {
      flex-direction: column;
      align-items: stretch;
      
      .el-select, .el-button {
        width: 100%;
        margin-bottom: 10px;
      }
    }
    
    .housing-list .housing-item {
      .housing-content .housing-actions {
        .el-button {
          font-size: 12px;
          padding: 8px 12px;
        }
      }
    }
  }
}
</style> 