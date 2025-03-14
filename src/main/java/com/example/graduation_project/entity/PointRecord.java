package com.example.graduation_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分记录实体类
 * id: 主键
 * userId: 用户id
 * points: 积分数
 * description: 来源描述
 * createTime: 创建时间
 */
@Data
@Entity
@Table(name = "point_records")
public class PointRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer points;

    private String description; // 来源描述，如“审核通过：校园垃圾清理”

    private LocalDateTime createTime = LocalDateTime.now();

}