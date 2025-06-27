package com.apartment.service;

import com.apartment.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 通知消息服务接口
 */
public interface NotificationService {
    
    /**
     * 创建通知
     * @param notification 通知实体
     * @return 创建的通知
     */
    Notification createNotification(Notification notification);
    
    /**
     * 创建租房申请通知
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param applicationId 申请ID
     * @return 创建的通知
     */
    Notification createRoomApplicationNotification(Long userId, String title, String content, Long applicationId);
    
    /**
     * 创建待办事项通知
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param todoId 待办事项ID
     * @return 创建的通知
     */
    Notification createTodoNotification(Long userId, String title, String content, Long todoId);
    
    /**
     * 获取用户通知列表
     * @param userId 用户ID
     * @param isRead 是否已读
     * @param pageable 分页参数
     * @return 通知分页结果
     */
    Page<Notification> getUserNotifications(Long userId, Boolean isRead, Pageable pageable);
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @return 更新后的通知
     */
    Notification markAsRead(Long notificationId);
    
    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     * @return 受影响的通知数量
     */
    int markAllAsRead(Long userId);
    
    /**
     * 统计用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    long countUnreadNotifications(Long userId);
    
    /**
     * 获取通知详情
     * @param notificationId 通知ID
     * @return 通知详情
     */
    Notification getNotificationById(Long notificationId);
} 