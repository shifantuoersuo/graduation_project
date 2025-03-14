package com.example.graduation_project.service.Impl;

import com.example.graduation_project.entity.Reward;
import com.example.graduation_project.entity.RewardRecord;
import com.example.graduation_project.repository.RewardRecordRepository;
import com.example.graduation_project.repository.RewardRepository;
import com.example.graduation_project.service.PointService;
import com.example.graduation_project.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.graduation_project.exception.CustomerException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final RewardRecordRepository rewardRecordRepository;
    private final PointService pointService;

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
        pointService.addPoints(userId, -reward.getCost(), "兑换奖励：" + reward.getName());

        // 更新库存
        reward.setStock(reward.getStock() - 1);
        rewardRepository.save(reward);

        return record;
    }

    @Override
    public List<RewardRecord> getUserRewardRecords(Long userId) {
        return rewardRecordRepository.findByUserId(userId);
    }
}

