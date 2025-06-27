package com.apartment.repository;

import com.apartment.model.UserFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户反馈Repository
 */
@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {

    /**
     * 根据用户ID查询反馈列表
     */
    Page<UserFeedback> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID和类型查询反馈列表
     */
    Page<UserFeedback> findByUserIdAndTypeOrderByCreateTimeDesc(Long userId, String type, Pageable pageable);

    /**
     * 根据用户ID和状态查询反馈列表
     */
    Page<UserFeedback> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status, Pageable pageable);

    /**
     * 根据用户ID、类型和状态查询反馈列表
     */
    Page<UserFeedback> findByUserIdAndTypeAndStatusOrderByCreateTimeDesc(Long userId, String type, String status, Pageable pageable);

    /**
     * 查询所有反馈列表（管理员用）
     */
    @Query("SELECT f FROM UserFeedback f ORDER BY f.createTime DESC")
    Page<UserFeedback> findAllOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 根据类型查询反馈列表（管理员用）
     */
    Page<UserFeedback> findByTypeOrderByCreateTimeDesc(String type, Pageable pageable);

    /**
     * 根据状态查询反馈列表（管理员用）
     */
    Page<UserFeedback> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    /**
     * 根据类型和状态查询反馈列表（管理员用）
     */
    Page<UserFeedback> findByTypeAndStatusOrderByCreateTimeDesc(String type, String status, Pageable pageable);

    /**
     * 统计用户反馈数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户待处理反馈数量
     */
    long countByUserIdAndStatus(Long userId, String status);
} 