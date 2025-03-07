package com.example.graduation_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * id：用户id
 * username：用户名
 * password：密码
 * email：邮箱
 * role：角色，默认为普通用户
 * created_time：创建时间
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

    @Column(nullable = false)
    private String role = "USER"; // 默认为普通用户

    @Column(nullable = false)
    private int points = 0; // 用户积分，默认 0 分

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

}

