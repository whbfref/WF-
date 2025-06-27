package com.apartment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 房间图片实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "room_images")
public class RoomImage extends BaseEntity {

    /**
     * 房间ID
     */
    @NotNull(message = "房间ID不能为空")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /**
     * 图片URL
     */
    @NotNull(message = "图片URL不能为空")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 是否为封面图
     */
    @Column(name = "is_cover", nullable = false)
    private Boolean isCover = false;

    /**
     * 所属房间（关联Room实体）
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;
} 