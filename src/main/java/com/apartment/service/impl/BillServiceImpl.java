package com.apartment.service.impl;

import com.apartment.model.Bill;
import com.apartment.model.Contract;
import com.apartment.model.Property;
import com.apartment.model.Room;
import com.apartment.model.User;
import com.apartment.repository.BillRepository;
import com.apartment.repository.ContractRepository;
import com.apartment.repository.PropertyRepository;
import com.apartment.repository.RoomRepository;
import com.apartment.repository.UserRepository;
import com.apartment.service.BillService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Optional;

/**
 * 账单服务实现类
 */
@Service
public class BillServiceImpl implements BillService {
    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private com.apartment.repository.TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllBills(int page, int size, String status, String type, String month) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<Bill> billsPage;
            
            // 根据条件筛选
            if (status != null && !status.isEmpty() && type != null && !type.isEmpty() && month != null && !month.isEmpty()) {
                billsPage = billRepository.findByStatusAndBillTypeAndBillMonth(status, type, month, pageable);
            } else if (status != null && !status.isEmpty() && type != null && !type.isEmpty()) {
                billsPage = billRepository.findByStatusAndBillType(status, type, pageable);
            } else if (status != null && !status.isEmpty() && month != null && !month.isEmpty()) {
                billsPage = billRepository.findByStatusAndBillMonth(status, month, pageable);
            } else if (type != null && !type.isEmpty() && month != null && !month.isEmpty()) {
                billsPage = billRepository.findByBillTypeAndBillMonth(type, month, pageable);
            } else if (status != null && !status.isEmpty()) {
                billsPage = billRepository.findByStatus(status, pageable);
            } else if (type != null && !type.isEmpty()) {
                billsPage = billRepository.findByBillType(type, pageable);
            } else if (month != null && !month.isEmpty()) {
                billsPage = billRepository.findByBillMonth(month, pageable);
            } else {
                billsPage = billRepository.findAll(pageable);
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
            logger.error("获取账单列表失败", e);
            result.put("error", "获取账单列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("账单不存在，ID: " + id));
    }

    @Override
    @Transactional
    public Bill saveBill(Bill bill) {
        // 生成账单编号
        if (bill.getBillNumber() == null || bill.getBillNumber().isEmpty()) {
            bill.setBillNumber(generateBillNumber());
        }
        return billRepository.save(bill);
    }

    @Override
    @Transactional
    public Bill updateBill(Bill bill) {
        // 验证账单是否存在
        getBillById(bill.getId());
        return billRepository.save(bill);
    }

    @Override
    @Transactional
    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        billRepository.delete(bill);
    }

    @Override
    @Transactional
    public Bill payBill(Long id) {
        Bill bill = getBillById(id);
        bill.setStatus("PAID");
        bill.setPaidDate(LocalDate.now());
        return billRepository.save(bill);
    }

    @Override
    @Transactional
    public void batchPayBills(List<Long> ids) {
        for (Long id : ids) {
            try {
                Bill bill = billRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("账单不存在，ID: " + id));
                bill.setStatus("PAID");
                bill.setPaidDate(LocalDate.now());
                billRepository.save(bill);
            } catch (Exception e) {
                logger.error("批量支付账单失败，账单ID: " + id, e);
                throw new RuntimeException("批量支付账单失败，账单ID: " + id + ", 错误: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void batchDeleteBills(List<Long> ids) {
        for (Long id : ids) {
            try {
                Bill bill = billRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("账单不存在，ID: " + id));
                billRepository.delete(bill);
            } catch (Exception e) {
                logger.error("批量删除账单失败，账单ID: " + id, e);
                throw new RuntimeException("批量删除账单失败，账单ID: " + id + ", 错误: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBillStats(String month, String type) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Object[]> statusStats = billRepository.countByStatus();
            List<Object[]> typeStats = billRepository.countByBillType();
            
            // 处理状态统计
            Map<String, Long> statusMap = new HashMap<>();
            for (Object[] stat : statusStats) {
                statusMap.put((String) stat[0], (Long) stat[1]);
            }
            
            // 处理类型统计
            Map<String, Long> typeMap = new HashMap<>();
            for (Object[] stat : typeStats) {
                typeMap.put((String) stat[0], (Long) stat[1]);
            }
            
            stats.put("unpaidCount", statusMap.getOrDefault("UNPAID", 0L));
            stats.put("paidCount", statusMap.getOrDefault("PAID", 0L));
            stats.put("overdueCount", statusMap.getOrDefault("OVERDUE", 0L));
            stats.put("totalRevenue", calculateTotalRevenue());
            stats.put("statusStats", statusMap);
            stats.put("typeStats", typeMap);
            
        } catch (Exception e) {
            logger.error("获取账单统计失败", e);
            stats.put("error", "获取账单统计失败: " + e.getMessage());
        }
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMonthlyBillSummary(String month) {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            List<Bill> monthlyBills = billRepository.findByBillMonth(month);
            
            BigDecimal totalAmount = monthlyBills.stream()
                    .map(Bill::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            long paidCount = monthlyBills.stream()
                    .mapToLong(bill -> "PAID".equals(bill.getStatus()) ? 1 : 0)
                    .sum();
            
            long unpaidCount = monthlyBills.size() - paidCount;
            
            summary.put("month", month);
            summary.put("totalBills", monthlyBills.size());
            summary.put("paidBills", paidCount);
            summary.put("unpaidBills", unpaidCount);
            summary.put("totalAmount", totalAmount);
            
        } catch (Exception e) {
            logger.error("获取月度账单汇总失败", e);
            summary.put("error", "获取月度账单汇总失败: " + e.getMessage());
        }
        
        return summary;
    }

    @Override
    @Transactional
    public List<Bill> generateBills(String month) {
        // 这里可以实现自动生成账单的逻辑
        // 暂时返回空列表，实际应用中需要根据合同数据生成
        return Collections.emptyList();
    }

    /**
     * 生成账单编号
     */
    private String generateBillNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "BILL" + timestamp;
    }

    /**
     * 计算总收入
     */
    private BigDecimal calculateTotalRevenue() {
        try {
            // 使用数据库聚合查询而不是在内存中计算
            BigDecimal totalRevenue = billRepository.sumPaidAmount();
            return totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("计算总收入失败", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 转换账单为包含详细信息的Map
     */
    private Map<String, Object> convertBillToDetailMap(Bill bill) {
        Map<String, Object> billMap = new HashMap<>();
        
        // 基本账单信息
        billMap.put("id", bill.getId());
        billMap.put("billNumber", bill.getBillNumber());
        billMap.put("billType", bill.getBillType());
        billMap.put("amount", bill.getAmount());
        billMap.put("billMonth", bill.getBillMonth());
        billMap.put("dueDate", bill.getDueDate());
        billMap.put("paidDate", bill.getPaidDate());
        billMap.put("paymentMethod", bill.getPaymentMethod());
        billMap.put("status", bill.getStatus());
        billMap.put("remark", bill.getRemark());
        billMap.put("createdAt", bill.getCreateTime());
        billMap.put("updatedAt", bill.getUpdateTime());
        
        // 简化关联信息，避免在事务中进行复杂查询
        billMap.put("contractId", bill.getContractId());
        billMap.put("contractNumber", bill.getContractId() != null ? "合同" + bill.getContractId() : "");
        billMap.put("tenantName", "租户" + (bill.getContractId() != null ? bill.getContractId() : "未知"));
        billMap.put("roomNumber", "房间" + (bill.getContractId() != null ? bill.getContractId() : "未知"));
        billMap.put("tenantPhone", "");
        billMap.put("tenantId", bill.getContractId());
        billMap.put("roomId", bill.getContractId());
        
        return billMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserBills(Long userId, String status, Pageable pageable) {
        try {
            // 首先通过userId查找tenant记录
            Optional<com.apartment.model.Tenant> tenantOpt = tenantRepository.findByUserId(userId);
            if (!tenantOpt.isPresent()) {
                // 如果用户没有tenant记录，返回空结果
                Map<String, Object> result = new HashMap<>();
                result.put("content", new ArrayList<>());
                result.put("totalElements", 0);
                result.put("totalPages", 0);
                result.put("size", pageable.getPageSize());
                result.put("number", pageable.getPageNumber());
                return result;
            }
            
            // 获取tenant ID
            Long tenantId = tenantOpt.get().getId();
            
            // 然后通过tenantId查询用户的所有合同
            List<Contract> userContracts = contractRepository.findByTenantId(tenantId);
            
            if (userContracts.isEmpty()) {
                // 如果用户没有合同，返回空结果
                Map<String, Object> result = new HashMap<>();
                result.put("content", new ArrayList<>());
                result.put("totalElements", 0);
                result.put("totalPages", 0);
                result.put("size", pageable.getPageSize());
                result.put("number", pageable.getPageNumber());
                return result;
            }
            
            // 获取合同ID列表
            List<Long> contractIds = userContracts.stream()
                    .map(Contract::getId)
                    .collect(java.util.stream.Collectors.toList());
            
            // 根据合同ID查询账单
            Page<Bill> billsPage;
            if (status != null && !status.isEmpty()) {
                billsPage = billRepository.findByContractIdInAndStatus(contractIds, status, pageable);
            } else {
                billsPage = billRepository.findByContractIdIn(contractIds, pageable);
            }
            
            // 转换为包含详细信息的Map列表
            List<Map<String, Object>> billDetailList = billsPage.getContent().stream()
                    .map(bill -> convertBillToUserDetailMap(bill, userContracts))
                    .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", billDetailList);
            result.put("totalElements", billsPage.getTotalElements());
            result.put("totalPages", billsPage.getTotalPages());
            result.put("size", billsPage.getSize());
            result.put("number", billsPage.getNumber());
            
            return result;
        } catch (Exception e) {
            logger.error("获取用户账单失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("content", new ArrayList<>());
            result.put("totalElements", 0);
            result.put("totalPages", 0);
            result.put("size", pageable.getPageSize());
            result.put("number", pageable.getPageNumber());
            return result;
        }
    }
    
    /**
     * 转换账单为用户详细信息的Map
     */
    private Map<String, Object> convertBillToUserDetailMap(Bill bill, List<Contract> userContracts) {
        Map<String, Object> billMap = new HashMap<>();
        
        // 基本账单信息
        billMap.put("id", bill.getId());
        billMap.put("billNumber", bill.getBillNumber());
        billMap.put("billType", bill.getBillType());
        billMap.put("amount", bill.getAmount());
        billMap.put("billMonth", bill.getBillMonth());
        billMap.put("dueDate", bill.getDueDate());
        billMap.put("paidDate", bill.getPaidDate());
        billMap.put("paymentMethod", bill.getPaymentMethod());
        billMap.put("status", bill.getStatus());
        billMap.put("remark", bill.getRemark());
        billMap.put("createTime", bill.getCreateTime());
        billMap.put("updateTime", bill.getUpdateTime());
        
        // 查找对应的合同信息
        Contract contract = userContracts.stream()
                .filter(c -> c.getId().equals(bill.getContractId()))
                .findFirst()
                .orElse(null);
        
        if (contract != null) {
            billMap.put("contractId", contract.getId());
            billMap.put("contractNumber", contract.getContractNumber());
            
            // 获取房间信息
            try {
                Room room = roomRepository.findById(contract.getRoomId()).orElse(null);
                if (room != null) {
                    billMap.put("roomId", room.getId());
                    billMap.put("roomNumber", room.getRoomNumber());
                    billMap.put("roomTitle", room.getRoomNumber());
                    
                    // 获取房产信息作为地址
                    try {
                        Property property = propertyRepository.findById(room.getPropertyId()).orElse(null);
                        if (property != null) {
                            billMap.put("roomAddress", property.getAddress());
                        } else {
                            billMap.put("roomAddress", "地址信息不可用");
                        }
                    } catch (Exception e) {
                        billMap.put("roomAddress", "地址信息不可用");
                    }
                } else {
                    billMap.put("roomId", contract.getRoomId());
                    billMap.put("roomNumber", "房间信息不可用");
                    billMap.put("roomTitle", "房间信息不可用");
                    billMap.put("roomAddress", "地址信息不可用");
                }
            } catch (Exception e) {
                billMap.put("roomId", contract.getRoomId());
                billMap.put("roomNumber", "房间信息不可用");
                billMap.put("roomTitle", "房间信息不可用");
                billMap.put("roomAddress", "地址信息不可用");
            }
            
            // 获取房东信息
            try {
                User landlord = userRepository.findById(contract.getLandlordId()).orElse(null);
                if (landlord != null) {
                    billMap.put("landlordId", landlord.getId());
                    billMap.put("landlordName", landlord.getUsername());
                    billMap.put("landlordPhone", landlord.getPhone());
                } else {
                    billMap.put("landlordId", contract.getLandlordId());
                    billMap.put("landlordName", "房东信息不可用");
                    billMap.put("landlordPhone", "联系方式不可用");
                }
            } catch (Exception e) {
                billMap.put("landlordId", contract.getLandlordId());
                billMap.put("landlordName", "房东信息不可用");
                billMap.put("landlordPhone", "联系方式不可用");
            }
        } else {
            billMap.put("contractId", bill.getContractId());
            billMap.put("contractNumber", "合同信息不可用");
            billMap.put("roomId", null);
            billMap.put("roomNumber", "房间信息不可用");
            billMap.put("roomTitle", "房间信息不可用");
            billMap.put("roomAddress", "地址信息不可用");
            billMap.put("landlordId", null);
            billMap.put("landlordName", "房东信息不可用");
            billMap.put("landlordPhone", "联系方式不可用");
        }
        
        return billMap;
    }

    @Override
    @Transactional
    public void payBill(Long billId, Long userId, String paymentMethod) {
        try {
            Bill bill = getBillById(billId);
            bill.setStatus("PAID");
            bill.setPaidDate(LocalDate.now());
            bill.setPaymentMethod(paymentMethod);
            billRepository.save(bill);
        } catch (Exception e) {
            logger.error("支付账单失败", e);
            throw new RuntimeException("支付账单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserPendingBills(Long userId) {
        try {
            // 首先通过userId查找tenant记录
            Optional<com.apartment.model.Tenant> tenantOpt = tenantRepository.findByUserId(userId);
            if (!tenantOpt.isPresent()) {
                return 0;
            }
            
            // 获取tenant ID
            Long tenantId = tenantOpt.get().getId();
            
            // 然后通过tenantId查询用户的所有合同
            List<Contract> userContracts = contractRepository.findByTenantId(tenantId);
            
            if (userContracts.isEmpty()) {
                return 0;
            }
            
            // 获取合同ID列表
            List<Long> contractIds = userContracts.stream()
                    .map(Contract::getId)
                    .collect(java.util.stream.Collectors.toList());
            
            // 统计待缴费账单数量（状态为UNPAID）
            return billRepository.countByContractIdInAndStatus(contractIds, "UNPAID");
        } catch (Exception e) {
            logger.error("统计用户待缴费账单数量失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserRecentBills(Long userId, int limit) {
        try {
            // 首先通过userId查找tenant记录
            Optional<com.apartment.model.Tenant> tenantOpt = tenantRepository.findByUserId(userId);
            if (!tenantOpt.isPresent()) {
                return new java.util.ArrayList<>();
            }
            
            // 获取tenant ID
            Long tenantId = tenantOpt.get().getId();
            
            // 然后通过tenantId查询用户的所有合同
            List<Contract> userContracts = contractRepository.findByTenantId(tenantId);
            
            if (userContracts.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            
            // 获取合同ID列表
            List<Long> contractIds = userContracts.stream()
                    .map(Contract::getId)
                    .collect(java.util.stream.Collectors.toList());
            
            // 查询最近的账单，按创建时间倒序排列
            org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(0, limit, 
                    org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "createTime"));
            
            org.springframework.data.domain.Page<Bill> billsPage = 
                billRepository.findByContractIdIn(contractIds, pageable);
            
            // 转换为前端需要的格式
            return billsPage.getContent().stream()
                    .map(bill -> {
                        Map<String, Object> billMap = new HashMap<>();
                        billMap.put("id", bill.getId());
                        billMap.put("billNumber", bill.getBillNumber());
                        billMap.put("type", "RENT"); // 租金账单类型
                        billMap.put("amount", bill.getAmount());
                        billMap.put("period", bill.getBillMonth());
                        billMap.put("status", bill.getStatus());
                        billMap.put("dueDate", bill.getDueDate());
                        billMap.put("createTime", bill.getCreateTime());
                        return billMap;
                    })
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取用户最近账单失败", e);
            return new java.util.ArrayList<>();
        }
    }
} 