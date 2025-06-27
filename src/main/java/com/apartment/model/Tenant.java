package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 租客实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity {

    /**
     * 关联用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 真实姓名
     */
    @NotNull(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    /**
     * 身份证号
     */
    @NotNull(message = "身份证号不能为空")
    @Size(max = 18, message = "身份证号长度不能超过18个字符")
    @Column(name = "id_card", nullable = false, length = 18, unique = true)
    private String idCard;

    /**
     * 紧急联系人
     */
    @Size(max = 50, message = "紧急联系人姓名长度不能超过50个字符")
    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    @Size(max = 20, message = "紧急联系电话长度不能超过20个字符")
    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    /**
     * 职业
     */
    @Size(max = 100, message = "职业描述长度不能超过100个字符")
    @Column(length = 100)
    private String occupation;

    /**
     * 工作单位
     */
    @Size(max = 255, message = "工作单位名称长度不能超过255个字符")
    @Column(name = "work_place", length = 255)
    private String workPlace;
} 