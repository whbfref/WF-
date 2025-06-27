package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 房产实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "properties")
public class Property extends BaseEntity {

    /**
     * 房东ID
     */
    @NotNull(message = "房东ID不能为空")
    @Column(name = "landlord_id")
    private Long landlordId;

    /**
     * 房产标题
     */
    @NotBlank(message = "房产标题不能为空")
    @Column(nullable = false)
    private String title;

    /**
     * 房产描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 房产类型：1-整租，2-合租
     */
    @NotNull(message = "房产类型不能为空")
    @Column(nullable = false)
    private Integer type;

    /**
     * 月租金
     */
    @NotNull(message = "月租金不能为空")
    @Positive(message = "月租金必须为正数")
    @Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyRent;

    /**
     * 房产面积(平方米)
     */
    @NotNull(message = "房产面积不能为空")
    @Positive(message = "房产面积必须为正数")
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal area;

    /**
     * 房产楼层
     */
    private Integer floor;

    /**
     * 总楼层
     */
    @Column(name = "total_floor")
    private Integer totalFloor;

    /**
     * 卧室数量
     */
    private Integer bedrooms;

    /**
     * 卫生间数量
     */
    private Integer bathrooms;

    /**
     * 房产状态：0-待审核，1-已上架，2-已下架，3-已出租
     */
    @Column(nullable = false)
    private Integer status = 0;

    /**
     * 省份
     */
    @Column(length = 50)
    private String province;

    /**
     * 城市
     */
    @Column(length = 50)
    private String city;

    /**
     * 区/县
     */
    @Column(length = 50)
    private String district;

    /**
     * 详细地址
     */
    @Column(length = 255)
    private String address;

    /**
     * 纬度
     */
    @Column(precision = 10, scale = 6)
    private Double latitude;

    /**
     * 经度
     */
    @Column(precision = 10, scale = 6)
    private Double longitude;

    /**
     * 房产图片URL列表
     */
    @ElementCollection
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    /**
     * 设施配置：如空调、热水器、洗衣机等，以逗号分隔
     */
    @Column(length = 255)
    private String facilities;

    /**
     * 是否支持押一付一
     */
    @Column(name = "support_one_payment", nullable = false)
    private Boolean supportOnePayment = false;

    /**
     * 是否支持押一付三
     */
    @Column(name = "support_three_payment", nullable = false)
    private Boolean supportThreePayment = true;

    /**
     * 最短租期(月)
     */
    @Column(name = "min_lease_term", nullable = false)
    private Integer minLeaseTerm = 12;
} 