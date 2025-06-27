package com.apartment.repository;

import com.apartment.model.LandlordApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 申请成为房东Repository
 */
@Repository
public interface LandlordApplicationRepository extends JpaRepository<LandlordApplication, Long> {

    /**
     * 根据用户ID查询申请
     */
    Optional<LandlordApplication> findByUserId(Long userId);

    /**
     * 根据用户ID和状态查询申请
     */
    Optional<LandlordApplication> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据状态查询申请列表
     */
    Page<LandlordApplication> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    /**
     * 查询所有申请列表
     */
    Page<LandlordApplication> findAllByOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 检查用户是否已有待审核的申请
     */
    boolean existsByUserIdAndStatus(Long userId, String status);

    /**
     * 统计各状态申请数量
     */
    long countByStatus(String status);
} 