package com.example.graduation_project.repository;

import com.example.graduation_project.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByUserId(Long userId);
}

