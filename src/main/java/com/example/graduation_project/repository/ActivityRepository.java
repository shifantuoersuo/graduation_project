package com.example.graduation_project.repository;

import com.example.graduation_project.Common.ActivityStatus;
import com.example.graduation_project.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUserId(Long userId);//根据用户id查询活动
    List<Activity> findByStatus(ActivityStatus status);//根据活动状态查询活动
}

