package com.apartment.repository;

import com.apartment.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 押金数据访问接口
 */
@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    
    /**
     * 根据用户ID查找押金
     * @param userId 用户ID
     * @return 押金列表
     */
    List<Deposit> findByUserId(Long userId);
    
    /**
     * 根据房产ID查找押金
     * @param propertyId 房产ID
     * @return 押金列表
     */
    List<Deposit> findByPropertyId(Long propertyId);
    
    /**
     * 根据房东ID查找押金
     * @param landlordId 房东ID
     * @return 押金列表
     */
    List<Deposit> findByLandlordId(Long landlordId);
    
    /**
     * 根据租约ID查找押金
     * @param leaseId 租约ID
     * @return 押金
     */
    Optional<Deposit> findByLeaseId(Long leaseId);
    
    /**
     * 根据用户ID和房产ID查找押金
     * @param userId 用户ID
     * @param propertyId 房产ID
     * @return 押金
     */
    Optional<Deposit> findByUserIdAndPropertyId(Long userId, Long propertyId);
    
    /**
     * 根据状态查找押金
     * @param status 状态
     * @return 押金列表
     */
    List<Deposit> findByStatus(Integer status);
    
    /**
     * 根据用户ID和状态查找押金
     * @param userId 用户ID
     * @param status 状态
     * @return 押金列表
     */
    List<Deposit> findByUserIdAndStatus(Long userId, Integer status);
} 