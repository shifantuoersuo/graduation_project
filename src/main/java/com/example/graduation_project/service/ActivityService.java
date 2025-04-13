package com.example.graduation_project.service;

import com.example.graduation_project.Common.ActivityStatus;
import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.entity.ReviewRequest;

import java.util.List;

public interface ActivityService {
    Activity submitActivity(Long userId, String username,Activity activity);
    List<Activity> getUserActivities(Long userId);
    List<Activity> getPendingActivities(); // 管理员查看待审核
    Activity reviewActivity(Long id, ReviewRequest reviewRequest);// 审核 + 评分

    List<Activity> getReviewedActivities();


}