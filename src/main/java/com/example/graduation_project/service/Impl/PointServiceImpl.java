package com.example.graduation_project.service.Impl;

import com.example.graduation_project.Common.Role;
import com.example.graduation_project.entity.PointRecord;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.PointRecordRepository;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.service.PointService;
import com.example.graduation_project.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PointServiceImpl implements PointService {

    private final PointRecordRepository pointRecordRepository;
    private final UserService userService;

    private final UserRepository userRepository;

    public PointServiceImpl(PointRecordRepository pointRecordRepository, UserService userService, UserRepository userRepository) {
        this.pointRecordRepository = pointRecordRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void addPoints(Long userId, Integer points, String description,String username) {
        PointRecord record = new PointRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());
        record.setUserName(username);
        pointRecordRepository.save(record);
    }

    @Override
    public List<PointRecord> getUserPointRecords(Long userId) {
        return pointRecordRepository.findByUserId(userId);
    }

    @Override
    public Integer getUserTotalPoints(Long userId) {
        return Optional.ofNullable(pointRecordRepository.getTotalPointsByUserId(userId)).orElse(0);
    }

    @Override
    public List<Map<String, Object>> getTopUsers(UserDetails userDetails) {
        List<User> rawList = userRepository.findByRole(Role.USER);
        List<Map<String, Object>> result = new ArrayList<>();
        rawList.sort((u1, u2) -> Long.compare(getUserTotalPoints(u2.getId()), getUserTotalPoints(u1.getId())));

        Map<Long, Integer> userIdToRank = new HashMap<>();
        for (int i = 0; i < rawList.size(); i++) {
            userIdToRank.put(rawList.get(i).getId(), i + 1);
        }

        User currentUser = userService.getByUsername(userDetails.getUsername());
        List<User> topUsers = rawList.stream().limit(10).collect(Collectors.toList());

        // 处理前10名并添加标识
        for (User row : topUsers) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", row.getUsername());
            map.put("totalPoints", getUserTotalPoints(row.getId()));
            map.put("rank", userIdToRank.get(row.getId()));
            map.put("isCurrent", row.getId().equals(currentUser.getId()));// 判断是否为当前用户
            result.add(map);
        }

        // 处理当前用户（如果不在前10）
        boolean isInTop10 = topUsers.stream().anyMatch(u -> u.getId().equals(currentUser.getId()));
        if (!isInTop10) {
            int currentTotalPoints = getUserTotalPoints(currentUser.getId());
            int currentUserRank = userIdToRank.get(currentUser.getId());

            Map<String, Object> currentMap = new HashMap<>();
            currentMap.put("username", currentUser.getUsername());
            currentMap.put("totalPoints", currentTotalPoints);
            currentMap.put("rank", currentUserRank);
            currentMap.put("isCurrent", true);
            result.add(currentMap);
        }

        return result;
    }


    @Override
    public List<PointRecord> getUserRecordsAfterDays(Long userId, int days) {
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        return pointRecordRepository.findByUserIdAndAfterTime(userId, start);
    }
    @Override
    public List<PointRecord> getUserRecordsByKeyword(Long userId, String keyword) {
        return pointRecordRepository.findByUserIdAndDescriptionContaining(userId, keyword);
    }

}

