package com.example.graduation_project.controller;

import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/pending")
    public List<Activity> getPendingActivities() {
        return activityService.getPendingActivities();
    }

    // 审核环保活动（管理员操作）
    @PostMapping("/approve/{id}")
    public String approveActivity(@PathVariable Long id, @RequestParam boolean isApproved) {
        return activityService.approveActivity(id, isApproved);
    }
}

