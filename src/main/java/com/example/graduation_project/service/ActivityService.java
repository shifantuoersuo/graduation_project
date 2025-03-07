package com.example.graduation_project.service;

import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.ActivityRepository;
import com.example.graduation_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    // 提交环保活动
    public String submitActivity(Long userId, Activity activity) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            activity.setUser(user.get());
            activity.setStatus("PENDING"); // 初始状态为待审核
            activityRepository.save(activity);
            return "环保活动提交成功，等待审核";
        }
        return "用户不存在，无法提交环保活动";
    }

    // 获取待审核的环保活动
    public List<Activity> getPendingActivities() {
        return activityRepository.findByStatus("PENDING");
    }

    // 审核环保活动（通过/驳回）
    public String approveActivity(Long activityId, boolean isApproved) {
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isPresent()) {
            Activity activity = activityOptional.get();
            if (isApproved) {
                activity.setStatus("APPROVED"); // 通过审核
                activity.setPoints(10); // 设定积分
            } else {
                activity.setStatus("REJECTED"); // 审核未通过
            }
            activityRepository.save(activity);
            return isApproved ? "活动已审核通过" : "活动未通过审核";
        }
        return "未找到该活动";
    }
}

