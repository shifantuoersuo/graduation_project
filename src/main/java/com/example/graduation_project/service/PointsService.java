package com.example.graduation_project.service;

import com.example.graduation_project.entity.Point;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.PointRepository;
import com.example.graduation_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PointsService {
    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    // 查询用户积分
    public int getUserPoints(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return pointRepository.findByUser(user.get()).map(Point::getTotalPoints).orElse(0);
        }
        return 0;
    }

    // 更新用户积分（管理员操作）
    public boolean updatePoints(Long userId, int points) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Point point = pointRepository.findByUser(user.get()).orElse(new Point());
            point.setUser(user.get());
            point.setTotalPoints(point.getTotalPoints() + points);
            pointRepository.save(point);
            return true;
        }
        return false;
    }

    // 用户兑换积分（扣除积分）
    public boolean redeemPoints(Long userId, int pointsToRedeem) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Point point = pointRepository.findByUser(user.get()).orElse(new Point());
            if (point.getTotalPoints() >= pointsToRedeem) {
                point.setTotalPoints(point.getTotalPoints() - pointsToRedeem);
                pointRepository.save(point);
                return true;
            }
        }
        return false;
    }
}

