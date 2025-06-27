package com.apartment.service.impl;

import com.apartment.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl implements FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    
    @Value("${file.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${file.upload.url.prefix:/api/v1}")
    private String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 创建目录
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetDirectory = uploadDir + File.separator + directory + File.separator + datePath;
        Path dirPath = Paths.get(targetDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path targetPath = dirPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath);
        
        // 返回访问URL - 使用相对路径
        String relativePath = directory + "/" + datePath + "/" + fileName;
        String fileUrl = urlPrefix + "/files/" + relativePath;
        
        logger.info("文件上传成功: {}", fileUrl);
        return fileUrl;
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("文件URL不能为空");
        }
        
        // 从URL中提取相对路径
        String relativePath = fileUrl.replace(urlPrefix + "/uploads/", "");
        String filePath = uploadDir + File.separator + relativePath.replace("/", File.separator);
        
        // 删除文件
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            logger.info("文件删除成功: {}", filePath);
        } else {
            logger.warn("文件不存在，无法删除: {}", filePath);
        }
    }
} 