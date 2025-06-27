package com.apartment.repository;

import com.apartment.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 通知消息数据访问接口
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * 根据用户ID查询通知
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 通知分页结果
     */
    Page<Notification> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和已读状态查询通知
     * @param userId 用户ID
     * @param isRead 是否已读
     * @param pageable 分页参数
     * @return 通知分页结果
     */
    Page<Notification> findByUserIdAndIsReadOrderByCreateTimeDesc(Long userId, Boolean isRead, Pageable pageable);
    
    /**
     * 根据用户ID和消息类型查询通知
     * @param userId 用户ID
     * @param type 消息类型
     * @param pageable 分页参数
     * @return 通知分页结果
     */
    Page<Notification> findByUserIdAndTypeOrderByCreateTimeDesc(Long userId, String type, Pageable pageable);
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @param readTime 阅读时间
     * @return 影响的行数
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readTime = :readTime WHERE n.id = :notificationId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("readTime") LocalDateTime readTime);
    
    /**
     * 标记用户的所有通知为已读
     * @param userId 用户ID
     * @param readTime 阅读时间
     * @return 影响的行数
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readTime = :readTime WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);
    
    /**
     * 统计用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
} 