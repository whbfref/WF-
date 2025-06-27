package com.apartment.service;

import com.apartment.model.UtilityBill;

import java.util.List;
import java.util.Map;

/**
 * 物业费账单服务接口
 */
public interface UtilityBillService {

    /**
     * 获取所有物业费账单（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param status 状态筛选
     * @param type 类型筛选
     * @param month 月份筛选
     * @return 账单列表和分页信息
     */
    Map<String, Object> getAllBills(int page, int size, String status, String type, String month);

    /**
     * 根据ID获取物业费账单
     * @param id 账单ID
     * @return 账单对象
     */
    UtilityBill getBillById(Long id);

    /**
     * 保存物业费账单
     * @param bill 账单对象
     * @return 保存后的账单对象
     */
    UtilityBill saveBill(UtilityBill bill);

    /**
     * 更新物业费账单
     * @param bill 账单对象
     * @return 更新后的账单对象
     */
    UtilityBill updateBill(UtilityBill bill);

    /**
     * 删除物业费账单
     * @param id 账单ID
     */
    void deleteBill(Long id);

    /**
     * 缴费
     * @param id 账单ID
     * @return 缴费后的账单对象
     */
    UtilityBill payBill(Long id);

    /**
     * 批量缴费
     * @param ids 账单ID列表
     */
    void batchPayBills(List<Long> ids);

    /**
     * 批量删除物业费账单
     * @param ids 账单ID列表
     */
    void batchDeleteBills(List<Long> ids);

    /**
     * 获取物业费账单统计数据
     * @return 统计数据
     */
    Map<String, Object> getBillStats();

    /**
     * 抄表录入
     * @param data 抄表数据
     * @return 录入结果
     */
    Map<String, Object> recordMeterReading(Map<String, Object> data);

    /**
     * 获取用户物业费账单
     * @param userId 用户ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 用户物业费账单
     */
    Object getUserUtilityBills(Long userId, String status, org.springframework.data.domain.Pageable pageable);

    /**
     * 支付物业费账单
     * @param billId 账单ID
     * @param userId 用户ID
     * @param paymentMethod 支付方式
     */
    void payUtilityBill(Long billId, Long userId, String paymentMethod);

    /**
     * 统计用户待缴费物业费账单数量
     * @param userId 用户ID
     * @return 待缴费账单数量
     */
    long countUserPendingBills(Long userId);
} 