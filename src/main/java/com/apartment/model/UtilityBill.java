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
 * 水电费账单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "utility_bills")
public class UtilityBill extends BaseEntity {

    /**
     * 账单编号
     */
    @Column(name = "bill_no", length = 50)
    private String billNo;

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
     * 账单类型：1-水费，2-电费，3-燃气费
     */
    @NotNull(message = "账单类型不能为空")
    @Column(nullable = false)
    private Integer type;

    /**
     * 账单月份，格式：yyyy-MM
     */
    @NotBlank(message = "账单月份不能为空")
    @Column(nullable = false, length = 7)
    private String month;

    /**
     * 上次读数
     */
    @Column(name = "last_reading", precision = 10, scale = 2)
    private BigDecimal lastReading = BigDecimal.ZERO;

    /**
     * 当前读数
     */
    @Column(name = "current_reading", precision = 10, scale = 2)
    private BigDecimal currentReading;

    /**
     * 使用量
     */
    @Column(name = "usage_amount", precision = 10, scale = 2)
    private BigDecimal consumption;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @Positive(message = "单价必须为正数")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal price;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须为正数")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 缴费状态：0-待缴费，1-已缴费
     */
    @Column(nullable = false)
    private Integer status = 0;

    /**
     * 缴费时间
     */
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    /**
     * 抄表时间
     */
    @Column(name = "reading_time")
    private LocalDateTime readingTime;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
} 