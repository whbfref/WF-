package com.apartment.service.impl;

import com.apartment.exception.BusinessException;
import com.apartment.model.*;
import com.apartment.repository.*;
import com.apartment.service.FileService;
import com.apartment.service.LandlordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

/**
 * 房东服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LandlordServiceImpl implements LandlordService {

    private final LandlordRepository landlordRepository;
    private final LandlordVerificationRepository verificationRepository;
    private final LandlordRatingRepository ratingRepository;
    private final LandlordIncomeRepository incomeRepository;
    private final LandlordTodoRepository todoRepository;
    private final PropertyRepository propertyRepository;
    private final ContractRepository contractRepository;
    private final BillRepository billRepository;
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final FileService fileService;

    @Override
    @Transactional
    public LandlordVerification applyVerification(Long userId, String realName, String idCard,
                                                String idCardFrontImageUrl, String idCardBackImageUrl,
                                                String bankCard, String bankName, String contactPhone, String contactEmail) {
        
        log.info("开始处理房东认证申请 - 用户ID: {}, 真实姓名: {}, 身份证: {}", userId, realName, idCard);
        
        // 检查是否已有申请
        if (verificationRepository.existsByUserId(userId)) {
            log.warn("用户ID {} 已有认证申请", userId);
            throw new BusinessException(2002, "认证申请已存在");
        }

        // 检查身份证号是否已被使用
        if (verificationRepository.existsByIdCard(idCard)) {
            log.warn("身份证号 {} 已被使用", idCard);
            throw new BusinessException(2005, "身份证号已被使用");
        }

        try {
            // 数据验证
            if (realName == null || realName.trim().isEmpty()) {
                throw new IllegalArgumentException("真实姓名不能为空");
            }
            if (idCard == null || !idCard.matches("^(\\d{15}|\\d{18}|\\d{17}[\\dXx])$")) {
                throw new IllegalArgumentException("身份证号格式不正确");
            }
            if (bankCard == null || !bankCard.matches("^\\d{16,19}$")) {
                throw new IllegalArgumentException("银行卡号格式不正确");
            }
            if (contactPhone == null || !contactPhone.matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
            
            log.info("数据验证通过，开始创建认证申请");
            
            // 创建认证申请
            LandlordVerification verification = new LandlordVerification();
            verification.setUserId(userId);
            verification.setRealName(realName.trim());
            verification.setIdCard(idCard.trim());
            verification.setIdCardFrontImage(idCardFrontImageUrl);
            verification.setIdCardBackImage(idCardBackImageUrl);
            verification.setBankCard(bankCard.trim());
            verification.setBankName(bankName.trim());
            verification.setContactPhone(contactPhone.trim());
            verification.setContactEmail(contactEmail != null ? contactEmail.trim() : null);

            LandlordVerification savedVerification = verificationRepository.save(verification);
            log.info("认证申请保存成功，申请ID: {}", savedVerification.getId());
            
            return savedVerification;
        } catch (IllegalArgumentException e) {
            log.error("数据验证失败: {}", e.getMessage());
            throw new BusinessException(400, e.getMessage());
        } catch (Exception e) {
            log.error("保存认证申请失败", e);
            throw new BusinessException(500, "保存认证申请失败: " + e.getMessage());
        }
    }

    @Override
    public LandlordVerification getVerificationStatus(Long userId) {
        return verificationRepository.findByUserId(userId)
                .orElse(null); // 如果没有申请记录，返回null而不是抛出异常
    }

    @Override
    public Landlord getLandlordInfo(Long userId) {
        return landlordRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(2001, "用户不是房东身份"));
    }

    @Override
    @Transactional
    public Landlord updateLandlordInfo(Long userId, String contactPhone, String contactEmail, String bankCard, String bankName) {
        Landlord landlord = getLandlordInfo(userId);
        
        // 更新联系信息
        if (contactPhone != null) {
            landlord.getUser().setPhone(contactPhone);
        }
        if (contactEmail != null) {
            landlord.getUser().setEmail(contactEmail);
        }
        if (bankCard != null) {
            landlord.setBankCard(bankCard);
        }
        if (bankName != null) {
            landlord.setBankName(bankName);
        }

        return landlordRepository.save(landlord);
    }

    @Override
    public Page<LandlordRating> getLandlordRatings(Long landlordId, Integer rating, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (rating != null && startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            return ratingRepository.findByLandlordIdAndRatingAndCreateTimeBetween(landlordId, rating, startDateTime, endDateTime, pageable);
        } else if (rating != null) {
            return ratingRepository.findByLandlordIdAndRating(landlordId, rating, pageable);
        } else if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            return ratingRepository.findByLandlordIdAndCreateTimeBetween(landlordId, startDateTime, endDateTime, pageable);
        } else {
            return ratingRepository.findByLandlordId(landlordId, pageable);
        }
    }

    @Override
    @Transactional
    public LandlordRating replyToRating(Long ratingId, Long landlordId, String content) {
        LandlordRating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new BusinessException(2007, "评价不存在"));

        if (!rating.getLandlordId().equals(landlordId)) {
            throw new BusinessException(2008, "无权回复该评价");
        }

        rating.setReply(content);
        rating.setReplyTime(LocalDateTime.now());

        return ratingRepository.save(rating);
    }

    @Override
    public Map<String, Object> getIncomeStats(Long landlordId, Integer year, Integer month) {
        Map<String, Object> stats = new HashMap<>();

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        // 基于实际合同和账单计算收入统计
        
        // 计算总收入 - 基于已支付的租金账单
        BigDecimal totalIncome = billRepository.sumPaidAmountByLandlordId(landlordId);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        stats.put("totalIncome", totalIncome);

        // 计算各类型收入
        BigDecimal rentalIncome = billRepository.sumPaidAmountByLandlordIdAndBillType(landlordId, "RENT");
        BigDecimal depositIncome = billRepository.sumPaidAmountByLandlordIdAndBillType(landlordId, "DEPOSIT");
        BigDecimal utilityIncome = billRepository.sumPaidAmountByLandlordIdAndBillType(landlordId, "UTILITY");
        
        if (rentalIncome == null) rentalIncome = BigDecimal.ZERO;
        if (depositIncome == null) depositIncome = BigDecimal.ZERO;
        if (utilityIncome == null) utilityIncome = BigDecimal.ZERO;

        stats.put("rentalIncome", rentalIncome);
        stats.put("depositIncome", depositIncome);
        stats.put("utilityIncome", utilityIncome);

        // 计算待收款 - 基于未支付的账单
        BigDecimal pending = billRepository.sumUnpaidAmountByLandlordId(landlordId);
        if (pending == null) pending = BigDecimal.ZERO;
        stats.put("pending", pending);

        // 月度统计
        if (month == null) {
            List<Object[]> monthlyStatsRaw = billRepository.findMonthlyIncomeByLandlordIdAndYear(landlordId, year);
            List<Map<String, Object>> monthlyStats = new ArrayList<>();
            
            // 转换为前端期望的格式
            for (Object[] stat : monthlyStatsRaw) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", stat[0]); // 月份
                monthData.put("income", stat[1]); // 收入金额
                monthlyStats.add(monthData);
            }
            
            stats.put("monthlyStats", monthlyStats);
        } else {
            BigDecimal monthlyIncome = billRepository.sumPaidAmountByLandlordIdAndYearAndMonth(landlordId, year, month);
            if (monthlyIncome == null) monthlyIncome = BigDecimal.ZERO;
            stats.put("monthlyIncome", monthlyIncome);
        }

        return stats;
    }

    @Override
    public Page<LandlordIncome> getIncomeDetails(Long landlordId, LandlordIncome.IncomeType type, Long propertyId,
                                               LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // 基于Bill表查询收入明细，然后转换为LandlordIncome格式
        try {
            log.info("查询房东收入明细 - landlordId: {}, type: {}, propertyId: {}, startDate: {}, endDate: {}", 
                    landlordId, type, propertyId, startDate, endDate);
            
            // 构建查询条件
            String billType = null;
            if (type != null) {
                switch (type) {
                    case RENT:
                        billType = "RENT";
                        break;
                    case DEPOSIT:
                        billType = "DEPOSIT";
                        break;
                    case UTILITY:
                        billType = "WATER"; // 水电费在Bill中可能是WATER, ELECTRICITY等
                        break;
                }
            }
            
            // 查询已支付的账单作为收入明细
            // 注意：这里propertyId实际上对应的是roomId
            Page<Bill> bills = billRepository.findPaidBillsByLandlordId(
                landlordId, billType, propertyId, startDate, endDate, pageable);
            
            log.info("查询到 {} 条收入记录", bills.getTotalElements());
            
            // 将Bill转换为LandlordIncome
            Page<LandlordIncome> incomes = bills.map(bill -> {
                LandlordIncome income = new LandlordIncome();
                income.setId(bill.getId());
                income.setLandlordId(landlordId);
                income.setAmount(bill.getAmount());
                income.setDescription(bill.getRemark()); // 使用remark作为描述
                
                // 设置支付时间
                if (bill.getPaidDate() != null) {
                    income.setPaymentTime(bill.getPaidDate().atStartOfDay());
                } else {
                    income.setPaymentTime(LocalDateTime.now());
                }
                
                // 设置收入类型
                if ("RENT".equals(bill.getBillType())) {
                    income.setType(LandlordIncome.IncomeType.RENT);
                } else if ("DEPOSIT".equals(bill.getBillType())) {
                    income.setType(LandlordIncome.IncomeType.DEPOSIT);
                } else if ("WATER".equals(bill.getBillType()) || "ELECTRICITY".equals(bill.getBillType()) || "GAS".equals(bill.getBillType())) {
                    income.setType(LandlordIncome.IncomeType.UTILITY);
                } else {
                    income.setType(LandlordIncome.IncomeType.RENT);
                }
                
                // 通过合同ID获取房产和租户信息
                try {
                    Contract contract = contractRepository.findById(bill.getContractId()).orElse(null);
                    if (contract != null) {
                        income.setPropertyId(contract.getRoomId()); // Contract中是roomId，不是propertyId
                        income.setUserId(contract.getTenantId()); // 设置租户ID作为用户ID
                        
                        // 获取房产信息 - 这里需要通过roomId获取房产
                        // 暂时设置一个默认描述
                        income.setDescription("账单ID: " + bill.getId() + ", 类型: " + bill.getBillType());
                    }
                } catch (Exception e) {
                    log.warn("获取合同关联信息失败: {}", e.getMessage());
                }
                
                return income;
            });
            
            return incomes;
            
        } catch (Exception e) {
            log.error("查询收入明细失败", e);
            throw new BusinessException(500, "查询收入明细失败: " + e.getMessage());
        }
    }

    @Override
    public Page<LandlordTodo> getTodos(Long landlordId, LandlordTodo.TodoType type, Pageable pageable) {
        if (type != null) {
            return todoRepository.findByLandlordIdAndType(landlordId, type, pageable);
        } else {
            return todoRepository.findByLandlordId(landlordId, pageable);
        }
    }

    @Override
    @Transactional
    public LandlordTodo processTodo(Long todoId, Long landlordId, LandlordTodo.TodoStatus status, String comment) {
        LandlordTodo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(2009, "待办事项不存在"));

        if (!todo.getLandlordId().equals(landlordId)) {
            throw new BusinessException(2009, "无权处理该待办事项");
        }

        todo.setStatus(status);
        todo.setComment(comment);

        return todoRepository.save(todo);
    }

    @Override
    public Long getLandlordIdByUserId(Long userId) {
        return landlordRepository.findByUserId(userId)
                .map(Landlord::getId)
                .orElse(null);
    }

    @Override
    public boolean isLandlord(Long userId) {
        return landlordRepository.existsByUserId(userId);
    }
    
    @Override
    public Map<String, Object> getPropertyStats(Long landlordId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取房东的所有房间统计（基于rooms表）
        Long totalRooms = roomRepository.countByLandlordId(landlordId);
        
        // 总房源数（房间数）
        stats.put("totalProperties", totalRooms.intValue());
        
        // 统计各状态房间数量
        // 根据Room实体：VACANT-空置，RENTED-已出租，MAINTENANCE-维修中
        Long vacantRooms = roomRepository.countByLandlordIdAndStatus(landlordId, "VACANT");
        Long rentedRooms = roomRepository.countByLandlordIdAndStatus(landlordId, "RENTED");
        Long maintenanceRooms = roomRepository.countByLandlordIdAndStatus(landlordId, "MAINTENANCE");

        stats.put("availableProperties", vacantRooms.intValue()); // 可出租（空置）
        stats.put("rentedProperties", rentedRooms.intValue()); // 已出租
        stats.put("maintenanceProperties", maintenanceRooms.intValue()); // 维修中

        return stats;
    }

    @Override
    @Transactional
    public LandlordTodo createRoomApplicationTodo(Long landlordId, Long applicationId, String title, String description) {
        LandlordTodo todo = new LandlordTodo();
        todo.setLandlordId(landlordId);
        todo.setType(LandlordTodo.TodoType.ROOM_APPLICATION);
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setRelatedId(applicationId);
        todo.setRelatedType("ROOM_APPLICATION");
        todo.setStatus(LandlordTodo.TodoStatus.PENDING);
        
        return todoRepository.save(todo);
    }
    
    @Override
    @Transactional
    public Landlord updateLandlordBankInfo(Long userId, String bankCard, String bankName) {
        Landlord landlord = landlordRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(2001, "房东信息不存在"));

        if (bankCard != null) {
            landlord.setBankCard(bankCard);
        }
        if (bankName != null) {
            landlord.setBankName(bankName);
        }

        return landlordRepository.save(landlord);
    }

    @Override
    public Page<Landlord> getAllLandlords(String realName, String phone, Boolean verified, Pageable pageable) {
        try {
            // 使用Specification进行动态查询
            return landlordRepository.findAll((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 真实姓名过滤
                if (realName != null && !realName.trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("realName"), "%" + realName + "%"));
                }
                
                // 电话过滤（通过关联的User表）
                if (phone != null && !phone.trim().isEmpty()) {
                    Join<Landlord, User> userJoin = root.join("user", JoinType.LEFT);
                    predicates.add(criteriaBuilder.like(userJoin.get("phone"), "%" + phone + "%"));
                }
                
                // 认证状态过滤
                if (verified != null) {
                    predicates.add(criteriaBuilder.equal(root.get("verified"), verified));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }, pageable);
        } catch (Exception e) {
            log.error("查询房东列表失败", e);
            throw new BusinessException(500, "查询房东列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Landlord applyForVerification(Long userId, String realName, String idCard,
                                       String idCardFrontImageUrl, String idCardBackImageUrl,
                                       String bankCard, String bankName, String contactPhone, String contactEmail) {
        
        log.info("开始处理房东认证申请 - 用户ID: {}, 真实姓名: {}, 身份证: {}", userId, realName, idCard);
        
        // 检查是否已有房东记录
        Landlord existingLandlord = landlordRepository.findByUserId(userId).orElse(null);
        if (existingLandlord != null) {
            log.warn("用户ID {} 已有房东记录", userId);
            throw new BusinessException(2002, "房东认证申请已存在");
        }

        // 检查身份证号是否已被使用
        if (landlordRepository.existsByIdCard(idCard)) {
            log.warn("身份证号 {} 已被使用", idCard);
            throw new BusinessException(2005, "身份证号已被使用");
        }

        try {
            // 数据验证
            if (realName == null || realName.trim().isEmpty()) {
                throw new IllegalArgumentException("真实姓名不能为空");
            }
            if (idCard == null || !idCard.matches("^(\\d{15}|\\d{18}|\\d{17}[\\dXx])$")) {
                throw new IllegalArgumentException("身份证号格式不正确");
            }
            if (bankCard == null || !bankCard.matches("^\\d{16,19}$")) {
                throw new IllegalArgumentException("银行卡号格式不正确");
            }
            if (contactPhone == null || !contactPhone.matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
            
            log.info("数据验证通过，开始创建房东记录");
            
            // 创建房东记录
            Landlord landlord = new Landlord();
            landlord.setUserId(userId);
            landlord.setRealName(realName.trim());
            landlord.setIdCard(idCard.trim());
            landlord.setIdCardFrontUrl(idCardFrontImageUrl);
            landlord.setIdCardBackUrl(idCardBackImageUrl);
            landlord.setBankCard(bankCard.trim());
            landlord.setBankName(bankName.trim());
            landlord.setContactPhone(contactPhone.trim());
            landlord.setContactEmail(contactEmail != null ? contactEmail.trim() : null);
            landlord.setVerified(false); // 默认未认证，等待管理员审核

            Landlord savedLandlord = landlordRepository.save(landlord);
            log.info("房东记录保存成功，房东ID: {}", savedLandlord.getId());
            
            return savedLandlord;
        } catch (IllegalArgumentException e) {
            log.error("数据验证失败: {}", e.getMessage());
            throw new BusinessException(400, e.getMessage());
        } catch (Exception e) {
            log.error("保存房东记录失败", e);
            throw new BusinessException(500, "保存房东记录失败: " + e.getMessage());
        }
    }

    @Override
    public Landlord getLandlordByUserId(Long userId) {
        return landlordRepository.findByUserId(userId).orElse(null);
    }

    @Override
    @Transactional
    public Landlord verifyLandlord(Long landlordId, Boolean verified) {
        log.info("管理员审核房东认证 - 房东ID: {}, 审核结果: {}", landlordId, verified);
        
        Landlord landlord = landlordRepository.findById(landlordId)
                .orElseThrow(() -> new BusinessException(2001, "房东不存在"));
        
        landlord.setVerified(verified);
        
        Landlord savedLandlord = landlordRepository.save(landlord);
        log.info("房东认证状态更新成功 - 房东ID: {}, 认证状态: {}", landlordId, verified);
        
        return savedLandlord;
    }

    @Override
    @Transactional
    public void deleteLandlord(Long landlordId) {
        log.info("删除房东 - 房东ID: {}", landlordId);
        
        try {
            // 检查房东是否存在
            Landlord landlord = landlordRepository.findById(landlordId)
                    .orElseThrow(() -> new BusinessException(2001, "房东不存在"));
            
            // 检查房东是否有关联的房间
            long roomCount = roomRepository.countByLandlordId(landlordId);
            if (roomCount > 0) {
                throw new BusinessException(2002, "无法删除房东，该房东还有 " + roomCount + " 个房间，请先删除相关房间");
            }
            
            // 检查房东是否有关联的房产
            List<Property> properties = propertyRepository.findByLandlordId(landlordId);
            if (!properties.isEmpty()) {
                throw new BusinessException(2003, "无法删除房东，该房东还有 " + properties.size() + " 个房产，请先删除相关房产");
            }
            
            // 删除房东记录
            landlordRepository.deleteById(landlordId);
            
            log.info("成功删除房东 - 房东ID: {}", landlordId);
        } catch (BusinessException e) {
            log.error("删除房东业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("删除房东失败", e);
            throw new BusinessException(500, "删除房东失败: " + e.getMessage());
        }
    }
} 