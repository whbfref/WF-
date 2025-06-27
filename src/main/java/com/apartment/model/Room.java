package com.apartment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 房间实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rooms")
public class Room extends BaseEntity {

    /**
     * 房间号
     */
    @NotNull(message = "房间号不能为空")
    @Size(max = 50, message = "房间号长度不能超过50个字符")
    @Column(nullable = false, length = 50)
    private String roomNumber;

    /**
     * 所属物业ID
     */
    @NotNull(message = "所属物业不能为空")
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    /**
     * 面积（平方米）
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal area;

    /**
     * 月租金
     */
    @Column(name = "rent_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal monthlyRent;

    /**
     * 房间类型
     */
    @Column(name = "room_type", length = 20)
    private String type;

    /**
     * 朝向
     */
    @Column(length = 20)
    private String orientation;

    /**
     * 楼层
     */
    @Column
    private Integer floor;

    /**
     * 房间布局
     */
    @Column(length = 20)
    private String layout;

    /**
     * 押金
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal deposit;

    /**
     * 付款方式
     */
    @Column(name = "payment_method", length = 20)
    private String paymentMethod = "MONTHLY";

    /**
     * 设施配置
     */
    @Column(columnDefinition = "TEXT")
    private String facilities;

    /**
     * 房间状态
     */
    @Column(nullable = false, length = 20)
    private String status = "VACANT"; // VACANT, RENTED, MAINTENANCE

    /**
     * 房间描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 所属物业（关联Property实体）
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", insertable = false, updatable = false)
    private Property property;

    /**
     * 房间图片列表
     */
    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<RoomImage> images = new ArrayList<>();
} 