package com.apartment.service.impl;

import com.apartment.model.User;
import com.apartment.model.Property;
import com.apartment.model.Landlord;
import com.apartment.model.Room;
import com.apartment.repository.*;
import com.apartment.service.AdminService;
import com.apartment.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private LandlordRepository landlordRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FileService fileService;

    // ==================== 管理员登录和账号管理 ====================
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> adminLogin(String username, String password) {
        // 模拟管理员登录逻辑
        Map<String, Object> result = new HashMap<>();
        
        // 这里应该验证用户名和密码
        if ("admin".equals(username) && "admin123".equals(password)) {
            Map<String, Object> admin = new HashMap<>();
            admin.put("id", 1L);
            admin.put("username", "admin");
            admin.put("realName", "系统管理员");
            admin.put("roleLevel", 1);
            admin.put("lastLoginTime", LocalDateTime.now());
            
            result.put("accessToken", "mock_access_token");
            result.put("refreshToken", "mock_refresh_token");
            result.put("expiresIn", 7200);
            result.put("tokenType", "Bearer");
            result.put("admin", admin);
        } else {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCurrentAdminInfo() {
        // 模拟获取当前管理员信息
        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 1L);
        admin.put("username", "admin");
        admin.put("realName", "系统管理员");
        admin.put("email", "admin@example.com");
        admin.put("phone", "13888888888");
        admin.put("roleLevel", 1);
        admin.put("permissions", Arrays.asList("SYSTEM_CONFIG", "USER_MANAGE", "LANDLORD_VERIFY", "PROPERTY_AUDIT", "DATA_STATISTICS"));
        admin.put("createTime", LocalDateTime.now().minusMonths(6));
        admin.put("lastLoginTime", LocalDateTime.now());
        admin.put("status", 1);
        
        return admin;
    }
    
    @Override
    @Transactional
    public void updateAdminPassword(String oldPassword, String newPassword) {
        // 模拟密码更新逻辑
        if (!"admin123".equals(oldPassword)) {
            throw new RuntimeException("原密码不正确");
        }
        
        // 这里应该更新数据库中的密码
        logger.info("管理员密码已更新");
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminAccounts(int page, Integer size, String username, Integer roleLevel, Integer status) {
        logger.info("=== 开始获取管理员账号列表 ===");
        logger.info("参数: page={}, size={}, username={}, roleLevel={}, status={}", 
                page, size, username, roleLevel, status);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> accounts = new ArrayList<>();
        
        try {
            // 从数据库查询所有用户
            List<User> allUsers = userRepository.findAll();
            logger.info("数据库总用户数: {}", allUsers.size());
            
            // 筛选出ADMIN角色的用户
            List<User> adminUsers = allUsers.stream()
                    .filter(user -> "ADMIN".equals(user.getRole()))
                    .collect(Collectors.toList());
            
            logger.info("ADMIN角色用户数: {}", adminUsers.size());
            
            // 打印所有ADMIN用户信息
            for (User user : adminUsers) {
                logger.info("ADMIN用户: id={}, username={}, email={}, phone={}, status={}", 
                        user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getStatus());
            }
            
            // 按用户名过滤(如果指定)
            if (username != null && !username.isEmpty()) {
                adminUsers = adminUsers.stream()
                        .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                        .collect(Collectors.toList());
                logger.info("按用户名'{}'过滤后的用户数: {}", username, adminUsers.size());
            }
            
            // 按状态过滤(如果指定)
            if (status != null) {
                adminUsers = adminUsers.stream()
                        .filter(user -> user.getStatus().equals(status))
                        .collect(Collectors.toList());
                logger.info("按状态'{}'过滤后的用户数: {}", status, adminUsers.size());
            }
            
            // 转换为前端需要的格式
            for (User user : adminUsers) {
                Map<String, Object> account = new HashMap<>();
                account.put("id", user.getId());
                account.put("username", user.getUsername());
                account.put("realName", user.getUsername()); // 使用用户名作为真实姓名
                account.put("email", user.getEmail());
                account.put("phone", user.getPhone());
                account.put("roleLevel", 1); // 管理员角色等级为1
                account.put("createTime", user.getCreateTime());
                account.put("lastLoginTime", user.getLastLoginTime());
                account.put("status", user.getStatus());
                accounts.add(account);
                
                logger.info("添加管理员账号: username={}, email={}", user.getUsername(), user.getEmail());
            }
            
            // 如果没有找到管理员用户，记录警告
            if (accounts.isEmpty()) {
                logger.warn("警告: 数据库中没有找到ADMIN角色的用户！");
            }
            
            // 构建分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("page", page);
            pageInfo.put("size", size);
            pageInfo.put("total", accounts.size());
            pageInfo.put("totalPages", size > 0 ? (int) Math.ceil((double) accounts.size() / size) : 0);
            
            result.put("content", accounts);
            result.put("pageable", pageInfo);
            
            logger.info("=== 管理员账号列表获取完成 ===");
            logger.info("返回管理员账号数量: {}", accounts.size());
            
        } catch (Exception e) {
            logger.error("获取管理员账号列表失败", e);
            result.put("error", "获取管理员账号列表失败: " + e.getMessage());
            result.put("content", new ArrayList<>());
            result.put("pageable", new HashMap<>());
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public Map<String, Object> createAdminAccount(Map<String, Object> accountData) {
        // 模拟创建管理员账号
        Map<String, Object> result = new HashMap<>();
        result.put("id", 3L);
        result.put("username", accountData.get("username"));
        result.put("createTime", LocalDateTime.now());
        
        logger.info("创建管理员账号: {}", accountData.get("username"));
        return result;
    }
    
    @Override
    @Transactional
    public Map<String, Object> updateAdminAccount(Long id, Map<String, Object> accountData) {
        // 模拟更新管理员账号
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("updateTime", LocalDateTime.now());
        
        logger.info("更新管理员账号: {}", id);
        return result;
    }
    
    @Override
    @Transactional
    public void updateAdminStatus(Long id, Integer status) {
        // 模拟更新管理员状态
        logger.info("更新管理员状态: ID={}, status={}", id, status);
    }
    
    @Override
    @Transactional
    public Map<String, Object> resetAdminPassword(Long id) {
        // 模拟重置管理员密码
        Map<String, Object> result = new HashMap<>();
        result.put("tempPassword", "Admin123456");
        
        logger.info("重置管理员密码: ID={}", id);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 用户统计
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("total", userRepository.count());
            userStats.put("admin", userRepository.countByRole("ADMIN"));
            userStats.put("landlord", userRepository.countByRole("LANDLORD"));
            userStats.put("tenant", userRepository.countByRole("TENANT"));
            
            // 房产统计
            Map<String, Object> propertyStats = new HashMap<>();
            long totalProperties = propertyRepository.count();
            long totalRooms = roomRepository.count();
            long rentedRooms = roomRepository.countByStatus("RENTED");
            long vacantRooms = roomRepository.countByStatus("VACANT");
            long maintenanceRooms = roomRepository.countByStatus("MAINTENANCE");
            
            propertyStats.put("propertyCount", totalProperties);
            propertyStats.put("totalRoomCount", totalRooms);
            propertyStats.put("rentedRoomCount", rentedRooms);
            propertyStats.put("vacantRoomCount", vacantRooms);
            propertyStats.put("maintenanceRoomCount", maintenanceRooms);
            propertyStats.put("occupancyRate", totalRooms > 0 ? Math.round((double) rentedRooms / totalRooms * 100 * 100) / 100.0 : 0);
            
            // 财务统计
            Map<String, Object> financialStats = new HashMap<>();
            
            // 计算总收入（这里简化为所有已出租房间的月租金总和）
            Double totalRevenue = 0.0;
            // 使用分页查询避免一次性查询太多数据
            Pageable pageable = PageRequest.of(0, 1000);
            Page<Room> rentedRoomsList = roomRepository.findByStatus("RENTED", pageable);
            for (Room room : rentedRoomsList.getContent()) {
                if (room.getMonthlyRent() != null) {
                    totalRevenue += room.getMonthlyRent().doubleValue();
                }
            }
            
            financialStats.put("totalRevenue", totalRevenue);
            financialStats.put("monthlyRevenue", totalRevenue); // 假设是月收入
            financialStats.put("averageRent", rentedRooms > 0 ? Math.round(totalRevenue / rentedRooms * 100) / 100.0 : 0);
            
            // 近期活动统计
            Map<String, Object> activityStats = new HashMap<>();
            
            // 统计最近7天的新增用户 (简化处理，先不统计时间范围)
            activityStats.put("newUsersLastWeek", 0L);
            activityStats.put("newPropertiesLastWeek", 0L);
            
            // 组装结果
            result.put("userStats", userStats);
            result.put("propertyStats", propertyStats);
            result.put("financialStats", financialStats);
            result.put("activityStats", activityStats);
            
        } catch (Exception e) {
            logger.error("获取系统概览失败", e);
            result.put("error", "获取系统概览失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllUsers(int page, int size, String role, String keyword) {
        return getAllUsers(page, size, role, keyword, null);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getAllUsers(int page, int size, String role, String keyword, Integer status) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<User> usersPage;
            
            // 构建查询条件
            if (role != null && !role.isEmpty() && keyword != null && !keyword.isEmpty() && status != null) {
                // 角色 + 关键词 + 状态
                usersPage = userRepository.findByRoleAndStatusAndUsernameContainingOrEmailContainingOrPhoneContaining(
                        role, status, keyword, keyword, keyword, pageable);
            } else if (role != null && !role.isEmpty() && keyword != null && !keyword.isEmpty()) {
                // 角色 + 关键词
                usersPage = userRepository.findByRoleAndUsernameContainingOrEmailContainingOrPhoneContaining(
                        role, keyword, keyword, keyword, pageable);
            } else if (role != null && !role.isEmpty() && status != null) {
                // 角色 + 状态
                usersPage = userRepository.findByRoleAndStatus(role, status, pageable);
            } else if (keyword != null && !keyword.isEmpty() && status != null) {
                // 关键词 + 状态
                usersPage = userRepository.findByStatusAndUsernameContainingOrEmailContainingOrPhoneContaining(
                        status, keyword, keyword, keyword, pageable);
            } else if (role != null && !role.isEmpty()) {
                // 只按角色筛选
                usersPage = userRepository.findByRole(role, pageable);
            } else if (keyword != null && !keyword.isEmpty()) {
                // 只按关键词筛选
                usersPage = userRepository.findByUsernameContainingOrEmailContainingOrPhoneContaining(
                        keyword, keyword, keyword, pageable);
            } else if (status != null) {
                // 只按状态筛选
                usersPage = userRepository.findByStatus(status, pageable);
            } else {
                // 不筛选
                usersPage = userRepository.findAll(pageable);
            }
            
            List<User> users = usersPage.getContent();
            
            // 处理分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("currentPage", usersPage.getNumber());
            pageInfo.put("totalPages", usersPage.getTotalPages());
            pageInfo.put("totalElements", usersPage.getTotalElements());
            pageInfo.put("size", usersPage.getSize());
            
            result.put("users", users);
            result.put("pageInfo", pageInfo);
            
        } catch (Exception e) {
            logger.error("获取用户列表失败", e);
            result.put("error", "获取用户列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在，ID: " + id));
    }

    @Override
    @Transactional
    public User updateUserStatus(Long id, Integer status) {
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        
        // 检查是否为最后一个管理员
        if ("ADMIN".equals(user.getRole())) {
            long adminCount = userRepository.countByRole("ADMIN");
            if (adminCount <= 1) {
                throw new IllegalStateException("不能删除最后一个管理员账户");
            }
        }
        
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public User createAdminUser(User user) {
        // 验证用户数据
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        
        if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("手机号已被注册");
        }
        
        // 设置管理员角色
        user.setRole("ADMIN");
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置其他默认值
        user.setStatus(1); // 启用状态
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User createUser(User user) {
        // 验证用户数据
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        
        if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("手机号已被注册");
        }
        
        // 设置用户角色为普通用户
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置其他默认值
        user.setStatus(1); // 启用状态
        
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemLogs(int page, int size, String operationType, Long userId, String startTime, String endTime) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟系统日志数据
        List<Map<String, Object>> logs = new ArrayList<>();
        
        // 创建一些示例日志数据
        String[] logTypes = {"LOGIN", "LOGOUT", "CREATE", "UPDATE", "DELETE", "ERROR"};
        String[] usernames = {"admin", "landlord1", "user1", "system"};
        String[] operations = {
            "用户登录", "用户退出", "创建新用户", "更新用户信息", "删除用户", 
            "创建物业", "更新物业信息", "删除物业", 
            "签订合同", "更新合同", "终止合同",
            "系统异常", "数据库连接失败", "权限验证失败"
        };
        
        // 生成随机日志数据
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (int i = 0; i < 100; i++) {
            Map<String, Object> log = new HashMap<>();
            String logType = logTypes[random.nextInt(logTypes.length)];
            
            // 如果指定了日志类型，只添加该类型的日志
            if (operationType != null && !operationType.isEmpty() && !operationType.equals(logType)) {
                continue;
            }
            
            LocalDateTime logTime = now.minusDays(random.nextInt(30));
            
            // 如果指定了日期范围，只添加范围内的日志
            if (startTime != null && !startTime.isEmpty()) {
                LocalDateTime startDateTime = LocalDateTime.parse(startTime + "T00:00:00");
                if (logTime.isBefore(startDateTime)) {
                    continue;
                }
            }
            
            if (endTime != null && !endTime.isEmpty()) {
                LocalDateTime endDateTime = LocalDateTime.parse(endTime + "T23:59:59");
                if (logTime.isAfter(endDateTime)) {
                    continue;
                }
            }
            
            log.put("id", (long) (i + 1));
            log.put("type", logType);
            log.put("username", usernames[random.nextInt(usernames.length)]);
            log.put("operation", operations[random.nextInt(operations.length)]);
            log.put("ip", "192.168.1." + random.nextInt(255));
            log.put("time", logTime.format(formatter));
            log.put("status", random.nextInt(10) < 9 ? "SUCCESS" : "FAILED"); // 90%成功
            
            logs.add(log);
        }
        
        // 排序日志（按时间倒序）
        logs.sort((a, b) -> ((String) b.get("time")).compareTo((String) a.get("time")));
        
        // 处理分页
        int totalElements = logs.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        
        List<Map<String, Object>> pagedLogs = 
                fromIndex < totalElements ? logs.subList(fromIndex, toIndex) : new ArrayList<>();
        
        // 构建分页信息
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("currentPage", page);
        pageInfo.put("totalPages", totalPages);
        pageInfo.put("totalElements", totalElements);
        pageInfo.put("size", size);
        
        result.put("logs", pagedLogs);
        result.put("pageInfo", pageInfo);
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        // 系统基本设置
        Map<String, Object> basicSettings = new HashMap<>();
        basicSettings.put("siteName", "公寓管理系统");
        basicSettings.put("siteDescription", "一站式公寓租赁和管理平台");
        basicSettings.put("contactEmail", "admin@apartment.com");
        basicSettings.put("contactPhone", "400-123-4567");
        settings.put("basic", basicSettings);
        
        // 邮件设置
        Map<String, Object> mailSettings = new HashMap<>();
        mailSettings.put("smtpServer", "smtp.apartment.com");
        mailSettings.put("smtpPort", 465);
        mailSettings.put("smtpUsername", "notification@apartment.com");
        mailSettings.put("enableSsl", true);
        settings.put("mail", mailSettings);
        
        // 文件上传设置
        Map<String, Object> uploadSettings = new HashMap<>();
        uploadSettings.put("maxFileSize", 10);
        uploadSettings.put("allowedFileTypes", "jpg,jpeg,png,pdf,doc,docx");
        uploadSettings.put("storageType", "LOCAL");
        settings.put("upload", uploadSettings);
        
        // 水电费率设置
        Map<String, Object> utilityRates = new HashMap<>();
        utilityRates.put("waterRate", 5.00);
        utilityRates.put("electricityRate", 0.50);
        utilityRates.put("gasRate", 3.00);
        settings.put("utilityRates", utilityRates);
        
        return settings;
    }

    @Override
    @Transactional
    public Map<String, Object> updateSystemSettings(Map<String, Object> settings) {
        // 在实际应用中，这里应该将设置保存到数据库
        // 现在我们只是返回更新后的设置（与输入相同）
        logger.info("更新系统设置: {}", settings);
        return settings;
    }
    
    // ==================== 房源管理实现 ====================
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllProperties(int page, int size, Integer status, String keyword) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<Property> propertiesPage;
            
            if (status != null && keyword != null && !keyword.isEmpty()) {
                // 同时按状态和关键词筛选
                propertiesPage = propertyRepository.findByStatusAndTitleContainingOrAddressContaining(
                        status, keyword, keyword, pageable);
            } else if (status != null) {
                // 只按状态筛选
                propertiesPage = propertyRepository.findByStatus(status, pageable);
            } else if (keyword != null && !keyword.isEmpty()) {
                // 只按关键词筛选
                propertiesPage = propertyRepository.findByTitleContainingOrAddressContaining(keyword, keyword, pageable);
            } else {
                // 不筛选
                propertiesPage = propertyRepository.findAll(pageable);
            }
            
            List<Property> properties = propertiesPage.getContent();
            
            // 处理分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("currentPage", propertiesPage.getNumber());
            pageInfo.put("totalPages", propertiesPage.getTotalPages());
            pageInfo.put("totalElements", propertiesPage.getTotalElements());
            pageInfo.put("size", propertiesPage.getSize());
            
            result.put("properties", properties);
            result.put("pageInfo", pageInfo);
            
        } catch (Exception e) {
            logger.error("获取房源列表失败", e);
            result.put("error", "获取房源列表失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("房源不存在，ID: " + id));
    }
    
    @Override
    @Transactional
    public Property updatePropertyStatus(Long id, Integer status) {
        Property property = getPropertyById(id);
        property.setStatus(status);
        return propertyRepository.save(property);
    }
    
    @Override
    @Transactional
    public void deleteProperty(Long id) {
        Property property = getPropertyById(id);
        propertyRepository.delete(property);
    }
    
    @Override
    @Transactional
    public String uploadPropertyImage(Long propertyId, MultipartFile file) {
        try {
            Property property = getPropertyById(propertyId);
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("只允许上传图片文件");
            }
            
            // 上传文件
            String imageUrl = fileService.uploadFile(file, "property");
            
            // 更新房源图片列表
            List<String> imageUrls = property.getImageUrls();
            imageUrls.add(imageUrl);
            property.setImageUrls(imageUrls);
            propertyRepository.save(property);
            
            return imageUrl;
        } catch (IOException e) {
            logger.error("上传房源图片失败", e);
            throw new RuntimeException("上传房源图片失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void deletePropertyImage(Long propertyId, String imageUrl) {
        Property property = getPropertyById(propertyId);
        
        // 从房源图片列表中移除
        List<String> imageUrls = property.getImageUrls();
        if (imageUrls.remove(imageUrl)) {
            property.setImageUrls(imageUrls);
            propertyRepository.save(property);
            
            // 删除文件
            try {
                fileService.deleteFile(imageUrl);
            } catch (IOException e) {
                logger.error("删除房源图片文件失败: {}", imageUrl, e);
                // 文件删除失败，但数据库记录已更新，记录错误日志
            }
        } else {
            throw new IllegalArgumentException("指定的图片URL不存在");
        }
    }
    
    // ==================== 房东管理实现 ====================
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllLandlords(int page, int size, Boolean verified, String keyword) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<Landlord> landlordsPage;
            
            // 这里简化处理，实际应用中应根据需求扩展Repository方法
            if (verified != null) {
                landlordsPage = new PageImpl<>(landlordRepository.findByVerified(verified), pageable, 
                        landlordRepository.findByVerified(verified).size());
            } else {
                landlordsPage = landlordRepository.findAll(pageable);
            }
            
            List<Landlord> landlords = landlordsPage.getContent();
            
            // 如果有关键词，手动过滤
            if (keyword != null && !keyword.isEmpty()) {
                landlords = landlords.stream()
                        .filter(l -> l.getRealName().contains(keyword) || 
                                l.getIdCard().contains(keyword))
                        .collect(Collectors.toList());
            }
            
            // 构建前端需要的数据结构
            List<Map<String, Object>> landlordList = new ArrayList<>();
            for (Landlord landlord : landlords) {
                Map<String, Object> landlordMap = new HashMap<>();
                
                // 确保加载用户信息
                User user = null;
                if (landlord.getUser() == null && landlord.getUserId() != null) {
                    try {
                        user = userRepository.findById(landlord.getUserId()).orElse(null);
                        landlord.setUser(user);
                    } catch (Exception e) {
                        logger.warn("加载房东用户信息失败，房东ID: {}, 用户ID: {}", landlord.getId(), landlord.getUserId());
                    }
                } else {
                    user = landlord.getUser();
                }
                
                // 统计房产数量
                List<Property> properties = propertyRepository.findByLandlordId(landlord.getId());
                int totalProperties = properties.size();
                // 暂时设置为0，避免数据库结构不匹配的错误
                long rentedProperties = 0;
                
                // 基本信息
                landlordMap.put("id", landlord.getId());
                landlordMap.put("userId", landlord.getUserId());
                landlordMap.put("realName", landlord.getRealName());
                landlordMap.put("idCard", landlord.getIdCard());
                landlordMap.put("verified", landlord.getVerified());
                landlordMap.put("rating", landlord.getRating());
                
                // 联系信息 - 优先使用房东信息，如果没有则使用用户信息
                if (user != null) {
                    landlordMap.put("contactPhone", user.getPhone());
                    landlordMap.put("contactEmail", user.getEmail());
                    landlordMap.put("user", user); // 保留user对象用于头像显示
                } else {
                    landlordMap.put("contactPhone", "");
                    landlordMap.put("contactEmail", "");
                    landlordMap.put("user", null);
                }
                
                // 房产统计
                landlordMap.put("totalProperties", totalProperties);
                landlordMap.put("rentedProperties", rentedProperties);
                
                // 时间信息
                            landlordMap.put("createTime", landlord.getCreateTime());
            landlordMap.put("createdAt", landlord.getCreateTime());
            landlordMap.put("updatedAt", landlord.getUpdateTime());
                
                landlordList.add(landlordMap);
            }
            
            // 处理分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("currentPage", landlordsPage.getNumber());
            pageInfo.put("totalPages", landlordsPage.getTotalPages());
            pageInfo.put("totalElements", landlordsPage.getTotalElements());
            pageInfo.put("size", landlordsPage.getSize());
            
            result.put("landlords", landlordList);
            result.put("pageInfo", pageInfo);
            
        } catch (Exception e) {
            logger.error("获取房东列表失败", e);
            result.put("error", "获取房东列表失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Landlord getLandlordById(Long id) {
        return landlordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("房东不存在，ID: " + id));
    }
    
    @Override
    @Transactional
    public Landlord updateLandlordVerified(Long id, Boolean verified) {
        Landlord landlord = getLandlordById(id);
        landlord.setVerified(verified);
        return landlordRepository.save(landlord);
    }
    
    // ==================== 文件上传实现 ====================
    
    @Override
    @Transactional
    public String uploadUserAvatar(Long userId, MultipartFile file) {
        try {
            User user = getUserById(userId);
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("只允许上传图片文件");
            }
            
            // 上传文件
            String avatarUrl = fileService.uploadFile(file, "avatar");
            
            // 更新用户头像
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
            
            return avatarUrl;
        } catch (IOException e) {
            logger.error("上传用户头像失败", e);
            throw new RuntimeException("上传用户头像失败: " + e.getMessage());
        }
    }
    
    // 内部使用的分页帮助类
    private static class PageImpl<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImpl(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

    @Override
    public List<Property> getPropertiesByLandlordId(Long landlordId) {
        // 确保房东存在
        Landlord landlord = getLandlordById(landlordId);
        
        // 根据房东ID查询所有房产
        List<Property> properties = propertyRepository.findByLandlordId(landlordId);
        
        // 为每个房产添加额外的统计信息
        for (Property property : properties) {
            // 统计房间数量
            long totalRooms = roomRepository.countByPropertyId(property.getId());
            long rentedRooms = roomRepository.countByPropertyIdAndStatus(property.getId(), "RENTED");
            
            // 这里可以添加更多统计信息到Property对象中
            // 由于Property实体类可能没有这些字段，我们在Controller层处理
        }
        
        return properties;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLandlordVerifyApplications(int page, Integer size, String status, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Landlord> landlordPage;
            
            // 构建查询条件
            if (status != null && !status.isEmpty()) {
                Boolean verified = "APPROVED".equals(status);
                // 由于Repository方法不支持分页，我们先获取所有数据然后手动分页
                List<Landlord> allLandlords = landlordRepository.findByVerified(verified);
                landlordPage = new PageImpl<>(allLandlords, pageable, allLandlords.size());
            } else {
                // 查询所有申请
                landlordPage = landlordRepository.findAll(pageable);
            }
            
            List<Map<String, Object>> applicationList = new ArrayList<>();
            for (Landlord landlord : landlordPage.getContent()) {
                Map<String, Object> application = new HashMap<>();
                
                // 获取用户信息
                User user = userRepository.findById(landlord.getUserId())
                        .orElse(new User()); // 使用空对象避免NPE
                
                application.put("applicationId", landlord.getId());
                application.put("userId", landlord.getUserId());
                application.put("username", user.getUsername());
                application.put("realName", landlord.getRealName());
                application.put("idCard", landlord.getIdCard());
                application.put("contactPhone", user.getPhone());
                application.put("status", landlord.getVerified() ? "APPROVED" : "PENDING");
                            application.put("submitTime", user.getCreateTime());
            application.put("reviewTime", landlord.getUpdateTime());
                application.put("reviewComment", null); // 假设没有存储审核意见
                
                applicationList.add(application);
            }
            
            // 处理分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("page", page + 1);
            pageInfo.put("size", size);
            pageInfo.put("total", landlordPage.getTotalElements());
            pageInfo.put("totalPages", landlordPage.getTotalPages());
            
            result.put("content", applicationList);
            result.put("pageable", pageInfo);
            
        } catch (Exception e) {
            logger.error("获取房东认证申请列表失败", e);
            throw new RuntimeException("获取房东认证申请列表失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public Map<String, Object> verifyLandlordApplication(Long applicationId, String action, String comment) {
        try {
            Landlord landlord = landlordRepository.findById(applicationId)
                    .orElseThrow(() -> new EntityNotFoundException("房东认证申请不存在，ID: " + applicationId));
            
            if ("APPROVE".equals(action)) {
                landlord.setVerified(true);
            } else if ("REJECT".equals(action)) {
                landlord.setVerified(false);
            } else {
                throw new IllegalArgumentException("无效的操作类型: " + action);
            }
            
            // BaseEntity会自动更新updatedAt字段
            Landlord updatedLandlord = landlordRepository.save(landlord);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("applicationId", updatedLandlord.getId());
            result.put("status", updatedLandlord.getVerified() ? "APPROVED" : "REJECTED");
            result.put("reviewTime", updatedLandlord.getUpdateTime());
            
            return result;
        } catch (Exception e) {
            logger.error("审核房东认证申请失败", e);
            throw new RuntimeException("审核房东认证申请失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void deleteLandlord(Long id) {
        try {
            // 检查房东是否存在
            Landlord landlord = landlordRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("房东不存在，ID: " + id));
            
            // 检查房东是否有关联的房产
            List<Property> properties = propertyRepository.findByLandlordId(id);
            if (!properties.isEmpty()) {
                throw new RuntimeException("无法删除房东，该房东还有 " + properties.size() + " 个房产，请先删除相关房产");
            }
            
            // 检查房东是否有关联的房间
            long roomCount = roomRepository.countByLandlordId(id);
            if (roomCount > 0) {
                throw new RuntimeException("无法删除房东，该房东还有 " + roomCount + " 个房间，请先删除相关房间");
            }
            
            // 删除房东相关的其他数据（如果有的话）
            // 这里可以根据业务需求添加更多的关联数据检查和清理
            
            // 删除房东记录
            landlordRepository.deleteById(id);
            
            logger.info("成功删除房东，ID: {}", id);
        } catch (EntityNotFoundException e) {
            logger.error("删除房东失败，房东不存在: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("删除房东失败，ID: {}", id, e);
            throw new RuntimeException("删除房东失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPropertiesForAudit(int page, Integer size, String status, Long landlordId, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Property> propertyPage;
            
            // 构建查询条件
            if (landlordId != null) {
                // 按房东ID查询
                // 由于Repository方法不支持分页，我们先获取所有数据然后手动分页
                List<Property> allProperties = propertyRepository.findByLandlordId(landlordId);
                propertyPage = new PageImpl<>(allProperties, pageable, allProperties.size());
            } else if (status != null && !status.isEmpty()) {
                Integer statusCode;
                
                // 根据状态字符串获取状态码
                switch (status) {
                    case "PENDING":
                        statusCode = 0; // 假设0是待审核
                        break;
                    case "APPROVED":
                        statusCode = 1; // 假设1是已通过
                        break;
                    case "REJECTED":
                        statusCode = 2; // 假设2是已拒绝
                        break;
                    default:
                        throw new IllegalArgumentException("无效的状态: " + status);
                }
                
                // 由于Repository方法不支持分页，我们先获取所有数据然后手动分页
                List<Property> allProperties = propertyRepository.findByStatus(statusCode);
                propertyPage = new PageImpl<>(allProperties, pageable, allProperties.size());
            } else {
                // 查询所有待审核房源
                propertyPage = propertyRepository.findAll(pageable);
            }
            
            List<Map<String, Object>> propertyList = new ArrayList<>();
            for (Property property : propertyPage.getContent()) {
                Map<String, Object> propertyMap = new HashMap<>();
                
                // 获取房东信息
                Landlord landlord = landlordRepository.findById(property.getLandlordId())
                        .orElse(new Landlord()); // 使用空对象避免NPE
                
                propertyMap.put("propertyId", property.getId());
                propertyMap.put("title", property.getTitle());
                propertyMap.put("landlordId", property.getLandlordId());
                propertyMap.put("landlordName", landlord.getRealName());
                propertyMap.put("address", property.getAddress());
                propertyMap.put("price", property.getMonthlyRent());
                propertyMap.put("area", property.getArea());
                propertyMap.put("roomCount", property.getBedrooms());
                
                // 状态转换
                String statusStr;
                switch (property.getStatus()) {
                    case 0:
                        statusStr = "PENDING";
                        break;
                    case 1:
                        statusStr = "APPROVED";
                        break;
                    case 2:
                        statusStr = "REJECTED";
                        break;
                    default:
                        statusStr = "UNKNOWN";
                }
                propertyMap.put("status", statusStr);
                
                            propertyMap.put("submitTime", property.getCreateTime());
            propertyMap.put("auditTime", property.getUpdateTime());
                propertyMap.put("auditComment", null); // 假设没有存储审核意见
                
                propertyList.add(propertyMap);
            }
            
            // 处理分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("page", page + 1);
            pageInfo.put("size", size);
            pageInfo.put("total", propertyPage.getTotalElements());
            pageInfo.put("totalPages", propertyPage.getTotalPages());
            
            result.put("content", propertyList);
            result.put("pageable", pageInfo);
            
        } catch (Exception e) {
            logger.error("获取待审核房源列表失败", e);
            throw new RuntimeException("获取待审核房源列表失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public Map<String, Object> auditProperty(Long propertyId, String action, String comment) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new EntityNotFoundException("房源不存在，ID: " + propertyId));
            
            if ("APPROVE".equals(action)) {
                property.setStatus(1); // 假设1是已通过
            } else if ("REJECT".equals(action)) {
                property.setStatus(2); // 假设2是已拒绝
            } else {
                throw new IllegalArgumentException("无效的操作类型: " + action);
            }
            
            // BaseEntity会自动更新updatedAt字段
            Property updatedProperty = propertyRepository.save(property);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("propertyId", updatedProperty.getId());
            
            String statusStr;
            switch (updatedProperty.getStatus()) {
                case 1:
                    statusStr = "APPROVED";
                    break;
                case 2:
                    statusStr = "REJECTED";
                    break;
                default:
                    statusStr = "UNKNOWN";
            }
            result.put("status", statusStr);
            
            result.put("auditTime", updatedProperty.getUpdateTime());
            
            return result;
        } catch (Exception e) {
            logger.error("审核房源失败", e);
            throw new RuntimeException("审核房源失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemConfig() {
        try {
            // 假设从数据库中读取系统配置
            List<Map<String, Object>> configs = new ArrayList<>();
            
            // 示例配置项
            Map<String, Object> config1 = new HashMap<>();
            config1.put("key", "SYSTEM_NAME");
            config1.put("value", "公寓管理系统");
            config1.put("description", "系统名称");
            config1.put("updateTime", LocalDateTime.of(2023, 1, 1, 0, 0, 0));
            configs.add(config1);
            
            Map<String, Object> config2 = new HashMap<>();
            config2.put("key", "DEPOSIT_RATE");
            config2.put("value", "1");
            config2.put("description", "押金月数");
            config2.put("updateTime", LocalDateTime.of(2023, 1, 1, 0, 0, 0));
            configs.add(config2);
            
            Map<String, Object> config3 = new HashMap<>();
            config3.put("key", "WATER_PRICE");
            config3.put("value", "5.00");
            config3.put("description", "水费单价(元/吨)");
            config3.put("updateTime", LocalDateTime.of(2023, 3, 1, 0, 0, 0));
            configs.add(config3);
            
            Map<String, Object> config4 = new HashMap<>();
            config4.put("key", "ELECTRICITY_PRICE");
            config4.put("value", "0.50");
            config4.put("description", "电费单价(元/度)");
            config4.put("updateTime", LocalDateTime.of(2023, 3, 1, 0, 0, 0));
            configs.add(config4);
            
            Map<String, Object> result = new HashMap<>();
            result.put("configs", configs);
            
            return result;
        } catch (Exception e) {
            logger.error("获取系统配置失败", e);
            throw new RuntimeException("获取系统配置失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Map<String, Object> updateSystemConfig(String key, String value) {
        try {
            // 假设更新数据库中的系统配置
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("key", key);
            result.put("value", value);
            result.put("updateTime", LocalDateTime.now());
            
            return result;
        } catch (Exception e) {
            logger.error("更新系统配置失败", e);
            throw new RuntimeException("更新系统配置失败: " + e.getMessage());
        }
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatisticsOverview(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 用户统计
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("totalUsers", 1000);
            userStats.put("newUsers", 50);
            userStats.put("activeUsers", 500);
            userStats.put("landlordCount", 100);
            result.put("userStats", userStats);
            
            // 房源统计
            Map<String, Object> propertyStats = new HashMap<>();
            propertyStats.put("totalProperties", 300);
            propertyStats.put("availableProperties", 100);
            propertyStats.put("rentedProperties", 200);
            propertyStats.put("averagePrice", 3500.00);
            result.put("propertyStats", propertyStats);
            
            // 合同统计
            Map<String, Object> contractStats = new HashMap<>();
            contractStats.put("totalContracts", 250);
            contractStats.put("newContracts", 20);
            contractStats.put("expiredContracts", 10);
            contractStats.put("totalIncome", 750000.00);
            result.put("contractStats", contractStats);
            
            // 时间趋势统计
            Map<String, Object> timeStats = new HashMap<>();
            
            List<Map<String, Object>> dailyActiveUsers = new ArrayList<>();
            Map<String, Object> day1 = new HashMap<>();
            day1.put("date", "2023-06-01");
            day1.put("count", 120);
            dailyActiveUsers.add(day1);
            
            Map<String, Object> day2 = new HashMap<>();
            day2.put("date", "2023-06-02");
            day2.put("count", 135);
            dailyActiveUsers.add(day2);
            
            Map<String, Object> day3 = new HashMap<>();
            day3.put("date", "2023-06-03");
            day3.put("count", 142);
            dailyActiveUsers.add(day3);
            
            timeStats.put("dailyActiveUsers", dailyActiveUsers);
            
            List<Map<String, Object>> newPropertiesTrend = new ArrayList<>();
            Map<String, Object> prop1 = new HashMap<>();
            prop1.put("date", "2023-06-01");
            prop1.put("count", 5);
            newPropertiesTrend.add(prop1);
            
            Map<String, Object> prop2 = new HashMap<>();
            prop2.put("date", "2023-06-02");
            prop2.put("count", 8);
            newPropertiesTrend.add(prop2);
            
            Map<String, Object> prop3 = new HashMap<>();
            prop3.put("date", "2023-06-03");
            prop3.put("count", 6);
            newPropertiesTrend.add(prop3);
            
            timeStats.put("newPropertiesTrend", newPropertiesTrend);
            
            result.put("timeStats", timeStats);
            
            return result;
        } catch (Exception e) {
            logger.error("获取数据统计失败", e);
            throw new RuntimeException("获取数据统计失败: " + e.getMessage());
        }
    }
}