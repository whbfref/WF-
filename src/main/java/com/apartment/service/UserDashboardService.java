package com.apartment.service;

import java.util.Map;

/**
 * 用户仪表板服务接口
 */
public interface UserDashboardService {

    /**
     * 获取用户仪表板统计数据
     */
    Map<String, Object> getUserDashboardStats(Long userId);

    /**
     * 获取用户最近活动
     */
    Map<String, Object> getUserRecentActivities(Long userId);
} 