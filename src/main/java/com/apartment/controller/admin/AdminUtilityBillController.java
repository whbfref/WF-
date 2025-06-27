package com.apartment.controller.admin;

import com.apartment.model.UtilityBill;
import com.apartment.api.ApiResponse;
import com.apartment.service.UtilityBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理员物业费账单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/utility-bills")
public class AdminUtilityBillController {

    @Autowired
    private UtilityBillService utilityBillService;

    /**
     * 获取物业费账单列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getUtilityBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Long propertyId) {
        
        try {
            Map<String, Object> result = utilityBillService.getAllBills(page, size, status, type, month);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取物业费账单列表失败", e);
            return ApiResponse.error("获取物业费账单列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取物业费账单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<UtilityBill> getUtilityBillById(@PathVariable Long id) {
        try {
            UtilityBill bill = utilityBillService.getBillById(id);
            return ApiResponse.success(bill);
        } catch (Exception e) {
            log.error("获取物业费账单详情失败", e);
            return ApiResponse.error("获取物业费账单详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建物业费账单
     */
    @PostMapping
    public ApiResponse<UtilityBill> createUtilityBill(@Valid @RequestBody UtilityBill bill) {
        try {
            UtilityBill savedBill = utilityBillService.saveBill(bill);
            return ApiResponse.success(savedBill);
        } catch (Exception e) {
            log.error("创建物业费账单失败", e);
            return ApiResponse.error("创建物业费账单失败: " + e.getMessage());
        }
    }

    /**
     * 更新物业费账单
     */
    @PutMapping("/{id}")
    public ApiResponse<UtilityBill> updateUtilityBill(@PathVariable Long id, @Valid @RequestBody UtilityBill bill) {
        try {
            bill.setId(id);
            UtilityBill updatedBill = utilityBillService.updateBill(bill);
            return ApiResponse.success(updatedBill);
        } catch (Exception e) {
            log.error("更新物业费账单失败", e);
            return ApiResponse.error("更新物业费账单失败: " + e.getMessage());
        }
    }

    /**
     * 删除物业费账单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUtilityBill(@PathVariable Long id) {
        try {
            utilityBillService.deleteBill(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("删除物业费账单失败", e);
            return ApiResponse.error("删除物业费账单失败: " + e.getMessage());
        }
    }

    /**
     * 缴费
     */
    @PostMapping("/{id}/pay")
    public ApiResponse<UtilityBill> payUtilityBill(@PathVariable Long id) {
        try {
            UtilityBill paidBill = utilityBillService.payBill(id);
            return ApiResponse.success(paidBill);
        } catch (Exception e) {
            log.error("缴费失败", e);
            return ApiResponse.error("缴费失败: " + e.getMessage());
        }
    }

    /**
     * 批量缴费
     */
    @PostMapping("/batch-pay")
    public ApiResponse<Void> batchPayUtilityBills(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            List<Long> ids = requestBody.get("ids");
            utilityBillService.batchPayBills(ids);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量缴费失败", e);
            return ApiResponse.error("批量缴费失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除物业费账单
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteUtilityBills(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            List<Long> ids = requestBody.get("ids");
            utilityBillService.batchDeleteBills(ids);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量删除物业费账单失败", e);
            return ApiResponse.error("批量删除物业费账单失败: " + e.getMessage());
        }
    }

    /**
     * 获取物业费账单统计
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getUtilityBillStats() {
        try {
            Map<String, Object> stats = utilityBillService.getBillStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取物业费账单统计失败", e);
            return ApiResponse.error("获取物业费账单统计失败: " + e.getMessage());
        }
    }

    /**
     * 抄表录入
     */
    @PostMapping("/meter-reading")
    public ApiResponse<Map<String, Object>> recordMeterReading(@RequestBody Map<String, Object> data) {
        try {
            Map<String, Object> result = utilityBillService.recordMeterReading(data);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("抄表录入失败", e);
            return ApiResponse.error("抄表录入失败: " + e.getMessage());
        }
    }
} 