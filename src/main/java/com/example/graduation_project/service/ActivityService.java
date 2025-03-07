package com.example.graduation_project.service;

import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.ActivityRepository;
import com.example.graduation_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public List<Activity> getUserActivities(Long userId) {
        return activityRepository.findByUserId(userId);
    }

    public String deleteActivity(Long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isPresent() && "PENDING".equals(activity.get().getStatus())) {
            activityRepository.delete(activity.get());
            return "活动删除成功";
        }
        return "无法删除活动（活动已审核或不存在）";
    }

    public String reviewActivity(Long id, boolean isApproved, String reason) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isPresent()) {
            if (isApproved) {
                activity.get().setStatus("APPROVED");
                activity.get().setPoints(10); // 设定积分
            } else {
                activity.get().setStatus("REJECTED");
                activity.get().setDescription(activity.get().getDescription() + "（拒绝理由：" + reason + "）");
            }
            activityRepository.save(activity.get());
            return isApproved ? "活动审核通过" : "活动被拒绝：" + reason;
        }
        return "未找到该活动";
    }

    public Page<Activity> listActivities(int page, int size) {
        return activityRepository.findAll(PageRequest.of(page, size));
    }
}

