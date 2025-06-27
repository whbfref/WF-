package com.apartment.api.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateRequest {

    /**
     * 电子邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
} 