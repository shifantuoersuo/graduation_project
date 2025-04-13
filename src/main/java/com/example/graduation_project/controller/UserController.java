package com.example.graduation_project.controller;

import com.example.graduation_project.Common.ActivityStatus;
import com.example.graduation_project.Common.Role;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.ActivityRepository;
import com.example.graduation_project.repository.PointRecordRepository;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.graduation_project.entity.PointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor//生成构造函数，自动注入userRepository
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PointRecordRepository pointRecordRepository;
    private final ActivityRepository activityRepository;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String token = userService.login(loginData.get("username"), loginData.get("password"));
        String role = userService.getRole(loginData.get("username"));
        System.out.println("token: " + token + " role: " + role);
        return ResponseEntity.ok(Map.of("token", token, "role", role));
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

//    @PutMapping("/change-password")
//    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
//                                                 @RequestBody Map<String, String> passwords) {
//        boolean success = userService.changePassword(
//                userDetails.getUsername(),
//                passwords.get("oldPassword"),
//                passwords.get("newPassword")
//        );
//        return ResponseEntity.ok("密码更改成功");
//
//    }
    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody Map<String, String> payload) {
        String oldPwd = payload.get("oldPassword");
        String newPwd = payload.get("newPassword");

        userService.changePassword(userDetails.getUsername(), oldPwd, newPwd);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public void resetPassword(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails) {
        userService.resetPassword(id, userDetails.getUsername());
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 该方法用于管理员修改用户信息
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserByAdmin(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long id,
                                                  @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserByAdmin(id, user, userDetails.getUsername()));
    }

    // 管理员获取所有用户信息
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> result = new HashMap<>();

        result.put("userCount", userRepository.count());
        result.put("activityCount", activityRepository.count());
        result.put("totalPoints", pointRecordRepository.findAll().stream()
                .mapToInt(PointRecord::getPoints).sum());
        result.put("pendingActivities", activityRepository.countByStatus(ActivityStatus.PENDING));

        // 近 7 天积分趋势
        List<Map<String, Object>> pointsTrends = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            int points = pointRecordRepository.findByDateRange(start, end)
                    .stream()
                    .mapToInt(PointRecord::getPoints)
                    .sum();

            pointsTrends.add(Map.of("date", date.toString(), "points", points));
        }
        result.put("pointsTrends", pointsTrends);

        // 近 7 天活动趋势
        List<Map<String, Object>> activityTrends = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            long count = activityRepository.countByDateRange(start, end);
            activityTrends.add(Map.of("date", date.toString(), "count", count));
        }
        result.put("activityTrends", activityTrends);
        System.out.println(result);

        return ResponseEntity.ok(result);
    }
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role) { // 新增角色参数
        Page<User> page = userService.searchUsers(pageNum, pageSize, keyword, role);
        return ResponseEntity.ok(page);
    }
}







