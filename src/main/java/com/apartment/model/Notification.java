package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 通知消息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    /**
     * 接收用户ID
     */
    @NotNull(message = "接收用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 消息内容
     */
    @Column(name = "content", length = 1000)
    private String content;

    /**
     * 消息类型
     */
    @Column(name = "type", length = 50)
    private String type;

    /**
     * 关联ID
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * 关联类型
     */
    @Column(name = "related_type", length = 50)
    private String relatedType;

    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 路由地址
     */
    @Column(name = "route", length = 200)
    private String route;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 阅读时间
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;

    /**
     * 消息类型枚举
     */
    public enum NotificationType {
        SYSTEM("系统消息"),
        ROOM_APPLICATION("租房申请"),
        CONTRACT("合同消息"),
        PAYMENT("支付消息"),
        MAINTENANCE("维修消息");

        private final String description;

        NotificationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 