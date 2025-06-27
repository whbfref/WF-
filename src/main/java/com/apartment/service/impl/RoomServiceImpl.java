package com.apartment.service.impl;

import com.apartment.model.Contract;
import com.apartment.model.Room;
import com.apartment.model.RoomImage;
import com.apartment.repository.ContractRepository;
import com.apartment.repository.RoomRepository;
import com.apartment.repository.RoomImageRepository;
import com.apartment.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房间服务实现类
 */
@Service
public class RoomServiceImpl implements RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RoomImageRepository roomImageRepository;

    @Value("${app.uploads.base-path:uploads}")
    private String uploadBasePath;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllRooms(int page, int size, String status, String type, String keyword) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<Room> roomsPage;
            
            // 使用关联查询获取房间和物业信息
            if (status != null && !status.isEmpty()) {
                roomsPage = roomRepository.findByStatusWithProperty(status, pageable);
            } else {
                roomsPage = roomRepository.findAllWithProperty(pageable);
            }
            
            List<Room> rooms = roomsPage.getContent();
            
            // 为每个房间添加租户信息和图片信息
            List<Map<String, Object>> roomsWithTenants = new ArrayList<>();
            for (Room room : rooms) {
                Map<String, Object> roomMap = new HashMap<>();
                roomMap.put("id", room.getId());
                roomMap.put("roomNumber", room.getRoomNumber());
                roomMap.put("type", room.getType());
                roomMap.put("status", room.getStatus());
                roomMap.put("monthlyRent", room.getMonthlyRent());
                roomMap.put("area", room.getArea());
                roomMap.put("floor", room.getFloor());
                roomMap.put("orientation", room.getOrientation());
                roomMap.put("propertyId", room.getPropertyId());
                
                // 添加物业信息
                try {
                    if (room.getProperty() != null) {
                        roomMap.put("propertyTitle", room.getProperty().getTitle());
                        roomMap.put("propertyAddress", room.getProperty().getAddress());
                    } else {
                        roomMap.put("propertyTitle", "楼栋" + room.getPropertyId());
                        roomMap.put("propertyAddress", "");
                    }
                } catch (Exception e) {
                    logger.warn("获取房间物业信息失败: " + e.getMessage());
                    roomMap.put("propertyTitle", "楼栋" + room.getPropertyId());
                    roomMap.put("propertyAddress", "");
                }
                
                // 添加房间图片信息
                try {
                    List<RoomImage> roomImages = roomImageRepository.findByRoomIdOrderBySortOrder(room.getId());
                    List<String> imageUrls = roomImages.stream()
                            .map(RoomImage::getImageUrl)
                            .collect(Collectors.toList());
                    roomMap.put("imageUrls", imageUrls);
                    
                    // 设置封面图
                    if (!imageUrls.isEmpty()) {
                        roomMap.put("coverImage", imageUrls.get(0));
                    } else {
                        roomMap.put("coverImage", null);
                    }
                } catch (Exception e) {
                    logger.warn("获取房间图片信息失败: " + e.getMessage());
                    roomMap.put("imageUrls", new ArrayList<>());
                    roomMap.put("coverImage", null);
                }
                
                // 添加新字段
                roomMap.put("layout", room.getLayout());
                roomMap.put("deposit", room.getDeposit());
                roomMap.put("paymentMethod", room.getPaymentMethod());
                roomMap.put("facilities", room.getFacilities());
                roomMap.put("description", room.getDescription());
                
                // 查找当前有效合同的租户信息（暂时不支持按状态查询）
                try {
                    List<Contract> contracts = contractRepository.findByRoomId(room.getId());
                    if (!contracts.isEmpty()) {
                        Contract contract = contracts.get(0); // 取第一个合同
                        // 这里需要通过tenant_id查询租户信息，暂时使用合同ID作为占位符
                        roomMap.put("tenantName", "租户" + contract.getTenantId());
                        roomMap.put("tenantId", contract.getTenantId());
                    } else {
                        roomMap.put("tenantName", null);
                        roomMap.put("tenantId", null);
                    }
                } catch (Exception e) {
                    logger.warn("获取房间租户信息失败: " + e.getMessage());
                    roomMap.put("tenantName", null);
                    roomMap.put("tenantId", null);
                }
                
                roomsWithTenants.add(roomMap);
            }
            
            result.put("content", roomsWithTenants);
            result.put("totalElements", roomsPage.getTotalElements());
            result.put("totalPages", roomsPage.getTotalPages());
            result.put("number", roomsPage.getNumber());
            result.put("size", roomsPage.getSize());
            
        } catch (Exception e) {
            logger.error("获取房间列表失败", e);
            result.put("error", "获取房间列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("房间不存在，ID: " + id));
    }

    @Override
    @Transactional
    public Room saveRoom(Room room) {
        try {
            // 验证必填字段
            if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("房间号不能为空");
            }
            
            if (room.getPropertyId() == null) {
                throw new IllegalArgumentException("所属物业不能为空");
            }
            
            if (room.getMonthlyRent() == null || room.getMonthlyRent().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("租金不能为空且不能小于0");
            }
            
            // 设置默认值
            if (room.getStatus() == null || room.getStatus().trim().isEmpty()) {
                room.setStatus("VACANT");
            }
            
            if (room.getPaymentMethod() == null || room.getPaymentMethod().trim().isEmpty()) {
                room.setPaymentMethod("MONTHLY");
            }
            
            if (room.getType() == null || room.getType().trim().isEmpty()) {
                room.setType("STANDARD");
            }
            
            if (room.getLayout() == null || room.getLayout().trim().isEmpty()) {
                room.setLayout("1室1厅");
            }
            
            if (room.getOrientation() == null || room.getOrientation().trim().isEmpty()) {
                room.setOrientation("SOUTH");
            }
            
            if (room.getFloor() == null) {
                room.setFloor(1);
            }
            
            if (room.getArea() == null) {
                room.setArea(BigDecimal.ZERO);
            }
            
            if (room.getDeposit() == null) {
                room.setDeposit(BigDecimal.ZERO);
            }
            
            // 检查房间号是否重复（同一物业下）
            if (room.getId() == null) {
                // 新增房间时检查重复
                List<Room> existingRooms = roomRepository.findByPropertyIdAndRoomNumber(room.getPropertyId(), room.getRoomNumber());
                if (!existingRooms.isEmpty()) {
                    throw new IllegalArgumentException("该物业下已存在相同房间号的房间");
                }
            } else {
                // 更新房间时检查重复（排除自己）
                List<Room> existingRooms = roomRepository.findByPropertyIdAndRoomNumberAndIdNot(
                    room.getPropertyId(), room.getRoomNumber(), room.getId());
                if (!existingRooms.isEmpty()) {
                    throw new IllegalArgumentException("该物业下已存在相同房间号的房间");
                }
            }
            
            return roomRepository.save(room);
        } catch (Exception e) {
            logger.error("保存房间失败: " + e.getMessage(), e);
            throw new RuntimeException("保存房间失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Room updateRoom(Room room) {
        // 验证房间是否存在
        getRoomById(room.getId());
        
        // 保存房间
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        // 验证房间是否存在
        Room room = getRoomById(id);
        
        // 删除房间
        roomRepository.delete(room);
    }

    @Override
    @Transactional
    public Room updateRoomStatus(Long id, String status) {
        // 验证房间是否存在
        Room room = getRoomById(id);
        
        // 更新状态
        room.setStatus(status);
        
        // 保存房间
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public String uploadRoomImage(Long id, MultipartFile file) {
        try {
            // 验证房间是否存在
            Room room = getRoomById(id);
            
            // 创建上传目录
            String uploadDir = uploadBasePath + "/rooms";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成文件名
            String fileName = "room_" + id + "_" + System.currentTimeMillis() + 
                    getFileExtension(file.getOriginalFilename());
            
            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // 生成图片URL
            String imageUrl = "/files/rooms/" + fileName;
            
            // 保存图片信息到数据库
            RoomImage roomImage = new RoomImage();
            roomImage.setRoomId(id);
            roomImage.setImageUrl(imageUrl);
            
            // 检查是否是第一张图片，如果是则设为封面图
            List<RoomImage> existingImages = roomImageRepository.findByRoomIdOrderBySortOrder(id);
            if (existingImages.isEmpty()) {
                roomImage.setIsCover(true);
                roomImage.setSortOrder(1);
            } else {
                roomImage.setIsCover(false);
                roomImage.setSortOrder(existingImages.size() + 1);
            }
            
            roomImageRepository.save(roomImage);
            
            return imageUrl;
        } catch (Exception e) {
            logger.error("上传房间图片失败", e);
            throw new RuntimeException("上传房间图片失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteRoomImage(Long roomId, Long imageId) {
        try {
            // 验证房间是否存在
            getRoomById(roomId);
            
            // 查找图片
            RoomImage roomImage = roomImageRepository.findById(imageId)
                    .orElseThrow(() -> new EntityNotFoundException("图片不存在"));
            
            // 验证图片是否属于该房间
            if (!roomImage.getRoomId().equals(roomId)) {
                throw new IllegalArgumentException("图片不属于该房间");
            }
            
            // 删除文件
            try {
                String fileName = roomImage.getImageUrl().substring(roomImage.getImageUrl().lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadBasePath + "/rooms/" + fileName);
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                logger.warn("删除图片文件失败: " + e.getMessage());
            }
            
            // 如果删除的是封面图，需要重新设置封面图
            boolean wasCover = roomImage.getIsCover();
            
            // 删除数据库记录
            roomImageRepository.delete(roomImage);
            
            // 如果删除的是封面图，将第一张图片设为封面图
            if (wasCover) {
                List<RoomImage> remainingImages = roomImageRepository.findByRoomIdOrderBySortOrder(roomId);
                if (!remainingImages.isEmpty()) {
                    RoomImage newCover = remainingImages.get(0);
                    newCover.setIsCover(true);
                    roomImageRepository.save(newCover);
                }
            }
            
        } catch (Exception e) {
            logger.error("删除房间图片失败", e);
            throw new RuntimeException("删除房间图片失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void setRoomCoverImage(Long roomId, Long imageId) {
        try {
            // 验证房间是否存在
            getRoomById(roomId);
            
            // 查找图片
            RoomImage roomImage = roomImageRepository.findById(imageId)
                    .orElseThrow(() -> new EntityNotFoundException("图片不存在"));
            
            // 验证图片是否属于该房间
            if (!roomImage.getRoomId().equals(roomId)) {
                throw new IllegalArgumentException("图片不属于该房间");
            }
            
            // 取消当前封面图
            RoomImage currentCover = roomImageRepository.findByRoomIdAndIsCover(roomId, true);
            if (currentCover != null) {
                currentCover.setIsCover(false);
                roomImageRepository.save(currentCover);
            }
            
            // 设置新封面图
            roomImage.setIsCover(true);
            roomImageRepository.save(roomImage);
            
        } catch (Exception e) {
            logger.error("设置房间封面图失败", e);
            throw new RuntimeException("设置房间封面图失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRoomImages(Long roomId) {
        try {
            // 验证房间是否存在
            getRoomById(roomId);
            
            List<RoomImage> images = roomImageRepository.findByRoomIdOrderBySortOrder(roomId);
            
            return images.stream().map(image -> {
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("id", image.getId());
                imageMap.put("url", image.getImageUrl());
                imageMap.put("isCover", image.getIsCover());
                imageMap.put("sortOrder", image.getSortOrder());
                return imageMap;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            logger.error("获取房间图片失败", e);
            throw new RuntimeException("获取房间图片失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRoomContracts(Long roomId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证房间是否存在
            getRoomById(roomId);
            
            // 查询房间的所有合同
            List<Contract> contracts = contractRepository.findByRoomId(roomId);
            
            // 按状态分组合同
            Map<String, List<Contract>> contractsByStatus = contracts.stream()
                    .collect(Collectors.groupingBy(Contract::getStatus));
            
            result.put("contracts", contracts);
            result.put("contractsByStatus", contractsByStatus);
            result.put("totalContracts", contracts.size());
            
            // 查询当前有效合同（暂时不支持按状态查询）
            // List<Contract> activeContracts = contractRepository.findActiveContracts(LocalDate.now());
            // activeContracts = activeContracts.stream()
            //         .filter(contract -> contract.getRoomId().equals(roomId))
            //         .collect(Collectors.toList());
            
            result.put("activeContract", contracts.isEmpty() ? null : contracts.get(0));
            
        } catch (Exception e) {
            logger.error("获取房间合同列表失败", e);
            result.put("error", "获取房间合同列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRoomStats(Long roomId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 验证房间是否存在
            Room room = getRoomById(roomId);
            
            // 房间基本信息
            stats.put("roomNumber", room.getRoomNumber());
            stats.put("roomType", room.getType());
            stats.put("roomStatus", room.getStatus());
            stats.put("roomArea", room.getArea());
            
            // 查询当前有效合同（暂时不支持按状态查询）
            List<Contract> contracts = contractRepository.findByRoomId(roomId);
            
            boolean isOccupied = !contracts.isEmpty();
            stats.put("isOccupied", isOccupied);
            
            if (isOccupied) {
                Contract contract = contracts.get(0);
                stats.put("tenantId", contract.getTenantId());
                stats.put("leaseStartDate", contract.getStartDate());
                stats.put("leaseEndDate", contract.getEndDate());
                stats.put("monthlyRent", contract.getMonthlyRent());
            }
            
        } catch (Exception e) {
            logger.error("获取房间统计数据失败", e);
            stats.put("error", "获取房间统计数据失败: " + e.getMessage());
        }
        
                return stats;
    }

    @Override
    @Transactional
    public void batchDeleteRooms(List<Long> ids) {
        try {
            for (Long id : ids) {
                // 验证房间是否存在
                Room room = getRoomById(id);
                // 删除房间
                roomRepository.delete(room);
            }
        } catch (Exception e) {
            logger.error("批量删除房间失败", e);
            throw new RuntimeException("批量删除房间失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void batchUpdateRoomStatus(List<Long> ids, String status) {
        try {
            for (Long id : ids) {
                // 验证房间是否存在
                Room room = getRoomById(id);
                // 更新状态
                room.setStatus(status);
                // 保存房间
                roomRepository.save(room);
            }
        } catch (Exception e) {
            logger.error("批量更新房间状态失败", e);
            throw new RuntimeException("批量更新房间状态失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getAvailableRooms(String keyword, String minPrice, String maxPrice, String roomType, String sortBy, Pageable pageable) {
        try {
            Page<Room> roomsPage;
            
            // 查询可租房源（状态为VACANT的房间）并关联物业信息
            roomsPage = roomRepository.findByStatusWithProperty("VACANT", pageable);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> availableRooms = roomsPage.getContent().stream().map(room -> {
                Map<String, Object> roomData = new HashMap<>();
                roomData.put("id", room.getId());
                roomData.put("title", room.getProperty() != null ? 
                    room.getProperty().getTitle() + " - " + room.getRoomNumber() : 
                    "房间" + room.getRoomNumber());
                roomData.put("address", room.getProperty() != null ? room.getProperty().getAddress() : "");
                roomData.put("area", room.getArea());
                roomData.put("roomType", room.getType());
                roomData.put("monthlyRent", room.getMonthlyRent());
                roomData.put("floor", room.getFloor());
                roomData.put("orientation", room.getOrientation());
                roomData.put("decoration", room.getProperty() != null ? room.getProperty().getFacilities() : "");
                roomData.put("status", "AVAILABLE");
                roomData.put("description", room.getDescription());
                
                // 获取房间的实际图片
                List<RoomImage> roomImages = roomImageRepository.findByRoomIdOrderBySortOrder(room.getId());
                if (!roomImages.isEmpty()) {
                    // 获取所有图片URL
                    List<String> imageUrls = roomImages.stream()
                            .map(RoomImage::getImageUrl)
                            .collect(Collectors.toList());
                    roomData.put("imageUrls", imageUrls);
                    
                    // 优先使用封面图片
                    RoomImage coverImage = roomImages.stream()
                        .filter(RoomImage::getIsCover)
                        .findFirst()
                        .orElse(roomImages.get(0));
                    roomData.put("imageUrl", coverImage.getImageUrl());
                    roomData.put("coverImage", coverImage.getImageUrl());
                } else {
                    roomData.put("imageUrls", new ArrayList<>());
                    roomData.put("imageUrl", null);
                    roomData.put("coverImage", null);
                }
                
                // 获取房东信息
                if (room.getProperty() != null && room.getProperty().getLandlordId() != null) {
                    // 这里可以根据需要查询房东详细信息
                    roomData.put("landlordName", "房东");
                    roomData.put("landlordPhone", "请联系管理员");
                } else {
                    roomData.put("landlordName", null);
                    roomData.put("landlordPhone", null);
                }
                
                roomData.put("createTime", room.getCreateTime());
                roomData.put("propertyId", room.getPropertyId());
                roomData.put("roomNumber", room.getRoomNumber());
                roomData.put("deposit", room.getDeposit());
                roomData.put("facilities", room.getFacilities());
                return roomData;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", availableRooms);
            result.put("totalElements", roomsPage.getTotalElements());
            result.put("totalPages", roomsPage.getTotalPages());
            result.put("size", roomsPage.getSize());
            result.put("number", roomsPage.getNumber());
            
            return result;
        } catch (Exception e) {
            logger.error("获取可租房源失败", e);
            // 返回空结果而不是空Map
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("content", new ArrayList<>());
            emptyResult.put("totalElements", 0L);
            emptyResult.put("totalPages", 0);
            emptyResult.put("size", pageable.getPageSize());
            emptyResult.put("number", pageable.getPageNumber());
            return emptyResult;
        }
    }
     
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRoomTenantInfo(Long roomId) {
        try {
            // 验证房间是否存在
            Room room = getRoomById(roomId);
            
            Map<String, Object> result = new HashMap<>();
            
            // 查找当前有效的租赁合同
            List<Contract> activeContracts = contractRepository.findByRoomIdAndStatus(roomId, "ACTIVE");
            
            if (activeContracts.isEmpty()) {
                result.put("hasTenant", false);
                result.put("message", "该房间暂无租户");
                return result;
            }
            
            // 获取最新的合同
            Contract activeContract = activeContracts.get(0);
            
            result.put("hasTenant", true);
            result.put("contractId", activeContract.getId());
            result.put("tenantId", activeContract.getTenantId());
            result.put("startDate", activeContract.getStartDate());
            result.put("endDate", activeContract.getEndDate());
            result.put("monthlyRent", activeContract.getMonthlyRent());
            result.put("deposit", activeContract.getDepositAmount());
            result.put("contractStatus", activeContract.getStatus());
            
            // 获取租户详细信息（如果需要的话）
            try {
                // 这里可以根据需要查询租户的详细信息
                // User tenant = userRepository.findById(activeContract.getTenantId()).orElse(null);
                // if (tenant != null) {
                //     result.put("tenantName", tenant.getUsername());
                //     result.put("tenantPhone", tenant.getPhone());
                //     result.put("tenantEmail", tenant.getEmail());
                // }
                result.put("tenantName", "租户" + activeContract.getTenantId());
                result.put("tenantPhone", "请联系管理员获取");
            } catch (Exception e) {
                logger.warn("获取租户详细信息失败: " + e.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("获取房间租户信息失败", e);
            throw new RuntimeException("获取租户信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Room> getRoomsByLandlordId(Long landlordId, String status, String keyword, Pageable pageable) {
        try {
            // 根据条件查询房间（暂时不支持关键词搜索，使用现有方法）
            if (status != null && !status.isEmpty()) {
                return roomRepository.findByLandlordIdAndStatus(landlordId, status, pageable);
            } else {
                return roomRepository.findByLandlordId(landlordId, pageable);
            }
        } catch (Exception e) {
            logger.error("获取房东房间列表失败", e);
            throw new RuntimeException("获取房间列表失败: " + e.getMessage());
        }
    }
     
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
} 