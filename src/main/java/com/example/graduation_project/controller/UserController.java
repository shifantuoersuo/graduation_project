package com.example.graduation_project.controller;

import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // 注册接口, 传入 User 对象, 成功返回 "注册成功", 否则返回 "注册失败"。调用 UserService 注册方法,不为空则注册成功
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.registerUser(user) != null ? "注册成功" : "注册失败";
    }
    // 登录接口, 传入 User 对象, 成功返回 token, 否则返回 "用户名或密码错误"。调用 UserService 登录方法,不为空则登录成功
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        String token = userService.loginUser(user.getUsername(), user.getPassword());
        return token != null ? token : "用户名或密码错误";
    }

    //分配管理员
    @PutMapping("/update-role/{userId}")
    @PreAuthorize("hasRole('ADMIN')")  // 只有管理员能调用
    public String updateUserRole(@PathVariable Long userId, @RequestParam String newRole) {
        return userService.updateUserRole(userId, newRole);
    }

}

