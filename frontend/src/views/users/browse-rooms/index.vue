<template>
  <div class="browse-rooms user-background">
    <div class="user-content">
      <div class="page-header animated-header">
        <h2 class="page-title gradient-title">浏览房源</h2>
        <p class="page-desc animated-desc">发现您理想的居住空间</p>
      </div>

      <!-- 搜索筛选区域 -->
      <el-card class="filter-card animated-filter" shadow="never">
        <div class="filter-header">
          <h3 class="filter-title">
            <el-icon><Search /></el-icon>
            智能搜索
          </h3>
          <div class="filter-stats">
            找到 <span class="highlight">{{ total }}</span> 套房源
          </div>
        </div>
        
        <el-form :model="searchForm" class="advanced-search-form">
          <!-- 第一行：基础搜索 -->
          <div class="form-row">
            <el-form-item label="关键词" class="form-item-wide">
              <el-input
                v-model="searchForm.keyword"
                placeholder="搜索房源名称、地址或关键词..."
                clearable
                size="large"
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <!-- 第二行：筛选条件 -->
          <div class="form-row">
            <el-form-item label="价格范围">
              <el-select v-model="searchForm.priceRange" placeholder="选择价格" size="large">
                <el-option label="不限价格" value="" />
                <el-option label="1000以下" value="0-1000" />
                <el-option label="1000-2000" value="1000-2000" />
                <el-option label="2000-3000" value="2000-3000" />
                <el-option label="3000-5000" value="3000-5000" />
                <el-option label="5000-8000" value="5000-8000" />
                <el-option label="8000以上" value="8000-" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="房间类型">
              <el-select v-model="searchForm.roomType" placeholder="选择类型" size="large">
                <el-option label="不限类型" value="" />
                <el-option label="单间" value="SINGLE" />
                <el-option label="一室一厅" value="ONE_BEDROOM" />
                <el-option label="两室一厅" value="TWO_BEDROOM" />
                <el-option label="三室一厅" value="THREE_BEDROOM" />
                <el-option label="四室及以上" value="FOUR_BEDROOM" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="面积范围">
              <el-select v-model="searchForm.areaRange" placeholder="选择面积" size="large">
                <el-option label="不限面积" value="" />
                <el-option label="30㎡以下" value="0-30" />
                <el-option label="30-50㎡" value="30-50" />
                <el-option label="50-80㎡" value="50-80" />
                <el-option label="80-120㎡" value="80-120" />
                <el-option label="120㎡以上" value="120-" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="排序方式">
              <el-select v-model="searchForm.sortBy" size="large">
                <el-option label="价格升序" value="price_asc" />
                <el-option label="价格降序" value="price_desc" />
                <el-option label="面积升序" value="area_asc" />
                <el-option label="面积降序" value="area_desc" />
                <el-option label="最新发布" value="create_time_desc" />
              </el-select>
            </el-form-item>
          </div>
          
          <!-- 操作按钮 -->
          <div class="form-actions">
            <el-button type="primary" size="large" @click="handleSearch" :loading="loading" class="search-btn">
              <el-icon><Search /></el-icon>
              立即搜索
            </el-button>
            <el-button size="large" @click="handleReset" class="reset-btn">
              <el-icon><Refresh /></el-icon>
              重置条件
            </el-button>
            <el-button size="large" @click="toggleAdvanced" class="advanced-btn">
              <el-icon><Setting /></el-icon>
              {{ showAdvanced ? '收起' : '高级' }}筛选
            </el-button>
          </div>
          
          <!-- 高级筛选 -->
          <el-collapse-transition>
            <div v-show="showAdvanced" class="advanced-filters">
              <div class="form-row">
                <el-form-item label="装修程度">
                  <el-checkbox-group v-model="searchForm.decorations">
                    <el-checkbox label="毛坯">毛坯</el-checkbox>
                    <el-checkbox label="简装">简装</el-checkbox>
                    <el-checkbox label="精装">精装</el-checkbox>
                    <el-checkbox label="豪装">豪装</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item label="房屋朝向">
                  <el-checkbox-group v-model="searchForm.orientations">
                    <el-checkbox label="南向">南向</el-checkbox>
                    <el-checkbox label="北向">北向</el-checkbox>
                    <el-checkbox label="东向">东向</el-checkbox>
                    <el-checkbox label="西向">西向</el-checkbox>
                    <el-checkbox label="南北通透">南北通透</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item label="楼层范围">
                  <el-slider
                    v-model="searchForm.floorRange"
                    range
                    :min="1"
                    :max="50"
                    :marks="{ 1: '1F', 10: '10F', 20: '20F', 30: '30F', 50: '50F+' }"
                    style="width: 300px;"
                  />
                </el-form-item>
              </div>
            </div>
          </el-collapse-transition>
        </el-form>
      </el-card>

      <!-- 房源列表 -->
      <div class="rooms-container user-fade-in" v-loading="loading">
        <div class="rooms-grid" v-if="roomList.length > 0">
          <el-card 
            v-for="(room, index) in roomList" 
            :key="room.id" 
            class="room-card animated-card" 
            shadow="hover"
            :style="{ animationDelay: `${index * 0.1}s` }"
            @click="handleViewDetail(room)"
        >
          <!-- 房源图片 -->
          <div class="room-image">
            <el-image
              :src="room.imageUrl || defaultRoomImage"
              fit="cover"
              class="image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="room-status" :class="getStatusClass(room.status)">
              {{ getStatusText(room.status) }}
            </div>
          </div>
          
          <!-- 房源信息 -->
          <div class="room-info">
            <h3 class="room-title">{{ room.title }}</h3>
            <p class="room-address">
              <el-icon><Location /></el-icon>
              {{ room.address }}
            </p>
            
            <div class="room-details">
              <div class="detail-item">
                <span class="label">面积:</span>
                <span class="value">{{ room.area }}㎡</span>
              </div>
              <div class="detail-item">
                <span class="label">类型:</span>
                <span class="value">{{ getRoomTypeText(room.roomType) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">楼层:</span>
                <span class="value">{{ room.floor }}层</span>
              </div>
            </div>
            
            <div class="room-price">
              <span class="price">¥{{ room.monthlyRent }}</span>
              <span class="unit">/月</span>
            </div>
            
            <div class="room-actions">
              <el-button 
                type="primary" 
                size="small" 
                @click.stop="handleApply(room)"
                :disabled="room.status !== 'AVAILABLE'"
              >
                立即申请
              </el-button>
              <el-button size="small" @click.stop="handleViewDetail(room)">
                查看详情
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-else description="暂无房源信息" />
    </div>

    <!-- 分页 -->
    <div class="pagination-container" v-if="total > 0">
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

    <!-- 房源详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedRoom?.title"
      width="900px"
      class="room-detail-dialog"
    >
      <div v-if="selectedRoom" class="room-detail">
        <div class="detail-images">
          <!-- 轮播图 -->
          <el-carousel 
            v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length > 0"
            :interval="4000" 
            type="card" 
            height="300px"
            class="room-carousel"
          >
            <el-carousel-item 
              v-for="(imageUrl, index) in selectedRoom.imageUrls" 
              :key="index"
              class="carousel-item"
            >
              <el-image
                :src="imageUrl"
                fit="cover"
                class="carousel-image"
                :preview-src-list="selectedRoom.imageUrls"
                :initial-index="index"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                    <p>图片加载失败</p>
                  </div>
                </template>
              </el-image>
            </el-carousel-item>
          </el-carousel>
          
          <!-- 单张图片或默认图片 -->
          <div v-else class="single-image-container">
            <el-image
              :src="selectedRoom.imageUrl || defaultRoomImage"
              fit="cover"
              class="main-image"
              :preview-src-list="selectedRoom.imageUrl ? [selectedRoom.imageUrl] : []"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                  <p>暂无图片</p>
                </div>
              </template>
            </el-image>
          </div>
          
          <!-- 图片数量指示器 -->
          <div v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length > 1" class="image-count">
            <el-icon><Picture /></el-icon>
            <span>{{ selectedRoom.imageUrls.length }}张图片</span>
          </div>
        </div>
        
        <div class="detail-info">
          <div class="info-section">
            <h4>基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">租金:</span>
                <span class="value price">¥{{ selectedRoom.monthlyRent }}/月</span>
              </div>
              <div class="info-item">
                <span class="label">面积:</span>
                <span class="value">{{ selectedRoom.area }}㎡</span>
              </div>
              <div class="info-item">
                <span class="label">类型:</span>
                <span class="value">{{ getRoomTypeText(selectedRoom.roomType) }}</span>
              </div>
              <div class="info-item">
                <span class="label">楼层:</span>
                <span class="value">{{ selectedRoom.floor }}层</span>
              </div>
              <div class="info-item">
                <span class="label">朝向:</span>
                <span class="value">{{ selectedRoom.orientation || '暂无' }}</span>
              </div>
              <div class="info-item">
                <span class="label">装修:</span>
                <span class="value">{{ selectedRoom.decoration || '暂无' }}</span>
              </div>
            </div>
          </div>
          
          <div class="info-section">
            <h4>房源描述</h4>
            <p class="description">{{ selectedRoom.description || '暂无描述' }}</p>
          </div>
          
          <div class="info-section">
            <h4>联系信息</h4>
            <div class="contact-info">
              <p><strong>房东:</strong> {{ selectedRoom.landlordName }}</p>
              <p><strong>联系电话:</strong> {{ selectedRoom.landlordPhone }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="handleApply(selectedRoom)"
            :disabled="selectedRoom?.status !== 'AVAILABLE'"
          >
            立即申请
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 申请对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="租房申请"
      width="500px"
    >
      <el-form
        ref="applyFormRef"
        :model="applyForm"
        :rules="applyRules"
        label-width="100px"
      >
        <el-form-item label="房源信息">
          <div class="apply-room-info">
            <p><strong>{{ applyRoom?.title }}</strong></p>
            <p>租金: ¥{{ applyRoom?.monthlyRent }}/月</p>
          </div>
        </el-form-item>
        
        <el-form-item label="申请理由" prop="reason">
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请简述您的申请理由"
          />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="applyForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="期望入住时间" prop="moveInDate">
          <el-date-picker
            v-model="applyForm.moveInDate"
            type="date"
            placeholder="选择入住时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApplication" :loading="applyLoading">
            提交申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Location, Picture, Refresh, Setting } from '@element-plus/icons-vue';
import { getAvailableRooms, applyForRoom } from '@/api/user';

// 默认房源图片
const defaultRoomImage = 'https://via.placeholder.com/300x200?text=房源图片';

// 响应式数据
const loading = ref(false);
const roomList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);

// 高级搜索显示状态
const showAdvanced = ref(false);

// 搜索表单
const searchForm = reactive({
  keyword: '',
  priceRange: '',
  roomType: '',
  areaRange: '',
  sortBy: 'create_time_desc',
  decorations: [],
  orientations: [],
  floorRange: [1, 50]
});

// 详情对话框
const detailDialogVisible = ref(false);
const selectedRoom = ref(null);

// 申请对话框
const applyDialogVisible = ref(false);
const applyLoading = ref(false);
const applyRoom = ref(null);
const applyFormRef = ref(null);
const applyForm = reactive({
  reason: '',
  contactPhone: '',
  moveInDate: ''
});

// 申请表单验证规则
const applyRules = {
  reason: [
    { required: true, message: '请输入申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由至少10个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  moveInDate: [
    { required: true, message: '请选择期望入住时间', trigger: 'change' }
  ]
};

// 组件挂载时获取房源列表
onMounted(() => {
  fetchRoomList();
});

// 获取房源列表
const fetchRoomList = async () => {
  try {
    loading.value = true;
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.keyword,
      roomType: searchForm.roomType,
      areaRange: searchForm.areaRange,
      sortBy: searchForm.sortBy
    };
    
    // 处理价格范围
    if (searchForm.priceRange) {
      const [minPrice, maxPrice] = searchForm.priceRange.split('-');
      params.minPrice = minPrice;
      if (maxPrice) {
        params.maxPrice = maxPrice;
      }
    }
    
    // 处理楼层范围 - 转换为字符串
    if (searchForm.floorRange && Array.isArray(searchForm.floorRange)) {
      params.minFloor = searchForm.floorRange[0];
      params.maxFloor = searchForm.floorRange[1];
    }
    
    // 处理装修和朝向数组
    if (searchForm.decorations && searchForm.decorations.length > 0) {
      params.decorations = searchForm.decorations.join(',');
    }
    if (searchForm.orientations && searchForm.orientations.length > 0) {
      params.orientations = searchForm.orientations.join(',');
    }
    
    console.log('请求房源列表，参数:', params);
    const response = await getAvailableRooms(params);
    console.log('房源列表响应:', response);
    
    if (response && response.code === 200 && response.data) {
      roomList.value = response.data.content || [];
      total.value = response.data.totalElements || response.data.pageable?.total || 0;
      console.log('房源数据加载成功:', roomList.value.length, '条记录');
    } else {
      console.warn('响应格式异常:', response);
      roomList.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取房源列表失败:', error);
    ElMessage.error('获取房源列表失败，请稍后重试');
    roomList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchRoomList();
};

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    priceRange: '',
    roomType: '',
    areaRange: '',
    sortBy: 'create_time_desc',
    decorations: [],
    orientations: [],
    floorRange: [1, 50]
  });
  handleSearch();
};

// 切换高级搜索
const toggleAdvanced = () => {
  showAdvanced.value = !showAdvanced.value;
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchRoomList();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchRoomList();
};

// 查看详情
const handleViewDetail = async (room) => {
  try {
    // 直接显示房间信息，因为后端已经返回了完整的图片数据
    selectedRoom.value = room;
    detailDialogVisible.value = true;
    
    // 如果没有图片信息，尝试获取
    if (!room.imageUrls || room.imageUrls.length === 0) {
      try {
        const { getRoomImages } = await import('@/api/rooms');
        const response = await getRoomImages(room.id);
        
        if (response && response.code === 200 && response.data) {
          // 更新房间信息，添加完整的图片数据
          const updatedRoom = {
            ...room,
            imageUrls: response.data.map(img => img.url),
            coverImage: response.data.find(img => img.isCover)?.url || response.data[0]?.url
          };
          selectedRoom.value = updatedRoom;
        }
      } catch (error) {
        console.error('获取房间图片失败:', error);
      }
    }
  } catch (error) {
    console.error('查看详情失败:', error);
    selectedRoom.value = room;
    detailDialogVisible.value = true;
  }
};

// 申请租房
const handleApply = (room) => {
  if (room.status !== 'AVAILABLE') {
    ElMessage.warning('该房源暂不可申请');
    return;
  }
  
  applyRoom.value = room;
  applyDialogVisible.value = true;
  detailDialogVisible.value = false;
};

// 提交申请
const submitApplication = async () => {
  if (!applyFormRef.value) return;
  
  try {
    await applyFormRef.value.validate();
    applyLoading.value = true;
    
    const applicationData = {
      roomId: applyRoom.value.id,
      remarks: applyForm.reason,
      expectedMoveInDate: applyForm.moveInDate,
      leaseDuration: 12 // 默认12个月，可以后续添加到表单中
    };
    
    const response = await applyForRoom(applicationData);
    if (response.code === 200) {
      ElMessage.success('申请提交成功，请等待房东审核');
      applyDialogVisible.value = false;
      resetApplyForm();
    }
  } catch (error) {
    console.error('提交申请失败:', error);
    ElMessage.error('提交申请失败');
  } finally {
    applyLoading.value = false;
  }
};

// 重置申请表单
const resetApplyForm = () => {
  Object.assign(applyForm, {
    reason: '',
    contactPhone: '',
    moveInDate: ''
  });
  if (applyFormRef.value) {
    applyFormRef.value.resetFields();
  }
};

// 获取状态样式类
const getStatusClass = (status) => {
  const statusClasses = {
    'AVAILABLE': 'available',
    'RENTED': 'rented',
    'MAINTENANCE': 'maintenance'
  };
  return statusClasses[status] || '';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusTexts = {
    'AVAILABLE': '可租',
    'RENTED': '已租',
    'MAINTENANCE': '维护中'
  };
  return statusTexts[status] || '未知';
};

// 获取房间类型文本
const getRoomTypeText = (roomType) => {
  const roomTypeTexts = {
    'SINGLE': '单间',
    'ONE_BEDROOM': '一室一厅',
    'TWO_BEDROOM': '两室一厅',
    'THREE_BEDROOM': '三室一厅'
  };
  return roomTypeTexts[roomType] || '未知';
};
</script>

<style scoped>
/* 移除不存在的CSS导入 */

.browse-rooms {
  padding: 20px;
}

/* 动画效果 */
.animated-filter {
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  animation: slideInUp 0.6s ease-out;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.filter-stats {
  color: #666;
  font-size: 14px;
}

.highlight {
  color: #667eea;
  font-weight: 600;
  font-size: 16px;
}

.advanced-search-form {
  animation: fadeIn 0.5s ease-out;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: end;
}

.form-item-wide {
  flex: 2;
}

.form-row .el-form-item {
  margin-bottom: 0;
  flex: 1;
}

.search-input {
  border-radius: 25px;
}

.search-input .el-input__wrapper {
  border-radius: 25px;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
}

.search-input .el-input__wrapper:hover {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin: 24px 0;
}

.search-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 25px;
  padding: 12px 32px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.reset-btn {
  border-radius: 25px;
  padding: 12px 24px;
  border: 2px solid #dcdfe6;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-1px);
}

.advanced-btn {
  border-radius: 25px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  border: none;
  color: white;
  transition: all 0.3s ease;
}

.advanced-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.3);
}

.advanced-filters {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.advanced-filters .form-row {
  margin-bottom: 16px;
}

.advanced-filters .el-checkbox {
  margin-right: 16px;
  margin-bottom: 8px;
}

/* 动画定义 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.animated-header {
  animation: slideInDown 0.8s ease-out;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
}

.gradient-title {
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: titleGlow 2s ease-in-out infinite alternate;
}

.page-desc {
  color: #666;
  font-size: 18px;
  margin: 0;
  opacity: 0;
  animation: fadeInUp 0.8s ease-out 0.3s forwards;
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes titleGlow {
  from {
    text-shadow: 0 0 20px rgba(102, 126, 234, 0.5);
  }
  to {
    text-shadow: 0 0 30px rgba(118, 75, 162, 0.8);
  }
}

.filter-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.rooms-container {
  min-height: 400px;
}

.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.room-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.room-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.animated-card {
  opacity: 0;
  transform: translateY(30px);
  animation: slideInUp 0.6s ease-out forwards;
}

.room-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}

.room-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.room-status.available {
  background: #67c23a;
  color: white;
}

.room-status.rented {
  background: #f56c6c;
  color: white;
}

.room-status.maintenance {
  background: #e6a23c;
  color: white;
}

.room-info {
  padding: 16px;
}

.room-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #303133;
}

.room-address {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 14px;
  margin: 0 0 12px 0;
}

.room-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.detail-item {
  font-size: 12px;
}

.detail-item .label {
  color: #909399;
}

.detail-item .value {
  color: #303133;
  font-weight: 500;
}

.room-price {
  margin-bottom: 16px;
}

.price {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
}

.unit {
  font-size: 14px;
  color: #909399;
}

.room-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.room-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 轮播图样式 */
.room-carousel {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.carousel-item {
  border-radius: 8px;
  overflow: hidden;
}

.carousel-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.carousel-image .image-slot {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.carousel-image .image-slot p {
  margin: 8px 0 0 0;
  font-size: 14px;
}

/* 单张图片容器 */
.single-image-container {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.detail-images .main-image {
  width: 100%;
  height: 300px;
  border-radius: 8px;
}

/* 图片数量指示器 */
.image-count {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-images {
  position: relative;
}

.info-section {
  margin-bottom: 20px;
}

.info-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
}

.info-item .label {
  color: #909399;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
}

.info-item .value.price {
  color: #f56c6c;
  font-size: 18px;
}

.description {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.contact-info p {
  margin: 8px 0;
  color: #606266;
}

.apply-room-info {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
}

.apply-room-info p {
  margin: 4px 0;
  color: #606266;
}

@media (max-width: 768px) {
  .rooms-grid {
    grid-template-columns: 1fr;
  }
  
  .room-detail {
    grid-template-columns: 1fr;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .room-details {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style> 