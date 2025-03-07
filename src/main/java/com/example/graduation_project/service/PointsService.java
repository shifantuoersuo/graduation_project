package com.example.graduation_project.service;

import com.example.graduation_project.entity.Point;
import com.example.graduation_project.entity.PointTransaction;
import com.example.graduation_project.entity.User;
import com.example.graduation_project.repository.PointRepository;
import com.example.graduation_project.repository.PointTransactionRepository;
import com.example.graduation_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PointsService {
    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

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
    // 用户积分历史查询
    public List<PointTransaction> getUserPointsHistory(Long userId) {
        return pointTransactionRepository.findByUserId(userId);
    }

    //管理员调整用户积分
    public String adjustUserPoints(Long userId, int points, String reason) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setPoints(user.get().getPoints() + points);
            userRepository.save(user.get());

            // 记录积分变动
            PointTransaction transaction = new PointTransaction();
            transaction.setUser(user.get());
            transaction.setPoints(points);
            transaction.setReason(reason);
            pointTransactionRepository.save(transaction);

            return "用户积分调整成功";
        }
        return "用户不存在";
    }

    public List<User> getTopUsersByPoints() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "points")).stream().limit(10).collect(Collectors.toList());
    }
}

