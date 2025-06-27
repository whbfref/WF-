<template>
  <div class="user-feedback">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">意见反馈</h1>
        <p class="page-subtitle">您的意见是我们改进的动力</p>
      </div>

      <div class="feedback-content">
        <!-- 提交反馈表单 -->
        <div class="feedback-form-card">
          <div class="card-header">
            <h3>提交反馈</h3>
          </div>
          
          <div class="form-content">
            <div class="form-group">
              <label>反馈类型</label>
              <select v-model="feedbackForm.type" class="form-select">
                <option value="">请选择反馈类型</option>
                <option value="BUG">功能异常</option>
                <option value="FEATURE">功能建议</option>
                <option value="IMPROVEMENT">体验优化</option>
                <option value="COMPLAINT">投诉建议</option>
                <option value="OTHER">其他</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>反馈标题</label>
              <input 
                type="text" 
                v-model="feedbackForm.title" 
                placeholder="请简要描述您的问题或建议"
                class="form-input"
              />
            </div>
            
            <div class="form-group">
              <label>详细描述</label>
              <textarea 
                v-model="feedbackForm.content" 
                placeholder="请详细描述您遇到的问题或建议，我们会认真对待每一条反馈"
                class="form-textarea"
                rows="6"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label>联系方式（可选）</label>
              <input 
                type="text" 
                v-model="feedbackForm.contact" 
                placeholder="请留下您的联系方式，方便我们与您沟通"
                class="form-input"
              />
            </div>
            
            <div class="form-actions">
              <button class="btn-secondary" @click="handleReset">重置</button>
              <button 
                class="btn-primary" 
                @click="handleSubmit" 
                :disabled="submitLoading"
              >
                {{ submitLoading ? '提交中...' : '提交反馈' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 我的反馈记录 -->
        <div class="feedback-history">
          <div class="card-header">
            <h3>我的反馈</h3>
            <div class="filter-actions">
              <select v-model="filterStatus" @change="handleFilter" class="filter-select">
                <option value="">全部状态</option>
                <option value="PENDING">待处理</option>
                <option value="PROCESSING">处理中</option>
                <option value="RESOLVED">已解决</option>
                <option value="CLOSED">已关闭</option>
              </select>
            </div>
          </div>
          
          <div class="feedback-list" v-if="feedbackList.length > 0">
            <div 
              v-for="feedback in feedbackList" 
              :key="feedback.id" 
              class="feedback-item"
              @click="handleViewDetail(feedback)"
            >
              <div class="feedback-header">
                <div class="feedback-title">{{ feedback.title }}</div>
                <div class="feedback-status" :class="feedback.status.toLowerCase()">
                  {{ getStatusText(feedback.status) }}
                </div>
              </div>
              
              <div class="feedback-meta">
                <span class="feedback-type">{{ getTypeText(feedback.type) }}</span>
                <span class="feedback-time">{{ formatTime(feedback.createTime) }}</span>
              </div>
              
              <div class="feedback-content-preview">
                {{ feedback.content.substring(0, 100) }}{{ feedback.content.length > 100 ? '...' : '' }}
              </div>
              
              <div class="feedback-footer" v-if="feedback.reply">
                <div class="reply-indicator">
                  <span class="reply-icon">💬</span>
                  <span>已回复</span>
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-state">
            <div class="empty-icon">📝</div>
            <h3>暂无反馈记录</h3>
            <p>您还没有提交过反馈，快来分享您的想法吧！</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 反馈详情弹窗 -->
    <div v-if="detailDialogVisible" class="modal-overlay" @click="detailDialogVisible = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>反馈详情</h3>
          <button class="close-btn" @click="detailDialogVisible = false">×</button>
        </div>
        
        <div class="modal-body" v-if="selectedFeedback">
          <div class="detail-section">
            <div class="detail-header">
              <h4>{{ selectedFeedback.title }}</h4>
              <div class="detail-meta">
                <span class="type-tag">{{ getTypeText(selectedFeedback.type) }}</span>
                <span class="status-tag" :class="selectedFeedback.status.toLowerCase()">
                  {{ getStatusText(selectedFeedback.status) }}
                </span>
              </div>
            </div>
            
            <div class="detail-info">
              <div class="info-item">
                <span class="label">提交时间:</span>
                <span class="value">{{ formatTime(selectedFeedback.createTime) }}</span>
              </div>
              <div class="info-item" v-if="selectedFeedback.contact">
                <span class="label">联系方式:</span>
                <span class="value">{{ selectedFeedback.contact }}</span>
              </div>
            </div>
            
            <div class="detail-content">
              <h5>反馈内容</h5>
              <p>{{ selectedFeedback.content }}</p>
            </div>
            
            <div class="detail-reply" v-if="selectedFeedback.reply">
              <h5>官方回复</h5>
              <div class="reply-content">
                <p>{{ selectedFeedback.reply.content }}</p>
                <div class="reply-meta">
                  <span>回复时间: {{ formatTime(selectedFeedback.reply.replyTime) }}</span>
                  <span>回复人: {{ selectedFeedback.reply.replier }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn-secondary" @click="detailDialogVisible = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';

// 响应式数据
const submitLoading = ref(false);
const feedbackList = ref([]);
const filterStatus = ref('');
const detailDialogVisible = ref(false);
const selectedFeedback = ref(null);

// 反馈表单
const feedbackForm = reactive({
  type: '',
  title: '',
  content: '',
  contact: ''
});

// 模拟反馈数据
const mockFeedbacks = [
  {
    id: 1,
    type: 'BUG',
    title: '租金账单页面加载缓慢',
    content: '在查看租金账单时，页面加载速度很慢，有时候需要等待10秒以上才能显示内容。希望能够优化一下加载速度。',
    contact: '13800138000',
    status: 'RESOLVED',
    createTime: '2024-01-15T10:30:00',
    reply: {
      content: '感谢您的反馈！我们已经优化了账单页面的加载速度，现在应该能够在3秒内完成加载。如果您还有其他问题，请随时联系我们。',
      replyTime: '2024-01-16T14:20:00',
      replier: '技术支持'
    }
  },
  {
    id: 2,
    type: 'FEATURE',
    title: '希望增加账单导出功能',
    content: '建议在账单页面增加导出功能，可以将账单信息导出为PDF或Excel格式，方便保存和打印。',
    contact: 'user@example.com',
    status: 'PROCESSING',
    createTime: '2024-01-18T16:45:00',
    reply: null
  },
  {
    id: 3,
    type: 'IMPROVEMENT',
    title: '手机端界面优化建议',
    content: '手机端的界面在小屏幕上显示不够友好，按钮太小，文字也比较难看清楚。希望能够针对移动端进行优化。',
    contact: '',
    status: 'PENDING',
    createTime: '2024-01-20T09:15:00',
    reply: null
  }
];

// 组件挂载时加载数据
onMounted(() => {
  fetchFeedbackList();
});

// 获取反馈列表
const fetchFeedbackList = () => {
  // 模拟API调用
  setTimeout(() => {
    let filteredList = [...mockFeedbacks];
    
    if (filterStatus.value) {
      filteredList = filteredList.filter(item => item.status === filterStatus.value);
    }
    
    feedbackList.value = filteredList;
  }, 300);
};

// 提交反馈
const handleSubmit = () => {
  // 表单验证
  if (!feedbackForm.type) {
    alert('请选择反馈类型');
    return;
  }
  
  if (!feedbackForm.title.trim()) {
    alert('请输入反馈标题');
    return;
  }
  
  if (!feedbackForm.content.trim()) {
    alert('请输入详细描述');
    return;
  }
  
  submitLoading.value = true;
  
  // 模拟提交
  setTimeout(() => {
    // 添加到列表
    const newFeedback = {
      id: Date.now(),
      type: feedbackForm.type,
      title: feedbackForm.title,
      content: feedbackForm.content,
      contact: feedbackForm.contact,
      status: 'PENDING',
      createTime: new Date().toISOString(),
      reply: null
    };
    
    mockFeedbacks.unshift(newFeedback);
    fetchFeedbackList();
    
    // 重置表单
    handleReset();
    submitLoading.value = false;
    alert('反馈提交成功！我们会尽快处理您的反馈。');
  }, 1000);
};

// 重置表单
const handleReset = () => {
  Object.assign(feedbackForm, {
    type: '',
    title: '',
    content: '',
    contact: ''
  });
};

// 筛选
const handleFilter = () => {
  fetchFeedbackList();
};

// 查看详情
const handleViewDetail = (feedback) => {
  selectedFeedback.value = feedback;
  detailDialogVisible.value = true;
};

// 获取类型文本
const getTypeText = (type) => {
  const typeTexts = {
    'BUG': '功能异常',
    'FEATURE': '功能建议',
    'IMPROVEMENT': '体验优化',
    'COMPLAINT': '投诉建议',
    'OTHER': '其他'
  };
  return typeTexts[type] || '未知';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusTexts = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'RESOLVED': '已解决',
    'CLOSED': '已关闭'
  };
  return statusTexts[status] || '未知';
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-';
  return new Date(time).toLocaleString();
};
</script>

<style scoped>
.user-feedback {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin: 0;
}

.feedback-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.feedback-form-card,
.feedback-history {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.card-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.form-input,
.form-select,
.form-textarea {
  padding: 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  font-family: inherit;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-primary {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #6c757d;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-secondary:hover {
  background: #5a6268;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 8px 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  background: white;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.feedback-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.feedback-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 16px;
  flex: 1;
  margin-right: 12px;
}

.feedback-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.feedback-status.pending {
  background: #fff3cd;
  color: #856404;
}

.feedback-status.processing {
  background: #cce5ff;
  color: #0066cc;
}

.feedback-status.resolved {
  background: #d1edff;
  color: #0c5460;
}

.feedback-status.closed {
  background: #f8d7da;
  color: #721c24;
}

.feedback-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #7f8c8d;
}

.feedback-type {
  background: #e9ecef;
  padding: 2px 8px;
  border-radius: 4px;
}

.feedback-content-preview {
  color: #6c757d;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
}

.feedback-footer {
  display: flex;
  justify-content: flex-end;
}

.reply-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #28a745;
  font-size: 12px;
  font-weight: 500;
}

.reply-icon {
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.empty-state p {
  margin: 0;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #7f8c8d;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

.close-btn:hover {
  background: #f8f9fa;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #e9ecef;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-header {
  border-bottom: 1px solid #e9ecef;
  padding-bottom: 16px;
}

.detail-header h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 18px;
}

.detail-meta {
  display: flex;
  gap: 12px;
}

.type-tag,
.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.type-tag {
  background: #e9ecef;
  color: #495057;
}

.detail-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  gap: 12px;
}

.info-item .label {
  font-weight: 500;
  color: #7f8c8d;
  min-width: 80px;
}

.info-item .value {
  color: #2c3e50;
}

.detail-content h5,
.detail-reply h5 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
}

.detail-content p {
  margin: 0;
  color: #495057;
  line-height: 1.6;
}

.reply-content {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #28a745;
}

.reply-content p {
  margin: 0 0 12px 0;
  color: #495057;
  line-height: 1.6;
}

.reply-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #7f8c8d;
}

@media (max-width: 768px) {
  .feedback-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feedback-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .feedback-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .detail-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .reply-meta {
    flex-direction: column;
    gap: 4px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 2rem;
  }
  
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style> 