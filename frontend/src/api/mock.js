/**
 * 模拟数据文件
 * 当后端API不可用时使用
 */

// 模拟成功响应
export function mockSuccess(data = null) {
  return {
    code: 200,
    message: '操作成功',
    data: data
  };
}

// 模拟用户数据
export function getMockUsers(params = {}) {
  const { role = 'USER', page = 0, size = 10, keyword = '', status } = params;
  
  const allUsers = [
    {
      id: 1,
      username: 'user001',
      email: 'user001@example.com',
      phone: '13800138001',
      role: 'USER',
      status: 1,
      avatarUrl: null,
      createTime: '2024-01-15 10:30:00'
    },
    {
      id: 2,
      username: 'user002',
      email: 'user002@example.com',
      phone: '13800138002',
      role: 'USER',
      status: 1,
      avatarUrl: null,
      createTime: '2024-01-16 14:20:00'
    }
  ];

  // 根据角色过滤
  let filteredUsers = allUsers.filter(user => user.role === role);
  
  // 根据关键词过滤
  if (keyword) {
    filteredUsers = filteredUsers.filter(user => 
      user.username.includes(keyword) || 
      user.email.includes(keyword) ||
      user.phone.includes(keyword)
    );
  }
  
  // 根据状态过滤
  if (status !== undefined && status !== '') {
    filteredUsers = filteredUsers.filter(user => user.status === parseInt(status));
  }
  
  // 分页
  const start = page * size;
  const end = start + size;
  const pageData = filteredUsers.slice(start, end);
  
  return {
    content: pageData,
    totalElements: filteredUsers.length,
    totalPages: Math.ceil(filteredUsers.length / size),
    size: size,
    number: page
  };
}

// 模拟房东数据
export function getMockLandlords(params = {}) {
  const { page = 0, size = 10, keyword = '', status } = params;
  
  const allLandlords = [
    {
      id: 1,
      username: 'landlord001',
      email: 'landlord001@example.com',
      phone: '13900139001',
      role: 'LANDLORD',
      status: 1,
      avatarUrl: null,
      createTime: '2024-01-10 08:30:00',
      propertyCount: 3,
      totalRevenue: 15000
    }
  ];

  // 根据关键词过滤
  let filteredLandlords = allLandlords;
  if (keyword) {
    filteredLandlords = filteredLandlords.filter(landlord => 
      landlord.username.includes(keyword) || 
      landlord.email.includes(keyword) ||
      landlord.phone.includes(keyword)
    );
  }
  
  // 根据状态过滤
  if (status !== undefined && status !== '') {
    filteredLandlords = filteredLandlords.filter(landlord => landlord.status === parseInt(status));
  }
  
  // 分页
  const start = page * size;
  const end = start + size;
  const pageData = filteredLandlords.slice(start, end);
  
  return {
    content: pageData,
    totalElements: filteredLandlords.length,
    totalPages: Math.ceil(filteredLandlords.length / size),
    size: size,
    number: page
  };
}

// 模拟房东房产数据
export function getMockLandlordProperties(landlordId) {
  const properties = [
    {
      id: 1,
      title: '精装两室一厅',
      address: '北京市朝阳区建国路88号',
      monthlyRent: 4500,
      status: 'AVAILABLE',
      roomCount: 3,
      createTime: '2024-01-15 10:00:00'
    }
  ];
  
  return {
    content: properties,
    totalElements: properties.length,
    totalPages: 1,
    size: 10,
    number: 0
  };
} 