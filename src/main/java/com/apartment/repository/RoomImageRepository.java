package com.apartment.repository;

import com.apartment.model.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 房间图片数据访问接口
 */
@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    
    /**
     * 根据房间ID查询图片列表
     * @param roomId 房间ID
     * @return 图片列表
     */
    List<RoomImage> findByRoomIdOrderBySortOrder(Long roomId);
    
    /**
     * 根据房间ID查询封面图
     * @param roomId 房间ID
     * @param isCover 是否为封面图
     * @return 封面图
     */
    RoomImage findByRoomIdAndIsCover(Long roomId, Boolean isCover);
    
    /**
     * 根据房间ID删除所有图片
     * @param roomId 房间ID
     */
    void deleteByRoomId(Long roomId);
} 