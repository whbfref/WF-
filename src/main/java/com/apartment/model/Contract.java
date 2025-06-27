package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity {

    /**
     * 合同编号
     */
    @Column(name = "contract_number", nullable = false, length = 50, unique = true)
    private String contractNumber;

    /**
     * 租户ID
     */
    @NotNull(message = "租户ID不能为空")
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /**
     * 房间ID
     */
    @NotNull(message = "房间ID不能为空")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /**
     * 房东ID
     */
    @Column(name = "landlord_id", nullable = false)
    private Long landlordId;

    /**
     * 开始日期
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * 月租金
     */
    @Column(name = "rent_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal monthlyRent;

    /**
     * 押金金额
     */
    @Column(name = "deposit", precision = 10, scale = 2, nullable = false)
    private BigDecimal depositAmount;

    /**
     * 合同状态：执行中(ACTIVE)、已完成(COMPLETED)、已终止(TERMINATED)
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    /**
     * 付款方式
     */
    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    /**
     * 付款日期（每月几号）
     */
    @Column(name = "payment_day")
    private Integer paymentDay = 1;

    /**
     * 电费单价
     */
    @Column(name = "electric_fee", precision = 5, scale = 2)
    private BigDecimal electricFee;

    /**
     * 水费单价
     */
    @Column(name = "water_fee", precision = 5, scale = 2)
    private BigDecimal waterFee;

    /**
     * 燃气费单价
     */
    @Column(name = "gas_fee", precision = 5, scale = 2)
    private BigDecimal gasFee;

    /**
     * 签约日期
     */
    @Column(name = "signed_date", nullable = false)
    private LocalDate signedDate;

    /**
     * 合同文件URL
     */
    @Column(name = "contract_file_url")
    private String contractFileUrl;

    /**
     * 终止原因
     */
    @Column(name = "termination_reason", columnDefinition = "TEXT")
    private String terminationReason;

    /**
     * 终止日期
     */
    @Column(name = "termination_date")
    private LocalDate terminationDate;
} 