package com.apartment.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 反馈请求DTO
 */
@Data
public class FeedbackRequest {

    /**
     * 反馈类型
     */
    @NotBlank(message = "反馈类型不能为空")
    private String type; // SUGGESTION(建议)、COMPLAINT(投诉)、BUG(问题)、OTHER(其他)

    /**
     * 反馈标题
     */
    @NotBlank(message = "反馈标题不能为空")
    @Size(min = 5, max = 100, message = "标题长度必须在5到100个字符之间")
    private String title;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(min = 10, max = 1000, message = "内容长度必须在10到1000个字符之间")
    private String content;

    /**
     * 联系方式
     */
    private String contactInfo;
} 