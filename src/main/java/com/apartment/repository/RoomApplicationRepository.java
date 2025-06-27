package com.apartment.repository;

import com.apartment.model.RoomApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 租房申请Repository
 */
@Repository
public interface RoomApplicationRepository extends JpaRepository<RoomApplication, Long> {

    /**
     * 根据用户ID查询申请列表
     */
    Page<RoomApplication> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态查询申请列表
     */
    Page<RoomApplication> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status, Pageable pageable);

    /**
     * 根据房间ID查询申请列表
     */
    Page<RoomApplication> findByRoomIdOrderByCreateTimeDesc(Long roomId, Pageable pageable);

    /**
     * 根据状态查询申请列表
     */
    Page<RoomApplication> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    /**
     * 查询用户对特定房间的申请
     */
    Optional<RoomApplication> findByUserIdAndRoomIdAndStatus(Long userId, Long roomId, String status);

    /**
     * 检查用户是否已申请过该房间（待审核或已通过状态）
     */
    @Query("SELECT COUNT(ra) > 0 FROM RoomApplication ra WHERE ra.userId = :userId AND ra.roomId = :roomId AND ra.status IN ('PENDING', 'APPROVED')")
    boolean existsByUserIdAndRoomIdAndStatusIn(@Param("userId") Long userId, @Param("roomId") Long roomId);

    /**
     * 统计用户申请数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户各状态申请数量
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * 获取用户最近申请
     */
    List<RoomApplication> findTop5ByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 查询房间的待审核申请
     */
    List<RoomApplication> findByRoomIdAndStatus(Long roomId, String status);
} 