package com.apartment.repository;

import com.apartment.model.LandlordIncome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 房东收入数据访问接口
 */
@Repository
public interface LandlordIncomeRepository extends JpaRepository<LandlordIncome, Long> {
    
    /**
     * 根据房东ID分页查询收入记录
     * @param landlordId 房东ID
     * @param pageable 分页参数
     * @return 收入记录分页结果
     */
    Page<LandlordIncome> findByLandlordId(Long landlordId, Pageable pageable);
    
    /**
     * 根据房东ID和收入类型查询收入记录
     * @param landlordId 房东ID
     * @param type 收入类型
     * @param pageable 分页参数
     * @return 收入记录分页结果
     */
    Page<LandlordIncome> findByLandlordIdAndType(Long landlordId, LandlordIncome.IncomeType type, Pageable pageable);
    
    /**
     * 根据房东ID和房产ID查询收入记录
     * @param landlordId 房东ID
     * @param propertyId 房产ID
     * @param pageable 分页参数
     * @return 收入记录分页结果
     */
    Page<LandlordIncome> findByLandlordIdAndPropertyId(Long landlordId, Long propertyId, Pageable pageable);
    
    /**
     * 根据房东ID和时间范围查询收入记录
     * @param landlordId 房东ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 收入记录分页结果
     */
    @Query("SELECT i FROM LandlordIncome i WHERE i.landlordId = :landlordId " +
           "AND i.paymentTime >= :startDate AND i.paymentTime <= :endDate")
    Page<LandlordIncome> findByLandlordIdAndPaymentTimeBetween(
            @Param("landlordId") Long landlordId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * 根据房东ID计算总收入
     * @param landlordId 房东ID
     * @return 总收入
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM LandlordIncome i WHERE i.landlordId = :landlordId")
    BigDecimal sumTotalIncomeByLandlordId(@Param("landlordId") Long landlordId);
    
    /**
     * 根据房东ID和收入类型计算收入
     * @param landlordId 房东ID
     * @param type 收入类型
     * @return 收入金额
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM LandlordIncome i WHERE i.landlordId = :landlordId AND i.type = :type")
    BigDecimal sumIncomeByLandlordIdAndType(@Param("landlordId") Long landlordId, @Param("type") LandlordIncome.IncomeType type);
    
    /**
     * 根据房东ID和年份统计月度收入
     * @param landlordId 房东ID
     * @param year 年份
     * @return 月度收入统计
     */
    @Query("SELECT MONTH(i.paymentTime), COALESCE(SUM(i.amount), 0) " +
           "FROM LandlordIncome i WHERE i.landlordId = :landlordId " +
           "AND YEAR(i.paymentTime) = :year GROUP BY MONTH(i.paymentTime)")
    List<Object[]> findMonthlyIncomeByLandlordIdAndYear(@Param("landlordId") Long landlordId, @Param("year") Integer year);
    
    /**
     * 根据房东ID、年份和月份计算收入
     * @param landlordId 房东ID
     * @param year 年份
     * @param month 月份
     * @return 收入金额
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM LandlordIncome i WHERE i.landlordId = :landlordId " +
           "AND YEAR(i.paymentTime) = :year AND MONTH(i.paymentTime) = :month")
    BigDecimal sumIncomeByLandlordIdAndYearAndMonth(
            @Param("landlordId") Long landlordId,
            @Param("year") Integer year,
            @Param("month") Integer month);
} 