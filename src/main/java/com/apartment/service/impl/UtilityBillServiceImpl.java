package com.apartment.service.impl;

import com.apartment.model.UtilityBill;
import com.apartment.model.User;
import com.apartment.model.Property;
import com.apartment.repository.UtilityBillRepository;
import com.apartment.repository.UserRepository;
import com.apartment.repository.PropertyRepository;
import com.apartment.service.UtilityBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 物业费账单服务实现类
 */
@Service
public class UtilityBillServiceImpl implements UtilityBillService {
    private static final Logger logger = LoggerFactory.getLogger(UtilityBillServiceImpl.class);

    @Autowired
    private UtilityBillRepository utilityBillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllBills(int page, int size, String status, String type, String month) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<UtilityBill> billsPage;
            
            // 转换参数
            Integer statusInt = status != null && !status.isEmpty() ? Integer.valueOf(status) : null;
            Integer typeInt = type != null && !type.isEmpty() ? Integer.valueOf(type) : null;
            
            // 根据条件筛选
            if (statusInt != null && typeInt != null && month != null && !month.isEmpty()) {
                billsPage = utilityBillRepository.findByStatusAndTypeAndMonth(statusInt, typeInt, month, pageable);
            } else if (statusInt != null && typeInt != null) {
                billsPage = utilityBillRepository.findByStatusAndType(statusInt, typeInt, pageable);
            } else if (statusInt != null && month != null && !month.isEmpty()) {
                billsPage = utilityBillRepository.findByStatusAndMonth(statusInt, month, pageable);
            } else if (typeInt != null && month != null && !month.isEmpty()) {
                billsPage = utilityBillRepository.findByTypeAndMonth(typeInt, month, pageable);
            } else if (statusInt != null) {
                billsPage = utilityBillRepository.findByStatus(statusInt, pageable);
            } else if (typeInt != null) {
                billsPage = utilityBillRepository.findByType(typeInt, pageable);
            } else if (month != null && !month.isEmpty()) {
                billsPage = utilityBillRepository.findByMonth(month, pageable);
            } else {
                billsPage = utilityBillRepository.findAll(pageable);
            }
            
            // 转换为包含详细信息的Map列表
            List<Map<String, Object>> billDetailList = billsPage.getContent().stream()
                    .map(this::convertBillToDetailMap)
                    .collect(java.util.stream.Collectors.toList());
            
            result.put("content", billDetailList);
            result.put("totalElements", billsPage.getTotalElements());
            result.put("totalPages", billsPage.getTotalPages());
            result.put("currentPage", billsPage.getNumber());
            result.put("size", billsPage.getSize());
            
        } catch (Exception e) {
            logger.error("获取物业费账单列表失败", e);
            result.put("error", "获取物业费账单列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public UtilityBill getBillById(Long id) {
        return utilityBillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("物业费账单不存在，ID: " + id));
    }

    @Override
    @Transactional
    public UtilityBill saveBill(UtilityBill bill) {
        // 生成账单编号
        if (bill.getBillNo() == null || bill.getBillNo().isEmpty()) {
            bill.setBillNo(generateBillNumber());
        }
        return utilityBillRepository.save(bill);
    }

    @Override
    @Transactional
    public UtilityBill updateBill(UtilityBill bill) {
        // 验证账单是否存在
        getBillById(bill.getId());
        return utilityBillRepository.save(bill);
    }

    @Override
    @Transactional
    public void deleteBill(Long id) {
        UtilityBill bill = getBillById(id);
        utilityBillRepository.delete(bill);
    }

    @Override
    @Transactional
    public UtilityBill payBill(Long id) {
        UtilityBill bill = getBillById(id);
        bill.setStatus(1); // 已缴费
        bill.setPaymentTime(LocalDateTime.now());
        return utilityBillRepository.save(bill);
    }

    @Override
    @Transactional
    public void batchPayBills(List<Long> ids) {
        for (Long id : ids) {
            try {
                UtilityBill bill = utilityBillRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("物业费账单不存在，ID: " + id));
                bill.setStatus(1); // 已缴费
                bill.setPaymentTime(LocalDateTime.now());
                utilityBillRepository.save(bill);
            } catch (Exception e) {
                logger.error("批量缴费失败，账单ID: " + id, e);
                throw new RuntimeException("批量缴费失败，账单ID: " + id + ", 错误: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void batchDeleteBills(List<Long> ids) {
        for (Long id : ids) {
            try {
                UtilityBill bill = utilityBillRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("物业费账单不存在，ID: " + id));
                utilityBillRepository.delete(bill);
            } catch (Exception e) {
                logger.error("批量删除物业费账单失败，账单ID: " + id, e);
                throw new RuntimeException("批量删除物业费账单失败，账单ID: " + id + ", 错误: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBillStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Object[]> statusStats = utilityBillRepository.countByStatus();
            List<Object[]> typeStats = utilityBillRepository.countByType();
            
            // 处理状态统计
            Map<Integer, Long> statusMap = new HashMap<>();
            for (Object[] stat : statusStats) {
                statusMap.put((Integer) stat[0], (Long) stat[1]);
            }
            
            // 处理类型统计
            Map<Integer, Long> typeMap = new HashMap<>();
            for (Object[] stat : typeStats) {
                typeMap.put((Integer) stat[0], (Long) stat[1]);
            }
            
            // 按类型统计待缴费账单
            stats.put("electricityUnpaid", getUnpaidCountByType(1)); // 电费
            stats.put("waterUnpaid", getUnpaidCountByType(2)); // 水费
            stats.put("gasUnpaid", getUnpaidCountByType(3)); // 燃气费
            stats.put("totalRevenue", calculateTotalRevenue());
            stats.put("statusStats", statusMap);
            stats.put("typeStats", typeMap);
            
        } catch (Exception e) {
            logger.error("获取物业费账单统计失败", e);
            stats.put("error", "获取物业费账单统计失败: " + e.getMessage());
        }
        
        return stats;
    }

    @Override
    @Transactional
    public Map<String, Object> recordMeterReading(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // TODO: 实现抄表录入逻辑
            result.put("success", true);
            result.put("message", "抄表录入成功");
            
        } catch (Exception e) {
            logger.error("抄表录入失败", e);
            result.put("success", false);
            result.put("message", "抄表录入失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成账单编号
     */
    private String generateBillNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "UTL" + timestamp;
    }

    /**
     * 计算总收入
     */
    private BigDecimal calculateTotalRevenue() {
        try {
            BigDecimal totalRevenue = utilityBillRepository.sumAmountByStatus(1); // 已缴费
            return totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("计算总收入失败", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 根据类型获取待缴费账单数量
     */
    private long getUnpaidCountByType(Integer type) {
        try {
            return utilityBillRepository.countByStatusAndType(0, type);
        } catch (Exception e) {
            logger.error("获取待缴费账单数量失败", e);
            return 0;
        }
    }
    
    /**
     * 转换账单为包含详细信息的Map
     */
    private Map<String, Object> convertBillToDetailMap(UtilityBill bill) {
        Map<String, Object> billMap = new HashMap<>();
        
        // 基本账单信息
        billMap.put("id", bill.getId());
        billMap.put("billNo", bill.getBillNo());
        billMap.put("userId", bill.getUserId());
        billMap.put("propertyId", bill.getPropertyId());
        billMap.put("landlordId", bill.getLandlordId());
        billMap.put("type", bill.getType());
        billMap.put("month", bill.getMonth());
        billMap.put("lastReading", bill.getLastReading());
        billMap.put("currentReading", bill.getCurrentReading());
        billMap.put("consumption", bill.getConsumption());
        billMap.put("price", bill.getPrice());
        billMap.put("amount", bill.getAmount());
        billMap.put("status", bill.getStatus());
        billMap.put("paymentTime", bill.getPaymentTime());
        billMap.put("readingTime", bill.getReadingTime());
        billMap.put("remark", bill.getRemark());
        billMap.put("createTime", bill.getCreateTime());
        billMap.put("updateTime", bill.getUpdateTime());
        
        return billMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserUtilityBills(Long userId, String status, Pageable pageable) {
        try {
            Page<UtilityBill> billsPage;
            if (status != null && !status.isEmpty()) {
                Integer statusInt = Integer.valueOf(status);
                billsPage = utilityBillRepository.findByStatusAndUserId(statusInt, userId, pageable);
            } else {
                billsPage = utilityBillRepository.findByUserId(userId, pageable);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", billsPage.getContent());
            result.put("totalElements", billsPage.getTotalElements());
            result.put("totalPages", billsPage.getTotalPages());
            result.put("size", billsPage.getSize());
            result.put("number", billsPage.getNumber());
            
            return result;
        } catch (Exception e) {
            logger.error("获取用户物业费账单失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public void payUtilityBill(Long billId, Long userId, String paymentMethod) {
        try {
            UtilityBill bill = getBillById(billId);
            bill.setStatus(1); // 已缴费
            bill.setPaymentTime(LocalDateTime.now());
            utilityBillRepository.save(bill);
        } catch (Exception e) {
            logger.error("支付物业费账单失败", e);
            throw new RuntimeException("支付物业费账单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserPendingBills(Long userId) {
        try {
            return utilityBillRepository.countByStatusAndUserId(0, userId);
        } catch (Exception e) {
            logger.error("统计用户待缴费物业费账单数量失败", e);
            return 0;
        }
    }
} 