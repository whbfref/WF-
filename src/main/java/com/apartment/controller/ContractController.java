package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.model.Contract;
import com.apartment.security.SecurityService;
import com.apartment.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

/**
 * 合同控制器
 */
@RestController
@RequestMapping("/contracts")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ContractController {

    @Autowired
    private ContractService contractService;

    /**
     * 获取合同列表（分页、筛选）
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> getAllContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> result = contractService.getAllContracts(page, size, status, tenantId, roomId, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取合同列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取合同列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD') or @securityService.isContractTenant(#id, authentication.name)")
    public ResponseEntity<?> getContractDetails(@PathVariable Long id) {
        try {
            Contract contract = contractService.getContractById(id);
            return ResponseEntity.ok(ApiResponse.success("获取合同详情成功", contract));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取合同详情失败: " + e.getMessage()));
        }
    }

    /**
     * 创建合同
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> createContract(@Valid @RequestBody Contract contract) {
        try {
            Contract savedContract = contractService.saveContract(contract);
            return ResponseEntity.ok(ApiResponse.success("创建合同成功", savedContract));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建合同失败: " + e.getMessage()));
        }
    }

    /**
     * 更新合同
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> updateContract(@PathVariable Long id, @Valid @RequestBody Contract contract) {
        try {
            contract.setId(id);
            Contract updatedContract = contractService.updateContract(contract);
            return ResponseEntity.ok(ApiResponse.success("更新合同成功", updatedContract));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新合同失败: " + e.getMessage()));
        }
    }

    /**
     * 终止合同
     */
    @PutMapping("/{id}/terminate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> terminateContract(
            @PathVariable Long id,
            @RequestBody Map<String, String> terminationData) {
        try {
            String reason = terminationData.get("reason");
            Contract terminatedContract = contractService.terminateContract(id, reason);
            return ResponseEntity.ok(ApiResponse.success("终止合同成功", terminatedContract));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("终止合同失败: " + e.getMessage()));
        }
    }

    /**
     * 上传合同文档
     */
    @PostMapping("/{id}/document")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> uploadContractDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String documentUrl = contractService.uploadContractDocument(id, file);
            return ResponseEntity.ok(ApiResponse.success("上传合同文档成功", documentUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传合同文档失败: " + e.getMessage()));
        }
    }

    /**
     * 获取当前用户的合同列表
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            Map<String, Object> result = contractService.getCurrentUserContracts(page, size, status);
            return ResponseEntity.ok(ApiResponse.success("获取我的合同列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取我的合同列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取即将到期的合同
     */
    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> getExpiringContracts(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> result = contractService.getExpiringContracts(days);
            return ResponseEntity.ok(ApiResponse.success("获取即将到期合同成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取即将到期合同失败: " + e.getMessage()));
        }
    }

    /**
     * 合同统计数据
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> getContractStats() {
        try {
            Map<String, Object> stats = contractService.getContractStats();
            return ResponseEntity.ok(ApiResponse.success("获取合同统计数据成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取合同统计数据失败: " + e.getMessage()));
        }
    }
} 