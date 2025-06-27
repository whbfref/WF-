package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 房东认证申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "landlord_verifications")
public class LandlordVerification extends BaseEntity {

    /**
     * 申请用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
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
    @NotBlank(message = "真实姓名不能为空")
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    @Column(name = "id_card", nullable = false, length = 18)
    private String idCard;

    /**
     * 身份证正面照片URL
     */
    @NotBlank(message = "身份证正面照片不能为空")
    @Column(name = "id_card_front_image", nullable = false)
    private String idCardFrontImage;

    /**
     * 身份证背面照片URL
     */
    @NotBlank(message = "身份证背面照片不能为空")
    @Column(name = "id_card_back_image", nullable = false)
    private String idCardBackImage;

    /**
     * 银行卡号
     */
    @NotBlank(message = "银行卡号不能为空")
    @Pattern(regexp = "^\\d{16,19}$", message = "银行卡号格式不正确")
    @Column(name = "bank_card", nullable = false, length = 30)
    private String bankCard;

    /**
     * 银行名称
     */
    @NotBlank(message = "银行名称不能为空")
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Column(name = "contact_phone", nullable = false, length = 11)
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /**
     * 申请状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING;

    /**
     * 审核时间
     */
    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    /**
     * 审核意见
     */
    @Column(name = "review_comment", length = 500)
    private String reviewComment;

    /**
     * 审核人ID
     */
    @Column(name = "reviewer_id")
    private Long reviewerId;

    /**
     * 认证状态枚举
     */
    public enum VerificationStatus {
        PENDING("待审核"),
        APPROVED("已通过"),
        REJECTED("已拒绝");

        private final String description;

        VerificationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 