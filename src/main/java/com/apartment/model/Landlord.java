package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房东实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "landlords")
public class Landlord extends BaseEntity {

    /**
     * 关联用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    /**
     * 关联的用户对象
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    @Column(name = "id_card", unique = true, nullable = false, length = 18)
    private String idCard;

    /**
     * 银行卡号
     */
    @Column(name = "bank_card", length = 30)
    private String bankCard;

    /**
     * 银行名称
     */
    @Column(name = "bank_name", length = 100)
    private String bankName;

    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 11)
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /**
     * 评分
     */
    @Column(precision = 2, scale = 1)
    private BigDecimal rating = new BigDecimal("5.0");

    /**
     * 是否认证
     */
    @Column(nullable = false)
    private Boolean verified = false;

    /**
     * 身份证正面照片URL
     */
    @Column(name = "id_card_front_url")
    private String idCardFrontUrl;

    /**
     * 身份证背面照片URL
     */
    @Column(name = "id_card_back_url")
    private String idCardBackUrl;

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