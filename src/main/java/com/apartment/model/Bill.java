package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bills")
public class Bill extends BaseEntity {

    /**
     * 合同ID
     */
    @NotNull(message = "合同ID不能为空")
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    /**
     * 账单编号
     */
    @Column(name = "bill_number", nullable = false, length = 50, unique = true)
    private String billNumber;

    /**
     * 账单类型
     */
    @Column(name = "bill_type", nullable = false, length = 20)
    private String billType; // RENT, WATER, ELECTRICITY, GAS, OTHER

    /**
     * 账单金额
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 账单月份
     */
    @Column(name = "bill_month", nullable = false, length = 7)
    private String billMonth; // 格式：YYYY-MM

    /**
     * 到期日
     */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * 支付日期
     */
    @Column(name = "paid_date")
    private LocalDate paidDate;

    /**
     * 支付方式
     */
    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    /**
     * 交易ID
     */
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /**
     * 账单状态
     */
    @Column(nullable = false, length = 20)
    private String status = "UNPAID"; // UNPAID, PAID, OVERDUE, CANCELLED

    /**
     * 备注
     */
    @Column(length = 1000)
    private String remark;
} 