package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.exception.BusinessException;
import com.apartment.model.*;
import com.apartment.repository.RoomRepository;
import com.apartment.service.LandlordService;
import com.apartment.service.RoomService;
import com.apartment.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 房东控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/landlords")
@RequiredArgsConstructor
public class LandlordController {

    private final LandlordService landlordService;
    private final JwtUtil jwtUtil;
    private final RoomRepository roomRepository;
    private final RoomService roomService;

    /**
     * 测试用户认证状态
     */
    @GetMapping("/test/auth")
    public ApiResponse<Map<String, Object>> testAuth(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            log.info("Authorization header: {}", authHeader);

            Long userId = jwtUtil.getUserIdFromRequest(request);
            log.info("获取到用户ID: {}", userId);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("authHeader", authHeader);
            result.put("authenticated", true);

            return ApiResponse.success("认证成功", result);
        } catch (Exception e) {
            log.error("认证测试失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("authenticated", false);
            result.put("error", e.getMessage());
            return ApiResponse.error("认证失败: " + e.getMessage());
        }
    }

    /**
     * 获取房东列表（管理员用）
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getLandlords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Boolean verified,
            HttpServletRequest request) {

        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Landlord> landlords = landlordService.getAllLandlords(realName, phone, verified, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", landlords.getContent());
            result.put("total", landlords.getTotalElements());
            result.put("totalPages", landlords.getTotalPages());
            result.put("page", page);
            result.put("size", size);

            return ApiResponse.success("操作成功", result);
        } catch (Exception e) {
            log.error("获取房东列表失败", e);
            return ApiResponse.error("获取房东列表失败");
        }
    }

    /**
     * 申请房东认证
     */
    @PostMapping("/verify")
    public ApiResponse<Map<String, Object>> applyVerification(
            @RequestParam("realName") String realName,
            @RequestParam("idCard") String idCard,
            @RequestParam("idCardFrontImageUrl") String idCardFrontImageUrl,
            @RequestParam("idCardBackImageUrl") String idCardBackImageUrl,
            @RequestParam("bankCard") String bankCard,
            @RequestParam("bankName") String bankName,
            @RequestParam("contactPhone") String contactPhone,
            @RequestParam(value = "contactEmail", required = false) String contactEmail,
            HttpServletRequest request) {

        try {
            // 获取用户ID
            Long userId;
            try {
                userId = jwtUtil.getUserIdFromRequest(request);
                log.info("获取到用户ID: {}", userId);
            } catch (Exception e) {
                log.error("获取用户ID失败", e);
                return ApiResponse.error(401, "用户认证失败，请重新登录");
            }

            // 调试信息：打印接收到的参数
            log.info("房东认证申请参数 - 用户ID: {}, 真实姓名: {}, 身份证: {}, 正面照片URL: {}, 背面照片URL: {}, 银行卡: {}, 银行名称: {}, 联系电话: {}, 联系邮箱: {}",
                    userId, realName, idCard, idCardFrontImageUrl, idCardBackImageUrl, bankCard, bankName, contactPhone, contactEmail);

            // 参数验证
            if (realName == null || realName.trim().isEmpty()) {
                return ApiResponse.error(400, "真实姓名不能为空");
            }
            if (idCard == null || idCard.trim().isEmpty()) {
                return ApiResponse.error(400, "身份证号不能为空");
            }
            if (bankCard == null || bankCard.trim().isEmpty()) {
                return ApiResponse.error(400, "银行卡号不能为空");
            }
            if (bankName == null || bankName.trim().isEmpty()) {
                return ApiResponse.error(400, "银行名称不能为空");
            }
            if (contactPhone == null || contactPhone.trim().isEmpty()) {
                return ApiResponse.error(400, "联系电话不能为空");
            }

            // 创建或更新房东信息
            Landlord landlord = landlordService.applyForVerification(
                    userId, realName, idCard, idCardFrontImageUrl, idCardBackImageUrl,
                    bankCard, bankName, contactPhone, contactEmail);

            Map<String, Object> result = new HashMap<>();
            result.put("landlordId", landlord.getId());
            result.put("verified", landlord.getVerified());
            result.put("message", "认证申请已提交，等待管理员审核");

            return ApiResponse.success("申请提交成功，等待审核", result);
        } catch (BusinessException e) {
            log.error("房东认证申请业务异常: {}", e.getMessage());
            return ApiResponse.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("房东认证申请失败", e);
            return ApiResponse.error(500, "申请提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取房东认证状态
     */
    @GetMapping("/verify/status")
    public ApiResponse<Map<String, Object>> getVerificationStatus(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            log.info("获取房东认证状态，用户ID: {}", userId);

            Landlord landlord = landlordService.getLandlordByUserId(userId);

            Map<String, Object> result = new HashMap<>();

            if (landlord != null) {
                result.put("landlordId", landlord.getId());
                result.put("verified", landlord.getVerified());
                result.put("status", landlord.getVerified() ? "APPROVED" : "PENDING");
                result.put("idCard", landlord.getIdCard());
                result.put("bankCard", maskBankCard(landlord.getBankCard()));
                result.put("bankName", landlord.getBankName());
                result.put("contactPhone", landlord.getContactPhone());
                result.put("contactEmail", landlord.getContactEmail());
                result.put("createdAt", landlord.getCreatedAt());
            } else {
                // 如果没有申请记录，返回默认状态
                result.put("landlordId", null);
                result.put("verified", false);
                result.put("status", "NOT_APPLIED");
            }

            return ApiResponse.success("操作成功", result);
        } catch (Exception e) {
            log.error("获取房东认证状态失败", e);
            return ApiResponse.error("获取认证状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取房东信息
     */
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> getLandlordInfo(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);

        Landlord landlord = landlordService.getLandlordInfo(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", landlord.getId());
        result.put("userId", landlord.getUserId());
        result.put("realName", landlord.getRealName());
        result.put("contactPhone", landlord.getUser().getPhone());
        result.put("contactEmail", landlord.getUser().getEmail());
        result.put("bankCard", maskBankCard(landlord.getBankCard()));
        result.put("bankName", landlord.getBankName());
        result.put("rating", landlord.getRating());
        result.put("verified", landlord.getVerified());
        result.put("createTime", landlord.getCreatedAt());

        // 获取房产统计数据
        Map<String, Object> propertyStats = landlordService.getPropertyStats(landlord.getId());
        result.putAll(propertyStats);

        return ApiResponse.success("操作成功", result);
    }

    /**
     * 更新房东信息
     */
    @PutMapping("/me")
    public ApiResponse<Map<String, Object>> updateLandlordInfo(
            @RequestBody Map<String, String> updateData,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);

        Landlord landlord = landlordService.updateLandlordInfo(
                userId,
                updateData.get("contactPhone"),
                updateData.get("contactEmail"),
                updateData.get("bankCard"),
                updateData.get("bankName"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", landlord.getId());
        result.put("contactPhone", landlord.getUser().getPhone());
        result.put("contactEmail", landlord.getUser().getEmail());
        result.put("bankCard", maskBankCard(landlord.getBankCard()));
        result.put("bankName", landlord.getBankName());
        result.put("updateTime", LocalDateTime.now());

        return ApiResponse.success("房东信息更新成功", result);
    }

    /**
     * 获取房东评价列表
     */
    @GetMapping("/ratings")
    public ApiResponse<Map<String, Object>> getRatings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LandlordRating> ratings = landlordService.getLandlordRatings(landlordId, rating, startDate, endDate, pageable);

        // 构建返回数据，确保包含用户和房产信息
        List<Map<String, Object>> ratingList = new ArrayList<>();
        for (LandlordRating ratingItem : ratings.getContent()) {
            Map<String, Object> ratingData = new HashMap<>();
            ratingData.put("id", ratingItem.getId());
            ratingData.put("rating", ratingItem.getRating());
            ratingData.put("comment", ratingItem.getComment());
            ratingData.put("reply", ratingItem.getReply());
            ratingData.put("replyTime", ratingItem.getReplyTime());
            ratingData.put("createTime", ratingItem.getCreateTime());
            
            // 用户信息
            if (ratingItem.getUser() != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", ratingItem.getUser().getId());
                userData.put("username", ratingItem.getUser().getUsername());
                userData.put("avatarUrl", ratingItem.getUser().getAvatarUrl());
                ratingData.put("user", userData);
            }
            
            // 房产信息
            if (ratingItem.getProperty() != null) {
                Map<String, Object> propertyData = new HashMap<>();
                propertyData.put("id", ratingItem.getProperty().getId());
                propertyData.put("title", ratingItem.getProperty().getTitle());
                propertyData.put("address", ratingItem.getProperty().getAddress());
                ratingData.put("property", propertyData);
            }
            
            ratingList.add(ratingData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", ratingList);

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", page);
        pageInfo.put("size", size);
        pageInfo.put("total", ratings.getTotalElements());
        pageInfo.put("totalPages", ratings.getTotalPages());
        result.put("pageable", pageInfo);

        return ApiResponse.success("操作成功", result);
    }

    /**
     * 回复评价
     */
    @PostMapping("/ratings/{ratingId}/reply")
    public ApiResponse<Map<String, Object>> replyToRating(
            @PathVariable Long ratingId,
            @RequestBody Map<String, String> replyData,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        LandlordRating rating = landlordService.replyToRating(ratingId, landlordId, replyData.get("content"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", rating.getId());
        result.put("ratingId", ratingId);
        result.put("content", rating.getReply());
        result.put("createTime", rating.getReplyTime());

        return ApiResponse.success("回复成功", result);
    }

    /**
     * 获取房东收入统计
     */
    @GetMapping("/income/stats")
    public ApiResponse<Map<String, Object>> getIncomeStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        Map<String, Object> stats = landlordService.getIncomeStats(landlordId, year, month);

        return ApiResponse.success("操作成功", stats);
    }

    /**
     * 获取房东收入明细
     */
    @GetMapping("/income/details")
    public ApiResponse<Map<String, Object>> getIncomeDetails(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        LandlordIncome.IncomeType incomeType = null;
        if (type != null) {
            incomeType = LandlordIncome.IncomeType.valueOf(type.toUpperCase());
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LandlordIncome> incomes = landlordService.getIncomeDetails(landlordId, incomeType, propertyId, startDate, endDate, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", incomes.getContent());

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", page);
        pageInfo.put("size", size);
        pageInfo.put("total", incomes.getTotalElements());
        pageInfo.put("totalPages", incomes.getTotalPages());
        result.put("pageable", pageInfo);

        return ApiResponse.success("操作成功", result);
    }

    /**
     * 获取房东待办事项
     */
    @GetMapping("/todos")
    public ApiResponse<Map<String, Object>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        LandlordTodo.TodoType todoType = null;
        if (type != null && !type.isEmpty()) {
            try {
                todoType = LandlordTodo.TodoType.valueOf(type);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error("无效的待办类型");
            }
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LandlordTodo> todos = landlordService.getTodos(landlordId, todoType, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", todos.getContent());

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", page);
        pageInfo.put("size", size);
        pageInfo.put("total", todos.getTotalElements());
        pageInfo.put("totalPages", todos.getTotalPages());
        result.put("pageable", pageInfo);

        return ApiResponse.success("操作成功", result);
    }

    /**
     * 处理待办事项
     */
    @PutMapping("/todos/{todoId}")
    public ApiResponse<Map<String, Object>> processTodo(
            @PathVariable Long todoId,
            @RequestBody Map<String, String> updateData,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        LandlordTodo.TodoStatus status = LandlordTodo.TodoStatus.valueOf(updateData.get("status"));
        String comment = updateData.get("comment");

        LandlordTodo todo = landlordService.processTodo(todoId, landlordId, status, comment);

        Map<String, Object> result = new HashMap<>();
        result.put("id", todo.getId());
        result.put("status", todo.getStatus().name());
        result.put("comment", todo.getComment());
        result.put("updateTime", todo.getUpdateTime());

        return ApiResponse.success("待办事项处理成功", result);
    }

    /**
     * 获取房东的租房申请列表
     */
    @GetMapping("/applications")
    public ApiResponse<Map<String, Object>> getApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        // 这里需要实现获取房东的租房申请列表的逻辑
        // 暂时返回空结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", new java.util.ArrayList<>());

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", page);
        pageInfo.put("size", size);
        pageInfo.put("total", 0);
        pageInfo.put("totalPages", 0);
        result.put("pageable", pageInfo);

        return ApiResponse.success("操作成功", result);
    }

    /**
     * 获取租房申请详情
     */
    @GetMapping("/applications/{applicationId}")
    public ApiResponse<RoomApplication> getApplicationDetail(
            @PathVariable Long applicationId,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        // 这里需要实现获取申请详情的逻辑，并验证房东权限
        // 暂时返回空结果
        return ApiResponse.error("功能开发中");
    }

    /**
     * 审核租房申请
     */
    @PutMapping("/applications/{applicationId}/review")
    public ApiResponse<Map<String, Object>> reviewApplication(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> reviewData,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        String status = reviewData.get("status");
        String reviewRemarks = reviewData.get("reviewRemarks");

        // 这里需要实现审核申请的逻辑
        // 暂时返回成功结果
        Map<String, Object> result = new HashMap<>();
        result.put("applicationId", applicationId);
        result.put("status", status);
        result.put("reviewTime", LocalDateTime.now());

        return ApiResponse.success("审核成功", result);
    }

    /**
     * 获取房东房间列表
     */
    @GetMapping("/rooms")
    public ApiResponse<Map<String, Object>> getRooms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {

        try {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

            // 使用RoomService的方法来获取包含图片信息的房间数据
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Room> roomsPage = roomService.getRoomsByLandlordId(landlordId, status, keyword, pageable);

            // 构建包含图片信息的房间列表
            List<Map<String, Object>> roomsWithImages = new ArrayList<>();
            for (Room room : roomsPage.getContent()) {
                Map<String, Object> roomMap = new HashMap<>();
                roomMap.put("id", room.getId());
                roomMap.put("roomNumber", room.getRoomNumber());
                roomMap.put("propertyId", room.getPropertyId());
                roomMap.put("area", room.getArea());
                roomMap.put("monthlyRent", room.getMonthlyRent());
                roomMap.put("type", room.getType());
                roomMap.put("orientation", room.getOrientation());
                roomMap.put("floor", room.getFloor());
                roomMap.put("layout", room.getLayout());
                roomMap.put("deposit", room.getDeposit());
                roomMap.put("paymentMethod", room.getPaymentMethod());
                roomMap.put("facilities", room.getFacilities());
                roomMap.put("status", room.getStatus());
                roomMap.put("description", room.getDescription());
                roomMap.put("createTime", room.getCreateTime());
                roomMap.put("updateTime", room.getUpdateTime());

                // 添加物业信息
                if (room.getProperty() != null) {
                    Map<String, Object> propertyMap = new HashMap<>();
                    propertyMap.put("id", room.getProperty().getId());
                    propertyMap.put("title", room.getProperty().getTitle());
                    propertyMap.put("address", room.getProperty().getAddress());
                    propertyMap.put("landlordId", room.getProperty().getLandlordId());
                    roomMap.put("property", propertyMap);
                }

                // 添加房间图片信息
                try {
                    List<Map<String, Object>> images = roomService.getRoomImages(room.getId());
                    List<String> imageUrls = images.stream()
                            .map(img -> (String) img.get("url"))
                            .collect(Collectors.toList());
                    roomMap.put("imageUrls", imageUrls);
                    
                    // 设置封面图
                    if (!imageUrls.isEmpty()) {
                        roomMap.put("coverImage", imageUrls.get(0));
        } else {
                        roomMap.put("coverImage", null);
                    }
                } catch (Exception e) {
                    log.warn("获取房间图片信息失败: " + e.getMessage());
                    roomMap.put("imageUrls", new ArrayList<>());
                    roomMap.put("coverImage", null);
                }

                roomsWithImages.add(roomMap);
        }

        Map<String, Object> result = new HashMap<>();
            result.put("content", roomsWithImages);

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", page);
        pageInfo.put("size", size);
            pageInfo.put("total", roomsPage.getTotalElements());
            pageInfo.put("totalPages", roomsPage.getTotalPages());
        result.put("pageable", pageInfo);

        return ApiResponse.success("操作成功", result);
        } catch (Exception e) {
            log.error("获取房东房间列表失败", e);
            return ApiResponse.error("获取房间列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取房东房间统计
     */
    @GetMapping("/rooms/stats")
    public ApiResponse<Map<String, Object>> getRoomStats(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Long landlordId = landlordService.getLandlordIdByUserId(userId);

        Map<String, Object> stats = landlordService.getPropertyStats(landlordId);
        return ApiResponse.success("操作成功", stats);
    }

    /**
     * 掩码银行卡号
     */
    private String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "****" + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 获取房东信息（新接口）
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getLandlordInfoNew(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);

        try {
            Landlord landlord = landlordService.getLandlordInfo(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("id", landlord.getId());
            result.put("userId", landlord.getUserId());
            result.put("realName", landlord.getRealName());
            result.put("idCard", landlord.getIdCard());
            result.put("idCardFrontUrl", landlord.getIdCardFrontUrl());
            result.put("idCardBackUrl", landlord.getIdCardBackUrl());
            result.put("bankCard", landlord.getBankCard());
            result.put("bankName", landlord.getBankName());
            result.put("rating", landlord.getRating());
            result.put("verified", landlord.getVerified());
            result.put("createdAt", landlord.getCreatedAt());
            result.put("updatedAt", landlord.getUpdatedAt());

            return ApiResponse.success("操作成功", result);
        } catch (Exception e) {
            log.error("获取房东信息失败", e);
            return ApiResponse.error("获取房东信息失败");
        }
    }

    /**
     * 更新房东信息（新接口）
     */
    @PutMapping("/info")
    public ApiResponse<Map<String, Object>> updateLandlordInfoNew(
            @RequestBody Map<String, String> updateData,
            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);

        try {
            Landlord landlord = landlordService.updateLandlordBankInfo(
                    userId,
                    updateData.get("bankCard"),
                    updateData.get("bankName"));

            Map<String, Object> result = new HashMap<>();
            result.put("id", landlord.getId());
            result.put("bankCard", landlord.getBankCard());
            result.put("bankName", landlord.getBankName());
            result.put("updateTime", LocalDateTime.now());

            return ApiResponse.success("房东信息更新成功", result);
        } catch (Exception e) {
            log.error("更新房东信息失败", e);
            return ApiResponse.error("更新房东信息失败");
        }
    }

    /**
     * 获取房东统计信息（新接口）
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getLandlordStatsNew(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);

        try {
            Landlord landlord = landlordService.getLandlordInfo(userId);
            Map<String, Object> stats = landlordService.getPropertyStats(landlord.getId());

            return ApiResponse.success("操作成功", stats);
        } catch (Exception e) {
            log.error("获取房东统计信息失败", e);
            return ApiResponse.error("获取统计信息失败");
        }
    }

    /**
     * 管理员审核房东认证
     */
    @PutMapping("/{landlordId}/verify")
    public ApiResponse<Map<String, Object>> verifyLandlordByAdmin(
            @PathVariable Long landlordId,
            @RequestParam Boolean verified,
            HttpServletRequest request) {

        try {
            // 这里应该检查管理员权限，暂时跳过
            log.info("管理员审核房东认证 - 房东ID: {}, 审核结果: {}", landlordId, verified);

            Landlord landlord = landlordService.verifyLandlord(landlordId, verified);

            Map<String, Object> result = new HashMap<>();
            result.put("landlordId", landlord.getId());
            result.put("verified", landlord.getVerified());
            result.put("message", verified ? "房东认证已通过" : "房东认证已拒绝");

            return ApiResponse.success("审核完成", result);
        } catch (BusinessException e) {
            log.error("房东认证审核业务异常: {}", e.getMessage());
            return ApiResponse.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("房东认证审核失败", e);
            return ApiResponse.error(500, "审核失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核的房东列表（管理员用）
     */
    @GetMapping("/pending")
    public ApiResponse<Map<String, Object>> getPendingLandlords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        try {
            // 这里应该检查管理员权限，暂时跳过
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Landlord> landlords = landlordService.getAllLandlords(null, null, false, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", landlords.getContent());
            result.put("total", landlords.getTotalElements());
            result.put("totalPages", landlords.getTotalPages());
            result.put("page", page);
            result.put("size", size);

            return ApiResponse.success("操作成功", result);
        } catch (Exception e) {
            log.error("获取待审核房东列表失败", e);
            return ApiResponse.error("获取待审核房东列表失败");
        }
    }

    /**
     * 删除房东（管理员用）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> deleteLandlord(
            @PathVariable Long id,
            HttpServletRequest request) {

        try {
            // 这里应该检查管理员权限，暂时跳过
            log.info("管理员删除房东 - 房东ID: {}", id);

            // 直接调用删除方法，让service层处理存在性检查

            // 删除房东
            landlordService.deleteLandlord(id);
            
            log.info("成功删除房东 - 房东ID: {}", id);
            return ApiResponse.success("删除房东成功");
        } catch (Exception e) {
            log.error("删除房东失败", e);
            return ApiResponse.error("删除房东失败: " + e.getMessage());
        }
    }

    /**
     * 更新房间状态
     */
    @PutMapping("/rooms/{roomId}/status")
    public ApiResponse<Map<String, Object>> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestBody Map<String, String> requestData,
            HttpServletRequest request) {

        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            Long landlordId = landlordService.getLandlordIdByUserId(userId);
            
            String status = requestData.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ApiResponse.error("状态参数不能为空");
            }

            // 验证状态值
            if (!Arrays.asList("VACANT", "RENTED", "MAINTENANCE").contains(status)) {
                return ApiResponse.error("无效的状态值");
            }

            // 验证房间是否属于当前房东
            Room room = roomService.getRoomById(roomId);
            if (room == null) {
                return ApiResponse.error("房间不存在");
            }

            // 检查房间是否属于当前房东
            if (!room.getProperty().getLandlordId().equals(landlordId)) {
                return ApiResponse.error("无权限操作此房间");
            }

            // 更新房间状态
            Room updatedRoom = roomService.updateRoomStatus(roomId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("roomId", updatedRoom.getId());
            result.put("status", updatedRoom.getStatus());
            result.put("message", "房间状态更新成功");

            return ApiResponse.success("房间状态更新成功", result);
        } catch (Exception e) {
            log.error("更新房间状态失败", e);
            return ApiResponse.error("更新房间状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取房间租户信息
     */
    @GetMapping("/rooms/{roomId}/tenant")
    public ApiResponse<Map<String, Object>> getRoomTenant(
            @PathVariable Long roomId,
            HttpServletRequest request) {

        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            Long landlordId = landlordService.getLandlordIdByUserId(userId);

            // 验证房间是否属于当前房东
            Room room = roomService.getRoomById(roomId);
            if (room == null) {
                return ApiResponse.error("房间不存在");
            }

            // 检查房间是否属于当前房东
            if (!room.getProperty().getLandlordId().equals(landlordId)) {
                return ApiResponse.error("无权限查看此房间信息");
            }

            // 获取租户信息
            Map<String, Object> tenantInfo = roomService.getRoomTenantInfo(roomId);

            return ApiResponse.success("获取租户信息成功", tenantInfo);
        } catch (Exception e) {
            log.error("获取房间租户信息失败", e);
            return ApiResponse.error("获取租户信息失败: " + e.getMessage());
        }
    }
}
