package com.example.graduation_project.service.Impl;

import com.example.graduation_project.entity.PointRecord;
import com.example.graduation_project.repository.PointRecordRepository;
import com.example.graduation_project.service.PointService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PointServiceImpl implements PointService {

    private final PointRecordRepository pointRecordRepository;

    public PointServiceImpl(PointRecordRepository pointRecordRepository) {
        this.pointRecordRepository = pointRecordRepository;
    }

    @Override
    public void addPoints(Long userId, Integer points, String description) {
        PointRecord record = new PointRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());
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
    public List<Map<String, Object>> getTopUsers() {
        List<Object[]> rawList = pointRecordRepository.findTopUsersByPoints();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rawList) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", row[0]);
            map.put("totalPoints", row[1]);
            result.add(map);
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

