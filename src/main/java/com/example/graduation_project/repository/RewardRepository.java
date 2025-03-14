package com.example.graduation_project.repository;

import com.example.graduation_project.entity.Reward;
import com.example.graduation_project.entity.RewardRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {}

