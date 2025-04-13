package com.example.graduation_project.service.Impl;

import com.example.graduation_project.entity.Reward;
import com.example.graduation_project.entity.RewardRecord;
import com.example.graduation_project.repository.RewardRecordRepository;
import com.example.graduation_project.repository.RewardRepository;
import com.example.graduation_project.repository.UserRepository;
import com.example.graduation_project.service.PointService;
import com.example.graduation_project.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.graduation_project.exception.CustomerException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final RewardRecordRepository rewardRecordRepository;
    private final PointService pointService;
    private final UserRepository userRepository;

    @Override
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    @Override
    public RewardRecord redeem(Long userId, Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new CustomerException("奖励不存在"));

        Integer userPoints = pointService.getUserTotalPoints(userId);
        if (userPoints < reward.getCost()) {
            throw new CustomerException("积分不足");
        }
        if (reward.getStock() <= 0) {
            throw new CustomerException("奖励库存不足");
        }

        // 生成兑换记录
        RewardRecord record = new RewardRecord();
        record.setUserId(userId);
        record.setRewardId(reward.getId());
        record.setRewardName(reward.getName());
        record.setCost(reward.getCost());
        rewardRecordRepository.save(record);

        // 扣除积分
        pointService.addPoints(userId, -reward.getCost(), "兑换奖励：" + reward.getName(),userRepository.findById(userId).get().getUsername());

        userRepository.findById(userId).get().setTotalPoints(userRepository.findById(userId).get().getTotalPoints() - reward.getCost());
        userRepository.findById(userId).get().setUpdateTime(LocalDateTime.now());
        userRepository.save(userRepository.findById(userId).get());
        // 更新库存
        reward.setStock(reward.getStock() - 1);
        rewardRepository.save(reward);

        return record;
    }

    @Override
    public List<RewardRecord> getUserRewardRecords(Long userId) {
        return rewardRecordRepository.findByUserId(userId);
    }

    @Override
    public Reward createReward(Reward reward) {
        // 检查重复名称
        if (rewardRepository.existsByName(reward.getName())) {
            throw new IllegalArgumentException("奖励名称已存在");
        }

        // 设置默认值
        if (reward.getCost() == null) reward.setCost(0);
        if (reward.getStock() == null) reward.setStock(0);

        return rewardRepository.save(reward);
    }

    @Override
    public Reward updateReward(Long id, Reward updatedReward) {
        return rewardRepository.findById(id)
                .map(existingReward -> {
                    // 名称冲突检查（排除自己）
                    if (!existingReward.getName().equals(updatedReward.getName())
                            && rewardRepository.existsByName(updatedReward.getName())) {
                        throw new IllegalArgumentException("奖励名称已存在");
                    }

                    existingReward.setName(updatedReward.getName());
                    existingReward.setDescription(updatedReward.getDescription());
                    existingReward.setCost(updatedReward.getCost());
                    existingReward.setStock(updatedReward.getStock());
                    existingReward.setImageUrl(updatedReward.getImageUrl());
                    return rewardRepository.save(existingReward);
                })
                .orElseThrow(() -> new CustomerException("奖励不存在"));
    }

    @Override
    public void deleteReward(Long id) {
        if (!rewardRepository.existsById(id)) {
            throw new CustomerException("奖励不存在");
        }
        rewardRepository.deleteById(id);
    }
}

