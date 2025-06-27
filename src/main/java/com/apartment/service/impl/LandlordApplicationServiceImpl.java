package com.apartment.service.impl;

import com.apartment.model.LandlordApplication;
import com.apartment.repository.LandlordApplicationRepository;
import com.apartment.service.LandlordApplicationService;
import com.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 申请成为房东服务实现类
 */
@Service
public class LandlordApplicationServiceImpl implements LandlordApplicationService {

    @Autowired
    private LandlordApplicationRepository applicationRepository;
    
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public LandlordApplication submitApplication(Long userId, LandlordApplication application) {
        // 检查用户是否已有待审核的申请
        if (hasUserPendingApplication(userId)) {
            throw new IllegalStateException("您已有待审核的申请，请勿重复提交");
        }
        
        application.setUserId(userId);
        application.setStatus("PENDING");
        return applicationRepository.save(application);
    }

    @Override
    public Optional<LandlordApplication> getUserApplication(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    @Override
    public Page<LandlordApplication> getAllApplications(String status, Pageable pageable) {
        if (StringUtils.hasText(status)) {
            return applicationRepository.findByStatusOrderByCreateTimeDesc(status, pageable);
        } else {
            return applicationRepository.findAllByOrderByCreateTimeDesc(pageable);
        }
    }

    @Override
    public LandlordApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("申请不存在, ID: " + id));
    }

    @Override
    @Transactional
    public LandlordApplication reviewApplication(Long id, String status, String reviewRemarks, Long reviewerId) {
        LandlordApplication application = getApplicationById(id);
        
        if (!"PENDING".equals(application.getStatus())) {
            throw new IllegalStateException("只能审核待审核状态的申请");
        }
        
        application.setStatus(status);
        application.setReviewRemarks(reviewRemarks);
        application.setReviewTime(LocalDateTime.now());
        application.setReviewerId(reviewerId);
        
        // 如果申请通过，更新用户角色为房东
        if ("APPROVED".equals(status)) {
            userService.updateUserRole(application.getUserId(), "LANDLORD");
        }
        
        return applicationRepository.save(application);
    }

    @Override
    public boolean hasUserPendingApplication(Long userId) {
        return applicationRepository.existsByUserIdAndStatus(userId, "PENDING");
    }

    @Override
    public long countApplicationsByStatus(String status) {
        return applicationRepository.countByStatus(status);
    }
} 