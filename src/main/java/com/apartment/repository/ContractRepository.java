package com.apartment.repository;

import com.apartment.model.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 合同数据访问接口
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
    /**
     * 查询租户的所有合同
     * @param tenantId 租户ID
     * @return 合同列表
     */
    List<Contract> findByTenantId(Long tenantId);
    
    /**
     * 分页查询租户的所有合同
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 合同分页列表
     */
    Page<Contract> findByTenantId(Long tenantId, Pageable pageable);
    
    /**
     * 查询房间的所有合同
     * @param roomId 房间ID
     * @return 合同列表
     */
    List<Contract> findByRoomId(Long roomId);
    
    /**
     * 分页查询房间的所有合同
     * @param roomId 房间ID
     * @param pageable 分页参数
     * @return 合同分页列表
     */
    Page<Contract> findByRoomId(Long roomId, Pageable pageable);
    
    /**
     * 查询房间的指定状态合同
     * @param roomId 房间ID
     * @param status 合同状态
     * @return 合同列表
     */
    List<Contract> findByRoomIdAndStatus(Long roomId, String status);
    
    /**
     * 分页查询房间的指定状态合同
     * @param roomId 房间ID
     * @param status 合同状态
     * @param pageable 分页参数
     * @return 合同分页列表
     */
    Page<Contract> findByRoomIdAndStatus(Long roomId, String status, Pageable pageable);
    
    /**
     * 分页查询指定状态的合同
     * @param status 合同状态
     * @param pageable 分页参数
     * @return 合同分页列表
     */
    Page<Contract> findByStatus(String status, Pageable pageable);
    
    /**
     * 统计指定状态的合同数量
     * @param status 合同状态
     * @return 合同数量
     */
    long countByStatus(String status);
    
    /**
     * 分页查询租户的指定状态合同
     * @param tenantId 租户ID
     * @param status 合同状态
     * @param pageable 分页参数
     * @return 合同分页列表
     */
    Page<Contract> findByTenantIdAndStatus(Long tenantId, String status, Pageable pageable);
    
    /**
     * 查询当前生效的合同
     * @param currentDate 当前日期
     * @return 合同列表
     */
    @Query("SELECT c FROM Contract c WHERE c.status = 'ACTIVE' AND c.startDate <= ?1 AND c.endDate >= ?1")
    List<Contract> findActiveContracts(LocalDate currentDate);
    
    /**
     * 统计活跃合同数量
     * @return 活跃合同数量
     */
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.status = 'ACTIVE' AND c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE")
    long countActiveContracts();
    
    /**
     * 查询即将到期的活跃合同
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 合同列表
     */
    @Query("SELECT c FROM Contract c WHERE c.status = 'ACTIVE' AND c.endDate BETWEEN ?1 AND ?2")
    List<Contract> findContractsAboutToExpire(LocalDate startDate, LocalDate endDate);
} 