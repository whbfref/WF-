package com.apartment.repository;

import com.apartment.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据电子邮箱查找用户
     * @param email 电子邮箱
     * @return 用户对象
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据手机号码查找用户
     * @param phone 手机号码
     * @return 用户对象
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查电子邮箱是否存在
     * @param email 电子邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号码是否存在
     * @param phone 手机号码
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 根据角色查找用户列表（分页）
     * @param role 角色
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByRole(String role, Pageable pageable);
    
    /**
     * 根据角色统计用户数量
     * @param role 角色
     * @return 用户数量
     */
    Long countByRole(String role);
    
    /**
     * 根据状态查找用户列表（分页）
     * @param status 状态
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据角色和状态查找用户列表（分页）
     * @param role 角色
     * @param status 状态
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByRoleAndStatus(String role, Integer status, Pageable pageable);
    
    /**
     * 根据状态和关键词查找用户（分页）
     * @param status 状态
     * @param username 用户名关键词
     * @param email 邮箱关键词
     * @param phone 手机号关键词
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByStatusAndUsernameContainingOrEmailContainingOrPhoneContaining(
        Integer status, String username, String email, String phone, Pageable pageable);
    
    /**
     * 根据角色、状态和关键词查找用户（分页）
     * @param role 角色
     * @param status 状态
     * @param username 用户名关键词
     * @param email 邮箱关键词
     * @param phone 手机号关键词
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByRoleAndStatusAndUsernameContainingOrEmailContainingOrPhoneContaining(
        String role, Integer status, String username, String email, String phone, Pageable pageable);
    
    /**
     * 根据关键字搜索用户（用户名、邮箱、手机号）
     * @param username 用户名关键字
     * @param email 邮箱关键字
     * @param phone 手机号关键字
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByUsernameContainingOrEmailContainingOrPhoneContaining(
            String username, String email, String phone, Pageable pageable);
    
    /**
     * 根据角色和关键字搜索用户
     * @param role 角色
     * @param username 用户名关键字
     * @param email 邮箱关键字
     * @param phone 手机号关键字
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByRoleAndUsernameContainingOrEmailContainingOrPhoneContaining(
            String role, String username, String email, String phone, Pageable pageable);

    long countByCreateTimeAfter(LocalDateTime date);
} 