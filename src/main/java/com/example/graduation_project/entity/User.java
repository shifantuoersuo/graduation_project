package com.example.graduation_project.entity;

import com.example.graduation_project.Common.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * id：主键
 * username：用户名
 * password：密码
 * email：邮箱
 * avatar：头像
 * role：角色，枚举类型
 * createTime：创建时间
 * updateTime：更新时间
 */
@Data// lombok注解，自动生成get、set方法
@Entity// JPA注解，声明为实体类
@Table(name = "users")// JPA注解，声明表名
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//策略是自动生成id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // 新增头像字段，存储 URL
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;// 枚举类型，声明角色

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer totalPoints = 0;  // 总积分

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer activityCount = 0; // 参与活动总数

    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime;
}

