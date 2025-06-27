package com.apartment.repository;

import com.apartment.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 租客数据访问接口
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    /**
     * 根据用户ID查找租客
     * @param userId 用户ID
     * @return 租客
     */
    Optional<Tenant> findByUserId(Long userId);
    
    /**
     * 根据身份证号查找租客
     * @param idCard 身份证号
     * @return 租客
     */
    Optional<Tenant> findByIdCard(String idCard);
    
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