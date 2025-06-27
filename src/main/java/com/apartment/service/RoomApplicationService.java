package com.apartment.service;

import com.apartment.api.dto.RoomApplicationResponse;
import com.apartment.model.RoomApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 租房申请服务接口
 */
public interface RoomApplicationService {

    /**
     * 提交租房申请
     */
    RoomApplication submitApplication(Long userId, RoomApplication application);

    /**
     * 获取用户申请列表
     */
    Page<RoomApplication> getUserApplications(Long userId, String status, Pageable pageable);

    /**
     * 获取用户申请列表（包含房间详细信息）
     */
    Page<RoomApplicationResponse> getUserApplicationsWithRoomInfo(Long userId, String status, Pageable pageable);

    /**
     * 获取所有申请列表（管理员用）
     */
    Page<RoomApplication> getAllApplications(String status, Pageable pageable);

    /**
     * 根据房间ID获取申请列表
     */
    Page<RoomApplication> getApplicationsByRoomId(Long roomId, Pageable pageable);

    /**
     * 根据ID获取申请详情
     */
    RoomApplication getApplicationById(Long id);

    /**
     * 审核申请
     */
    RoomApplication reviewApplication(Long id, String status, String reviewRemarks, Long reviewerId);

    /**
     * 取消申请
     */
    RoomApplication cancelApplication(Long id, Long userId);

    /**
     * 检查用户是否已申请过该房间
     */
    boolean hasUserAppliedForRoom(Long userId, Long roomId);

    /**
     * 统计用户申请数量
     */
    long countUserApplications(Long userId);

    /**
     * 统计用户各状态申请数量
     */
    long countUserApplicationsByStatus(Long userId, String status);

    /**
     * 获取用户最近申请
     */
    List<RoomApplication> getUserRecentApplications(Long userId);

    /**
     * 根据房间ID获取房东ID
     * @param roomId 房间ID
     * @return 房东ID
     */
    Long getLandlordIdByRoomId(Long roomId);
} 