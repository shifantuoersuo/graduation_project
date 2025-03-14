package com.example.graduation_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兑换记录实体类
 * id: 自增主键
 * userId: 用户id
 * rewardId: 奖品id
 * rewardName: 奖品名称
 * cost: 奖品价格
 * redeemTime: 兑换时间
 */
@Data
@Entity
@Table(name = "reward_records")
public class RewardRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long rewardId;
    private String rewardName;
    private Integer cost;
    private LocalDateTime redeemTime = LocalDateTime.now();
}
