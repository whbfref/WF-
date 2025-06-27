package com.apartment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件访问控制器
 */
@RestController
@RequestMapping("/api/v1")
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Value("${file.upload.dir:uploads}")
    private String uploadDir;
    
    /**
     * 获取文件
     */
    @GetMapping("/files/**")
    public ResponseEntity<Resource> getFile(HttpServletRequest request) {
        try {
            // 从请求URI中提取文件路径
            String requestURI = request.getRequestURI();
            String filePath = requestURI.substring("/api/v1/files/".length());
            
            logger.info("请求文件路径: {}", filePath);
            
            // 构建完整的文件路径
            Path fullPath = Paths.get(uploadDir).resolve(filePath).normalize();
            File file = fullPath.toFile();
            
            logger.info("完整文件路径: {}", fullPath.toString());
            
            if (!file.exists() || !file.isFile()) {
                logger.warn("文件不存在: {}", fullPath.toString());
                return ResponseEntity.notFound().build();
            }
            
            // 安全检查 - 确保文件在上传目录内
            Path uploadPath = Paths.get(uploadDir).normalize().toAbsolutePath();
            Path requestedPath = fullPath.normalize().toAbsolutePath();
            
            if (!requestedPath.startsWith(uploadPath)) {
                logger.warn("非法文件访问尝试: {}", requestedPath.toString());
                return ResponseEntity.badRequest().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 确定MIME类型
            String contentType = Files.probeContentType(fullPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            logger.info("成功访问文件: {}, 类型: {}, 大小: {} bytes", 
                       fullPath.toString(), contentType, file.length());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("文件访问失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 