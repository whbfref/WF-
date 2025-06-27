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
import java.util.List;
import java.util.Map;

/**
 * 物业控制器
 */
@RestController
@RequestMapping({"/properties", "/api/v1/properties"})
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    /**
     * 获取物业列表（分页、筛选）
     */
    @GetMapping
    public ResponseEntity<?> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> result = propertyService.getAllProperties(page, size, status, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取物业列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取物业列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取物业详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyDetails(@PathVariable Long id) {
        try {
            Property property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(ApiResponse.success("获取物业详情成功", property));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取物业详情失败: " + e.getMessage()));
        }
    }

    /**
     * 创建物业
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> createProperty(@Valid @RequestBody Property property) {
        try {
            Property savedProperty = propertyService.saveProperty(property);
            return ResponseEntity.ok(ApiResponse.success("创建物业成功", savedProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建物业失败: " + e.getMessage()));
        }
    }

    /**
     * 更新物业
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> updateProperty(@PathVariable Long id, @Valid @RequestBody Property property) {
        try {
            property.setId(id);
            Property updatedProperty = propertyService.updateProperty(property);
            return ResponseEntity.ok(ApiResponse.success("更新物业成功", updatedProperty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新物业失败: " + e.getMessage()));
        }
    }

    /**
     * 删除物业
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok(ApiResponse.success("删除物业成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除物业失败: " + e.getMessage()));
        }
    }

    /**
     * 上传物业图片
     */
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> uploadPropertyImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = propertyService.uploadPropertyImage(id, file);
            return ResponseEntity.ok(ApiResponse.success("上传物业图片成功", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传物业图片失败: " + e.getMessage()));
        }
    }

    /**
     * 获取物业的房间列表
     */
    @GetMapping("/{id}/rooms")
    public ResponseEntity<?> getPropertyRooms(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            Map<String, Object> result = propertyService.getPropertyRooms(id, page, size, status);
            return ResponseEntity.ok(ApiResponse.success("获取物业房间列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取物业房间列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取物业统计数据
     */
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> getPropertyStats(@PathVariable Long id) {
        try {
            Map<String, Object> stats = propertyService.getPropertyStats(id);
            return ResponseEntity.ok(ApiResponse.success("获取物业统计数据成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取物业统计数据失败: " + e.getMessage()));
        }
    }
} 