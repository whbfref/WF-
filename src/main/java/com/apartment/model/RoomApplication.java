package com.apartment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租房申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "room_applications")
public class RoomApplication extends BaseEntity {

    /**
     * 申请用户ID
     */
    @NotNull(message = "申请用户不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 房间ID
     */
    @NotNull(message = "房间不能为空")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /**
     * 申请状态
     */
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING(待审核)、APPROVED(已通过)、REJECTED(已拒绝)、CANCELLED(已取消)

    /**
     * 期望入住时间
     */
    @NotNull(message = "期望入住时间不能为空")
    @Column(name = "expected_move_in_date", nullable = false)
    private LocalDate expectedMoveInDate;

    /**
     * 租期长度（月）
     */
    @NotNull(message = "租期长度不能为空")
    @Column(name = "lease_duration", nullable = false)
    private Integer leaseDuration;

    /**
     * 申请备注
     */
    @Size(max = 500, message = "申请备注不能超过500个字符")
    @Column(columnDefinition = "TEXT")
    private String remarks;

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
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 房间关联
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;
} 