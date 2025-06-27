package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.api.dto.*;
import com.apartment.model.User;
import com.apartment.model.UserFeedback;
import com.apartment.model.RoomApplication;
import com.apartment.model.LandlordApplication;
import com.apartment.model.Landlord;
import com.apartment.model.LandlordTodo;
import com.apartment.model.Notification;
import com.apartment.service.UserService;
import com.apartment.service.UserFeedbackService;
import com.apartment.service.RoomApplicationService;
import com.apartment.service.LandlordApplicationService;
import com.apartment.service.BillService;
import com.apartment.service.UtilityBillService;
import com.apartment.service.ContractService;
import com.apartment.service.RoomService;
import com.apartment.service.UserDashboardService;
import com.apartment.service.LandlordService;
import com.apartment.service.NotificationService;
import com.apartment.repository.LandlordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    // 验证码缓存（实际项目中应该使用Redis）
    private final Map<String, String> verificationCodeCache = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private UserFeedbackService feedbackService;

    @Autowired
    private RoomApplicationService applicationService;

    @Autowired
    private LandlordApplicationService landlordApplicationService;

    @Autowired
    private BillService billService;

    @Autowired
    private UtilityBillService utilityBillService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserDashboardService userDashboardService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LandlordService landlordService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LandlordRepository landlordRepository;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return ApiResponse.success(user);
    }

    /**
     * 获取指定用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userService.findByUsername(currentUsername);

        // 普通用户只能查看自己的信息
        if ("USER".equals(currentUser.getRole()) && !currentUser.getId().equals(id)) {
            return ApiResponse.error(403, "权限不足");
        }

        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ApiResponse.success(user);
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/me")
    public ApiResponse<User> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        User updateUser = new User();
        BeanUtils.copyProperties(request, updateUser);

        User updatedUser = userService.updateUser(currentUser.getId(), updateUser);
        return ApiResponse.success(updatedUser);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                logger.error("用户未认证");
                return ApiResponse.error("用户未认证，请重新登录");
            }
            
            String username = auth.getName();
            logger.info("用户 {} 正在上传头像", username);
            
            User currentUser = userService.findByUsername(username);
            if (currentUser == null) {
                logger.error("用户不存在: {}", username);
                return ApiResponse.error("用户不存在");
            }
            
            logger.info("用户信息: ID={}, 角色={}, 状态={}", currentUser.getId(), currentUser.getRole(), currentUser.getStatus());
            
            if (file.isEmpty()) {
                return ApiResponse.error("请选择要上传的文件");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error("只能上传图片文件");
            }
            
            // 验证文件大小 (2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                return ApiResponse.error("图片大小不能超过2MB");
            }

            String avatarUrl = userService.uploadAvatar(currentUser.getId(), file);
            logger.info("头像上传成功: {}", avatarUrl);

            Map<String, String> result = new HashMap<>();
            result.put("avatarUrl", avatarUrl);
            return ApiResponse.success("头像上传成功", result);
        } catch (Exception e) {
            logger.error("头像上传失败", e);
            return ApiResponse.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户列表（管理员功能）
     */
    @GetMapping
    public ApiResponse<Page<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> users = userService.findUsers(username, role, startTime, endTime, pageable);
        return ApiResponse.success(users);
    }

    /**
     * 禁用/启用用户账号（管理员功能）
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        userService.updateUserStatus(id, status);
        return ApiResponse.success("用户状态更新成功");
    }

    /**
     * 变更用户角色（管理员功能）
     */
    @PutMapping("/{id}/role")
    public ApiResponse<Void> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String role = request.get("role");
        userService.updateUserRole(id, role);
        return ApiResponse.success("用户角色更新成功");
    }

    /**
     * 重置用户密码（管理员功能）
     */
    @PostMapping("/{id}/password/reset")
    public ApiResponse<Map<String, String>> resetPassword(@PathVariable Long id) {
        // 生成临时密码
        String tempPassword = generateTempPassword();
        userService.resetPassword(id, tempPassword);

        Map<String, String> result = new HashMap<>();
        result.put("tempPassword", tempPassword);
        return ApiResponse.success("密码重置成功", result);
    }

    /**
     * 提交反馈
     */
    @PostMapping("/feedback")
    public ApiResponse<Map<String, Object>> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        UserFeedback feedback = new UserFeedback();
        BeanUtils.copyProperties(request, feedback);

        UserFeedback savedFeedback = feedbackService.submitFeedback(currentUser.getId(), feedback);

        Map<String, Object> result = new HashMap<>();
        result.put("feedbackId", savedFeedback.getId());
        result.put("createTime", savedFeedback.getCreateTime());
        return ApiResponse.success("反馈提交成功", result);
    }

    /**
     * 查询用户反馈列表
     */
    @GetMapping("/feedback")
    public ApiResponse<Page<UserFeedback>> getUserFeedback(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserFeedback> feedback;

        // 管理员可以查看所有反馈，普通用户只能查看自己的反馈
        if ("ADMIN".equals(currentUser.getRole())) {
            feedback = feedbackService.getAllFeedback(type, status, pageable);
        } else {
            feedback = feedbackService.getUserFeedback(currentUser.getId(), type, status, pageable);
        }

        return ApiResponse.success(feedback);
    }

    /**
     * 获取用户租房信息
     */
    @GetMapping("/rentals")
    public ApiResponse<Object> getUserRentals() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        // 获取用户当前租房合同
        Object contracts = contractService.getUserContracts(currentUser.getId());
        return ApiResponse.success(contracts);
    }

    /**
     * 获取用户租金账单
     */
    @GetMapping("/rent-bills")
    public ApiResponse<Object> getUserRentBills(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        Pageable pageable = PageRequest.of(page - 1, size);
        Object bills = billService.getUserBills(currentUser.getId(), status, pageable);
        return ApiResponse.success(bills);
    }

    /**
     * 获取用户物业费账单
     */
    @GetMapping("/utility-bills")
    public ApiResponse<Object> getUserUtilityBills(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        Pageable pageable = PageRequest.of(page - 1, size);
        Object bills = utilityBillService.getUserUtilityBills(currentUser.getId(), status, pageable);
        return ApiResponse.success(bills);
    }

    /**
     * 支付租金
     */
    @PostMapping("/rent-bills/{billId}/pay")
    public ApiResponse<Void> payRentBill(@PathVariable Long billId, @RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        String paymentMethod = request.get("paymentMethod");
        billService.payBill(billId, currentUser.getId(), paymentMethod);
        return ApiResponse.success("支付成功");
    }

    /**
     * 支付物业费
     */
    @PostMapping("/utility-bills/{billId}/pay")
    public ApiResponse<Void> payUtilityBill(@PathVariable Long billId, @RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        String paymentMethod = request.get("paymentMethod");
        utilityBillService.payUtilityBill(billId, currentUser.getId(), paymentMethod);
        return ApiResponse.success("支付成功");
    }

    /**
     * 获取可租房源
     */
    @GetMapping("/available-rooms")
    public ApiResponse<Object> getAvailableRooms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) String sortBy) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Object rooms = roomService.getAvailableRooms(keyword, minPrice, maxPrice, roomType, sortBy, pageable);
        return ApiResponse.success(rooms);
    }

    /**
     * 申请租房
     */
    @PostMapping("/room-applications")
    public ApiResponse<Map<String, Object>> applyForRoom(@Valid @RequestBody RoomApplicationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        RoomApplication application = new RoomApplication();
        BeanUtils.copyProperties(request, application);

        RoomApplication savedApplication = applicationService.submitApplication(currentUser.getId(), application);

        // 获取房东ID
        Long landlordId = applicationService.getLandlordIdByRoomId(application.getRoomId());
        
        if (landlordId != null) {
            try {
                // 根据房东ID获取房东信息
                Landlord landlord = landlordRepository.findById(landlordId).orElse(null);
                if (landlord != null) {
                    // 创建待办事项
                    String todoTitle = "新的租房申请";
                    String todoDescription = String.format("用户 %s 申请租房，房间ID: %d", 
                        currentUser.getUsername(), application.getRoomId());
                    
                    LandlordTodo todo = landlordService.createRoomApplicationTodo(
                        landlordId, savedApplication.getId(), todoTitle, todoDescription);
                    
                    // 创建通知消息
                    String notificationTitle = "新的租房申请";
                    String notificationContent = String.format("用户 %s 申请租房，请及时处理", 
                        currentUser.getUsername());
                    
                    notificationService.createRoomApplicationNotification(
                        landlord.getUserId(), notificationTitle, notificationContent, savedApplication.getId());
                }
            } catch (Exception e) {
                logger.error("创建待办事项和通知失败", e);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("applicationId", savedApplication.getId());
        result.put("createTime", savedApplication.getCreateTime());
        return ApiResponse.success("申请提交成功", result);
    }

    /**
     * 获取租房申请记录
     */
    @GetMapping("/room-applications")
    public ApiResponse<Page<RoomApplicationResponse>> getRoomApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<RoomApplicationResponse> applications = applicationService.getUserApplicationsWithRoomInfo(currentUser.getId(), status, pageable);
        return ApiResponse.success(applications);
    }

    /**
     * 取消租房申请
     */
    @PutMapping("/room-applications/{id}/cancel")
    public ApiResponse<Void> cancelRoomApplication(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        applicationService.cancelApplication(id, currentUser.getId());
        return ApiResponse.success("申请已取消");
    }

    /**
     * 申请成为房东
     */
    @PostMapping("/landlord-application")
    public ApiResponse<Map<String, Object>> applyToBeLandlord(@Valid @RequestBody LandlordApplicationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        LandlordApplication application = new LandlordApplication();
        BeanUtils.copyProperties(request, application);

        LandlordApplication savedApplication = landlordApplicationService.submitApplication(currentUser.getId(), application);

        Map<String, Object> result = new HashMap<>();
        result.put("applicationId", savedApplication.getId());
        result.put("createTime", savedApplication.getCreateTime());
        return ApiResponse.success("申请提交成功", result);
    }

    /**
     * 获取房东申请状态
     */
    @GetMapping("/landlord-application")
    public ApiResponse<LandlordApplication> getLandlordApplication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        LandlordApplication application = landlordApplicationService.getUserApplication(currentUser.getId())
                .orElse(null);
        return ApiResponse.success(application);
    }

    /**
     * 获取用户仪表盘统计数据
     */
    @GetMapping("/dashboard/stats")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);

        Map<String, Object> stats = userDashboardService.getUserDashboardStats(currentUser.getId());
        return ApiResponse.success(stats);
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send-verification-code")
    public ApiResponse<Map<String, String>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        
        if (phone == null || phone.trim().isEmpty()) {
            return ApiResponse.error("手机号不能为空");
        }
        
        // 验证手机号格式
        if (!phone.matches("^1[3456789]\\d{9}$")) {
            return ApiResponse.error("手机号格式不正确");
        }
        
        // 生成6位验证码
        String verifyCode = String.format("%06d", (int)(Math.random() * 1000000));
        
        // 这里应该调用短信服务发送验证码，暂时模拟
        logger.info("发送验证码到手机号: {}, 验证码: {}", phone, verifyCode);
        
        // 将验证码存储到缓存中（这里简化处理，实际应该使用Redis等）
        // 暂时存储在内存中，5分钟有效期
        verificationCodeCache.put(phone, verifyCode);
        
        // 返回验证码给前端显示（仅用于开发测试）
        Map<String, String> result = new HashMap<>();
        result.put("verifyCode", verifyCode);
        result.put("message", "验证码发送成功");
        
        return ApiResponse.success("验证码发送成功", result);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userService.findByUsername(username);
        
        // 验证当前密码
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            return ApiResponse.error("当前密码不正确");
        }
        
        // 验证验证码
        String cachedCode = verificationCodeCache.get(request.getPhone());
        if (cachedCode == null || !cachedCode.equals(request.getVerifyCode())) {
            return ApiResponse.error("验证码不正确或已过期");
        }
        
        // 验证手机号是否匹配
        if (!request.getPhone().equals(currentUser.getPhone())) {
            return ApiResponse.error("手机号不匹配");
        }
        
        // 更新密码
        userService.resetPassword(currentUser.getId(), request.getNewPassword());
        
        // 清除验证码
        verificationCodeCache.remove(request.getPhone());
        
        return ApiResponse.success("密码修改成功");
    }

    /**
     * 生成临时密码
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
