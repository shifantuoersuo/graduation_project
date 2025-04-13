package com.example.graduation_project.service;

import com.example.graduation_project.entity.PointRecord;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface PointService {
    void addPoints(Long userId, Integer points, String description, String username);
    List<PointRecord> getUserPointRecords(Long userId);
    Integer getUserTotalPoints(Long userId);
    List<Map<String, Object>> getTopUsers(UserDetails userDetails); // 积分排行榜

    List<PointRecord> getUserRecordsAfterDays(Long userId, int days);

    List<PointRecord> getUserRecordsByKeyword(Long userId, String keyword);
}
