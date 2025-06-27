package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 申请成为房东实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "landlord_applications")
public class LandlordApplication extends BaseEntity {

    /**
     * 申请用户ID
     */
    @NotNull(message = "申请用户不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 20, message = "真实姓名长度必须在2到20个字符之间")
    @Column(name = "real_name", nullable = false, length = 20)
    private String realName;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Column(name = "id_card", nullable = false, length = 18)
    private String idCard;

    /**
     * 身份证正面照片URL
     */
    @NotBlank(message = "身份证正面照片不能为空")
    @Column(name = "id_card_front_url", nullable = false, length = 255)
    private String idCardFrontUrl;

    /**
     * 身份证反面照片URL
     */
    @NotBlank(message = "身份证反面照片不能为空")
    @Column(name = "id_card_back_url", nullable = false, length = 255)
    private String idCardBackUrl;

    /**
     * 房产证明文件URL
     */
    @Column(name = "property_proof_url", length = 255)
    private String propertyProofUrl;

    /**
     * 申请理由
     */
    @NotBlank(message = "申请理由不能为空")
    @Size(min = 10, max = 500, message = "申请理由长度必须在10到500个字符之间")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    /**
     * 申请状态
     */
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING(待审核)、APPROVED(已通过)、REJECTED(已拒绝)

    /**
     * 审核备注
     */
    @Column(name = "review_remarks", columnDefinition = "TEXT")
    private String reviewRemarks;

    /**
     * 审核时间
     */
    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    /**
     * 审核人ID
     */
    @Column(name = "reviewer_id")
    private Long reviewerId;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    /**
     * 用户关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
} 