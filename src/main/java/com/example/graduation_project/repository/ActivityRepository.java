package com.example.graduation_project.repository;

import com.example.graduation_project.Common.ActivityStatus;
import com.example.graduation_project.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUserId(Long userId);//根据用户id查询活动
    List<Activity> findByStatus(ActivityStatus status);//根据活动状态查询活动

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.createTime >= :start AND a.createTime < :end")
    long countByDateRange(@Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);

    Object countByStatus(ActivityStatus activityStatus);

    List<Activity> findByStatusNot(ActivityStatus activityStatus);
}

