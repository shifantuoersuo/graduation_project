package com.example.graduation_project.service;

import com.example.graduation_project.entity.Reward;
import com.example.graduation_project.entity.RewardRecord;

import java.util.List;

public interface RewardService {
    List<Reward> getAllRewards();
    RewardRecord redeem(Long userId, Long rewardId);
    List<RewardRecord> getUserRewardRecords(Long userId);
}

