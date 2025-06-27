package com.apartment.repository;

import com.apartment.model.LandlordVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房东认证申请数据访问接口
 */
@Repository
public interface LandlordVerificationRepository extends JpaRepository<LandlordVerification, Long> {
    
    /**
     * 根据用户ID查找认证申请
     * @param userId 用户ID
     * @return 认证申请
     */
    Optional<LandlordVerification> findByUserId(Long userId);
    
    /**
     * 根据身份证号查找认证申请
     * @param idCard 身份证号
     * @return 认证申请
     */
    Optional<LandlordVerification> findByIdCard(String idCard);
    
    /**
     * 根据状态查找认证申请列表
     * @param status 申请状态
     * @return 认证申请列表
     */
    List<LandlordVerification> findByStatus(LandlordVerification.VerificationStatus status);
    
    /**
     * 检查用户是否已有认证申请
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByUserId(Long userId);
    
    /**
     * 检查身份证号是否已被使用
     * @param idCard 身份证号
     * @return 是否存在
     */
    boolean existsByIdCard(String idCard);
} 