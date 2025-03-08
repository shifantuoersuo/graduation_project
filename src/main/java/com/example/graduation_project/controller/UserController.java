package com.example.graduation_project.controller;

import com.example.graduation_project.entity.User;
import com.example.graduation_project.security.JwtUtil;
import com.example.graduation_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 注册接口, 传入 User 对象, 成功返回 "注册成功", 否则返回 "注册失败"。调用 UserService 注册方法,不为空则注册成功
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.registerUser(user) != null ? "注册成功" : "注册失败";
    }
    // 登录接口, 传入 User 对象, 成功返回 token, 否则返回 "用户名或密码错误"。
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> dbUser = userService.findByUsername(user.getUsername());
        if (dbUser.isPresent() && passwordEncoder.matches(user.getPassword(), dbUser.get().getPassword())) {

            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
    }

    // 需要 JWT 认证的 API，获取当前用户信息
    @GetMapping("/me")
    public Optional<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = token.replace("Bearer ", ""); // 去掉 Bearer 前缀
        return userService.findByUsername(username);
    }


    //分配管理员
    @PutMapping("/update-role/{userId}")
    @PreAuthorize("hasRole('ADMIN')")  // 只有管理员能调用
    public String updateUserRole(@PathVariable Long userId, @RequestParam String newRole) {
        return userService.updateUserRole(userId, newRole);
    }

}

