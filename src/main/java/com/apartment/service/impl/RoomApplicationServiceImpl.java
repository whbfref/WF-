package com.apartment.service.impl;

import com.apartment.api.dto.RoomApplicationResponse;
import com.apartment.model.Room;
import com.apartment.model.RoomApplication;
import com.apartment.model.RoomImage;
import com.apartment.model.User;
import com.apartment.model.Property;
import com.apartment.repository.RoomApplicationRepository;
import com.apartment.repository.PropertyRepository;
import com.apartment.service.RoomApplicationService;
import com.apartment.service.RoomService;
import com.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 租房申请服务实现类
 */
@Service
public class RoomApplicationServiceImpl implements RoomApplicationService {

    @Autowired
    private RoomApplicationRepository applicationRepository;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    @Transactional
    public RoomApplication submitApplication(Long userId, RoomApplication application) {
        // 检查用户是否已申请过该房间
        if (hasUserAppliedForRoom(userId, application.getRoomId())) {
            throw new IllegalStateException("您已申请过该房间，请勿重复申请");
        }
        
        application.setUserId(userId);
        application.setStatus("PENDING");
        return applicationRepository.save(application);
    }

    @Override
    public Page<RoomApplication> getUserApplications(Long userId, String status, Pageable pageable) {
        Page<RoomApplication> applicationPage;
        if (StringUtils.hasText(status)) {
            applicationPage = applicationRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status, pageable);
        } else {
            applicationPage = applicationRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        }
        
        // 手动填充房间信息
        List<RoomApplication> enrichedApplications = applicationPage.getContent().stream()
                .map(this::enrichApplicationWithRoomInfo)
                .collect(Collectors.toList());
        
        return new PageImpl<>(enrichedApplications, pageable, applicationPage.getTotalElements());
    }

    /**
     * 获取用户申请列表（包含房间详细信息）
     */
    public Page<RoomApplicationResponse> getUserApplicationsWithRoomInfo(Long userId, String status, Pageable pageable) {
        Page<RoomApplication> applicationPage;
        if (StringUtils.hasText(status)) {
            applicationPage = applicationRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, status, pageable);
        } else {
            applicationPage = applicationRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        }
        
        // 转换为DTO并填充房间信息
        List<RoomApplicationResponse> responseList = applicationPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responseList, pageable, applicationPage.getTotalElements());
    }

    /**
     * 转换申请实体为响应DTO
     */
    private RoomApplicationResponse convertToResponseDTO(RoomApplication application) {
        RoomApplicationResponse response = new RoomApplicationResponse();
        
        // 复制申请基本信息
        response.setId(application.getId());
        response.setUserId(application.getUserId());
        response.setRoomId(application.getRoomId());
        response.setStatus(application.getStatus());
        response.setExpectedMoveInDate(application.getExpectedMoveInDate());
        response.setLeaseDuration(application.getLeaseDuration());
        response.setRemarks(application.getRemarks());
        response.setReviewRemarks(application.getReviewRemarks());
        response.setReviewTime(application.getReviewTime());
        response.setReviewerId(application.getReviewerId());
        response.setCreateTime(application.getCreateTime());
        response.setUpdateTime(application.getUpdateTime());
        
        // 填充房间信息
        try {
            Room room = roomService.getRoomById(application.getRoomId());
            if (room != null) {
                response.setRoomNumber(room.getRoomNumber());
                response.setRoomTitle(room.getRoomNumber()); // 使用房间号作为标题
                response.setRoomDescription(room.getDescription());
                response.setMonthlyRent(room.getMonthlyRent());
                response.setRoomArea(room.getArea() != null ? room.getArea().doubleValue() : null);
                response.setRoomType(room.getType());
                response.setRoomStatus(room.getStatus());
                
                // 设置房间地址（从物业信息获取）
                if (room.getProperty() != null) {
                    response.setRoomAddress(room.getProperty().getAddress());
                    response.setPropertyName(room.getProperty().getTitle());
                    response.setPropertyAddress(room.getProperty().getAddress());
                }
                
                // 设置房间图片
                List<RoomImage> images = room.getImages();
                if (images != null && !images.isEmpty()) {
                    response.setRoomImageUrl(images.get(0).getImageUrl()); // 第一张作为封面
                    response.setImageUrls(images.stream()
                            .map(RoomImage::getImageUrl)
                            .collect(Collectors.toList()));
                }
            }
        } catch (Exception e) {
            System.err.println("获取房间信息失败: " + e.getMessage());
        }
        
        // 填充用户信息
        try {
            Optional<User> userOpt = userService.findById(application.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.setUsername(user.getUsername());
                response.setUserPhone(user.getPhone());
                response.setUserEmail(user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("获取用户信息失败: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * 为申请信息填充房间详细信息
     */
    private RoomApplication enrichApplicationWithRoomInfo(RoomApplication application) {
        try {
            Room room = roomService.getRoomById(application.getRoomId());
            if (room != null) {
                // 创建一个新的申请对象，避免修改原对象
                RoomApplication enrichedApplication = new RoomApplication();
                enrichedApplication.setId(application.getId());
                enrichedApplication.setUserId(application.getUserId());
                enrichedApplication.setRoomId(application.getRoomId());
                enrichedApplication.setStatus(application.getStatus());
                enrichedApplication.setExpectedMoveInDate(application.getExpectedMoveInDate());
                enrichedApplication.setLeaseDuration(application.getLeaseDuration());
                enrichedApplication.setRemarks(application.getRemarks());
                enrichedApplication.setReviewRemarks(application.getReviewRemarks());
                enrichedApplication.setReviewTime(application.getReviewTime());
                enrichedApplication.setReviewerId(application.getReviewerId());
                enrichedApplication.setCreateTime(application.getCreateTime());
                enrichedApplication.setUpdateTime(application.getUpdateTime());
                
                // 添加房间信息作为临时属性（通过反射或者扩展字段）
                // 这里我们需要创建一个包含房间信息的DTO
                return application; // 暂时返回原对象，稍后创建DTO
            }
        } catch (Exception e) {
            // 如果获取房间信息失败，返回原申请信息
            System.err.println("获取房间信息失败: " + e.getMessage());
        }
        return application;
    }

    @Override
    public Page<RoomApplication> getAllApplications(String status, Pageable pageable) {
        if (StringUtils.hasText(status)) {
            return applicationRepository.findByStatusOrderByCreateTimeDesc(status, pageable);
        } else {
            return applicationRepository.findAll(pageable);
        }
    }

    @Override
    public Page<RoomApplication> getApplicationsByRoomId(Long roomId, Pageable pageable) {
        return applicationRepository.findByRoomIdOrderByCreateTimeDesc(roomId, pageable);
    }

    @Override
    public RoomApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("申请不存在, ID: " + id));
    }

    @Override
    @Transactional
    public RoomApplication reviewApplication(Long id, String status, String reviewRemarks, Long reviewerId) {
        RoomApplication application = getApplicationById(id);
        
        if (!"PENDING".equals(application.getStatus())) {
            throw new IllegalStateException("只能审核待审核状态的申请");
        }
        
        application.setStatus(status);
        application.setReviewRemarks(reviewRemarks);
        application.setReviewTime(LocalDateTime.now());
        application.setReviewerId(reviewerId);
        
        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public RoomApplication cancelApplication(Long id, Long userId) {
        RoomApplication application = getApplicationById(id);
        
        if (!application.getUserId().equals(userId)) {
            throw new IllegalStateException("只能取消自己的申请");
        }
        
        if (!"PENDING".equals(application.getStatus())) {
            throw new IllegalStateException("只能取消待审核状态的申请");
        }
        
        application.setStatus("CANCELLED");
        return applicationRepository.save(application);
    }

    @Override
    public boolean hasUserAppliedForRoom(Long userId, Long roomId) {
        return applicationRepository.existsByUserIdAndRoomIdAndStatusIn(userId, roomId);
    }

    @Override
    public long countUserApplications(Long userId) {
        return applicationRepository.countByUserId(userId);
    }

    @Override
    public long countUserApplicationsByStatus(Long userId, String status) {
        return applicationRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public List<RoomApplication> getUserRecentApplications(Long userId) {
        return applicationRepository.findTop5ByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public Long getLandlordIdByRoomId(Long roomId) {
        try {
            Room room = roomService.getRoomById(roomId);
            if (room != null && room.getPropertyId() != null) {
                // 通过Property ID获取所属物业信息
                Property property = propertyRepository.findById(room.getPropertyId()).orElse(null);
                if (property != null) {
                    return property.getLandlordId();
                }
            }
        } catch (Exception e) {
            System.err.println("获取房东ID失败: " + e.getMessage());
        }
        return null;
    }
} 