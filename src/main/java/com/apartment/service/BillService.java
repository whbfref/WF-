package com.apartment.service;

import com.apartment.model.Bill;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 账单服务接口
 */
public interface BillService {

    /**
     * 获取所有账单（分页、筛选）
     * @param page 页码
     * @param size 每页数量
     * @param status 状态筛选
     * @param type 类型筛选
     * @param month 月份筛选
     * @return 账单列表和分页信息
     */
    Map<String, Object> getAllBills(int page, int size, String status, String type, String month);

    /**
     * 根据ID获取账单
     * @param id 账单ID
     * @return 账单对象
     */
    Bill getBillById(Long id);

    /**
     * 保存账单
     * @param bill 账单对象
     * @return 保存后的账单对象
     */
    Bill saveBill(Bill bill);

    /**
     * 更新账单
     * @param bill 账单对象
     * @return 更新后的账单对象
     */
    Bill updateBill(Bill bill);

    /**
     * 删除账单
     * @param id 账单ID
     */
    void deleteBill(Long id);

    /**
     * 支付账单
     * @param id 账单ID
     * @return 支付后的账单对象
     */
    Bill payBill(Long id);

    /**
     * 批量支付账单
     * @param ids 账单ID列表
     */
    void batchPayBills(List<Long> ids);

    /**
     * 批量删除账单
     * @param ids 账单ID列表
     */
    void batchDeleteBills(List<Long> ids);

    /**
     * 获取账单统计数据
     * @param month 月份
     * @param type 类型
     * @return 统计数据
     */
    Map<String, Object> getBillStats(String month, String type);

    /**
     * 获取月度账单汇总
     * @param month 月份
     * @return 月度汇总数据
     */
    Map<String, Object> getMonthlyBillSummary(String month);

    /**
     * 生成账单
     * @param month 月份
     * @return 生成的账单列表
     */
    List<Bill> generateBills(String month);

    /**
     * 获取用户账单
     * @param userId 用户ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 用户账单
     */
    Object getUserBills(Long userId, String status, org.springframework.data.domain.Pageable pageable);

    /**
     * 支付账单
     * @param billId 账单ID
     * @param userId 用户ID
     * @param paymentMethod 支付方式
     */
    void payBill(Long billId, Long userId, String paymentMethod);

    /**
     * 统计用户待缴费账单数量
     * @param userId 用户ID
     * @return 待缴费账单数量
     */
    long countUserPendingBills(Long userId);

    /**
     * 获取用户最近账单
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近账单列表
     */
    List<Map<String, Object>> getUserRecentBills(Long userId, int limit);
} 