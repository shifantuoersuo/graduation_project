package com.example.graduation_project.controller;

import com.example.graduation_project.Common.Result;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
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
    // 更新用户信息
    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(userDetails.getUsername(), updatedUser));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody Map<String, String> passwords) {
        boolean success = userService.changePassword(
                userDetails.getUsername(),
                passwords.get("oldPassword"),
                passwords.get("newPassword")
        );
        return ResponseEntity.ok("密码更改成功");

    }

    /**
     * 根据用户ID删除用户信息
     * 该方法仅允许具有ADMIN角色的用户调用
     *
     * @param id 用户ID
     * @return 如果成功删除，返回204 No Content响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    // 该方法用于管理员修改用户信息
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserByAdmin(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserByAdmin(id, user));
    }

    // 管理员获取所有用户信息
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * 分页查询
     * pageNum: 当前的页码
     * pageSize：每页的个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<User> pageInfo = userService.selectPage(pageNum, pageSize);
        return Result.success(pageInfo);  // 返回的是分页的对象
    }


}




