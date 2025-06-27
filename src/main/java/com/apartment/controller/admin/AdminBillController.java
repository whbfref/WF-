package com.apartment.controller.admin;

import com.apartment.model.Bill;
import com.apartment.api.ApiResponse;
import com.apartment.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理员账单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/bills")
public class AdminBillController {

    @Autowired
    private BillService billService;

    /**
     * 获取账单列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String month) {
        
        try {
            Map<String, Object> result = billService.getAllBills(page, size, status, type, month);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取账单列表失败", e);
            return ApiResponse.error("获取账单列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取账单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Bill> getBillById(@PathVariable Long id) {
        try {
            Bill bill = billService.getBillById(id);
            return ApiResponse.success(bill);
        } catch (Exception e) {
            log.error("获取账单详情失败", e);
            return ApiResponse.error("获取账单详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建账单
     */
    @PostMapping
    public ApiResponse<Bill> createBill(@Valid @RequestBody Bill bill) {
        try {
            Bill savedBill = billService.saveBill(bill);
            return ApiResponse.success(savedBill);
        } catch (Exception e) {
            log.error("创建账单失败", e);
            return ApiResponse.error("创建账单失败: " + e.getMessage());
        }
    }

    /**
     * 更新账单
     */
    @PutMapping("/{id}")
    public ApiResponse<Bill> updateBill(@PathVariable Long id, @Valid @RequestBody Bill bill) {
        try {
            bill.setId(id);
            Bill updatedBill = billService.updateBill(bill);
            return ApiResponse.success(updatedBill);
        } catch (Exception e) {
            log.error("更新账单失败", e);
            return ApiResponse.error("更新账单失败: " + e.getMessage());
        }
    }

    /**
     * 删除账单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBill(@PathVariable Long id) {
        try {
            billService.deleteBill(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("删除账单失败", e);
            return ApiResponse.error("删除账单失败: " + e.getMessage());
        }
    }

    /**
     * 支付账单
     */
    @PostMapping("/{id}/pay")
    public ApiResponse<Bill> payBill(@PathVariable Long id) {
        try {
            Bill paidBill = billService.payBill(id);
            return ApiResponse.success(paidBill);
        } catch (Exception e) {
            log.error("支付账单失败", e);
            return ApiResponse.error("支付账单失败: " + e.getMessage());
        }
    }

    /**
     * 批量支付账单
     */
    @PostMapping("/batch-pay")
    public ApiResponse<Void> batchPayBills(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            List<Long> ids = requestBody.get("ids");
            billService.batchPayBills(ids);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量支付账单失败", e);
            return ApiResponse.error("批量支付账单失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除账单
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteBills(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            List<Long> ids = requestBody.get("ids");
            billService.batchDeleteBills(ids);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量删除账单失败", e);
            return ApiResponse.error("批量删除账单失败: " + e.getMessage());
        }
    }

    /**
     * 获取账单统计
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getBillStats(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String type) {
        try {
            Map<String, Object> stats = billService.getBillStats(month, type);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取账单统计失败", e);
            return ApiResponse.error("获取账单统计失败: " + e.getMessage());
        }
    }

    /**
     * 生成账单
     */
    @PostMapping("/generate")
    public ApiResponse<Void> generateBills(@RequestBody Map<String, Object> requestData) {
        try {
            String month = requestData.get("month").toString();
            billService.generateBills(month);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("生成账单失败", e);
            return ApiResponse.error("生成账单失败: " + e.getMessage());
        }
    }

    /**
     * 导出账单
     */
    @GetMapping("/export")
    public ApiResponse<Void> exportBills(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String month) {
        try {
            // TODO: 实现导出功能
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("导出账单失败", e);
            return ApiResponse.error("导出账单失败: " + e.getMessage());
        }
    }
} 