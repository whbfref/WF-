package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.model.User;
import com.apartment.model.Property;
import com.apartment.model.Landlord;
import com.apartment.service.AdminService;
import com.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping({"/admin", "/api/v1/admin"})
// @PreAuthorize("hasRole('ADMIN')") // 临时注释，方便测试
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserService userService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            Map<String, Object> loginResult = adminService.adminLogin(username, password);
            return ResponseEntity.ok(ApiResponse.success("登录成功", loginResult));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取当前管理员信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin() {
        try {
            Map<String, Object> adminInfo = adminService.getCurrentAdminInfo();
            return ResponseEntity.ok(ApiResponse.success("获取管理员信息成功", adminInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取管理员信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 修改管理员密码
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> passwordData) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            
            adminService.updateAdminPassword(oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码修改失败: " + e.getMessage()));
        }
    }

    /**
     * 获取系统概览数据
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getSystemOverview() {
        try {
            Map<String, Object> overview = adminService.getSystemOverview();
            return ResponseEntity.ok(ApiResponse.success("获取系统概览数据成功", overview));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取系统概览数据失败: " + e.getMessage()));
        }
    }

    // ==================== 管理员账号管理 ====================
    
    /**
     * 获取管理员账号列表
     */
    @GetMapping("/accounts")
    public ResponseEntity<?> getAdminAccounts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer roleLevel,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = adminService.getAdminAccounts(page-1, size, username, roleLevel, status);
            return ResponseEntity.ok(ApiResponse.success("获取管理员列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取管理员列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建管理员账号
     */
    @PostMapping("/accounts")
    public ResponseEntity<?> createAdminAccount(@RequestBody Map<String, Object> accountData) {
        try {
            Map<String, Object> result = adminService.createAdminAccount(accountData);
            return ResponseEntity.ok(ApiResponse.success("管理员账号创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("管理员账号创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新管理员账号
     */
    @PutMapping("/accounts/{id}")
    public ResponseEntity<?> updateAdminAccount(
            @PathVariable Long id,
            @RequestBody Map<String, Object> accountData) {
        try {
            Map<String, Object> result = adminService.updateAdminAccount(id, accountData);
            return ResponseEntity.ok(ApiResponse.success("管理员账号更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("管理员账号更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新管理员账号状态
     */
    @PutMapping("/accounts/{id}/status")
    public ResponseEntity<?> updateAdminStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusData) {
        try {
            Integer status = statusData.get("status");
            if (status == null || (status != 0 && status != 1)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态值无效，应为0(禁用)或1(启用)"));
            }
            
            adminService.updateAdminStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("管理员账号状态更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("管理员账号状态更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 重置管理员密码
     */
    @PostMapping("/accounts/{id}/password/reset")
    public ResponseEntity<?> resetAdminPassword(@PathVariable Long id) {
        try {
            Map<String, Object> result = adminService.resetAdminPassword(id);
            return ResponseEntity.ok(ApiResponse.success("密码重置成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码重置失败: " + e.getMessage()));
        }
    }

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = adminService.getAllUsers(page, size, role, keyword, status);
            return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        try {
            User user = adminService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("获取用户详情成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户详情失败: " + e.getMessage()));
        }
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        try {
            Integer status = statusMap.get("status");
            if (status == null || (status != 0 && status != 1)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态值无效，应为0(禁用)或1(启用)"));
            }
            
            User user = adminService.updateUserStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("更新用户状态成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新用户状态失败: " + e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("删除用户成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除用户失败: " + e.getMessage()));
        }
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/users/{id}/avatar")
    public ResponseEntity<?> uploadUserAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = adminService.uploadUserAvatar(id, file);
            return ResponseEntity.ok(ApiResponse.success("上传用户头像成功", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传用户头像失败: " + e.getMessage()));
        }
    }

    /**
     * 创建管理员用户
     */
    @PostMapping("/users/admin")
    public ResponseEntity<?> createAdminUser(@RequestBody User user) {
        try {
            User adminUser = adminService.createAdminUser(user);
            return ResponseEntity.ok(ApiResponse.success("创建管理员用户成功", adminUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建管理员用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建普通用户
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User newUser = adminService.createUser(user);
            return ResponseEntity.ok(ApiResponse.success("创建用户成功", newUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建用户失败: " + e.getMessage()));
        }
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/users/{id}/password/reset")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id) {
        try {
            String tempPassword = "temp" + (100000 + new Random().nextInt(900000)); // 生成6位随机密码
            userService.resetPassword(id, tempPassword);
            
            Map<String, String> result = new HashMap<>();
            result.put("tempPassword", tempPassword);
            
            return ResponseEntity.ok(ApiResponse.success("重置用户密码成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("重置用户密码失败: " + e.getMessage()));
        }
    }

    // ==================== 房东管理 ====================

    /**
     * 获取房东列表
     */
    @GetMapping("/landlords")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllLandlords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> result = adminService.getAllLandlords(page, size, verified, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取房东列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房东列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房东详情
     */
    @GetMapping("/landlords/{id}")
    public ResponseEntity<?> getLandlordDetails(@PathVariable Long id) {
        try {
            Landlord landlord = adminService.getLandlordById(id);
            return ResponseEntity.ok(ApiResponse.success("获取房东详情成功", landlord));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房东详情失败: " + e.getMessage()));
        }
    }

    /**
     * 更新房东认证状态
     */
    @PutMapping("/landlords/{id}/verified")
    public ResponseEntity<?> updateLandlordVerified(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> verifiedMap) {
        try {
            Boolean verified = verifiedMap.get("verified");
            if (verified == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("认证状态不能为空"));
            }
            
            Landlord landlord = adminService.updateLandlordVerified(id, verified);
            return ResponseEntity.ok(ApiResponse.success("更新房东认证状态成功", landlord));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新房东认证状态失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房东的房产列表
     */
    @GetMapping("/landlords/{id}/properties")
    public ResponseEntity<?> getLandlordProperties(@PathVariable Long id) {
        try {
            List<Property> properties = adminService.getPropertiesByLandlordId(id);
            return ResponseEntity.ok(ApiResponse.success("获取房东房产列表成功", properties));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房东房产列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除房东
     */
    @DeleteMapping("/landlords/{id}")
    public ResponseEntity<?> deleteLandlord(@PathVariable Long id) {
        try {
            adminService.deleteLandlord(id);
            return ResponseEntity.ok(ApiResponse.success("删除房东成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除房东失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取房东认证申请列表
     */
    @GetMapping("/landlords/verify")
    public ResponseEntity<?> getLandlordVerifyApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> result = adminService.getLandlordVerifyApplications(page-1, size, status, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("获取房东认证申请列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房东认证申请列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 审核房东认证申请
     */
    @PutMapping("/landlords/verify/{applicationId}")
    public ResponseEntity<?> verifyLandlordApplication(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> verifyData) {
        try {
            String action = verifyData.get("action");
            String comment = verifyData.get("comment");
            
            if (action == null || (!action.equals("APPROVE") && !action.equals("REJECT"))) {
                return ResponseEntity.badRequest().body(ApiResponse.error("审核操作无效，应为APPROVE或REJECT"));
            }
            
            Map<String, Object> result = adminService.verifyLandlordApplication(applicationId, action, comment);
            return ResponseEntity.ok(ApiResponse.success("审核操作成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("审核操作失败: " + e.getMessage()));
        }
    }

    // ==================== 房源管理 ====================

    /**
     * 获取房源列表
     */
    @GetMapping("/properties")
    public ResponseEntity<?> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> result = adminService.getAllProperties(page, size, status, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取房源列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房源列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房源详情
     */
    @GetMapping("/properties/{id}")
    public ResponseEntity<?> getPropertyDetails(@PathVariable Long id) {
        try {
            Property property = adminService.getPropertyById(id);
            return ResponseEntity.ok(ApiResponse.success("获取房源详情成功", property));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房源详情失败: " + e.getMessage()));
        }
    }

    /**
     * 更新房源状态
     */
    @PutMapping("/properties/{id}/status")
    public ResponseEntity<?> updatePropertyStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        try {
            Integer status = statusMap.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态不能为空"));
            }
            
            Property property = adminService.updatePropertyStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("更新房源状态成功", property));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新房源状态失败: " + e.getMessage()));
        }
    }

    /**
     * 删除房源
     */
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            adminService.deleteProperty(id);
            return ResponseEntity.ok(ApiResponse.success("删除房源成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除房源失败: " + e.getMessage()));
        }
    }

    /**
     * 上传房源图片
     */
    @PostMapping("/properties/{id}/images")
    public ResponseEntity<?> uploadPropertyImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = adminService.uploadPropertyImage(id, file);
            return ResponseEntity.ok(ApiResponse.success("上传房源图片成功", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传房源图片失败: " + e.getMessage()));
        }
    }

    /**
     * 删除房源图片
     */
    @DeleteMapping("/properties/{id}/images")
    public ResponseEntity<?> deletePropertyImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> imageMap) {
        try {
            String imageUrl = imageMap.get("imageUrl");
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("图片URL不能为空"));
            }
            
            adminService.deletePropertyImage(id, imageUrl);
            return ResponseEntity.ok(ApiResponse.success("删除房源图片成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除房源图片失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取待审核房源列表
     */
    @GetMapping("/properties/audit")
    public ResponseEntity<?> getPropertiesForAudit(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long landlordId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> result = adminService.getPropertiesForAudit(page-1, size, status, landlordId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("获取待审核房源列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取待审核房源列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 审核房源
     */
    @PutMapping("/properties/{propertyId}/audit")
    public ResponseEntity<?> auditProperty(
            @PathVariable Long propertyId,
            @RequestBody Map<String, String> auditData) {
        try {
            String action = auditData.get("action");
            String comment = auditData.get("comment");
            
            if (action == null || (!action.equals("APPROVE") && !action.equals("REJECT"))) {
                return ResponseEntity.badRequest().body(ApiResponse.error("审核操作无效，应为APPROVE或REJECT"));
            }
            
            Map<String, Object> result = adminService.auditProperty(propertyId, action, comment);
            return ResponseEntity.ok(ApiResponse.success("审核操作成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("审核操作失败: " + e.getMessage()));
        }
    }

    // ==================== 系统管理 ====================

    /**
     * 获取系统日志
     */
    @GetMapping("/logs")
    public ResponseEntity<?> getSystemLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        try {
            Map<String, Object> result = adminService.getSystemLogs(page-1, size, operationType, userId, startTime, endTime);
            return ResponseEntity.ok(ApiResponse.success("获取系统日志成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取系统日志失败: " + e.getMessage()));
        }
    }

    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    public ResponseEntity<?> getSystemConfig() {
        try {
            Map<String, Object> config = adminService.getSystemConfig();
            return ResponseEntity.ok(ApiResponse.success("获取系统配置成功", config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取系统配置失败: " + e.getMessage()));
        }
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/config")
    public ResponseEntity<?> updateSystemConfig(@RequestBody Map<String, String> configData) {
        try {
            String key = configData.get("key");
            String value = configData.get("value");
            
            if (key == null || key.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("配置键不能为空"));
            }
            
            Map<String, Object> result = adminService.updateSystemConfig(key, value);
            return ResponseEntity.ok(ApiResponse.success("配置更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("配置更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取数据统计
     */
    @GetMapping("/statistics/overview")
    public ResponseEntity<?> getStatisticsOverview(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> statistics = adminService.getStatisticsOverview(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("获取数据统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取数据统计失败: " + e.getMessage()));
        }
    }
} 