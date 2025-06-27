package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.model.Property;
import com.apartment.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房屋管理控制器
 */
@RestController
@RequestMapping("/api/v1/housing")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
public class HousingController {

    @Autowired
    private PropertyService propertyService;

    /**
     * 获取房源列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getHousingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        try {
            // 转换分页参数（前端传入的是1开始，后端需要0开始）
            Map<String, Object> result = propertyService.getAllProperties(page - 1, size, status, keyword);
            
            // 转换数据格式以匹配前端期望
            Map<String, Object> response = new HashMap<>();
            response.put("list", result.get("content"));
            response.put("total", result.get("totalElements"));
            response.put("page", page);
            response.put("size", size);
            
            return ResponseEntity.ok(ApiResponse.success("获取房源列表成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房源列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房源详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHousingDetail(@PathVariable Long id) {
        try {
            Property property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(ApiResponse.success("获取房源详情成功", property));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房源详情失败: " + e.getMessage()));
        }
    }

    /**
     * 创建房源
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> createHousing(@Valid @RequestBody Property property) {
        try {
            Property savedProperty = propertyService.saveProperty(property);
            return ResponseEntity.ok(ApiResponse.success("创建房源成功", savedProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建房源失败: " + e.getMessage()));
        }
    }

    /**
     * 更新房源
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> updateHousing(@PathVariable Long id, @Valid @RequestBody Property property) {
        try {
            property.setId(id);
            Property updatedProperty = propertyService.updateProperty(property);
            return ResponseEntity.ok(ApiResponse.success("更新房源成功", updatedProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新房源失败: " + e.getMessage()));
        }
    }

    /**
     * 删除房源
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> deleteHousing(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok(ApiResponse.success("删除房源成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除房源失败: " + e.getMessage()));
        }
    }

    /**
     * 更新房源状态
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> updateHousingStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态不能为空"));
            }
            
            // 转换状态为数字格式（如果需要）
            Integer statusCode = convertStatusToCode(status);
            Property updatedProperty = propertyService.updatePropertyStatus(id, statusCode);
            
            return ResponseEntity.ok(ApiResponse.success("更新房源状态成功", updatedProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新房源状态失败: " + e.getMessage()));
        }
    }

    /**
     * 上传房源图片
     */
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> uploadHousingImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = propertyService.uploadPropertyImage(id, file);
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            return ResponseEntity.ok(ApiResponse.success("上传房源图片成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传房源图片失败: " + e.getMessage()));
        }
    }

    /**
     * 批量删除房源
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> batchDeleteHousing(@RequestBody Map<String, String> request) {
        try {
            String idsStr = request.get("ids");
            if (idsStr == null || idsStr.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("房源ID不能为空"));
            }
            
            String[] idArray = idsStr.split(",");
            for (String idStr : idArray) {
                Long id = Long.parseLong(idStr.trim());
                propertyService.deleteProperty(id);
            }
            
            return ResponseEntity.ok(ApiResponse.success("批量删除房源成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("批量删除房源失败: " + e.getMessage()));
        }
    }

    /**
     * 批量更新房源状态
     */
    @PatchMapping("/batch/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> batchUpdateHousingStatus(@RequestBody Map<String, String> request) {
        try {
            String idsStr = request.get("ids");
            String status = request.get("status");
            
            if (idsStr == null || idsStr.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("房源ID不能为空"));
            }
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态不能为空"));
            }
            
            String[] idArray = idsStr.split(",");
            Integer statusCode = convertStatusToCode(status);
            
            for (String idStr : idArray) {
                Long id = Long.parseLong(idStr.trim());
                propertyService.updatePropertyStatus(id, statusCode);
            }
            
            return ResponseEntity.ok(ApiResponse.success("批量更新房源状态成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("批量更新房源状态失败: " + e.getMessage()));
        }
    }

    /**
     * 转换状态字符串为状态码
     */
    private Integer convertStatusToCode(String status) {
        switch (status.toUpperCase()) {
            case "AVAILABLE":
                return 1; // 可租
            case "RENTED":
                return 3; // 已租
            case "MAINTENANCE":
                return 2; // 维护中
            case "OFFLINE":
                return 0; // 下线
            default:
                return 1; // 默认可租
        }
    }
} 