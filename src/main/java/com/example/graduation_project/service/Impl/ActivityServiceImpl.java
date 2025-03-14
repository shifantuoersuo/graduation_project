package com.example.graduation_project.service.Impl;

import com.example.graduation_project.Common.ActivityStatus;
import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.repository.ActivityRepository;
import com.example.graduation_project.service.ActivityService;
import com.example.graduation_project.service.PointService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final PointService pointService;

    public ActivityServiceImpl(ActivityRepository activityRepository, PointService pointService) {
        this.activityRepository = activityRepository;
        this.pointService = pointService;
    }

    @Override
    public Activity submitActivity(Long userId, Activity activity) {
        activity.setUserId(userId);
        activity.setStatus(ActivityStatus.PENDING);
        activity.setCreateTime(LocalDateTime.now());
        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> getUserActivities(Long userId) {
        return activityRepository.findByUserId(userId);
    }

    @Override
    public List<Activity> getPendingActivities() {
        return activityRepository.findByStatus(ActivityStatus.PENDING);
    }


    /**
     * 审核活动记录
     * 根据活动ID查找活动，更新其状态和积分，并保存更改
     *
     * @param activityId 活动ID
     * @param status     活动状态
     * @param points     审核通过时增加的积分
     * @return 更新后的活动记录
     */
    @Override
    public Activity reviewActivity(Long activityId, ActivityStatus status, int points) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (status == ActivityStatus.APPROVED) {
            pointService.addPoints(activity.getUserId(), points, "审核通过：" + activity.getTitle());
        }

        activity.setStatus(status);
        activity.setPoints(status == ActivityStatus.APPROVED ? points : 0);
        activity.setUpdateTime(LocalDateTime.now());
        return activityRepository.save(activity);
    }
}

