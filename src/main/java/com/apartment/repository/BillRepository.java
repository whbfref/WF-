package com.apartment.repository;

import com.apartment.model.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 账单数据访问接口
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    /**
     * 查询已支付账单的总金额
     * @return 总金额
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b WHERE b.status = 'PAID'")
    BigDecimal sumPaidAmount();

    @Query("SELECT NEW map(DATE_FORMAT(b.dueDate, '%Y-%m') as month, SUM(b.amount) as amount) " +
           "FROM Bill b " +
           "WHERE b.status = 'PAID' " +
           "GROUP BY DATE_FORMAT(b.dueDate, '%Y-%m') " +
           "ORDER BY month DESC")
    List<Map<String, Object>> getMonthlyRentData();

    /**
     * 根据状态查找账单
     */
    Page<Bill> findByStatus(String status, Pageable pageable);

    /**
     * 根据类型查找账单
     */
    Page<Bill> findByBillType(String billType, Pageable pageable);

    /**
     * 根据状态和类型查找账单
     */
    Page<Bill> findByStatusAndBillType(String status, String billType, Pageable pageable);

    /**
     * 根据账单月份查找账单
     */
    @Query("SELECT b FROM Bill b WHERE b.billMonth = :month")
    Page<Bill> findByBillMonth(@Param("month") String month, Pageable pageable);

    /**
     * 根据状态和月份查找账单
     */
    @Query("SELECT b FROM Bill b WHERE b.status = :status AND b.billMonth = :month")
    Page<Bill> findByStatusAndBillMonth(@Param("status") String status, @Param("month") String month, Pageable pageable);

    /**
     * 根据类型和月份查找账单
     */
    @Query("SELECT b FROM Bill b WHERE b.billType = :billType AND b.billMonth = :month")
    Page<Bill> findByBillTypeAndBillMonth(@Param("billType") String billType, @Param("month") String month, Pageable pageable);

    /**
     * 根据状态、类型和月份查找账单
     */
    @Query("SELECT b FROM Bill b WHERE b.status = :status AND b.billType = :billType AND b.billMonth = :month")
    Page<Bill> findByStatusAndBillTypeAndBillMonth(@Param("status") String status, @Param("billType") String billType, @Param("month") String month, Pageable pageable);

    /**
     * 根据月份查找账单列表
     */
    @Query("SELECT b FROM Bill b WHERE b.billMonth = :month")
    List<Bill> findByBillMonth(@Param("month") String month);

    /**
     * 统计各状态账单数量
     */
    @Query("SELECT b.status, COUNT(b) FROM Bill b GROUP BY b.status")
    List<Object[]> countByStatus();

    /**
     * 统计各类型账单数量
     */
    @Query("SELECT b.billType, COUNT(b) FROM Bill b GROUP BY b.billType")
    List<Object[]> countByBillType();

    /**
     * 根据合同ID查找账单
     */
    List<Bill> findByContractId(Long contractId);

    /**
     * 根据合同ID列表查找账单
     */
    Page<Bill> findByContractIdIn(List<Long> contractIds, Pageable pageable);
    
    /**
     * 根据合同ID列表和状态查找账单
     */
    Page<Bill> findByContractIdInAndStatus(List<Long> contractIds, String status, Pageable pageable);
    
    /**
     * 根据合同ID列表查找账单
     */
    List<Bill> findByContractIdIn(List<Long> contractIds);
    
    /**
     * 根据合同ID列表和状态统计账单数量
     */
    long countByContractIdInAndStatus(List<Long> contractIds, String status);
    
    /**
     * 根据房东ID计算已支付账单总金额
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b " +
           "JOIN Contract c ON b.contractId = c.id " +
           "WHERE c.landlordId = :landlordId AND b.status = 'PAID'")
    BigDecimal sumPaidAmountByLandlordId(@Param("landlordId") Long landlordId);
    
    /**
     * 根据房东ID和账单类型计算已支付账单金额
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b " +
           "JOIN Contract c ON b.contractId = c.id " +
           "WHERE c.landlordId = :landlordId AND b.billType = :billType AND b.status = 'PAID'")
    BigDecimal sumPaidAmountByLandlordIdAndBillType(@Param("landlordId") Long landlordId, @Param("billType") String billType);
    
    /**
     * 根据房东ID计算未支付账单总金额
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b " +
           "JOIN Contract c ON b.contractId = c.id " +
           "WHERE c.landlordId = :landlordId AND b.status IN ('UNPAID', 'OVERDUE')")
    BigDecimal sumUnpaidAmountByLandlordId(@Param("landlordId") Long landlordId);
    
    /**
     * 根据房东ID和年份统计月度收入
     */
    @Query("SELECT MONTH(b.paidDate), COALESCE(SUM(b.amount), 0) " +
           "FROM Bill b JOIN Contract c ON b.contractId = c.id " +
           "WHERE c.landlordId = :landlordId AND YEAR(b.paidDate) = :year AND b.status = 'PAID' " +
           "GROUP BY MONTH(b.paidDate)")
    List<Object[]> findMonthlyIncomeByLandlordIdAndYear(@Param("landlordId") Long landlordId, @Param("year") Integer year);
    
    /**
     * 根据房东ID、年份和月份计算收入
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Bill b " +
           "JOIN Contract c ON b.contractId = c.id " +
           "WHERE c.landlordId = :landlordId AND YEAR(b.paidDate) = :year AND MONTH(b.paidDate) = :month AND b.status = 'PAID'")
    BigDecimal sumPaidAmountByLandlordIdAndYearAndMonth(
            @Param("landlordId") Long landlordId,
            @Param("year") Integer year,
            @Param("month") Integer month);
    
    /**
     * 根据房东ID查询已支付的账单明细（用于收入明细）
     */
    @Query("SELECT b FROM Bill b " +
           "JOIN Contract c ON b.contractId = c.id " +
           "WHERE c.landlordId = :landlordId AND b.status = 'PAID' " +
           "AND (:billType IS NULL OR b.billType = :billType) " +
           "AND (:roomId IS NULL OR c.roomId = :roomId) " +
           "AND (:startDate IS NULL OR b.paidDate >= :startDate) " +
           "AND (:endDate IS NULL OR b.paidDate <= :endDate) " +
           "ORDER BY b.paidDate DESC")
    Page<Bill> findPaidBillsByLandlordId(
            @Param("landlordId") Long landlordId,
            @Param("billType") String billType,
            @Param("roomId") Long roomId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            Pageable pageable);
} 