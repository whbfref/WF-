package com.apartment.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param directory 存储目录
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    String uploadFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @throws IOException IO异常
     */
    void deleteFile(String fileUrl) throws IOException;
} 