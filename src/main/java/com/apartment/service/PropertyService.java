package com.apartment.service;

import com.apartment.model.Property;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 物业服务接口
 */
public interface PropertyService {

    /**
     * 获取物业列表（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param status 状态筛选
     * @param keyword 关键词搜索
     * @return 物业列表和分页信息
     */
    Map<String, Object> getAllProperties(int page, int size, String status, String keyword);

    /**
     * 根据ID获取物业
     * @param id 物业ID
     * @return 物业对象
     */
    Property getPropertyById(Long id);

    /**
     * 保存物业
     * @param property 物业对象
     * @return 保存后的物业对象
     */
    Property saveProperty(Property property);

    /**
     * 更新物业
     * @param property 物业对象
     * @return 更新后的物业对象
     */
    Property updateProperty(Property property);

    /**
     * 删除物业
     * @param id 物业ID
     */
    void deleteProperty(Long id);

    /**
     * 上传物业图片
     * @param id 物业ID
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadPropertyImage(Long id, MultipartFile file);

    /**
     * 获取物业的房间列表
     * @param propertyId 物业ID
     * @param page 页码
     * @param size 每页数量
     * @param status 状态筛选
     * @return 房间列表和分页信息
     */
    Map<String, Object> getPropertyRooms(Long propertyId, int page, int size, String status);

    /**
     * 获取物业统计数据
     * @param propertyId 物业ID
     * @return 统计数据
     */
    Map<String, Object> getPropertyStats(Long propertyId);

    /**
     * 更新物业状态
     * @param id 物业ID
     * @param status 状态
     * @return 更新后的物业对象
     */
    Property updatePropertyStatus(Long id, Integer status);
} 