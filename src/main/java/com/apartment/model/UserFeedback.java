package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户反馈实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_feedback")
public class UserFeedback extends BaseEntity {

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 反馈类型
     */
    @NotBlank(message = "反馈类型不能为空")
    @Column(nullable = false, length = 20)
    private String type; // SUGGESTION(建议)、COMPLAINT(投诉)、BUG(问题)、OTHER(其他)

    /**
     * 反馈标题
     */
    @NotBlank(message = "反馈标题不能为空")
    @Size(min = 5, max = 100, message = "标题长度必须在5到100个字符之间")
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(min = 10, max = 1000, message = "内容长度必须在10到1000个字符之间")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 联系方式
     */
    @Column(name = "contact_info", length = 50)
    private String contactInfo;

    /**
     * 处理状态
     */
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING(待处理)、PROCESSING(处理中)、COMPLETED(已完成)、CLOSED(已关闭)

    /**
     * 回复内容
     */
    @Column(columnDefinition = "TEXT")
    private String reply;

    /**
     * 回复时间
     */
    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    /**
     * 用户关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
} 