package com.apartment.repository;

import com.apartment.model.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 房东数据访问接口
 */
@Repository
public interface LandlordRepository extends JpaRepository<Landlord, Long>, JpaSpecificationExecutor<Landlord> {
    
    /**
     * 根据用户ID查找房东
     * @param userId 用户ID
     * @return 房东
     */
    Optional<Landlord> findByUserId(Long userId);
    
    /**
     * 根据身份证号查找房东
     * @param idCard 身份证号
     * @return 房东
     */
    Optional<Landlord> findByIdCard(String idCard);
    
    /**
     * 根据认证状态查找房东
     * @param verified 是否认证
     * @return 房东列表
     */
    List<Landlord> findByVerified(Boolean verified);
    
    /**
     * 根据评分范围查找房东
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @return 房东列表
     */
    List<Landlord> findByRatingBetween(BigDecimal minRating, BigDecimal maxRating);
    
    /**
     * 检查用户ID是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByUserId(Long userId);
    
    /**
     * 检查身份证号是否存在
     * @param idCard 身份证号
     * @return 是否存在
     */
    boolean existsByIdCard(String idCard);
} 