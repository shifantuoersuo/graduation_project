package com.example.graduation_project.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分交易记录实体类
 * 关联了User表
 * points: 变动积分
 * reason: 积分变动原因
 * timestamp: 记录时间
 */

@Data
@Entity
@Table(name = "point_transactions")
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //指定关联的实体类User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int points; // 变动积分

    @Column(nullable = false)
    private String reason; // 积分变动原因

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // 记录时间
}

