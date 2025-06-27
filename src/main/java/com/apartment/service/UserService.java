package com.apartment.service;

import com.apartment.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 根据ID查找用户
     */
    Optional<User> findById(Long id);

    /**
     * 保存用户
     */
    User save(User user);

    /**
     * 更新用户信息
     */
    User updateUser(Long userId, User user);

    /**
     * 上传用户头像
     */
    String uploadAvatar(Long userId, MultipartFile file);

    /**
     * 查询用户列表
     */
    Page<User> findUsers(String username, String role, String startTime, String endTime, Pageable pageable);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 更新用户角色
     */
    void updateUserRole(Long userId, String role);

    /**
     * 判断用户名是否已存在
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否已存在
     */
    boolean existsByEmail(String email);

    /**
     * 判断手机号是否已存在
     */
    boolean existsByPhone(String phone);

    /**
     * 重置用户密码
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);
} 