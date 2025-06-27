package com.apartment.service.impl;

import com.apartment.model.Contract;
import com.apartment.model.Property;
import com.apartment.model.Room;
import com.apartment.model.Tenant;
import com.apartment.model.User;
import com.apartment.repository.ContractRepository;
import com.apartment.repository.PropertyRepository;
import com.apartment.repository.RoomRepository;
import com.apartment.repository.TenantRepository;
import com.apartment.repository.UserRepository;
import com.apartment.service.ContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 合同服务实现类
 */
@Service
public class ContractServiceImpl implements ContractService {
    private static final Logger logger = LoggerFactory.getLogger(ContractServiceImpl.class);

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Value("${app.uploads.base-path:uploads}")
    private String uploadBasePath;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllContracts(int page, int size, String status, Long tenantId, Long roomId, String keyword) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<Contract> contractsPage;
            
            // 创建动态条件查询
            if (status != null && !status.isEmpty() && tenantId != null) {
                contractsPage = contractRepository.findByTenantIdAndStatus(tenantId, status, pageable);
            } else if (status != null && !status.isEmpty() && roomId != null) {
                contractsPage = contractRepository.findByRoomIdAndStatus(roomId, status, pageable);
            } else if (status != null && !status.isEmpty()) {
                contractsPage = contractRepository.findByStatus(status, pageable);
            } else if (tenantId != null) {
                contractsPage = contractRepository.findByTenantId(tenantId, pageable);
            } else if (roomId != null) {
                contractsPage = contractRepository.findByRoomId(roomId, pageable);
            } else {
                contractsPage = contractRepository.findAll(pageable);
            }
            
            List<Contract> contracts = contractsPage.getContent();
            
            // 处理分页信息
            Map<String, Object> pageInfo = new HashMap<>();
            pageInfo.put("currentPage", contractsPage.getNumber());
            pageInfo.put("totalPages", contractsPage.getTotalPages());
            pageInfo.put("totalElements", contractsPage.getTotalElements());
            pageInfo.put("size", contractsPage.getSize());
            
            result.put("contracts", contracts);
            result.put("pageInfo", pageInfo);
            
        } catch (Exception e) {
            logger.error("获取合同列表失败", e);
            result.put("error", "获取合同列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("合同不存在，ID: " + id));
    }

    @Override
    @Transactional
    public Contract saveContract(Contract contract) {
        // 验证合同数据
        validateContract(contract);
        
        // 生成合同编号
        if (contract.getContractNumber() == null || contract.getContractNumber().isEmpty()) {
            contract.setContractNumber(generateContractNumber());
        }
        
        // 设置合同状态
        if (contract.getStatus() == null || contract.getStatus().isEmpty()) {
            contract.setStatus("ACTIVE");
        }
        
        // 设置签订日期
        if (contract.getSignedDate() == null) {
            contract.setSignedDate(LocalDate.now());
        }
        
        // 保存合同
        Contract savedContract = contractRepository.save(contract);
        
        // 更新房间状态为"已租"
        updateRoomStatus(contract.getRoomId(), "RENTED");
        
        return savedContract;
    }

    @Override
    @Transactional
    public Contract updateContract(Contract contract) {
        // 验证合同是否存在
        Contract existingContract = getContractById(contract.getId());
        
        // 验证合同数据
        validateContract(contract);
        
        // 保留原始合同号
        contract.setContractNumber(existingContract.getContractNumber());
        
        // 保存合同
        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public Contract terminateContract(Long id, String reason) {
        // 获取合同
        Contract contract = getContractById(id);
        
        // 设置终止信息
        contract.setStatus("TERMINATED");
        contract.setTerminationDate(LocalDate.now());
        contract.setTerminationReason(reason);
        
        // 保存合同
        Contract terminatedContract = contractRepository.save(contract);
        
        // 更新房间状态为"空闲"
        updateRoomStatus(contract.getRoomId(), "VACANT");
        
        return terminatedContract;
    }

    @Override
    @Transactional
    public String uploadContractDocument(Long id, MultipartFile file) {
        try {
            // 获取合同
            Contract contract = getContractById(id);
            
            // 创建上传目录
            String uploadDir = uploadBasePath + "/contracts";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成文件名
            String fileName = "contract_" + id + "_" + System.currentTimeMillis() + 
                    getFileExtension(file.getOriginalFilename());
            
            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // 更新合同文档URL
            String documentUrl = "/files/contracts/" + fileName;
            contract.setContractFileUrl(documentUrl);
            contractRepository.save(contract);
            
            return documentUrl;
        } catch (Exception e) {
            logger.error("上传合同文档失败", e);
            throw new RuntimeException("上传合同文档失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCurrentUserContracts(int page, int size, String status) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在: " + username));
        
        // 获取用户角色
        String role = user.getRole();
        
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Contract> contractsPage;
        
        if ("LANDLORD".equals(role)) {
            // 房东查看与其物业相关的所有合同
            // 这里需要先查询房东的所有物业，然后查询物业的所有房间，然后查询房间的所有合同
            // 为简化演示，这里假设已有合适的查询方法
            contractsPage = contractRepository.findAll(pageable);
        } else {
            // 普通用户只查看自己的合同
            // 暂时不支持按状态筛选，因为数据库中没有status字段
            contractsPage = contractRepository.findByTenantId(user.getId(), pageable);
        }
        
        List<Contract> contracts = contractsPage.getContent();
        
        // 处理分页信息
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("currentPage", contractsPage.getNumber());
        pageInfo.put("totalPages", contractsPage.getTotalPages());
        pageInfo.put("totalElements", contractsPage.getTotalElements());
        pageInfo.put("size", contractsPage.getSize());
        
        result.put("contracts", contracts);
        result.put("pageInfo", pageInfo);
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExpiringContracts(int days) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算日期范围
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        
        // 查询即将到期的合同
        List<Contract> expiringContracts = contractRepository.findContractsAboutToExpire(startDate, endDate);
        
        // 按剩余天数分组
        Map<Long, List<Contract>> groupedContracts = expiringContracts.stream()
                .collect(Collectors.groupingBy(contract -> 
                        ChronoUnit.DAYS.between(LocalDate.now(), contract.getEndDate())));
        
        result.put("contracts", expiringContracts);
        result.put("groupedByDays", groupedContracts);
        result.put("totalCount", expiringContracts.size());
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getContractStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 统计各状态合同数量（暂时不支持按状态统计）
            long totalCount = contractRepository.count();
            // long activeCount = contractRepository.countByStatus("ACTIVE");
            // long expiredCount = contractRepository.countByStatus("EXPIRED");
            // long terminatedCount = contractRepository.countByStatus("TERMINATED");
            
            stats.put("total", totalCount);
            stats.put("active", 0L);  // 暂时设为0
            stats.put("expired", 0L);
            stats.put("terminated", 0L);
            
            // 按月统计新签合同数量
            // 这里需要自定义查询方法，为简化演示，使用模拟数据
            Map<String, Long> monthlyStats = new HashMap<>();
            monthlyStats.put("1月", 12L);
            monthlyStats.put("2月", 15L);
            monthlyStats.put("3月", 18L);
            monthlyStats.put("4月", 22L);
            monthlyStats.put("5月", 20L);
            monthlyStats.put("6月", 25L);
            
            stats.put("monthlyStats", monthlyStats);
            
        } catch (Exception e) {
            logger.error("获取合同统计数据失败", e);
            stats.put("error", "获取合同统计数据失败: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 验证合同数据
     */
    private void validateContract(Contract contract) {
        // 验证租户是否存在
        userRepository.findById(contract.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("租户不存在，ID: " + contract.getTenantId()));
        
        // 验证房间是否存在
        Room room = roomRepository.findById(contract.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("房间不存在，ID: " + contract.getRoomId()));
        
        // 验证合同日期
        if (contract.getStartDate().isAfter(contract.getEndDate())) {
            throw new IllegalArgumentException("合同开始日期不能晚于结束日期");
        }
        
        // 验证房间是否已被租用（仅针对新合同）
        if (contract.getId() == null) {
            // 检查是否有重叠的活跃合同
            List<Contract> activeContracts = contractRepository.findByRoomIdAndStatus(contract.getRoomId(), "ACTIVE");
            
            // 检查日期是否重叠
            for (Contract existingContract : activeContracts) {
                if (isDateRangeOverlap(contract.getStartDate(), contract.getEndDate(), 
                                     existingContract.getStartDate(), existingContract.getEndDate())) {
                    throw new IllegalStateException("房间在该时间段已被租用，合同编号: " + existingContract.getContractNumber());
                }
            }
        }
    }
    
    /**
     * 检查日期范围是否重叠
     */
    private boolean isDateRangeOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2.plusDays(1)) && end1.isAfter(start2.minusDays(1));
    }
    
    /**
     * 更新房间状态
     */
    private void updateRoomStatus(Long roomId, String status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("房间不存在，ID: " + roomId));
        
        room.setStatus(status);
        roomRepository.save(room);
    }
    
    /**
     * 生成合同编号
     */
    private String generateContractNumber() {
        // 生成格式为 "C-yyyyMMdd-randomNumber" 的合同编号
        String datePart = LocalDate.now().toString().replaceAll("-", "");
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        
        return "C-" + datePart + "-" + randomPart;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getUserContracts(Long userId) {
        try {
            // 首先通过userId查找tenant记录
            Optional<Tenant> tenantOpt = tenantRepository.findByUserId(userId);
            if (!tenantOpt.isPresent()) {
                // 如果用户没有tenant记录，返回空列表
                return new ArrayList<>();
            }
            
            Tenant tenant = tenantOpt.get();
            List<Contract> contracts = contractRepository.findByTenantId(tenant.getId());
            
            // 转换为前端期望的格式
            List<Map<String, Object>> rentalList = new ArrayList<>();
            
            for (Contract contract : contracts) {
                Map<String, Object> rental = new HashMap<>();
                
                // 基本合同信息
                rental.put("id", contract.getId());
                rental.put("contractNumber", contract.getContractNumber());
                rental.put("startDate", contract.getStartDate());
                rental.put("endDate", contract.getEndDate());
                rental.put("monthlyRent", contract.getMonthlyRent());
                rental.put("deposit", contract.getDepositAmount());
                rental.put("status", contract.getStatus());
                rental.put("signedDate", contract.getSignedDate());
                rental.put("paymentMethod", contract.getPaymentMethod());
                
                // 获取房间信息
                try {
                    Room room = roomRepository.findById(contract.getRoomId()).orElse(null);
                    if (room != null) {
                        rental.put("roomId", room.getId());
                        rental.put("roomTitle", room.getRoomNumber());
                        rental.put("roomNumber", room.getRoomNumber());
                        rental.put("roomArea", room.getArea());
                        rental.put("roomType", room.getType());
                        rental.put("roomImageUrl", null); // 暂时设为null，后续可以添加图片功能
                        
                        // 获取房产信息作为地址
                        try {
                            Property property = propertyRepository.findById(room.getPropertyId()).orElse(null);
                            if (property != null) {
                                rental.put("roomAddress", property.getAddress());
                                rental.put("propertyTitle", property.getTitle());
                            } else {
                                rental.put("roomAddress", "地址信息不可用");
                                rental.put("propertyTitle", "房产信息不可用");
                            }
                        } catch (Exception e) {
                            rental.put("roomAddress", "地址信息不可用");
                            rental.put("propertyTitle", "房产信息不可用");
                        }
                    } else {
                        rental.put("roomId", contract.getRoomId());
                        rental.put("roomTitle", "房间信息不可用");
                        rental.put("roomNumber", "未知");
                        rental.put("roomArea", null);
                        rental.put("roomType", "未知");
                        rental.put("roomAddress", "地址信息不可用");
                        rental.put("roomImageUrl", null);
                        rental.put("propertyTitle", "房产信息不可用");
                    }
                } catch (Exception e) {
                    logger.warn("获取房间信息失败: " + e.getMessage());
                    rental.put("roomId", contract.getRoomId());
                    rental.put("roomTitle", "房间信息不可用");
                    rental.put("roomNumber", "未知");
                    rental.put("roomArea", null);
                    rental.put("roomType", "未知");
                    rental.put("roomAddress", "地址信息不可用");
                    rental.put("roomImageUrl", null);
                    rental.put("propertyTitle", "房产信息不可用");
                }
                
                // 获取房东信息
                try {
                    User landlord = userRepository.findById(contract.getLandlordId()).orElse(null);
                    if (landlord != null) {
                        rental.put("landlordId", landlord.getId());
                        rental.put("landlordName", landlord.getUsername());
                        rental.put("landlordPhone", landlord.getPhone());
                    } else {
                        rental.put("landlordId", contract.getLandlordId());
                        rental.put("landlordName", "房东信息不可用");
                        rental.put("landlordPhone", "联系方式不可用");
                    }
                } catch (Exception e) {
                    logger.warn("获取房东信息失败: " + e.getMessage());
                    rental.put("landlordId", contract.getLandlordId());
                    rental.put("landlordName", "房东信息不可用");
                    rental.put("landlordPhone", "联系方式不可用");
                }
                
                // 获取租户信息
                try {
                    User tenantUser = userRepository.findById(tenant.getUserId()).orElse(null);
                    if (tenantUser != null) {
                        rental.put("tenantId", tenantUser.getId());
                        rental.put("tenantName", tenantUser.getUsername());
                        rental.put("tenantPhone", tenantUser.getPhone());
                    } else {
                        rental.put("tenantId", tenant.getUserId());
                        rental.put("tenantName", "租户信息不可用");
                        rental.put("tenantPhone", "联系方式不可用");
                    }
                } catch (Exception e) {
                    logger.warn("获取租户信息失败: " + e.getMessage());
                    rental.put("tenantId", tenant.getUserId());
                    rental.put("tenantName", "租户信息不可用");
                    rental.put("tenantPhone", "联系方式不可用");
                }
                
                // 合同条款
                rental.put("contractTerms", "标准租房合同条款，包括但不限于：1. 租金按时缴纳；2. 保持房屋清洁；3. 不得转租；4. 损坏赔偿等。");
                
                rentalList.add(rental);
            }
            
            return rentalList;
        } catch (Exception e) {
            logger.error("获取用户合同失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserActiveContracts(Long userId) {
        try {
            // 首先通过userId查找tenant记录
            Optional<Tenant> tenantOpt = tenantRepository.findByUserId(userId);
            if (!tenantOpt.isPresent()) {
                return 0;
            }
            
            Tenant tenant = tenantOpt.get();
            List<Contract> contracts = contractRepository.findByTenantId(tenant.getId());
            // 统计活跃合同数量（状态为ACTIVE且在有效期内）
            LocalDate now = LocalDate.now();
            return contracts.stream()
                    .filter(contract -> "ACTIVE".equals(contract.getStatus()) 
                            && !contract.getStartDate().isAfter(now) 
                            && !contract.getEndDate().isBefore(now))
                    .count();
        } catch (Exception e) {
            logger.error("统计用户活跃合同数量失败", e);
            return 0;
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}