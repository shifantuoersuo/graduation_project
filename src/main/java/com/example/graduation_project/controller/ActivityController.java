package com.example.graduation_project.controller;

import com.example.graduation_project.Common.ActivityStatus;
import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.entity.ReviewRequest;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.service.ActivityService;
import com.example.graduation_project.service.Impl.ActivityServiceImpl;
import com.example.graduation_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final UserService userService;

    /**
     * 提交活动的API接口
     *
     * 该方法允许用户提交一个新的活动，并返回提交后的活动对象。
     * 只有经过身份验证的用户才能调用此接口。
     *
     * @param userDetails 当前登录的用户信息，通过Spring Security的@AuthenticationPrincipal注解自动注入
     * @param activity 用户提交的活动对象，通过@RequestBody注解从HTTP请求体中解析出来
     * @return ResponseEntity<Activity> 返回一个包含提交后的活动对象的HTTP响应
     */
    @PostMapping("/submit")
    public ResponseEntity<Activity> submitActivity(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody Activity activity) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(activityService.submitActivity(user.getId(), user.getUsername(),activity));
    }

    /**
     * 获取当前用户的活动列表
     * @param userDetails 当前认证的用户详情
     * @return 包含用户活动列表的响应实体
     */
    @GetMapping("/my")
    public ResponseEntity<List<Activity>> getMyActivities(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(activityService.getUserActivities(user.getId()));
    }

    /**
     * 获取所有待审核的活动列表
     * 该接口仅允许具有ADMIN角色的用户访问。
     * @return 包含待审核活动列表的响应实体
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Activity>> getPendingActivities() {
        return ResponseEntity.ok(activityService.getPendingActivities());
    }


    @PostMapping("/review/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Activity> reviewActivity(@PathVariable Long id,
                                                   @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok(activityService.reviewActivity(id, reviewRequest));
    }

    /**
     * 获取所有已审核的活动（非 PENDING 状态）
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Activity>> getReviewedActivities() {
        return ResponseEntity.ok(activityService.getReviewedActivities());
    }
}
