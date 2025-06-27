package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping({"/dashboard", "/api/v1/dashboard"})
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData() {
        logger.info("开始获取仪表盘数据");
        try {
            Map<String, Object> data = dashboardService.getDashboardData();
            logger.info("获取仪表盘数据成功");
            return ResponseEntity.ok(ApiResponse.success("获取仪表盘数据成功", data));
        } catch (Exception e) {
            logger.error("获取仪表盘数据失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取仪表盘数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取默认仪表盘数据
     */
    private Map<String, Object> getDefaultDashboardData() {
        Map<String, Object> data = new HashMap<>();
        data.put("newUsers", 102400);
        data.put("propertyCount", 81212);
        data.put("totalRevenue", 9280);
        data.put("rentedCount", 13600);
        return data;
    }

    /**
     * 获取用户访问来源数据
     */
    @GetMapping("/visit-source")
    public ResponseEntity<?> getVisitSourceData() {
        try {
            Map<String, Object> data = dashboardService.getVisitSourceData();
            return ResponseEntity.ok(ApiResponse.success("获取访问来源数据成功", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取访问来源数据失败: " + e.getMessage()));
        }
    }

    /**
     * 获取每周用户活跃量数据
     */
    @GetMapping("/weekly-activity")
    public ResponseEntity<?> getWeeklyActivityData() {
        try {
            Map<String, Object> data = dashboardService.getWeeklyActivityData();
            return ResponseEntity.ok(ApiResponse.success("获取每周活跃数据成功", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取每周活跃数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取每年租金变化数据
     */
    @GetMapping("/yearly-rent")
    public ResponseEntity<?> getYearlyRentData() {
        try {
            Map<String, Object> data = dashboardService.getYearlyRentData();
            return ResponseEntity.ok(ApiResponse.success("获取年度租金数据成功", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取年度租金数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取每月租金变化数据
     */
    @GetMapping("/monthly-rent")
    public ResponseEntity<?> getMonthlyRentData() {
        try {
            Map<String, Object> data = dashboardService.getMonthlyRentData();
            return ResponseEntity.ok(ApiResponse.success("获取月度租金数据成功", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取月度租金数据失败: " + e.getMessage()));
        }
    }
} 