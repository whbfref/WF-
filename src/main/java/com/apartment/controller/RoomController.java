package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.model.Room;
import com.apartment.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 房间控制器
 */
@RestController
@RequestMapping({"/rooms", "/api/v1/rooms"})
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * 获取房间列表（分页、筛选）
     */
    @GetMapping
    public ResponseEntity<?> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        try {
            Map<String, Object> result = roomService.getAllRooms(page, size, status, type, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取房间列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房间列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房间详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomDetails(@PathVariable Long id) {
        try {
            Room room = roomService.getRoomById(id);
            return ResponseEntity.ok(ApiResponse.success("获取房间详情成功", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房间详情失败: " + e.getMessage()));
        }
    }

    /**
     * 创建房间
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> createRoom(@Valid @RequestBody Room room) {
        try {
            Room savedRoom = roomService.saveRoom(room);
            return ResponseEntity.ok(ApiResponse.success("创建房间成功", savedRoom));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建房间失败: " + e.getMessage()));
        }
    }

    /**
     * 更新房间
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @Valid @RequestBody Room room) {
        try {
            room.setId(id);
            Room updatedRoom = roomService.updateRoom(room);
            return ResponseEntity.ok(ApiResponse.success("更新房间成功", updatedRoom));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新房间失败: " + e.getMessage()));
        }
    }

    /**
     * 删除房间
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(ApiResponse.success("删除房间成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除房间失败: " + e.getMessage()));
        }
    }

    /**
     * 更新房间状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> updateRoomStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusMap) {
        try {
            String status = statusMap.get("status");
            if (status == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态值不能为空"));
            }
            
            Room room = roomService.updateRoomStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("更新房间状态成功", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新房间状态失败: " + e.getMessage()));
        }
    }

    /**
     * 上传房间图片
     */
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> uploadRoomImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = roomService.uploadRoomImage(id, file);
            return ResponseEntity.ok(ApiResponse.success("上传房间图片成功", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传房间图片失败: " + e.getMessage()));
        }
    }

    /**
     * 删除房间图片
     */
    @DeleteMapping("/{roomId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> deleteRoomImage(
            @PathVariable Long roomId,
            @PathVariable Long imageId) {
        try {
            roomService.deleteRoomImage(roomId, imageId);
            return ResponseEntity.ok(ApiResponse.success("删除房间图片成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除房间图片失败: " + e.getMessage()));
        }
    }

    /**
     * 设置房间封面图
     */
    @PutMapping("/{roomId}/images/{imageId}/cover")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> setRoomCoverImage(
            @PathVariable Long roomId,
            @PathVariable Long imageId) {
        try {
            roomService.setRoomCoverImage(roomId, imageId);
            return ResponseEntity.ok(ApiResponse.success("设置封面图成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("设置封面图失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房间图片列表
     */
    @GetMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'USER')")
    public ResponseEntity<?> getRoomImages(@PathVariable Long id) {
        try {
            List<Map<String, Object>> images = roomService.getRoomImages(id);
            return ResponseEntity.ok(ApiResponse.success("获取房间图片成功", images));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房间图片失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房间历史合同
     */
    @GetMapping("/{id}/contracts")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> getRoomContracts(@PathVariable Long id) {
        try {
            Map<String, Object> contracts = roomService.getRoomContracts(id);
            return ResponseEntity.ok(ApiResponse.success("获取房间合同列表成功", contracts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房间合同列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取房间统计数据
     */
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> getRoomStats(@PathVariable Long id) {
        try {
            Map<String, Object> stats = roomService.getRoomStats(id);
            return ResponseEntity.ok(ApiResponse.success("获取房间统计数据成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取房间统计数据失败: " + e.getMessage()));
        }
    }

    /**
     * 批量删除房间
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> batchDeleteRooms(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("房间ID列表不能为空"));
            }
            
            roomService.batchDeleteRooms(ids);
            return ResponseEntity.ok(ApiResponse.success("批量删除房间成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("批量删除房间失败: " + e.getMessage()));
        }
    }

    /**
     * 批量更新房间状态
     */
    @PutMapping("/batch/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<?> batchUpdateRoomStatus(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            String status = (String) request.get("status");
            
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("房间ID列表不能为空"));
            }
            
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态值不能为空"));
            }
            
            roomService.batchUpdateRoomStatus(ids, status);
            return ResponseEntity.ok(ApiResponse.success("批量更新房间状态成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("批量更新房间状态失败: " + e.getMessage()));
        }
    }
} 