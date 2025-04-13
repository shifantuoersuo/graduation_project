package com.example.graduation_project.entity;

import com.example.graduation_project.Common.ActivityStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 *   实体类：活动
 *    包含：id、userId、title、description、imageUrl、status、points、createTime、updateTime
 *    id：主键，自增长
 *    userId：活动完成者的id
 *    title：活动标题
 *    description：活动描述
 *    imageUrl：活动相关图片
 *    status：审核状态：PENDING、APPROVED、REJECTED
 *    points：审核后获得的积分
 *    createTime：创建时间
 *    updateTime：更新时间
 */
@Data
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String username;      // 活动完成者的用户名

    private String title;         // 活动标题
    private String description;   // 活动描述
    private String imageUrl;      // 活动相关图片

    @Enumerated(EnumType.STRING)
    private ActivityStatus status = ActivityStatus.PENDING;  // 审核状态：PENDING、APPROVED、REJECTED

    private Integer points = 0;   // 审核后获得的积分

    @Column(name = "reject_reason")
    private String rejectReason;


    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime;

}
