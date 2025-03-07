package com.example.graduation_project.controller;

import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    // 用户提交环保活动
    @PostMapping("/submit/{userId}")
    public String submitActivity(@PathVariable Long userId, @RequestBody Activity activity) {
        return activityService.submitActivity(userId, activity);
    }

    // 获取待审核的环保活动（管理员查看）
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public List<Activity> getPendingActivities() {
        return activityService.getPendingActivities();
    }

    // 审核环保活动（管理员操作）
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{id}")
    public String approveActivity(@PathVariable Long id, @RequestParam boolean isApproved) {
        return activityService.approveActivity(id, isApproved);
    }

    //用户查询自己提交的环保活动
    @GetMapping("/user/{userId}")
    public List<Activity> getUserActivities(@PathVariable Long userId) {
        return activityService.getUserActivities(userId);
    }

    //用户删除未审核的环保活动
    @DeleteMapping("/delete/{id}")
    public String deleteActivity(@PathVariable Long id) {
        return activityService.deleteActivity(id);
    }

    // 管理员拒绝活动时，添加拒绝原因
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/review/{id}")
    public String reviewActivity(@PathVariable Long id, @RequestParam boolean isApproved, @RequestParam(required = false) String reason) {
        return activityService.reviewActivity(id, isApproved, reason);
    }

    //分页查询环保活动
    @GetMapping("/list")
    public Page<Activity> listActivities(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        return activityService.listActivities(page, size);
    }
}

