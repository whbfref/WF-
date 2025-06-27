package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 房东评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "landlord_ratings")
public class LandlordRating extends BaseEntity {

    /**
     * 房东ID
     */
    @NotNull(message = "房东ID不能为空")
    @Column(name = "landlord_id", nullable = false)
    private Long landlordId;

    /**
     * 关联的房东对象
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", insertable = false, updatable = false)
    private Landlord landlord;

    /**
     * 评价用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 关联的用户对象
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 房产ID
     */
    @NotNull(message = "房产ID不能为空")
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    /**
     * 关联的房产对象
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", insertable = false, updatable = false)
    private Property property;

    /**
     * 评分 (1-5)
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    /**
     * 评价内容
     */
    @Column(name = "comment", length = 1000)
    private String comment;

    /**
     * 房东回复内容
     */
    @Column(name = "reply", length = 1000)
    private String reply;

    /**
     * 回复时间
     */
    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
} 