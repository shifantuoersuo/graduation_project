package com.example.graduation_project.controller;

import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return "用户名已存在";
        }
        userService.registerUser(user);
        return "注册成功";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        Optional<User> dbUser = userService.findByUsername(user.getUsername());
        if (dbUser.isPresent() && passwordEncoder.matches(user.getPassword(), dbUser.get().getPassword())) {
            return "登录成功"; // 这里可以返回 JWT 令牌
        }
        return "用户名或密码错误";
    }
}

