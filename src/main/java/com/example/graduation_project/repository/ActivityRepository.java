package com.example.graduation_project.repository;

import com.example.graduation_project.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByStatus(String status);//根据状态查询活动

    List<Activity> findByUserId(Long userId);//根据用户id查询活动
}

