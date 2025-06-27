package com.apartment.service;

import com.apartment.model.User;
import com.apartment.model.Property;
import com.apartment.model.Landlord;

import java.util.Map;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    Map<String, Object> adminLogin(String username, String password);
    
    /**
     * 获取当前管理员信息
     * @return 管理员信息
     */
    Map<String, Object> getCurrentAdminInfo();
    
    /**
     * 修改管理员密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updateAdminPassword(String oldPassword, String newPassword);
    
    /**
     * 获取管理员账号列表
     * @param page 页码
     * @param size 每页数量
     * @param username 用户名
     * @param roleLevel 角色等级
     * @param status 状态
     * @return 管理员列表和分页信息
     */
    Map<String, Object> getAdminAccounts(int page, Integer size, String username, Integer roleLevel, Integer status);
    
    /**
     * 创建管理员账号
     * @param accountData 账号数据
     * @return 创建结果
     */
    Map<String, Object> createAdminAccount(Map<String, Object> accountData);
    
    /**
     * 更新管理员账号
     * @param id 管理员ID
     * @param accountData 账号数据
     * @return 更新结果
     */
    Map<String, Object> updateAdminAccount(Long id, Map<String, Object> accountData);
    
    /**
     * 更新管理员账号状态
     * @param id 管理员ID
     * @param status 状态
     */
    void updateAdminStatus(Long id, Integer status);
    
    /**
     * 重置管理员密码
     * @param id 管理员ID
     * @return 重置结果
     */
    Map<String, Object> resetAdminPassword(Long id);

    /**
     * 获取系统概览数据
     * @return 系统概览数据
     */
    Map<String, Object> getSystemOverview();

    /**
     * 获取所有用户列表（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param role 角色筛选
     * @param keyword 关键词搜索
     * @return 用户列表和分页信息
     */
    Map<String, Object> getAllUsers(int page, int size, String role, String keyword);
    
    /**
     * 获取所有用户列表（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param role 角色筛选
     * @param keyword 关键词搜索
     * @param status 状态筛选
     * @return 用户列表和分页信息
     */
    Map<String, Object> getAllUsers(int page, int size, String role, String keyword, Integer status);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态(0禁用,1启用)
     * @return 更新后的用户对象
     */
    User updateUserStatus(Long id, Integer status);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 创建管理员用户
     * @param user 用户对象
     * @return 创建的管理员用户
     */
    User createAdminUser(User user);
    
    /**
     * 创建普通用户
     * @param user 用户对象
     * @return 创建的用户
     */
    User createUser(User user);

    /**
     * 获取系统日志
     * @param page 页码
     * @param size 每页数量
     * @param operationType 操作类型
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表和分页信息
     */
    Map<String, Object> getSystemLogs(int page, int size, String operationType, Long userId, String startTime, String endTime);

    /**
     * 获取系统配置
     * @return 系统配置
     */
    Map<String, Object> getSystemSettings();

    /**
     * 更新系统配置
     * @param settings 系统配置
     * @return 更新后的系统配置
     */
    Map<String, Object> updateSystemSettings(Map<String, Object> settings);
    
    /**
     * 获取系统配置
     * @return 系统配置
     */
    Map<String, Object> getSystemConfig();
    
    /**
     * 更新系统配置
     * @param key 配置键
     * @param value 配置值
     * @return 更新后的配置
     */
    Map<String, Object> updateSystemConfig(String key, String value);
    
    /**
     * 获取所有房源列表（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param status 状态筛选
     * @param keyword 关键词搜索
     * @return 房源列表和分页信息
     */
    Map<String, Object> getAllProperties(int page, int size, Integer status, String keyword);
    
    /**
     * 根据ID获取房源详情
     * @param id 房源ID
     * @return 房源详情
     */
    Property getPropertyById(Long id);
    
    /**
     * 更新房源状态
     * @param id 房源ID
     * @param status 状态值
     * @return 更新后的房源
     */
    Property updatePropertyStatus(Long id, Integer status);
    
    /**
     * 删除房源
     * @param id 房源ID
     */
    void deleteProperty(Long id);
    
    /**
     * 获取所有房东列表（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param verified 认证状态
     * @param keyword 关键词搜索
     * @return 房东列表和分页信息
     */
    Map<String, Object> getAllLandlords(int page, int size, Boolean verified, String keyword);
    
    /**
     * 根据ID获取房东详情
     * @param id 房东ID
     * @return 房东详情
     */
    Landlord getLandlordById(Long id);
    
    /**
     * 更新房东认证状态
     * @param id 房东ID
     * @param verified 认证状态
     * @return 更新后的房东
     */
    Landlord updateLandlordVerified(Long id, Boolean verified);
    
    /**
     * 删除房东
     * @param id 房东ID
     */
    void deleteLandlord(Long id);
    
    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadUserAvatar(Long userId, MultipartFile file);
    
    /**
     * 上传房源图片
     * @param propertyId 房源ID
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadPropertyImage(Long propertyId, MultipartFile file);
    
    /**
     * 删除房源图片
     * @param propertyId 房源ID
     * @param imageUrl 图片URL
     */
    void deletePropertyImage(Long propertyId, String imageUrl);

    /**
     * 获取房东的房产列表
     * @param landlordId 房东ID
     * @return 房产列表
     */
    List<Property> getPropertiesByLandlordId(Long landlordId);
    
    /**
     * 获取房东认证申请列表
     * @param page 页码
     * @param size 每页数量
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 申请列表和分页信息
     */
    Map<String, Object> getLandlordVerifyApplications(int page, Integer size, String status, String startDate, String endDate);
    
    /**
     * 审核房东认证申请
     * @param applicationId 申请ID
     * @param action 操作(APPROVE/REJECT)
     * @param comment 审核意见
     * @return 审核结果
     */
    Map<String, Object> verifyLandlordApplication(Long applicationId, String action, String comment);
    
    /**
     * 获取待审核房源列表
     * @param page 页码
     * @param size 每页数量
     * @param status 状态
     * @param landlordId 房东ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 房源列表和分页信息
     */
    Map<String, Object> getPropertiesForAudit(int page, Integer size, String status, Long landlordId, String startDate, String endDate);
    
    /**
     * 审核房源
     * @param propertyId 房源ID
     * @param action 操作(APPROVE/REJECT)
     * @param comment 审核意见
     * @return 审核结果
     */
    Map<String, Object> auditProperty(Long propertyId, String action, String comment);
    
    /**
     * 获取数据统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getStatisticsOverview(String startDate, String endDate);
} 