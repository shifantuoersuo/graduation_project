package com.example.graduation_project.service.Impl;

import com.example.graduation_project.Common.ActivityStatus;
import com.example.graduation_project.entity.Activity;
import com.example.graduation_project.entity.ReviewRequest;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.ActivityRepository;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.service.ActivityService;
import com.example.graduation_project.service.PointService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final PointService pointService;
    private final UserRepository userRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, PointService pointService, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.pointService = pointService;
        this.userRepository = userRepository;
    }

    @Override
    public Activity submitActivity(Long userId, String username,Activity activity) {
        activity.setUserId(userId);
        activity.setUsername(username);
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
     * @param request    审核请求
     *                    status: 审核状态
     *                    points: 审核通过时给予的积分
     *                    reason: 审核拒绝时给予的理由
     * @return 更新后的活动记录
     */
    public Activity reviewActivity(Long activityId, ReviewRequest request) {
        ActivityStatus status = request.getStatus();
        int points = request.getPoints();
        String reason = request.getReason();
        System.out.println("审核活动：" + activityId + "，状态：" + status + "，积分：" + points + "，理由：" + reason);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("活动不存在"));

        ActivityStatus originalStatus = activity.getStatus();
        User user = userRepository.findById(activity.getUserId())
                .orElseThrow(() -> new RuntimeException("关联用户不存在"));

        if (status == ActivityStatus.APPROVED) {
            if (originalStatus != ActivityStatus.APPROVED) {
                pointService.addPoints(user.getId(), points, "审核通过：" + activity.getTitle(),user.getUsername());
                user.setTotalPoints(user.getTotalPoints() + points);
                user.setActivityCount(user.getActivityCount() + 1);
            } else if (activity.getPoints() != points) {
                int diff = points - activity.getPoints();
                pointService.addPoints(user.getId(), diff, "积分调整：" + activity.getTitle(),user.getUsername());
                user.setTotalPoints(user.getTotalPoints() + diff);
            }
            activity.setPoints(points);
            activity.setRejectReason(null); // 清空拒绝理由
        } else if (status == ActivityStatus.REJECTED) {
            if (originalStatus == ActivityStatus.APPROVED) {
                user.setTotalPoints(user.getTotalPoints() - activity.getPoints());
                user.setActivityCount(user.getActivityCount() - 1);
                pointService.addPoints(user.getId(), -activity.getPoints(), "审核撤销：" + activity.getTitle(), user.getUsername());
            }
            activity.setPoints(0);
            activity.setRejectReason(reason); // ✅ 保存拒绝理由
        } else if (status == ActivityStatus.PENDING) {
            if (originalStatus == ActivityStatus.APPROVED) {
                user.setTotalPoints(user.getTotalPoints() - activity.getPoints());
                user.setActivityCount(user.getActivityCount() - 1);
                pointService.addPoints(user.getId(), -activity.getPoints(), "审核撤销：" + activity.getTitle(), user.getUsername());
            }
            activity.setPoints(0);
            activity.setRejectReason(null); // 恢复为未审核时清空拒绝理由
        }

        activity.setStatus(status);
        activity.setUpdateTime(LocalDateTime.now());
        activity.setUsername(user.getUsername());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> getReviewedActivities() {
        return activityRepository.findByStatusNot(ActivityStatus.PENDING);
    }


}

