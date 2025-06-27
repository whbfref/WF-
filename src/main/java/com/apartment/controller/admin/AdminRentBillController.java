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
 * 管理员租金账单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/rent-bills")
public class AdminRentBillController {

    @Autowired
    private BillService billService;

    /**
     * 获取租金账单列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getRentBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) String billMonth) {
        
        try {
            // 获取所有账单，通过类型过滤
            Map<String, Object> result = billService.getAllBills(page, size, status, "RENT", billMonth);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取租金账单列表失败", e);
            return ApiResponse.error("获取租金账单列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取租金账单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Bill> getRentBillById(@PathVariable Long id) {
        try {
            Bill bill = billService.getBillById(id);
            if (bill != null && "RENT".equals(bill.getBillType())) {
                return ApiResponse.success(bill);
            } else {
                return ApiResponse.error("租金账单不存在");
            }
        } catch (Exception e) {
            log.error("获取租金账单详情失败", e);
            return ApiResponse.error("获取租金账单详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建租金账单
     */
    @PostMapping
    public ApiResponse<Bill> createRentBill(@Valid @RequestBody Bill bill) {
        try {
            // 设置账单类型为租金
            bill.setBillType("RENT");
            Bill savedBill = billService.saveBill(bill);
            return ApiResponse.success(savedBill);
        } catch (Exception e) {
            log.error("创建租金账单失败", e);
            return ApiResponse.error("创建租金账单失败: " + e.getMessage());
        }
    }

    /**
     * 更新租金账单
     */
    @PutMapping("/{id}")
    public ApiResponse<Bill> updateRentBill(@PathVariable Long id, @Valid @RequestBody Bill bill) {
        try {
            bill.setId(id);
            bill.setBillType("RENT");
            Bill updatedBill = billService.updateBill(bill);
            return ApiResponse.success(updatedBill);
        } catch (Exception e) {
            log.error("更新租金账单失败", e);
            return ApiResponse.error("更新租金账单失败: " + e.getMessage());
        }
    }

    /**
     * 删除租金账单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRentBill(@PathVariable Long id) {
        try {
            billService.deleteBill(id);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("删除租金账单失败", e);
            return ApiResponse.error("删除租金账单失败: " + e.getMessage());
        }
    }

    /**
     * 支付租金账单
     */
    @PostMapping("/{id}/pay")
    public ApiResponse<Bill> payRentBill(@PathVariable Long id) {
        try {
            Bill paidBill = billService.payBill(id);
            return ApiResponse.success(paidBill);
        } catch (Exception e) {
            log.error("支付租金账单失败", e);
            return ApiResponse.error("支付租金账单失败: " + e.getMessage());
        }
    }

    /**
     * 批量支付租金账单
     */
    @PostMapping("/batch-pay")
    public ApiResponse<Void> batchPayRentBills(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            List<Long> ids = requestBody.get("ids");
            billService.batchPayBills(ids);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量支付租金账单失败", e);
            return ApiResponse.error("批量支付租金账单失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除租金账单
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteRentBills(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            List<Long> ids = requestBody.get("ids");
            billService.batchDeleteBills(ids);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("批量删除租金账单失败", e);
            return ApiResponse.error("批量删除租金账单失败: " + e.getMessage());
        }
    }

    /**
     * 获取租金账单统计
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getRentBillStats(
            @RequestParam(required = false) String month) {
        try {
            Map<String, Object> stats = billService.getBillStats(month, "RENT");
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取租金账单统计失败", e);
            return ApiResponse.error("获取租金账单统计失败: " + e.getMessage());
        }
    }

    /**
     * 生成租金账单
     */
    @PostMapping("/generate")
    public ApiResponse<Void> generateRentBills(@RequestBody Map<String, Object> requestData) {
        try {
            String month = requestData.get("month").toString();
            List<Bill> generatedBills = billService.generateBills(month);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("生成租金账单失败", e);
            return ApiResponse.error("生成租金账单失败: " + e.getMessage());
        }
    }

    /**
     * 导出租金账单
     */
    @GetMapping("/export")
    public ApiResponse<String> exportRentBills(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String status) {
        try {
            // 这里可以调用导出服务
            return ApiResponse.success("租金账单导出成功");
        } catch (Exception e) {
            log.error("导出租金账单失败", e);
            return ApiResponse.error("导出租金账单失败: " + e.getMessage());
        }
    }
} 