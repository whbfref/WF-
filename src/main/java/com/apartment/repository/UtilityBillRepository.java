package com.apartment.repository;

import com.apartment.model.UtilityBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物业费账单数据访问接口
 */
@Repository
public interface UtilityBillRepository extends JpaRepository<UtilityBill, Long> {
    
    /**
     * 根据用户ID查找账单
     * @param userId 用户ID
     * @return 账单列表
     */
    List<UtilityBill> findByUserId(Long userId);
    
    /**
     * 根据房产ID查找账单
     * @param propertyId 房产ID
     * @return 账单列表
     */
    List<UtilityBill> findByPropertyId(Long propertyId);
    
    /**
     * 根据房东ID查找账单
     * @param landlordId 房东ID
     * @return 账单列表
     */
    List<UtilityBill> findByLandlordId(Long landlordId);
    
    /**
     * 根据账单类型和用户ID查找账单
     * @param type 账单类型
     * @param userId 用户ID
     * @return 账单列表
     */
    List<UtilityBill> findByTypeAndUserId(Integer type, Long userId);
    
    /**
     * 根据账单月份和用户ID查找账单
     * @param month 账单月份
     * @param userId 用户ID
     * @return 账单列表
     */
    List<UtilityBill> findByMonthAndUserId(String month, Long userId);
    
    /**
     * 根据状态和用户ID查找账单
     * @param status 状态
     * @param userId 用户ID
     * @return 账单列表
     */
    List<UtilityBill> findByStatusAndUserId(Integer status, Long userId);
    
    /**
     * 根据账单编号查找账单
     * @param billNo 账单编号
     * @return 账单
     */
    UtilityBill findByBillNo(String billNo);
    
    /**
     * 统计用户某月的账单总额
     * @param userId 用户ID
     * @param month 账单月份
     * @return 账单总额
     */
    @Query("SELECT SUM(u.amount) FROM UtilityBill u WHERE u.userId = ?1 AND u.month = ?2")
    BigDecimal sumAmountByUserIdAndMonth(Long userId, String month);
    
    /**
     * 根据状态查找账单
     */
    Page<UtilityBill> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据类型查找账单
     */
    Page<UtilityBill> findByType(Integer type, Pageable pageable);
    
    /**
     * 根据状态和类型查找账单
     */
    Page<UtilityBill> findByStatusAndType(Integer status, Integer type, Pageable pageable);
    
    /**
     * 根据账单月份查找账单
     */
    @Query("SELECT b FROM UtilityBill b WHERE b.month = :month")
    Page<UtilityBill> findByMonth(@Param("month") String month, Pageable pageable);
    
    /**
     * 根据状态和月份查找账单
     */
    @Query("SELECT b FROM UtilityBill b WHERE b.status = :status AND b.month = :month")
    Page<UtilityBill> findByStatusAndMonth(@Param("status") Integer status, @Param("month") String month, Pageable pageable);
    
    /**
     * 根据类型和月份查找账单
     */
    @Query("SELECT b FROM UtilityBill b WHERE b.type = :type AND b.month = :month")
    Page<UtilityBill> findByTypeAndMonth(@Param("type") Integer type, @Param("month") String month, Pageable pageable);
    
    /**
     * 根据状态、类型和月份查找账单
     */
    @Query("SELECT b FROM UtilityBill b WHERE b.status = :status AND b.type = :type AND b.month = :month")
    Page<UtilityBill> findByStatusAndTypeAndMonth(@Param("status") Integer status, @Param("type") Integer type, @Param("month") String month, Pageable pageable);
    
    /**
     * 根据月份查找账单列表
     */
    @Query("SELECT b FROM UtilityBill b WHERE b.month = :month")
    List<UtilityBill> findByMonth(@Param("month") String month);
    
    /**
     * 统计各状态账单数量
     */
    @Query("SELECT b.status, COUNT(b) FROM UtilityBill b GROUP BY b.status")
    List<Object[]> countByStatus();
    
    /**
     * 统计各类型账单数量
     */
    @Query("SELECT b.type, COUNT(b) FROM UtilityBill b GROUP BY b.type")
    List<Object[]> countByType();
    
    /**
     * 统计总金额
     */
    @Query("SELECT SUM(u.amount) FROM UtilityBill u")
    BigDecimal sumTotalAmount();
    
    /**
     * 根据状态统计金额
     */
    @Query("SELECT SUM(u.amount) FROM UtilityBill u WHERE u.status = ?1")
    BigDecimal sumAmountByStatus(Integer status);
    
    /**
     * 根据月份统计账单数量
     */
    long countByMonth(String month);
    
    /**
     * 根据状态和月份统计账单数量
     */
    long countByStatusAndMonth(Integer status, String month);
    
    /**
     * 根据状态和月份统计金额
     */
    @Query("SELECT SUM(u.amount) FROM UtilityBill u WHERE u.status = ?1 AND u.month = ?2")
    BigDecimal sumAmountByStatusAndMonth(Integer status, String month);
    
    /**
     * 根据状态和类型统计账单数量
     */
    long countByStatusAndType(Integer status, Integer type);
    
    /**
     * 根据用户ID查找账单（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 账单分页列表
     */
    Page<UtilityBill> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据状态和用户ID查找账单（分页）
     * @param status 状态
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 账单分页列表
     */
    Page<UtilityBill> findByStatusAndUserId(Integer status, Long userId, Pageable pageable);
    
    /**
     * 根据状态和用户ID统计账单数量
     * @param status 状态
     * @param userId 用户ID
     * @return 账单数量
     */
    long countByStatusAndUserId(Integer status, Long userId);
} 