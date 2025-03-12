package com.example.graduation_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 *   实体类：活动
 *   user_id: 用户id
 *   title: 活动标题
 *   description: 活动描述
 *   imageUrl: 活动图片url
 *   status: 活动状态，默认待审核
 *   points: 活动积分，默认0
 *   createTime: 活动创建时间
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
    private String imageUrl;
    private String status = "PENDING"; // 待审核
    private Integer points;
    private LocalDateTime createTime = LocalDateTime.now();

}
