package com.apartment.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 租房申请响应DTO
 */
@Data
public class RoomApplicationResponse {

    /**
     * 申请ID
     */
    private Long id;

    /**
     * 申请用户ID
     */
    private Long userId;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 申请状态
     */
    private String status;

    /**
     * 期望入住时间
     */
    private LocalDate expectedMoveInDate;

    /**
     * 租期长度（月）
     */
    private Integer leaseDuration;

    /**
     * 申请备注
     */
    private String remarks;

    /**
     * 审核备注
     */
    private String reviewRemarks;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 房间信息
     */
    private String roomNumber;
    private String roomTitle;
    private String roomAddress;
    private BigDecimal monthlyRent;
    private String roomImageUrl;
    private List<String> imageUrls;
    private String roomDescription;
    private Double roomArea;
    private String roomType;
    private String roomStatus;

    /**
     * 用户信息
     */
    private String username;
    private String userPhone;
    private String userEmail;

    /**
     * 房东信息
     */
    private String landlordName;
    private String landlordPhone;
    private String landlordEmail;
    
    /**
     * 物业信息
     */
    private String propertyName;
    private String propertyAddress;
} 