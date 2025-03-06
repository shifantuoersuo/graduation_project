package com.example.graduation_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 *   实体类：活动
 *   用于存储用户发布的活动信息
 *   包括：
 *   - 用户ID
 *   - 活动标题
 *   - 活动描述
 *   - 活动状态（待审核、审核通过、审核不通过）
 *   - 积分
 *   - 创建时间
 */
@Data
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String description;
    private String status = "PENDING"; // 待审核
    private int points = 0;
    private LocalDateTime createdAt = LocalDateTime.now();

}
