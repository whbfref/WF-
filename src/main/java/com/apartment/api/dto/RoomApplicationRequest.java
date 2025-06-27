package com.apartment.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 租房申请请求DTO
 */
@Data
public class RoomApplicationRequest {

    /**
     * 房间ID
     */
    @NotNull(message = "房间不能为空")
    private Long roomId;

    /**
     * 期望入住时间
     */
    @NotNull(message = "期望入住时间不能为空")
    private LocalDate expectedMoveInDate;

    /**
     * 租期长度（月）
     */
    @NotNull(message = "租期长度不能为空")
    private Integer leaseDuration;

    /**
     * 申请备注
     */
    @Size(max = 500, message = "申请备注不能超过500个字符")
    private String remarks;
} 