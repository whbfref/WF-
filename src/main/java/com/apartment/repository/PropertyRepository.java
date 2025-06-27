package com.apartment.repository;

import com.apartment.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 房产数据访问接口
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    /**
     * 根据房东ID查找房产
     * @param landlordId 房东ID
     * @return 房产列表
     */
    List<Property> findByLandlordId(Long landlordId);
    
    /**
     * 根据房产状态查找房产
     * @param status 房产状态
     * @return 房产列表
     */
    List<Property> findByStatus(Integer status);
    
    /**
     * 根据房产状态查找房产（分页）
     * @param status 房产状态
     * @param pageable 分页参数
     * @return 房产分页列表
     */
    Page<Property> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据城市和状态查找房产
     * @param city 城市
     * @param status 房产状态
     * @return 房产列表
     */
    List<Property> findByCityAndStatus(String city, Integer status);
    
    /**
     * 根据月租金范围和状态查找房产
     * @param minRent 最低月租金
     * @param maxRent 最高月租金
     * @param status 房产状态
     * @return 房产列表
     */
    List<Property> findByMonthlyRentBetweenAndStatus(BigDecimal minRent, BigDecimal maxRent, Integer status);
    
    /**
     * 根据区域和月租金范围查找房产
     * @param city 城市
     * @param district 区县
     * @param minRent 最低月租金
     * @param maxRent 最高月租金
     * @param status 房产状态
     * @return 房产列表
     */
    @Query("SELECT p FROM Property p WHERE p.city = ?1 AND p.district = ?2 AND p.monthlyRent BETWEEN ?3 AND ?4 AND p.status = ?5")
    List<Property> findPropertiesByCityAndDistrictAndRentRange(String city, String district, BigDecimal minRent, BigDecimal maxRent, Integer status);
    
    /**
     * 根据名称或地址模糊查询房产（分页）
     * @param name 名称关键字
     * @param address 地址关键字
     * @param pageable 分页参数
     * @return 房产分页列表
     */
    Page<Property> findByTitleContainingOrAddressContaining(String name, String address, Pageable pageable);
    
    /**
     * 根据状态和名称或地址模糊查询房产（分页）
     * @param status 状态
     * @param name 名称关键字
     * @param address 地址关键字
     * @param pageable 分页参数
     * @return 房产分页列表
     */
    Page<Property> findByStatusAndTitleContainingOrAddressContaining(Integer status, String name, String address, Pageable pageable);
} 