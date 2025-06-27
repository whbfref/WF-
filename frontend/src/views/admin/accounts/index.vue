<template>
  <div class="admin-accounts">
    <h2 class="page-title">管理员账号管理</h2>
    
    <div class="filter-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索用户名"
        class="search-input"
        clearable
        @clear="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
      <el-select v-model="selectedRoleLevel" placeholder="角色等级" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="超级管理员" :value="1" />
        <el-option label="管理员" :value="2" />
        <el-option label="客服" :value="3" />
      </el-select>
      <el-select v-model="selectedStatus" placeholder="账号状态" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleAdd">添加管理员</el-button>
    </div>
    
    <el-table
      :data="accountList"
      border
      style="width: 100%"
      v-loading="loading"
      table-layout="auto"
      :header-cell-style="{textAlign: 'center'}"
      :cell-style="{textAlign: 'center'}"
    >
      <el-table-column type="index" width="60" align="center" fixed />
      <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
      <el-table-column prop="realName" label="真实姓名" min-width="120" show-overflow-tooltip />
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <el-table-column prop="phone" label="手机号" min-width="130" show-overflow-tooltip />
      <el-table-column label="角色等级" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getRoleLevelType(scope.row.roleLevel)">
            {{ getRoleLevelText(scope.row.roleLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180" show-overflow-tooltip />
      <el-table-column prop="lastLoginTime" label="最后登录" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button 
            :type="scope.row.status === 1 ? 'danger' : 'success'" 
            size="small" 
            @click="handleToggleStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button type="warning" size="small" @click="handleResetPassword(scope.row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 添加/编辑管理员对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色等级" prop="roleLevel">
          <el-select v-model="formData.roleLevel" placeholder="请选择角色等级">
            <el-option label="超级管理员" :value="1" />
            <el-option label="管理员" :value="2" />
            <el-option label="客服" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限" prop="permissions">
          <el-checkbox-group v-model="formData.permissions">
            <el-checkbox label="SYSTEM_CONFIG">系统配置</el-checkbox>
            <el-checkbox label="USER_MANAGE">用户管理</el-checkbox>
            <el-checkbox label="LANDLORD_VERIFY">房东认证</el-checkbox>
            <el-checkbox label="PROPERTY_AUDIT">房源审核</el-checkbox>
            <el-checkbox label="DATA_STATISTICS">数据统计</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { 
  getAdminAccounts, 
  createAdminAccount, 
  updateAdminAccount, 
  updateAdminAccountStatus, 
  resetAdminPassword 
} from '@/api/admin';

// 搜索和筛选条件
const searchQuery = ref('');
const selectedRoleLevel = ref('');
const selectedStatus = ref('');

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 加载状态
const loading = ref(false);
const submitLoading = ref(false);

// 管理员账号列表数据
const accountList = ref([]);

// 对话框相关
const dialogVisible = ref(false);
const dialogTitle = ref('');
const isEdit = ref(false);
const formRef = ref(null);

// 表单数据
const formData = ref({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  roleLevel: 2,
  permissions: []
});

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  roleLevel: [
    { required: true, message: '请选择角色等级', trigger: 'change' }
  ]
};

// 组件挂载时执行
onMounted(() => {
  fetchAccountList();
});

// 获取角色等级文本
const getRoleLevelText = (level) => {
  switch (level) {
    case 1:
      return '超级管理员';
    case 2:
      return '管理员';
    case 3:
      return '客服';
    default:
      return '未知';
  }
};

// 获取角色等级标签类型
const getRoleLevelType = (level) => {
  switch (level) {
    case 1:
      return 'danger';
    case 2:
      return 'primary';
    case 3:
      return 'info';
    default:
      return '';
  }
};

// 获取管理员账号列表数据
const fetchAccountList = async () => {
  loading.value = true;
  
  try {
    const response = await getAdminAccounts({
      page: currentPage.value,
      size: pageSize.value,
      username: searchQuery.value || undefined,
      roleLevel: selectedRoleLevel.value !== '' ? selectedRoleLevel.value : undefined,
      status: selectedStatus.value !== '' ? selectedStatus.value : undefined
    });
    
    if (response && response.code === 200) {
      if (response.data) {
        if (response.data.content) {
          // 分页对象格式
          accountList.value = response.data.content;
          total.value = response.data.pageable?.total || response.data.totalElements || 0;
        } else if (Array.isArray(response.data)) {
          // 直接返回数组格式
          accountList.value = response.data;
          total.value = response.data.length;
        } else {
          accountList.value = [];
          total.value = 0;
        }
      } else {
        accountList.value = [];
        total.value = 0;
      }
    } else {
      ElMessage.error(response?.message || '获取管理员账号列表失败');
    }
  } catch (error) {
    console.error('获取管理员账号列表异常:', error);
    ElMessage.error('获取管理员账号列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchAccountList();
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchAccountList();
};

// 分页大小变化处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchAccountList();
};

// 添加管理员
const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '添加管理员';
  formData.value = {
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: '',
    roleLevel: 2,
    permissions: []
  };
  dialogVisible.value = true;
};

// 编辑管理员
const handleEdit = (account) => {
  isEdit.value = true;
  dialogTitle.value = '编辑管理员';
  formData.value = {
    id: account.id,
    username: account.username,
    realName: account.realName,
    email: account.email,
    phone: account.phone,
    roleLevel: account.roleLevel,
    permissions: account.permissions || []
  };
  dialogVisible.value = true;
};

// 切换管理员状态
const handleToggleStatus = (account) => {
  const action = account.status === 1 ? '禁用' : '启用';
  const newStatus = account.status === 1 ? 0 : 1;
  
  ElMessageBox.confirm(
    `确定要${action}管理员 "${account.username}" 吗？`,
    `${action}确认`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: account.status === 1 ? 'warning' : 'info'
    }
  )
    .then(async () => {
      try {
        const response = await updateAdminAccountStatus(account.id, newStatus);
        if (response && response.code === 200) {
          // 更新本地状态
          const index = accountList.value.findIndex(item => item.id === account.id);
          if (index !== -1) {
            accountList.value[index].status = newStatus;
            ElMessage.success(`${action}管理员成功`);
          }
        } else {
          ElMessage.error(response?.message || `${action}管理员失败`);
        }
      } catch (error) {
        console.error(`${action}管理员失败:`, error);
        ElMessage.error(`${action}管理员失败，请稍后重试`);
      }
    })
    .catch(() => {
      // 取消操作
    });
};

// 重置密码
const handleResetPassword = (account) => {
  ElMessageBox.confirm(
    `确定要重置管理员 "${account.username}" 的密码吗？`,
    '重置密码确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        const response = await resetAdminPassword(account.id);
        if (response && response.code === 200) {
          ElMessageBox.alert(
            `密码重置成功！临时密码：${response.data.tempPassword}`,
            '重置成功',
            {
              confirmButtonText: '确定',
              type: 'success'
            }
          );
        } else {
          ElMessage.error(response?.message || '重置密码失败');
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        ElMessage.error('重置密码失败，请稍后重试');
      }
    })
    .catch(() => {
      // 取消操作
    });
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    submitLoading.value = true;
    
    let response;
    if (isEdit.value) {
      // 编辑管理员
      const { id, ...updateData } = formData.value;
      response = await updateAdminAccount(id, updateData);
    } else {
      // 创建管理员
      response = await createAdminAccount(formData.value);
    }
    
    if (response && response.code === 200) {
      ElMessage.success(`${isEdit.value ? '更新' : '创建'}管理员成功`);
      dialogVisible.value = false;
      fetchAccountList();
    } else {
      ElMessage.error(response?.message || `${isEdit.value ? '更新' : '创建'}管理员失败`);
    }
  } catch (error) {
    if (error !== false) { // 不是表单验证错误
      console.error(`${isEdit.value ? '更新' : '创建'}管理员失败:`, error);
      ElMessage.error(`${isEdit.value ? '更新' : '创建'}管理员失败，请稍后重试`);
    }
  } finally {
    submitLoading.value = false;
  }
};

// 关闭对话框
const handleDialogClose = () => {
  dialogVisible.value = false;
  if (formRef.value) {
    formRef.value.resetFields();
  }
};
</script>

<style scoped>
.admin-accounts {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.filter-container {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  width: 200px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 