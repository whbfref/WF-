package com.apartment.service;

import com.apartment.model.LandlordApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 申请成为房东服务接口
 */
public interface LandlordApplicationService {

    /**
     * 提交申请成为房东
     */
    LandlordApplication submitApplication(Long userId, LandlordApplication application);

    /**
     * 获取用户的申请
     */
    Optional<LandlordApplication> getUserApplication(Long userId);

    /**
     * 获取所有申请列表（管理员用）
     */
    Page<LandlordApplication> getAllApplications(String status, Pageable pageable);

    /**
     * 根据ID获取申请详情
     */
    LandlordApplication getApplicationById(Long id);

    /**
     * 审核申请
     */
    LandlordApplication reviewApplication(Long id, String status, String reviewRemarks, Long reviewerId);

    /**
     * 检查用户是否已有待审核的申请
     */
    boolean hasUserPendingApplication(Long userId);

    /**
     * 统计各状态申请数量
     */
    long countApplicationsByStatus(String status);
} 