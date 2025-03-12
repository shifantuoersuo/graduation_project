package com.example.graduation_project.controller;

import com.example.graduation_project.Common.Result;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.exception.CustomerException;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;



@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor//生成构造函数，自动注入userRepository
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String token = userService.login(loginData.get("username"), loginData.get("password"));
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getByUsername(userDetails.getUsername()));
    }
}




//    //分配管理员
//    @PutMapping("/update-role/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")  // 只有管理员能调用
//    public String updateUserRole(@PathVariable Long userId, @RequestParam String newRole) {
//        return userService.updateUserRole(userId, newRole);
//    }



