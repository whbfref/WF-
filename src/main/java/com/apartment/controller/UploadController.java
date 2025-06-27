package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/v1/upload")
@CrossOrigin(origins = "*")
public class UploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    
    @Autowired
    private FileService fileService;
    
    /**
     * 上传房屋图片
     */
    @PostMapping("/housing-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> uploadHousingImage(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("只允许上传图片文件"));
            }
            
            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(ApiResponse.error("文件大小不能超过2MB"));
            }
            
            // 上传文件
            String imageUrl = fileService.uploadFile(file, "housing");
            
            return ResponseEntity.ok(ApiResponse.success("上传图片成功", imageUrl));
        } catch (Exception e) {
            logger.error("上传房屋图片失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("上传图片失败: " + e.getMessage()));
        }
    }
    
    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'USER')")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("只允许上传图片文件"));
            }
            
            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(ApiResponse.error("文件大小不能超过5MB"));
            }
            
            // 上传文件
            String avatarUrl = fileService.uploadFile(file, "avatar");
            
            return ResponseEntity.ok(ApiResponse.success("上传头像成功", avatarUrl));
        } catch (Exception e) {
            logger.error("上传头像失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("上传头像失败: " + e.getMessage()));
        }
    }
    
    /**
     * 上传通用文件
     */
    @PostMapping("/file")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'USER')")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "general") String directory) {
        try {
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(ApiResponse.error("文件大小不能超过10MB"));
            }
            
            // 上传文件
            String fileUrl = fileService.uploadFile(file, directory);
            
            return ResponseEntity.ok(ApiResponse.success("上传文件成功", fileUrl));
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("上传文件失败: " + e.getMessage()));
        }
    }
} 