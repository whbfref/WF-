package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号码格式不正确")
    @Column(unique = true, length = 20)
    private String phone;

    /**
     * 电子邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Column(unique = true, length = 100)
    private String email;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    /**
     * 用户角色
     */
    @Column(nullable = false, length = 20)
    private String role = "USER"; // USER, LANDLORD, ADMIN

    /**
     * 用户状态
     */
    @Column(nullable = false)
    private Integer status = 1; // 0禁用, 1正常

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间 (兼容数据库created_at字段)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间 (兼容数据库updated_at字段)
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 