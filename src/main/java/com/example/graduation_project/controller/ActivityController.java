package com.example.graduation_project.controller;

import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    // 提交环保活动
    @PostMapping("/submit")
    public String submitActivity(@RequestBody Activity activity) {
        activityRepository.save(activity);
        return "环保活动提交成功，等待审核";
    }
    // 获取待审核的环保活动
    @GetMapping("/pending")
    public List<Activity> getPendingActivities() {
        return activityRepository.findByStatus("PENDING");
    }
    // 审核环保活动
    @PostMapping("/approve/{id}")
    public String approveActivity(@PathVariable Long id) {
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            activity.setStatus("APPROVED");
            activity.setPoints(10); // 设定积分
            activityRepository.save(activity);
            return "活动已审核通过";
        }
        return "未找到该活动";
    }
}
