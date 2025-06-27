package com.apartment.service;

import com.apartment.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 房东服务接口
 */
public interface LandlordService {
    
    /**
     * 申请房东认证（新版本 - 直接操作landlords表）
     * @param userId 用户ID
     * @param realName 真实姓名
     * @param idCard 身份证号
     * @param idCardFrontImageUrl 身份证正面照片URL
     * @param idCardBackImageUrl 身份证背面照片URL
     * @param bankCard 银行卡号
     * @param bankName 银行名称
     * @param contactPhone 联系电话
     * @param contactEmail 联系邮箱
     * @return 房东信息
     */
    Landlord applyForVerification(Long userId, String realName, String idCard,
                                String idCardFrontImageUrl, String idCardBackImageUrl,
                                String bankCard, String bankName, String contactPhone, String contactEmail);
    
    /**
     * 根据用户ID获取房东信息
     * @param userId 用户ID
     * @return 房东信息
     */
    Landlord getLandlordByUserId(Long userId);
    
    /**
     * 管理员审核房东认证
     * @param landlordId 房东ID
     * @param verified 是否通过认证
     * @return 更新后的房东信息
     */
    Landlord verifyLandlord(Long landlordId, Boolean verified);
    
    /**
     * 申请房东认证（旧版本 - 兼容性保留）
     * @param userId 用户ID
     * @param realName 真实姓名
     * @param idCard 身份证号
     * @param idCardFrontImageUrl 身份证正面照片URL
     * @param idCardBackImageUrl 身份证背面照片URL
     * @param bankCard 银行卡号
     * @param bankName 银行名称
     * @param contactPhone 联系电话
     * @param contactEmail 联系邮箱
     * @return 认证申请
     */
    LandlordVerification applyVerification(Long userId, String realName, String idCard,
                                         String idCardFrontImageUrl, String idCardBackImageUrl,
                                         String bankCard, String bankName, String contactPhone, String contactEmail);
    
    /**
     * 获取房东认证状态（旧版本 - 兼容性保留）
     * @param userId 用户ID
     * @return 认证申请
     */
    LandlordVerification getVerificationStatus(Long userId);
    
    /**
     * 获取房东信息
     * @param userId 用户ID
     * @return 房东信息
     */
    Landlord getLandlordInfo(Long userId);
    
    /**
     * 更新房东信息
     * @param userId 用户ID
     * @param contactPhone 联系电话
     * @param contactEmail 联系邮箱
     * @param bankCard 银行卡号
     * @param bankName 银行名称
     * @return 更新后的房东信息
     */
    Landlord updateLandlordInfo(Long userId, String contactPhone, String contactEmail, String bankCard, String bankName);
    
    /**
     * 获取房东评价列表
     * @param landlordId 房东ID
     * @param rating 评分过滤
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 评价分页结果
     */
    Page<LandlordRating> getLandlordRatings(Long landlordId, Integer rating, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * 回复评价
     * @param ratingId 评价ID
     * @param landlordId 房东ID
     * @param content 回复内容
     * @return 更新后的评价
     */
    LandlordRating replyToRating(Long ratingId, Long landlordId, String content);
    
    /**
     * 获取房东收入统计
     * @param landlordId 房东ID
     * @param year 年份
     * @param month 月份
     * @return 收入统计
     */
    Map<String, Object> getIncomeStats(Long landlordId, Integer year, Integer month);
    
    /**
     * 获取房东收入明细
     * @param landlordId 房东ID
     * @param type 收入类型
     * @param propertyId 房产ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 收入明细分页结果
     */
    Page<LandlordIncome> getIncomeDetails(Long landlordId, LandlordIncome.IncomeType type, Long propertyId,
                                        LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * 获取房东待办事项
     * @param landlordId 房东ID
     * @param type 待办类型
     * @param pageable 分页参数
     * @return 待办事项分页结果
     */
    Page<LandlordTodo> getTodos(Long landlordId, LandlordTodo.TodoType type, Pageable pageable);
    
    /**
     * 处理待办事项
     * @param todoId 待办事项ID
     * @param landlordId 房东ID
     * @param status 新状态
     * @param comment 处理备注
     * @return 更新后的待办事项
     */
    LandlordTodo processTodo(Long todoId, Long landlordId, LandlordTodo.TodoStatus status, String comment);
    
    /**
     * 根据用户ID获取房东ID
     * @param userId 用户ID
     * @return 房东ID
     */
    Long getLandlordIdByUserId(Long userId);
    
    /**
     * 检查用户是否为房东
     * @param userId 用户ID
     * @return 是否为房东
     */
    boolean isLandlord(Long userId);
    
    /**
     * 获取房东房产统计
     * @param landlordId 房东ID
     * @return 房产统计信息
     */
    Map<String, Object> getPropertyStats(Long landlordId);
    
    /**
     * 创建租房申请待办事项
     * @param landlordId 房东ID
     * @param applicationId 申请ID
     * @param title 标题
     * @param description 描述
     * @return 创建的待办事项
     */
    LandlordTodo createRoomApplicationTodo(Long landlordId, Long applicationId, String title, String description);
    
    /**
     * 更新房东银行信息
     * @param userId 用户ID
     * @param bankCard 银行卡号
     * @param bankName 银行名称
     * @return 更新后的房东信息
     */
    Landlord updateLandlordBankInfo(Long userId, String bankCard, String bankName);
    
    /**
     * 获取所有房东列表（管理员用）
     * @param realName 真实姓名过滤
     * @param phone 电话过滤
     * @param verified 认证状态过滤
     * @param pageable 分页参数
     * @return 房东分页结果
     */
    Page<Landlord> getAllLandlords(String realName, String phone, Boolean verified, Pageable pageable);
    
    /**
     * 删除房东
     * @param landlordId 房东ID
     */
    void deleteLandlord(Long landlordId);
} 