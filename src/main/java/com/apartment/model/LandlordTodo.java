package com.apartment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 房东待办事项实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "landlord_todos")
public class LandlordTodo extends BaseEntity {

    /**
     * 房东ID
     */
    @NotNull(message = "房东ID不能为空")
    @Column(name = "landlord_id", nullable = false)
    private Long landlordId;

    /**
     * 关联的房东对象
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", insertable = false, updatable = false)
    private Landlord landlord;

    /**
     * 待办类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TodoType type;

    /**
     * 待办标题
     */
    @NotBlank(message = "待办标题不能为空")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 待办描述
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 关联ID (如合同ID、房产ID等)
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * 关联类型（用于区分relatedId的类型）
     */
    @Column(name = "related_type", length = 50)
    private String relatedType;

    /**
     * 待办状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TodoStatus status = TodoStatus.PENDING;

    /**
     * 处理备注
     */
    @Column(name = "comment", length = 500)
    private String comment;

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
     * 待办类型枚举
     */
    public enum TodoType {
        CONTRACT_REVIEW("合同审核"),
        RENT_COLLECTION("租金收取"),
        MAINTENANCE("维修处理"),
        RATING_REPLY("评价回复"),
        ROOM_APPLICATION("租房申请");

        private final String description;

        TodoType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 待办状态枚举
     */
    public enum TodoStatus {
        PENDING("待处理"),
        PROCESSING("处理中"),
        DONE("已完成"),
        IGNORED("已忽略");

        private final String description;

        TodoStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 