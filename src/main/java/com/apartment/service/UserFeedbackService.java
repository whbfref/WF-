package com.apartment.service;

import com.apartment.model.UserFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户反馈服务接口
 */
public interface UserFeedbackService {

    /**
     * 提交反馈
     */
    UserFeedback submitFeedback(Long userId, UserFeedback feedback);

    /**
     * 获取用户反馈列表
     */
    Page<UserFeedback> getUserFeedback(Long userId, String type, String status, Pageable pageable);

    /**
     * 获取所有反馈列表（管理员用）
     */
    Page<UserFeedback> getAllFeedback(String type, String status, Pageable pageable);

    /**
     * 根据ID获取反馈详情
     */
    UserFeedback getFeedbackById(Long id);

    /**
     * 回复反馈（管理员用）
     */
    UserFeedback replyFeedback(Long id, String reply, Long reviewerId);

    /**
     * 更新反馈状态
     */
    UserFeedback updateFeedbackStatus(Long id, String status);

    /**
     * 统计用户反馈数量
     */
    long countUserFeedback(Long userId);

    /**
     * 统计用户待处理反馈数量
     */
    long countUserPendingFeedback(Long userId);
} 