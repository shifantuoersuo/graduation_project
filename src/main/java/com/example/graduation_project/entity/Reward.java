package com.example.graduation_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分兑换实体类
 * id: 奖励id
 * name: 奖励物品名称
 * description: 奖励描述
 * cost: 所需积分
 * imageUrl: 奖励图像（可选）
 * stock: 剩余库存
 * createTime: 创建时间
 */
@Data
@Entity
@Table(name = "rewards")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;         // 奖励名称
    private String description;  // 奖励描述
    private Integer cost;        // 所需积分
    private String imageUrl;     // 奖励图像（可选）

    private Integer stock;       // 剩余库存

    private LocalDateTime createTime = LocalDateTime.now();
}

