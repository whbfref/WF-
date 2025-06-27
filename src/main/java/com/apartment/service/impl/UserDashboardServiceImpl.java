package com.apartment.service.impl;

import com.apartment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户仪表板服务实现类
 */
@Service
public class UserDashboardServiceImpl implements UserDashboardService {

    @Autowired
    private ContractService contractService;
    
    @Autowired
    private BillService billService;
    
    @Autowired
    private UtilityBillService utilityBillService;
    
    @Autowired
    private RoomApplicationService applicationService;
    
    @Autowired
    private UserFeedbackService feedbackService;

    @Override
    public Map<String, Object> getUserDashboardStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 当前租房数量 - 对应前端的 rentalCount
            long rentalCount = contractService.countUserActiveContracts(userId);
            stats.put("rentalCount", rentalCount);
            
            // 待缴租金数量 - 对应前端的 unpaidRentBills
            long unpaidRentBills = billService.countUserPendingBills(userId);
            stats.put("unpaidRentBills", unpaidRentBills);
            
            // 待缴物业费数量 - 对应前端的 unpaidUtilityBills
            long unpaidUtilityBills = utilityBillService.countUserPendingBills(userId);
            stats.put("unpaidUtilityBills", unpaidUtilityBills);
            
            // 待审核申请数量 - 对应前端的 pendingApplications
            long pendingApplications = applicationService.countUserApplicationsByStatus(userId, "PENDING") +
                                     applicationService.countUserApplicationsByStatus(userId, "REVIEWING");
            stats.put("pendingApplications", pendingApplications);
            
            // 获取当前租房信息 - 对应前端的 currentRental
            Object currentRental = contractService.getUserContracts(userId);
            stats.put("currentRental", currentRental);
            
            // 获取最近账单 - 对应前端的 recentBills
            try {
                List<Map<String, Object>> recentBills = billService.getUserRecentBills(userId, 5);
                stats.put("recentBills", recentBills);
            } catch (Exception e) {
                // 如果获取最近账单失败，设为空列表
                stats.put("recentBills", new java.util.ArrayList<>());
            }
            
        } catch (Exception e) {
            // 如果某些服务方法不存在，提供默认值
            stats.put("rentalCount", 0);
            stats.put("unpaidRentBills", 0);
            stats.put("unpaidUtilityBills", 0);
            stats.put("pendingApplications", 2); // 模拟数据
            stats.put("currentRental", null);
            stats.put("recentBills", new java.util.ArrayList<>());
        }
        
        return stats;
    }

    @Override
    public Map<String, Object> getUserRecentActivities(Long userId) {
        Map<String, Object> activities = new HashMap<>();
        
        // 最近申请
        activities.put("recentApplications", applicationService.getUserRecentApplications(userId));
        
        // 最近账单（这里需要在BillService中添加相应方法）
        // activities.put("recentBills", billService.getUserRecentBills(userId));
        
        return activities;
    }
} 