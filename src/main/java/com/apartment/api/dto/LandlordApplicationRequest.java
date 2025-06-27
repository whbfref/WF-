package com.apartment.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 申请成为房东请求DTO
 */
@Data
public class LandlordApplicationRequest {

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 20, message = "真实姓名长度必须在2到20个字符之间")
    private String realName;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    /**
     * 身份证正面照片URL
     */
    @NotBlank(message = "身份证正面照片不能为空")
    private String idCardFrontUrl;

    /**
     * 身份证反面照片URL
     */
    @NotBlank(message = "身份证反面照片不能为空")
    private String idCardBackUrl;

    /**
     * 房产证明文件URL
     */
    private String propertyProofUrl;

    /**
     * 申请理由
     */
    @NotBlank(message = "申请理由不能为空")
    @Size(min = 10, max = 500, message = "申请理由长度必须在10到500个字符之间")
    private String reason;
} 