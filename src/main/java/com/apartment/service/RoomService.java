package com.apartment.service;

import com.apartment.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 房间服务接口
 */
public interface RoomService {

    /**
     * 获取房间列表（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param status 状态筛选
     * @param type 类型筛选
     * @param keyword 关键词搜索
     * @return 房间列表和分页信息
     */
    Map<String, Object> getAllRooms(int page, int size, String status, String type, String keyword);

    /**
     * 根据ID获取房间
     * @param id 房间ID
     * @return 房间对象
     */
    Room getRoomById(Long id);

    /**
     * 保存房间
     * @param room 房间对象
     * @return 保存后的房间对象
     */
    Room saveRoom(Room room);

    /**
     * 更新房间
     * @param room 房间对象
     * @return 更新后的房间对象
     */
    Room updateRoom(Room room);

    /**
     * 删除房间
     * @param id 房间ID
     */
    void deleteRoom(Long id);

    /**
     * 更新房间状态
     * @param id 房间ID
     * @param status 新状态
     * @return 更新后的房间对象
     */
    Room updateRoomStatus(Long id, String status);

    /**
     * 上传房间图片
     * @param id 房间ID
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadRoomImage(Long id, MultipartFile file);

    /**
     * 删除房间图片
     * @param roomId 房间ID
     * @param imageId 图片ID
     */
    void deleteRoomImage(Long roomId, Long imageId);

    /**
     * 设置房间封面图
     * @param roomId 房间ID
     * @param imageId 图片ID
     */
    void setRoomCoverImage(Long roomId, Long imageId);

    /**
     * 获取房间图片列表
     * @param roomId 房间ID
     * @return 图片列表
     */
    List<Map<String, Object>> getRoomImages(Long roomId);

    /**
     * 获取房间的合同列表
     * @param roomId 房间ID
     * @return 合同列表和统计信息
     */
    Map<String, Object> getRoomContracts(Long roomId);

    /**
     * 获取房间统计数据
     * @param roomId 房间ID
     * @return 统计数据
     */
    Map<String, Object> getRoomStats(Long roomId);
    
    /**
     * 批量删除房间
     * @param ids 房间ID列表
     */
    void batchDeleteRooms(List<Long> ids);
    
    /**
     * 批量更新房间状态
     * @param ids 房间ID列表
     * @param status 新状态
     */
    void batchUpdateRoomStatus(List<Long> ids, String status);

    /**
     * 获取可租房源
     * @param keyword 关键词
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param roomType 房间类型
     * @param sortBy 排序方式
     * @param pageable 分页参数
     * @return 可租房源列表
     */
    Object getAvailableRooms(String keyword, String minPrice, String maxPrice, String roomType, String sortBy, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据房东ID获取房间列表（分页）
     * @param landlordId 房东ID
     * @param status 状态筛选
     * @param keyword 关键词搜索
     * @param pageable 分页参数
     * @return 房间分页结果
     */
    Page<Room> getRoomsByLandlordId(Long landlordId, String status, String keyword, Pageable pageable);

    /**
     * 获取房间租户信息
     * @param roomId 房间ID
     * @return 租户信息
     */
    Map<String, Object> getRoomTenantInfo(Long roomId);
} 