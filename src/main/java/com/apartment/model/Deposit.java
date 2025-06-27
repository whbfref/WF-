package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 押金实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "deposits")
public class Deposit extends BaseEntity {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 房产ID
     */
    @NotNull(message = "房产ID不能为空")
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    /**
     * 房东ID
     */
    @NotNull(message = "房东ID不能为空")
    @Column(name = "landlord_id", nullable = false)
    private Long landlordId;

    /**
     * 租约ID
     */
    @Column(name = "lease_id")
    private Long leaseId;

    /**
     * 押金金额
     */
    @NotNull(message = "押金金额不能为空")
    @Positive(message = "押金金额必须为正数")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 支付状态：0-待支付，1-已支付，2-已退还，3-已扣除
     */
    @Column(nullable = false)
    private Integer status = 0;

    /**
     * 支付时间
     */
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    /**
     * 退还时间
     */
    @Column(name = "refund_time")
    private LocalDateTime refundTime;

    /**
     * 扣除金额
     */
    @Column(name = "deduction_amount", precision = 10, scale = 2)
    private BigDecimal deductionAmount = BigDecimal.ZERO;

    /**
     * 扣除原因
     */
    @Column(name = "deduction_reason", columnDefinition = "TEXT")
    private String deductionReason;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
} 